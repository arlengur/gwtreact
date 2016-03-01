/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.*;

import com.tecomgroup.qos.service.rbac.AuthorizeService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.util.Assert;

import com.tecomgroup.qos.HasUniqueKey;
import com.tecomgroup.qos.OperationType;
import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.communication.pm.PMTaskConfiguration;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.*;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.PoliciesEvent;
import com.tecomgroup.qos.event.PolicyEvent;
import com.tecomgroup.qos.exception.*;
import com.tecomgroup.qos.messages.DefaultPolicyValidationMessages;
import com.tecomgroup.qos.messages.PolicyValidationMessages;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;
import com.tecomgroup.qos.util.*;

/**
 * @author kunilov.p
 * 
 */
@Service("policyConfigurationService")
public class DefaultPolicyConfigurationService extends AbstractService
		implements
			InternalPolicyConfigurationService {

	private class PolicyManagerRegistrationInfo implements HasUniqueKey {
		private String policyManagerName;

		private boolean allAgentsSupported;

		private Set<String> agents;

		public Set<String> getAgents() {
			return agents;
		}

		@Override
		public String getKey() {
			return policyManagerName;
		}

		public boolean isAllAgentsSupported() {
			return allAgentsSupported;
		}

		public void setAgents(final Set<String> agents) {
			this.agents = agents;
		}

		public void setAllAgentsSupported(final boolean allAgentsSupported) {
			this.allAgentsSupported = allAgentsSupported;
		}

		public void setPolicyManagerName(final String policyManagerName) {
			this.policyManagerName = policyManagerName;
		}

	}

	private static Set<String> getTaskKeysFromPolicies(
			final List<MPolicy> policies) {
		final Set<String> taskKeys = new HashSet<String>();

		if (SimpleUtils.isNotNullAndNotEmpty(policies)) {
			for (final MPolicy policy : policies) {
				taskKeys.add(policy.getSource().getKey());
			}
		}
		return taskKeys;
	}

	private AgentService agentService;

	private TaskRetriever taskRetriever;

	private AlertService alertService;

	private PolicyComponentTemplateService policyComponentTemplateService;

	private final Map<String, PolicyManagerRegistrationInfo> policyManagerRegistrationInfo;

	private static Logger LOGGER = Logger
			.getLogger(DefaultPolicyConfigurationService.class);

	private SourceService sourceService;

	private AuthorizeService authorizeService;

	public DefaultPolicyConfigurationService() {
		policyManagerRegistrationInfo = new HashMap<String, PolicyManagerRegistrationInfo>();
	}

	@Override
	public void addAgentToRegisteredPMInfo(final String agentKey) {
		synchronized (policyManagerRegistrationInfo) {
			for (final PolicyManagerRegistrationInfo pmRegistrationInfo : policyManagerRegistrationInfo
					.values()) {
				if (pmRegistrationInfo.isAllAgentsSupported()) {
					final Set<String> supportedAgents = pmRegistrationInfo
							.getAgents();
					// it is not necessary to update pm info if agent was
					// already registered
					if (!supportedAgents.contains(agentKey)) {
						supportedAgents.add(agentKey);
						LOGGER.info("New agent (" + agentKey
								+ ") was registered on Policy Manager ("
								+ pmRegistrationInfo.getKey() + ")");
					}
				}
			}
		}
	}

	private void applyAgentPolicyTemplate(final String agentKey,
			final MPolicyTemplate policyTemplate) {
		final String moduleKey = policyTemplate.getSource().getSimpleKey(1);

		final List<MAgentTask> tasks = taskRetriever.getAgentTasks(agentKey,
				new HashSet<String>(Arrays.asList(moduleKey)), null, null,
				null, false, true);

		if (SimpleUtils.isNotNullAndNotEmpty(tasks)) {
			for (final MAgentTask task : tasks) {
				registerTaskPolicyTemplate(task, policyTemplate);
			}
		}
	}

	@Override
	public void applyAgentPolicyTemplates(final String agentKey,
			final MAgentTask task) {
		final List<MPolicyTemplate> agentPolicyTemplates = getPolicyTemplates(
				agentKey,
				Source.getModuleSource(agentKey, task.getParent().getKey()),
				false, null);
		for (final MPolicyTemplate agentPolicyTemplate : agentPolicyTemplates) {
			try {
				applyAgentPolicyTemplate(agentKey, agentPolicyTemplate);
			} catch (final Exception ex) {
				LOGGER.error("Unable to apply agent policy template: "
						+ agentPolicyTemplate + " to task: " + task, ex);
			}
		}
	}

	private void applyParameterPolicyTemplate(final MAgentTask task,
			final MPolicyTemplate taskPolicyTemplate,
			final ParameterIdentifier parameterIdentifier) {
		try {
			if (policyConditionHasTheSameParameter(
					taskPolicyTemplate.getCondition(), parameterIdentifier)) {
				final String policyKey = createPolicyKey(
						taskPolicyTemplate.getKey(),
						createParameterStorageKeyForPolicy(task,
								parameterIdentifier));
				final MPolicy existingPolicy = executeInTransaction(true,
						new TransactionCallback<MPolicy>() {

							@Override
							public MPolicy doInTransaction(
									final TransactionStatus status) {
								final CriterionQuery query = modelSpace
										.createCriterionQuery();

								return modelSpace.findUniqueEntity(
										MPolicy.class,
										query.eq("key", policyKey));
							}
						});
				if (existingPolicy == null) {
					final MPolicy taskPolicy = taskPolicyTemplate.createPolicy(
							policyKey, task, parameterIdentifier);
					saveOrUpdateTaskPolicy(task, taskPolicy);

					LOGGER.info("Create new policy: " + taskPolicy);
				} else {
					// We don't update concrete policy, because the user may
					// have already changed it.
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException("Unable to apply policy template: "
					+ taskPolicyTemplate + " of task: " + task
					+ " to parameter: " + parameterIdentifier, ex);
		}
	}

	@Override
	public void applyParameterPolicyTemplates(final MAgentTask task,
			final ParameterIdentifier parameterIdentifier) {
		final List<MPolicyTemplate> parameterPolicyTemplates = getPolicyTemplates(
				SimpleUtils.findSystemComponent(task).getKey(),
				Source.getTaskSource(task.getKey()), false,
				parameterIdentifier.getName());

		for (final MPolicyTemplate parameterPolicyTemplate : parameterPolicyTemplates) {
			try {
				applyParameterPolicyTemplate(task, parameterPolicyTemplate,
						parameterIdentifier);
			} catch (final Exception ex) {
				LOGGER.error("Unable to apply policy template: "
						+ parameterPolicyTemplate + " of task: " + task
						+ " to parameter: " + parameterIdentifier, ex);
			}
		}
	}

	@Override
	@Transactional
	public void applyPolicyActionsTemplate(final String templateName,
			final Set<String> policyKeys) {
		try {
			final MPolicyActionsTemplate template = (MPolicyActionsTemplate) policyComponentTemplateService
					.getTemplateByName(templateName,
							MPolicyActionsTemplate.class.getName());
			if (template != null) {
				final List<MPolicy> policies = modelSpace.find(MPolicy.class,
						modelSpace.createCriterionQuery().in("key", policyKeys));
				updatePolicyActionsByTemplate(policies, template);
			} else {
				throw new ServiceException(
						"Unable to apply actions template to policy. Template with name "
								+ templateName + " was not found.");
			}
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK,"Action template {} applied to policies : {}",templateName,Arrays.toString(policyKeys.toArray(new String[policyKeys.size()])));
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.NOK, "Unable to apply action template {} to policies : {}",templateName, Arrays.toString(policyKeys.toArray(new String[policyKeys.size()])));
			throw e;
		}
	}

	@Override
	@Transactional
	public void applyPolicyConditionsTemplate(final String templateName,
			final Set<String> policyKeys) throws CompatibilityException,
			ParameterTypeCompatibilityException {
		try {
			final MPolicyConditionsTemplate template = (MPolicyConditionsTemplate) policyComponentTemplateService
					.getTemplateByName(templateName,
							MPolicyConditionsTemplate.class.getName());
			if (template != null) {
				final List<MPolicy> policies = modelSpace
						.find(MPolicy.class, modelSpace.createCriterionQuery()
								.in("key", policyKeys));
				if (!policies.isEmpty()
						&& checkConditionsTemplateAndPoliciesCompatibility(
								template, policies)) {
					updatePolicyConditionLevelsByTemplate(policies, template);
				}

			} else {
				throw new SourceNotFoundException("Policy conditions template "
						+ templateName + " not found");
			}
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK,"Condition template {} applied to policies : {}", template.getName(),Arrays.toString(policyKeys.toArray(new String[policyKeys.size()])));
		} catch (Exception e) {
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY,AuditLogger.SyslogActionStatus.NOK,"Unable to apply action template {} to policies : {}",
					templateName, Arrays.toString(policyKeys.toArray(new String[policyKeys.size()])));
			throw e;
		}
	}

	private void applyTaskPolicyTemplate(final MAgentTask task,
			final MPolicyTemplate taskPolicyTemplate) {
		final List<MResultParameterConfiguration> foundParameterConfigurations = getParameterConfigurationsForPolicyCondition(
				task, taskPolicyTemplate.getCondition());
		if (SimpleUtils.isNotNullAndNotEmpty(foundParameterConfigurations)) {
			for (final MResultParameterConfiguration parameterConfiguration : foundParameterConfigurations) {
				applyParameterPolicyTemplate(task, taskPolicyTemplate,
						parameterConfiguration.getParameterIdentifier());
			}
		}
	}

	@Override
	public void applyTaskPolicyTemplates(final MAgentTask task) {
		final List<MPolicyTemplate> taskPolicyTemplates = getPolicyTemplates(
				SimpleUtils.findSystemComponent(task).getKey(),
				Source.getTaskSource(task.getKey()), false, null);
		for (final MPolicyTemplate taskPolicyTemplate : taskPolicyTemplates) {
			try {
				applyTaskPolicyTemplate(task, taskPolicyTemplate);
			} catch (final Exception ex) {
				LOGGER.error("Unable to apply policy template: "
						+ taskPolicyTemplate + " of task: " + task
						+ " to all its parameters", ex);
			}
		}
	}

	/**
	 * Returns true if polices and template parameter types and units are
	 * compatible. Throws exceptions otherwise.
	 * 
	 * @throws CompatibilityException
	 *             if policies are incompatible between each other
	 * @throws ParameterTypeCompatibilityException
	 *             if template and polices parameter types are diferent
	 */
	private boolean checkConditionsTemplateAndPoliciesCompatibility(
			final MPolicyConditionsTemplate template,
			final List<MPolicy> policies)
			throws ParameterTypeCompatibilityException, CompatibilityException {
		if (!policies.isEmpty()) {
			final List<MAgentTask> tasks = taskRetriever
					.getTasksByKeys(getTaskKeysFromPolicies(policies));

			final Collection<MResultParameterConfiguration> parameterConfigurations = PolicyUtils
					.getPolicyParameterConfigurations(policies, tasks);

			final ParameterType policyParameterType = parameterConfigurations
					.iterator().next().getType();
			final ParameterType templateParameterType = template
					.getParameterType();
			if (!ConfigurationUtil
					.areParameterConfigurationsCompatible(parameterConfigurations)) {
				throw new CompatibilityException(
						"Type or unit incompatibility of parameters.");
			} else if (policyParameterType != templateParameterType) {
				throw new ParameterTypeCompatibilityException(
						"Template and policies parameters types are not equal.",
						policyParameterType, templateParameterType);
			}
		}
		return true;
	}

	/**
	 * @return true if any of policy actions was removed
	 */
	private boolean clearActionsWithContacts(final MPolicy policy) {
		final List<MPolicyActionWithContacts> actionsToRemove = policy
				.getActionsWithContacts();
		boolean result = false;
		if (actionsToRemove.size() > 0) {
			policy.getActions().removeAll(actionsToRemove);
			for (final MPolicyActionWithContacts action : actionsToRemove) {
				modelSpace.delete(action);
			}
			result = true;
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public void clearPolicyActionsTemplates(final Set<String> policyKeys) {
		try {
			if (SimpleUtils.isNotNullAndNotEmpty(policyKeys)) {
				final List<MPolicy> policies = modelSpace.find(MPolicy.class,
						modelSpace.createCriterionQuery().in("key", policyKeys));
				final List<MPolicy> policiesWithUpdatedActions = new ArrayList<>();
				for (final MPolicy policy : policies) {
					if (clearActionsWithContacts(policy)) {
						policiesWithUpdatedActions.add(policy);
					}
					policy.setActionsTemplate(null);
					modelSpace.saveOrUpdate(policy);
				}

				if (policiesWithUpdatedActions.size() > 0) {
					sendPoliciesEvents(createPoliciesEvents(
							policiesWithUpdatedActions, EventType.UPDATE));
				}
				AuditLogger.major(AuditLogger.SyslogCategory.POLICY,
						AuditLogger.SyslogActionStatus.OK,
						"Clear action template of policies : {}", Arrays
								.toString(policyKeys.toArray(new String[policyKeys
										.size()])));
			}
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY,
					AuditLogger.SyslogActionStatus.NOK,
					"Unable to clear action template of policies : {}", Arrays
							.toString(policyKeys.toArray(new String[policyKeys
									.size()])));
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void clearPolicyConditionsTemplates(final Set<String> policyKeys) {
		try {
			if (SimpleUtils.isNotNullAndNotEmpty(policyKeys)) {
				final List<MPolicy> policies = modelSpace.find(MPolicy.class,
						modelSpace.createCriterionQuery().in("key", policyKeys));
				for (final MPolicy policy : policies) {
					policy.setConditionsTemplate(null);
					modelSpace.saveOrUpdate(policy);
				}
				AuditLogger.major(AuditLogger.SyslogCategory.POLICY,
						AuditLogger.SyslogActionStatus.OK,
						"Clear condition template of policies : {}", Arrays
								.toString(policyKeys.toArray(new String[policyKeys
										.size()])));
			}
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY,
					AuditLogger.SyslogActionStatus.NOK,
					"Unable to clear condition template of policies : {}", Arrays
							.toString(policyKeys.toArray(new String[policyKeys
									.size()])));
			throw e;
		}
	}

	private Criterion createAgentAndTaskPoliciesCriterion(
			final String agentKey, final String searchString) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.and(query.eq("source.key", agentKey),
				query.eq("source.type", Source.Type.AGENT));

		final List<MAgentTask> agentTasks = taskRetriever.getAgentTasks(
				agentKey, null, 0, Integer.MAX_VALUE);

		for (final MAgentTask task : agentTasks) {
			criterion = query.or(
					criterion,
					query.and(query.eq("source.key", task.getKey()),
							query.eq("source.type", Source.Type.TASK)));
		}

		criterion = query.and(criterion,
				Utils.createNotDeletedAndDisabledCriterion(true));

		if (SimpleUtils.isNotNullAndNotEmpty(searchString)) {
			Criterion searchCriterion = query.istringContains("displayName",
					searchString);
			for (final MAgentTask task : agentTasks) {
				if (StringUtils.containsIgnoreCase(task.getDisplayName(), searchString) ||
					StringUtils.containsIgnoreCase(task.getParent().getParent().getDisplayName(), searchString)) {
					searchCriterion = query.or(searchCriterion, query.and(
							query.eq("source.key", task.getKey()),
							query.eq("source.type", Source.Type.TASK)));
				}
			}
			final Set<ParameterIdentifier> parameterIdentifiers = findParameterIdentifiersByDisplayName(
					agentTasks, searchString);
			if (SimpleUtils.isNotNullAndNotEmpty(parameterIdentifiers)) {
				searchCriterion = query.or(searchCriterion, query.in(
						"condition.parameterIdentifier", parameterIdentifiers));
			}

			criterion = query.and(criterion, searchCriterion);
		}

		return criterion;
	}

	private String createParameterStorageKeyForPolicy(final MAgentTask task,
			final ParameterIdentifier parameterIdentifier) {
		String parameterStorageKey = null;

		if (parameterIdentifier != null) {
			parameterStorageKey = parameterIdentifier
					.createParameterStorageKey();
			if (parameterIdentifier.hasUnsafeRequiredProperties()) {
				parameterStorageKey = parameterIdentifier
						.createParameterStorageKeyAsSafeString(task
								.getResultConfiguration()
								.findParameterConfiguration(parameterIdentifier)
								.getLocation().getId().toString());
			}
		}
		return parameterStorageKey;
	}

	private List<PoliciesEvent> createPoliciesEvents(
			final List<MPolicy> policies, final EventType eventType) {
		final Map<Source, List<MPolicy>> policiesBySource = new HashMap<Source, List<MPolicy>>();
		final List<PoliciesEvent> events = new ArrayList<>();

		for (final MPolicy policy : policies) {
			final Source source = policy.getSource();
			List<MPolicy> concreteSourcePolicies = policiesBySource.get(source);
			if (concreteSourcePolicies == null) {
				concreteSourcePolicies = new ArrayList<MPolicy>();
				policiesBySource.put(source, concreteSourcePolicies);
			}
			concreteSourcePolicies.add(policy);
		}

		for (final Source source : policiesBySource.keySet()) {
			final List<MPolicy> concreteSourcePolicies = policiesBySource
					.get(source);
			final Source systemComponent = getSystemComponentSource(source);
			events.add(new PoliciesEvent(source, systemComponent, Collections
					.unmodifiableList(concreteSourcePolicies), eventType));
		}
		return events;
	}

	private Criterion createPoliciesSearchCriterion(final String searchString) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = null;
		for (final MAgent agent : modelSpace.getAll(MAgent.class)) {
			final Criterion agentCriterion = createAgentAndTaskPoliciesCriterion(
					agent.getKey(), searchString);
			if (criterion == null) {
				criterion = agentCriterion;
			} else {
				criterion = query.or(criterion, agentCriterion);
			}
		}
		return criterion;
	}

	/**
	 * Creates a policy key.
	 * 
	 * @param policyTemplateKey
	 *            an agent policy template key or a task policy template key.
	 * @param extraKey
	 *            a full task key to create a task policy template key or a
	 *            parameter key to create a concrete policy key.
	 * @return the policy key.
	 */
	private String createPolicyKey(final String policyTemplateKey,
			final String extraKey) {
		return policyTemplateKey
				+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
				+ extraKey;
	}

	private MPolicyTemplate createTaskPolicyTemplate(final String agentKey,
			final String taskKey, final MPolicySharedData policyTemplate) {
		final MPolicyTemplate taskPolicyTemplate = new MPolicyTemplate(
				agentKey, policyTemplate);

		taskPolicyTemplate.setSource(Source.getTaskSource(taskKey));

		final String taskPolicyTemplateKey = createTaskPolicyTemplateKey(
				policyTemplate.getKey(), taskKey, policyTemplate.getSource());
		taskPolicyTemplate.setKey(taskPolicyTemplateKey);

		return taskPolicyTemplate;
	}

	private String createTaskPolicyTemplateKey(final String policyTemplateKey,
			final String taskKey, final Source policyTemplateSource) {
		String policyKey = policyTemplateKey;
		if (!Source.Type.TASK.equals(policyTemplateSource.getType())) {
			policyKey = createPolicyKey(policyTemplateKey, taskKey);
		}
		return policyKey;
	}

	@Override
	public void delete(final MPolicy policy) throws ServiceException {
		delete(policy, true);
	}

	private void delete(final MPolicy policy, final boolean sendEvent)
			throws ServiceException {
		if (!policy.isDeleted()) {
			try {
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								policy.setDeleted(true);
								saveOrUpdatePolicyInTransaction(policy);

								// there is no difference between alert
								// disabling and deletion
								alertService.disableAlertsByOriginator(Source
										.getPolicySource(policy.getKey()));

								if (LOGGER.isInfoEnabled()) {
									LOGGER.info("Delete policy: " + policy);
								}
								if (sendEvent) {
									final Source agentSource = getSystemComponentSource(policy);
									notifyListenersInTransaction(new PolicyEvent(
											agentSource, policy,
											EventType.DELETE));
								}
							}
						});
			} catch (final Exception ex) {
				throw new ServiceException("Unable to delete policy: "
						+ policy.getKey(), ex);
			}
		}
	}

	/**
	 * This method must be only called in transaction.
	 * 
	 * @param policyKeys
	 * @param operationType
	 */
	private void deleteOrDisablePolicies(final Set<String> policyKeys,
			final OperationType operationType) {

		if (!SimpleUtils.isNotNullAndNotEmpty(policyKeys)) {
			return;
		}

		final List<MPolicy> policies = modelSpace.find(MPolicy.class,
				modelSpace.createCriterionQuery().in("key", policyKeys));
		if (SimpleUtils.isNotNullAndNotEmpty(policies)) {
			for (final MPolicy policy : policies) {
				switch (operationType) {
					case DISABLE :
						disable(policy, false);
						break;
					case DELETE :
						delete(policy, false);
						break;
					default :
						throw new UnsupportedOperationException(
								"Operation not supported: " + operationType);
				}
			}
			sendPoliciesEvents(createPoliciesEvents(policies, EventType.DELETE));
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePolicies(final Set<String> policyKeys)
			throws ServiceException {
		try {
			deleteOrDisablePolicies(policyKeys, OperationType.DELETE);
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK,"Policies deleted : {}",Arrays.toString(policyKeys.toArray(new String[policyKeys.size()])));
		} catch (final Exception ex) {
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK,"Unable to delete policies : {}",Arrays.toString(policyKeys.toArray(new String[policyKeys.size()])));
			throw new ServiceException(ex);
		}
	}

	@Override
	public void detachDifferentTypePoliciesFromConditionsTemplate(
			final MPolicyConditionsTemplate template) {
		final List<MPolicy> templatePolicies = modelSpace.find(
				MPolicy.class,
				modelSpace.createCriterionQuery().eq("conditionsTemplate.name",
						template.getName()));

		if (!templatePolicies.isEmpty()) {
			final List<MAgentTask> tasks = taskRetriever
					.getTasksByKeys(getTaskKeysFromPolicies(templatePolicies));

			final Map<String, MAgentTask> tasksByKeys = SimpleUtils
					.getMap(tasks);
			for (final MPolicy policy : templatePolicies) {
				final MAgentTask task = tasksByKeys.get(policy.getSource()
						.getKey());
				if (task != null) {
					final MResultParameterConfiguration policyParameter = PolicyUtils
							.findParameterConfiguration(policy, task);
					if (policyParameter.getType() != template
							.getParameterType()) {
						policy.setConditionsTemplate(null);
					}

				} else {
					throw new SourceNotFoundException(
							"Source not found for policy=" + policy);
				}
			}
		}
	}

	@Override
	@Transactional
	public void detachPoliciesFromActionsTemplate(
			final MPolicyActionsTemplate template) {
		final List<MPolicy> policiesAppliedByTemplate = modelSpace.find(
				MPolicy.class,
				modelSpace.createCriterionQuery().eq("actionsTemplate.name",
						template.getName()));

		for (final MPolicy policy : policiesAppliedByTemplate) {
			policy.setActionsTemplate(null);
			modelSpace.saveOrUpdate(policy);
		}
	}

	@Override
	public void detachPoliciesFromConditionsTemplate(
			final MPolicyConditionsTemplate template) {
		final List<MPolicy> policiesAppliedByTemplate = modelSpace.find(
				MPolicy.class,
				modelSpace.createCriterionQuery().eq("conditionsTemplate.name",
						template.getName()));

		for (final MPolicy policy : policiesAppliedByTemplate) {
			policy.setConditionsTemplate(null);
			modelSpace.saveOrUpdate(policy);
		}
	}

	@Override
	public void disable(final MPolicy policy) throws ServiceException {
		disable(policy, true);
	}

	private void disable(final MPolicy policy, final boolean sendEvent)
			throws ServiceException {
		if (!policy.isDisabled()) {
			try {
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								policy.setDisabled(true);
								saveOrUpdatePolicyInTransaction(policy);

								alertService.disableAlertsByOriginator(Source
										.getPolicySource(policy.getKey()));

								if (LOGGER.isInfoEnabled()) {
									LOGGER.info("Disable policy: " + policy);
								}
								if (sendEvent) {
									final Source agentSource = getSystemComponentSource(policy);
									notifyListenersInTransaction(new PolicyEvent(
											agentSource, policy,
											EventType.DELETE));
								}
							}
						});
			} catch (final Exception ex) {
				throw new ServiceException("Unable to disable policy: "
						+ policy.getKey(), ex);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void disablePolicies(final Set<String> policyKeys)
			throws ServiceException {
		try {
			deleteOrDisablePolicies(policyKeys, OperationType.DISABLE);
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
	}

	private MPolicy doSaveOrUpdatePolicy(final MPolicy policy) {
		if (policy.getKey() == null) {
			policy.setKey("key" + UUID.randomUUID());
		}

		final Source policySource = policy.getSource();
		if (policySource.getType() != Source.Type.TASK) {
			throw new UnknownSourceException(
					"Policy must have only task as source: " + policy);
		}

		final MAgentTask task = sourceService.getDomainSource(policySource);
		saveOrUpdateTaskPolicy(task, policy);

		HibernateEntityConverter.convertHibernateCollections(policy,
				PersistentCollection.class);
		return policy;
	}

	private Set<ParameterIdentifier> findParameterIdentifiersByDisplayName(
			final List<MAgentTask> agentTasks, final String searchString) {
		final Set<ParameterIdentifier> result = new HashSet<ParameterIdentifier>();
		for (final MAgentTask agentTask : agentTasks) {
			final MResultConfiguration resultConfiguration = agentTask
					.getResultConfiguration();
			if (resultConfiguration != null) {
				final List<MResultParameterConfiguration> parameterConfigurations = resultConfiguration
						.getParameterConfigurations();
				if (SimpleUtils.isNotNullAndNotEmpty(parameterConfigurations)) {
					for (final MResultParameterConfiguration paramConfiguration : parameterConfigurations) {
						final String displayName = paramConfiguration
								.getParsedDisplayFormat();
						if (StringUtils.containsIgnoreCase(displayName,
								searchString)) {
							result.add(paramConfiguration
									.getParameterIdentifier());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MPolicy> getAgentPolicies(final String agentKey,
			final String searchString, final Order order,
			final Integer startPosition, final Integer size) {
		return modelSpace.find(MPolicy.class,
				createAgentAndTaskPoliciesCriterion(agentKey, searchString),
				order, startPosition, size);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getAgentPoliciesCount(final String agentKey,
			final String searchString) {
		return modelSpace.count(MPolicy.class,
				createAgentAndTaskPoliciesCriterion(agentKey, searchString));
	}

	private Source getAgentSource(final MSystemComponent systemComponent) {
		final Source systemComponentSource = Source
				.getAgentSource(systemComponent.getKey());
		systemComponentSource.setDisplayName(systemComponent.getDisplayName());
		return systemComponentSource;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MPolicy> getAllPolicies() {
		final List<MPolicy> result = new ArrayList<MPolicy>();

		final List<MPolicy> allPolicies = modelSpace.find(MPolicy.class,
				Utils.createNotDeletedAndDisabledCriterion(true));
		for (final MPolicy policy : allPolicies) {
			result.add(policy);
		}
		return result;
	}

	@Override
	public Set<String> getDeletedRecipientKeys(
			final Collection<MPolicyActionWithContacts> actions) {
		final Map<Long, String> recipientIdKeyMap = new HashMap<>();

		for (final MPolicyActionWithContacts action : actions) {
			for (final MContactInformation contact : action.getContacts()) {
				recipientIdKeyMap.put(contact.getId(), contact.getKey());
			}
		}

		if (recipientIdKeyMap.size() > 0) {
			final List<MContactInformation> existingRecipients = executeInTransaction(
					true, new TransactionCallback<List<MContactInformation>>() {

						@Override
						public List<MContactInformation> doInTransaction(
								final TransactionStatus status) {
							return modelSpace.find(
									MContactInformation.class,
									modelSpace.createCriterionQuery().in("id",
											recipientIdKeyMap.keySet()));
						}
					});

			for (final MContactInformation contact : existingRecipients) {
				recipientIdKeyMap.remove(contact.getId());
			}
		}

		return new HashSet<String>(recipientIdKeyMap.values());
	}

	private List<MResultParameterConfiguration> getParameterConfigurationsForPolicyCondition(
			final MAgentTask task, final MPolicyCondition policyCondition) {
		List<MResultParameterConfiguration> result = null;
		if (task.hasParameters()) {
			if (policyCondition instanceof MContinuousThresholdFallCondition) {
				final MContinuousThresholdFallCondition thresholdFallPolicyCondition = (MContinuousThresholdFallCondition) policyCondition;
				final List<MResultParameterConfiguration> foundParameterConfigurations = task
						.getResultConfiguration().findParameterConfigurations(
								thresholdFallPolicyCondition
										.getParameterIdentifier().getName());
				if (foundParameterConfigurations != null
						&& !foundParameterConfigurations.isEmpty()) {
					result = new LinkedList<MResultParameterConfiguration>(
							foundParameterConfigurations);
				}
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<Source, PMConfiguration> getPMConfigurations(
			final String policyManagerName) {

		if (policyManagerName == null || policyManagerName.isEmpty()) {
			throw new IllegalArgumentException(
					"policyManagerName can not be null or empty");
		}

		Set<String> pmSupportedAgents = null;
		final PolicyManagerRegistrationInfo pmRegistrationInfo;
		synchronized (policyManagerRegistrationInfo) {
			pmRegistrationInfo = policyManagerRegistrationInfo
					.get(policyManagerName);
			if (pmRegistrationInfo != null) {
				pmSupportedAgents = pmRegistrationInfo.getAgents();
			}
		}

		if (pmSupportedAgents == null) {
			throw new ServiceException("PolicyManager " + policyManagerName
					+ " not registered");
		}
		if (pmSupportedAgents.isEmpty() && !pmRegistrationInfo.allAgentsSupported) {
			throw new ServiceException("PolicyManager " + policyManagerName
					+ " suports no agents");
		}
		final Map<Source, PMConfiguration> pmConfigurations = new HashMap<Source, PMConfiguration>();

		final List<MAgentTask> tasks = taskRetriever.getAgentTasks(
				pmSupportedAgents, null, null, true);
		if (tasks != null && !tasks.isEmpty()) {
			final Map<String, MAgentTask> taskMap = new HashMap<String, MAgentTask>();
			for (final MAgentTask task : tasks) {
				taskMap.put(task.getKey(), task);
			}
			final CriterionQuery query = modelSpace.createCriterionQuery();
			final List<MPolicy> policies = modelSpace.find(
					MPolicy.class,
					SimpleUtils.mergeCriterions(
							query.in("source.key", taskMap.keySet()),
							query.eq("source.type", Source.Type.TASK),
							Utils.createNotDeletedAndDisabledCriterion(true)),
					null, null, null);
			if (policies != null && !policies.isEmpty()) {
				for (final MPolicy policy : policies) {
					final Source policySource = policy.getSource();
					final MAgentTask task = taskMap.get(policySource.getKey());
					if (policySource.getType() == Source.Type.TASK) {
						policySource.setDisplayName(task.getDisplayName());
					}
					PMTaskConfiguration pmConfiguration = (PMTaskConfiguration) pmConfigurations
							.get(policySource);
					if (pmConfiguration == null) {
						final List<MPolicy> taskPolicies = new ArrayList<MPolicy>();
						taskPolicies.add(policy);
						final Source agentSource = getSystemComponentSource(task);
						pmConfiguration = new PMTaskConfiguration(agentSource,
								policySource, task.getResultConfiguration(),
								taskPolicies);
						pmConfigurations.put(policySource, pmConfiguration);
					} else {
						List<MPolicy> taskPolicies = pmConfiguration
								.getPolicies();
						if (taskPolicies == null) {
							taskPolicies = new ArrayList<MPolicy>();
							pmConfiguration.setPolicies(taskPolicies);
						}
						taskPolicies.add(policy);
					}
				}
			}
		}
		return pmConfigurations;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MPolicy> getPolicies(final Criterion criterion,
			final Order order, final Integer startPosition, final Integer size,
			final String searchText) {
		Criterion queryCriterion = null;
		if (SimpleUtils.isNotNullAndNotEmpty(searchText)) {
			queryCriterion = createPoliciesSearchCriterion(searchText);
		} else {
			queryCriterion = Utils.createNotDeletedAndDisabledCriterion(true);
		}

		final Criterion probeCriterion = buildProbeFilterCriterion();
		if(probeCriterion == null) {
			return new ArrayList<>();
		}

		final List<MPolicy> policies = modelSpace.find(MPolicy.class,
				SimpleUtils.mergeCriterions(
						queryCriterion,
						criterion,
						probeCriterion), order,	startPosition, size);
		return policies;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MPolicy> getPolicies(final Source source) {
		return getPolicies(source, null, true);
	}

	private List<MPolicy> getPolicies(final Source source,
			final String policyKeyPattern, final boolean onlyActive) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = Utils
				.createNotDeletedAndDisabledCriterion(onlyActive);
		criterion = query.and(criterion,
				query.eq("source.key", source.getKey()));
		criterion = query.and(criterion,
				query.eq("source.type", source.getType()));
		if (policyKeyPattern != null) {
			criterion = query.and(criterion,
					query.like("key", "%" + policyKeyPattern));
		}
		return modelSpace.find(MPolicy.class, criterion);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MPolicy> getPoliciesByUser(final MUser user) {
		return modelSpace.find(MPolicy.class, CriterionQueryFactory.getQuery()
				.collectionContains("actions.contacts", user));
	}

	private Criterion buildProbeFilterCriterion() {
		List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();
		if(!agentKeys.isEmpty()) {
				final List<MAgentTask> tasks = taskRetriever.getAgentTasks(
						agentKeys, null, null, true);
				final Map<String, MAgentTask> taskMap = new HashMap<String, MAgentTask>();
				for (final MAgentTask task : tasks) {
					taskMap.put(task.getKey(), task);
				}

				final CriterionQuery query = modelSpace.createCriterionQuery();
				Criterion c = query.in("source.key", taskMap.keySet());
				return query.and(c, query.eq("source.type", Source.Type.TASK));
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Long getPoliciesCount(final Criterion criterion,
			final String searchText) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion queryCriterion = null;
		if (SimpleUtils.isNotNullAndNotEmpty(searchText)) {
			queryCriterion = createPoliciesSearchCriterion(searchText);
		} else {
			queryCriterion = Utils.createNotDeletedAndDisabledCriterion(true);
		}

		if (criterion != null) {
			queryCriterion = query.and(criterion, queryCriterion);
		}

		final Criterion probeCriterion = buildProbeFilterCriterion();
		if (probeCriterion == null) {
			return 0L;
		}

		return modelSpace.count(MPolicy.class, SimpleUtils.mergeCriterions(queryCriterion,probeCriterion));
	}

	@Override
	@Transactional(readOnly = true)
	public MPolicy getPolicy(final String policyKey) {
		return getPolicy(policyKey, true);
	}

	@Override
	@Transactional(readOnly = true)
	public MPolicy getPolicy(final String policyKey, final boolean onlyActive) {
		final CriterionQuery query = modelSpace.createCriterionQuery();

		return modelSpace
				.findUniqueEntity(MPolicy.class, query.and(
						query.eq("key", policyKey),
						Utils.createNotDeletedAndDisabledCriterion(onlyActive)));
	}

	private MPolicyTemplate getPolicyTemplate(final String agentKey,
			final String templateKey, final Source templateSource,
			final boolean activeOnly) {
		return executeInTransaction(true,
				new TransactionCallback<MPolicyTemplate>() {

					@Override
					public MPolicyTemplate doInTransaction(
							final TransactionStatus status) {
						final CriterionQuery query = modelSpace
								.createCriterionQuery();

						return modelSpace.findUniqueEntity(
								MPolicyTemplate.class,
								SimpleUtils.mergeCriterions(
										query.eq("key", templateKey),
										query.eq("source.key",
												templateSource.getKey()),
										query.eq("source.type",
												templateSource.getType()),
										query.eq("agentName", agentKey),
										Utils.createNotDeletedAndDisabledCriterion(false)));
					}
				});
	}

	private List<MPolicyTemplate> getPolicyTemplates(final String agentName,
			final Source source, final boolean activeOnly,
			final String parameterName) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("agentName", agentName);
		criterion = query.and(criterion,
				query.eq("source.key", source.getKey()));
		criterion = query.and(criterion,
				query.eq("source.type", source.getType()));
		criterion = query.and(criterion,
				Utils.createNotDeletedAndDisabledCriterion(true));

		if (SimpleUtils.isNotNullAndNotEmpty(parameterName)) {
			criterion = query.and(criterion, query.eq(
					"condition.parameterIdentifier.name", parameterName));
		}

		final Criterion finalCriterion = criterion;
		return executeInTransaction(true,
				new TransactionCallback<List<MPolicyTemplate>>() {

					@Override
					public List<MPolicyTemplate> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.find(MPolicyTemplate.class,
								finalCriterion);
					}
				});
	}

	@Override
	public Set<String> getRegisteredPolicyManagersBySystemComponent(
			final String systemComponentKey) {
		final Set<String> registeredPolicyManagers = new HashSet<>();
		synchronized (policyManagerRegistrationInfo) {
			for (final Map.Entry<String, PolicyManagerRegistrationInfo> policyManagerRegistrationInfoEntry : policyManagerRegistrationInfo
					.entrySet()) {
				final Set<String> pmSystemComponentKeys = policyManagerRegistrationInfoEntry
						.getValue().getAgents();
				if (pmSystemComponentKeys.contains(systemComponentKey)) {
					registeredPolicyManagers
							.add(policyManagerRegistrationInfoEntry.getKey());
				}
			}
		}
		return registeredPolicyManagers;
	}

	private Source getSystemComponentSource(final MSource source) {
		final MSystemComponent systemComponent = sourceService
				.getSystemComponent(source);
		return getAgentSource(systemComponent);
	}

	private Source getSystemComponentSource(final Source source) {
		final MSystemComponent agent = sourceService.getSystemComponent(source);
		return getAgentSource(agent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MPolicy> getTaskParameterPolicies(final MAgentTask task,
												  final ParameterIdentifier parameterIdentifier) {
		return getPolicies(Source.getTaskSource(task.getKey()),
				createParameterStorageKeyForPolicy(task, parameterIdentifier),
				true);
	}

	private boolean policyConditionHasTheSameParameter(
			final MPolicyCondition policyCondition,
			final ParameterIdentifier parameterIdentifier) {
		return policyCondition instanceof MContinuousThresholdFallCondition
				&& ((MContinuousThresholdFallCondition) policyCondition)
						.getParameterIdentifier().getName()
						.equals(parameterIdentifier.getName());
	}

	@Override
	@Transactional
	public void reapplyPolicyActionsTemplate(
			final MPolicyActionsTemplate template) {
		assert (template.getId() != null);

		final List<MPolicy> policies = modelSpace.find(
				MPolicy.class,
				modelSpace.createCriterionQuery().eq("actionsTemplate",
						template));
		updatePolicyActionsByTemplate(policies, template);
	}

	@Override
	public void reapplyPolicyConditionsTemplate(
			final MPolicyConditionsTemplate template)
			throws CompatibilityException, ParameterTypeCompatibilityException {
		assert (template.getId() != null);

		final List<MPolicy> policies = modelSpace.find(
				MPolicy.class,
				modelSpace.createCriterionQuery().eq("conditionsTemplate",
						template));
		if (!policies.isEmpty()
				&& checkConditionsTemplateAndPoliciesCompatibility(template,
						policies)) {
			updatePolicyConditionLevelsByTemplate(policies, template);
		}
	}

	private void registerAgentPolicyTemplate(final String agentKey,
			final MPolicy policyTemplate) {
		if (validateAgentPolicyTemplate(agentKey, policyTemplate)) {
			MPolicyTemplate agentPolicyTemplate = getPolicyTemplate(agentKey,
					policyTemplate.getKey(), policyTemplate.getSource(), false);

			final MPolicyTemplate updatedAgentPolicyTemplate = new MPolicyTemplate(
					agentKey, policyTemplate);

			boolean isAgentPolicyTemplateUpdated = false;
			boolean isAgentPolicyTemplateCreated = false;
			if (agentPolicyTemplate == null) {
				agentPolicyTemplate = updatedAgentPolicyTemplate;
				isAgentPolicyTemplateCreated = true;
			} else {
				isAgentPolicyTemplateUpdated |= agentPolicyTemplate
						.updateSimpleFields(updatedAgentPolicyTemplate);
			}

			if (isAgentPolicyTemplateCreated || isAgentPolicyTemplateUpdated) {
				saveOrUpdatePolicyTemplate(agentPolicyTemplate, true);
				if (isAgentPolicyTemplateCreated) {
					LOGGER.info("Register new agent policy template: "
							+ agentPolicyTemplate);
				} else if (isAgentPolicyTemplateUpdated) {
					LOGGER.info("Update agent policy template: "
							+ agentPolicyTemplate);
				}
			}
			applyAgentPolicyTemplate(agentKey, agentPolicyTemplate);
		}
	}

	@Override
	public void registerPolicy(final String agentName,
			final MPolicy policyTemplate) {
		try {
			if (Source.Type.MODULE.equals(policyTemplate.getSource().getType())) {
				registerAgentPolicyTemplate(agentName, policyTemplate);
			} else if (Source.Type.TASK.equals(policyTemplate.getSource()
					.getType())) {
				registerTaskPolicyTemplate(policyTemplate);
			} else {
				throw new ServiceException(
						"Unsupported policy template source type: "
								+ policyTemplate.getSource());
			}
		} catch (final Exception ex) {
			throw new ServiceException("Unable to register policy: "
					+ policyTemplate.getKey() + " for agent: " + agentName, ex);
		}
	}

	@Override
	@Transactional
	public Set<String> registerPolicyManager(final String policyManagerName,
			final Collection<String> pmSupportedAgents) {
		final Set<String> registeredAgents = new HashSet<>();
		PolicyManagerRegistrationInfo pmRegistrationInfo = new PolicyManagerRegistrationInfo();
		Assert.state(policyManagerName != null && !policyManagerName.isEmpty(),
				"policyManagerName can not be null or empty");

		final List<String> serverSupportedAgents = agentService
				.getAllAgentKeysNoFiltering();

		if (pmSupportedAgents == null || pmSupportedAgents.isEmpty()) {
			LOGGER.info("Registering Policy Manager (" + policyManagerName
					+ ") for all agents...");
			registeredAgents.addAll(serverSupportedAgents);
			pmRegistrationInfo.setAllAgentsSupported(true);
		} else {
			LOGGER.info("Registering Policy Manager (" + policyManagerName
					+ ") for " + pmSupportedAgents + "...");
			registeredAgents.addAll(pmSupportedAgents);
			pmRegistrationInfo.setAllAgentsSupported(false);
		}
		if (serverSupportedAgents.containsAll(registeredAgents)) {
			pmRegistrationInfo.setPolicyManagerName(policyManagerName);
			pmRegistrationInfo.setAgents(new HashSet<String>(registeredAgents));
			synchronized (policyManagerRegistrationInfo) {
				policyManagerRegistrationInfo.put(pmRegistrationInfo.getKey(),
						pmRegistrationInfo);
			}
			LOGGER.info("PolicyManager (" + policyManagerName
					+ ") with following supported agents [" + registeredAgents
					+ "] is registered");
		} else {
			final Set<String> unSupportedAgents = new HashSet<String>(
					serverSupportedAgents);
			unSupportedAgents.addAll(registeredAgents);
			unSupportedAgents.removeAll(serverSupportedAgents);
			registeredAgents.clear();
			pmRegistrationInfo = null;
			throw new ServiceException("Agents " + unSupportedAgents
					+ " not supported by server");
		}
		return registeredAgents;
	}

	private void registerTaskPolicyTemplate(final MAgentTask task,
			final MPolicySharedData policyTemplate) {
		// task must always have system component as agent
		final String systemComponentKey = SimpleUtils.findSystemComponent(task)
				.getKey();
		final String taskKey = task.getKey();

		MPolicyTemplate taskPolicyTemplate = getPolicyTemplate(
				systemComponentKey,
				createTaskPolicyTemplateKey(policyTemplate.getKey(), taskKey,
						policyTemplate.getSource()),
				Source.getTaskSource(taskKey), false);

		final MPolicyTemplate updatedTaskPolicyTemplate = createTaskPolicyTemplate(
				systemComponentKey, taskKey, policyTemplate);

		boolean isTaskPolicyTemplateUpdated = false;
		boolean isTaskPolicyTemplateCreated = false;
		if (taskPolicyTemplate == null) {
			taskPolicyTemplate = updatedTaskPolicyTemplate;
			isTaskPolicyTemplateCreated = true;
		} else {
			isTaskPolicyTemplateUpdated |= taskPolicyTemplate
					.updateSimpleFields(updatedTaskPolicyTemplate);
		}

		if (isTaskPolicyTemplateCreated || isTaskPolicyTemplateUpdated) {
			saveOrUpdatePolicyTemplate(taskPolicyTemplate, true);
			if (isTaskPolicyTemplateCreated) {
				LOGGER.info("Register new task policy template: "
						+ taskPolicyTemplate);
			} else if (isTaskPolicyTemplateUpdated) {
				LOGGER.info("Update task policy template: "
						+ taskPolicyTemplate);
			}
		}
		applyTaskPolicyTemplate(task, taskPolicyTemplate);
	}

	private void registerTaskPolicyTemplate(final MPolicy policyTemplate) {
		if (validatePolicy(policyTemplate)) {
			final String taskKey = policyTemplate.getSource().getKey();
			final MAgentTask task = taskRetriever.getTaskByKey(taskKey);
			if (task == null) {
				throw new ServiceException("Task " + taskKey
						+ " not found. It could be aready deleted.");
			}
			registerTaskPolicyTemplate(task, policyTemplate);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeSendActionsWithContact(final MContactInformation contact) {

		final List<MPolicyActionWithContacts> actionsWithOurContact = modelSpace
				.find(MPolicyActionWithContacts.class,
						modelSpace.createCriterionQuery().collectionContains(
								"contacts", contact));

		final List<MPolicyActionWithContacts> actionsToRemove = new ArrayList<>();
		for (final MPolicyActionWithContacts action : actionsWithOurContact) {
			if (action instanceof MPolicyActionWithContacts) {
				final MPolicyActionWithContacts actionWithContacts = action;
				final List<MContactInformation> contacts = actionWithContacts
						.getContacts();
				if (contacts.size() == 1) {
					actionsToRemove.add(actionWithContacts);
				} else {
					for (final MContactInformation actionContact : contacts) {
						if (actionContact.getKey().equals(contact.getKey())) {
							contacts.remove(actionContact);
							break;
						}
					}
				}
			}
		}

		final CriterionQuery criterionQuery = modelSpace.createCriterionQuery();
		Criterion criterion = null;

		for (final MPolicyActionWithContacts action : actionsToRemove) {
			if (criterion == null) {
				criterion = criterionQuery
						.collectionContains("actions", action);
			} else {
				criterion = criterionQuery.or(criterion,
						criterionQuery.collectionContains("actions", action));
			}
		}

		final List<MPolicy> policies = modelSpace
				.find(MPolicy.class, criterion);

		for (final MPolicy policy : policies) {
			policy.getActions().removeAll(actionsToRemove);
		}

		sendPoliciesEvents(createPoliciesEvents(policies, EventType.UPDATE));

		final List<MPolicyActionsTemplate> templates = modelSpace.find(
				MPolicyActionsTemplate.class, criterion);

		for (final MPolicyActionsTemplate template : templates) {
			template.getActions().removeAll(actionsToRemove);
		}

		for (final MPolicyActionWithContacts action : actionsToRemove) {
			modelSpace.delete(action);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void resetDisabledPolicies(final MAgentTask task,
			final ParameterIdentifier parameterIdentifier) {
		final Source taskSource = Source.getTaskSource(task.getKey());

		final List<MPolicy> disabledPolicies = getPolicies(taskSource,
				createParameterStorageKeyForPolicy(task, parameterIdentifier),
				false);
		if (SimpleUtils.isNotNullAndNotEmpty(disabledPolicies)) {
			for (final MPolicy policy : disabledPolicies) {
				policy.setDisabled(false);
				saveOrUpdatePolicyInTransaction(policy);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Reset disabled policy: " + policy);
				}

				try {
					alertService.resetDisabledAlerts(Source
							.getPolicySource(policy.getKey()));
				} catch (final Exception ex) {
					LOGGER.error("Unable to reset disabled alerts for policy: "
							+ policy, ex);
				}
			}
			final Source systemComponent = getSystemComponentSource(taskSource);
			notifyListenersInTransaction(new PoliciesEvent(taskSource,
					systemComponent,
					Collections.unmodifiableList(disabledPolicies),
					EventType.UPDATE));
		}
	}

	@Override
	public MPolicy saveOrUpdatePolicy(final MPolicy policy) {
		try {
			boolean isNewPolicy=policy.getId()==null?true:false;
			final MPolicyActionsTemplate actionsTemplate = policy
					.getActionsTemplate();
			if (actionsTemplate != null
					&& !PolicyUtils.arePolicyActionsEqual(
					actionsTemplate.getActions(),
					policy.getActionsWithContacts())) {
				policy.setActionsTemplate(null);
			}

			final MPolicyConditionsTemplate conditionsTemplate = policy
					.getConditionsTemplate();
			if (conditionsTemplate != null
					&& policy.getCondition() instanceof MPolicyConditionLevels
					&& !PolicyUtils.arePolicyConditionLevelsEqual(
					conditionsTemplate.getConditionLevels(),
					(MPolicyConditionLevels) policy.getCondition())) {
				policy.setConditionsTemplate(null);
			}

			final Set<String> deletedContacts = getDeletedRecipientKeys(policy
					.getActionsWithContacts());
			if (deletedContacts.size() > 0) {
				throw new DeletedContactInformationException(deletedContacts);
			}
			MPolicy result= doSaveOrUpdatePolicy(policy);
			if(isNewPolicy) {
				AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK, "Create policy : {}", result.getDisplayName());
			}else{
				AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK, "Update policy : {}", result.getDisplayName());
			}
			return result;
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.NOK, "Unable to update policy : {}, reason : {} ", policy.getDisplayName(), e.getMessage());
			throw e;
		}
	}

	private void saveOrUpdatePolicyInTransaction(final MPolicy policy) {
		executeInTransaction(false, new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				modelSpace.saveOrUpdate(policy);
			}
		});
	}

	/**
	 * Saves or updates a policy template with common validations.
	 * 
	 * Use {{@link #saveOrUpdateTaskPolicy(MAgentTask, MPolicy)} to save or
	 * update a policy with the validations related to {@link MAgentTask}.
	 * 
	 * @param policyTemplate
	 *            a policy template to save or update.
	 * @param validate
	 *            a flag indicating whether policy template should be validated
	 *            before saving.
	 */
	private void saveOrUpdatePolicyTemplate(
			final MPolicyTemplate policyTemplate, final boolean validate) {

		if (validate) {
			try {
				if (policyTemplate.getCondition() instanceof MContinuousThresholdFallCondition) {
					PolicyUtils.validateConditionLevels(
							(MContinuousThresholdFallCondition) policyTemplate
									.getCondition(),
							DefaultPolicyValidationMessages.getInstance());
				}
			} catch (final Exception ex) {
				throw new ServiceException(
						"Policy condition levels are inconsistent", ex);
			}
		}

		executeInTransaction(false, new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				modelSpace.saveOrUpdate(policyTemplate);
			}
		});
	}

	/**
	 * Saves or updates task concrete policy or task policy template with all
	 * necessary validations.
	 * 
	 * @param task
	 * @param taskPolicy
	 */
	private void saveOrUpdateTaskPolicy(final MAgentTask task,
			final MPolicy taskPolicy) {
		final boolean newPolicy = taskPolicy.getId() == null;
		validatePolicySource(task, taskPolicy);
		try {
			PolicyUtils.validateAndInitPolicyCondition(taskPolicy,
					task.getResultConfiguration(), JSEvaluator.getInstance(),
					DefaultPolicyValidationMessages.getInstance());
		} catch (final Exception ex) {
			throw new ServiceException(
					"Policy condition levels are inconsistent", ex);
		}
		saveOrUpdatePolicyInTransaction(taskPolicy);
		// don't send event if the created policy is new and disabled,
		// because in this case it is not necessary to notify policy manager.
		if (!(newPolicy && taskPolicy.isDisabled())) {
			final Source systemComponentSource = getSystemComponentSource(taskPolicy);
			if (newPolicy) {
				notifyListenersWithoutTransaction(new PolicyEvent(
						systemComponentSource, taskPolicy, EventType.CREATE));
			} else {
				notifyListenersWithoutTransaction(new PolicyEvent(
						systemComponentSource, taskPolicy, EventType.UPDATE));
			}
		}
	}

	private void sendPoliciesEvents(final List<PoliciesEvent> events) {
		for (final PoliciesEvent event : events) {
			notifyListenersInTransaction(event);
		}
	}

	public void setAgentService(final AgentService agentService) {
		this.agentService = agentService;
	}

	public void setAlertService(final AlertService alertService) {
		this.alertService = alertService;
	}

	public void setAuthorizeService(AuthorizeService authorizeService) {
		this.authorizeService = authorizeService;
	}

	/**
	 * @param policyComponentTemplateService
	 *            the policyComponentTemplateService to set
	 */
	public void setPolicyComponentTemplateService(
			final PolicyComponentTemplateService policyComponentTemplateService) {
		this.policyComponentTemplateService = policyComponentTemplateService;
	}

	public void setSourceService(final SourceService sourceService) {
		this.sourceService = sourceService;
	}

	public void setTaskService(final TaskRetriever taskRetriever) {
		this.taskRetriever = taskRetriever;
	}

	private void updatePolicyActionsByTemplate(final List<MPolicy> policies,
			final MPolicyActionsTemplate actionsTemplate) {

		if (SimpleUtils.isNotNullAndNotEmpty(policies)) {
			List<MPolicyActionWithContacts> templateActions = null;
			if (actionsTemplate != null) {
				templateActions = actionsTemplate.getActions();
			}

			final List<MPolicy> policiesWithActionsToUpdate = new ArrayList<>();
			for (final MPolicy policy : policies) {
				if (!PolicyUtils.arePolicyActionsEqual(policy.getActions(),
						templateActions)) {
					policiesWithActionsToUpdate.add(policy);
				}
				policy.setActionsTemplate(actionsTemplate);
			}

			if (!policiesWithActionsToUpdate.isEmpty()) {
				// it is necessary to create events before real policies update
				final List<PoliciesEvent> events = createPoliciesEvents(
						policiesWithActionsToUpdate, EventType.UPDATE);

				for (final MPolicy policy : policiesWithActionsToUpdate) {
					clearActionsWithContacts(policy);
					if (SimpleUtils.isNotNullAndNotEmpty(templateActions)) {
						for (final MPolicyActionWithContacts templateAction : templateActions) {
							policy.addAction(templateAction.copy());
						}
					}
					modelSpace.saveOrUpdate(policy);
				}
				sendPoliciesEvents(events);
			}
		}
	}

	private void updatePolicyConditionLevelsByTemplate(
			final List<MPolicy> policies,
			final MPolicyConditionsTemplate template) {

		if (SimpleUtils.isNotNullAndNotEmpty(policies)) {

			final List<MPolicy> policiesToUpdate = new ArrayList<>();

			for (final MPolicy policy : policies) {
				if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
					final MContinuousThresholdFallCondition policyCondition = (MContinuousThresholdFallCondition) policy
							.getCondition();
					final MPolicyConditionLevels templateConditionLevels = template
							.getConditionLevels();
					if (!PolicyUtils.arePolicyConditionLevelsEqual(
							templateConditionLevels, policyCondition)) {
						if (policyCondition != null) {
							policyCondition
									.updateSimpleFields(templateConditionLevels);
						} else {
							throw new SourceNotFoundException(
									"Existing policy doesn't have a condition: "
											+ policy);
						}
						policiesToUpdate.add(policy);
					}
					policy.setConditionsTemplate(template);
				}
			}
			// send update events
			if (!policiesToUpdate.isEmpty()) {
				final List<PoliciesEvent> events = createPoliciesEvents(
						policiesToUpdate, EventType.UPDATE);

				for (final MPolicy policy : policiesToUpdate) {
					modelSpace.saveOrUpdate(policy);
				}

				sendPoliciesEvents(events);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void updatePolicyConfigurationsByUser(final MUser user) {
		final List<MPolicy> policies = getPoliciesByUser(user);
		sendPoliciesEvents(createPoliciesEvents(policies, EventType.UPDATE));
	}


	@Override
	@Transactional(readOnly = true)
	public void updatePolicyConfigurationsByUsers(List<MUser> users) {
		final List<MPolicy> policies = new ArrayList<MPolicy>();
		for (MUser user : users) {
			final List<MPolicy> found = getPoliciesByUser(user);
			if (found != null && !found.isEmpty()) {
				policies.addAll(found);
			}
		}
		sendPoliciesEvents(createPoliciesEvents(policies, EventType.UPDATE));
	}

	private boolean validateAgentPolicyTemplate(final String agentKey,
			final MPolicy policyTemplate) {
		if (policyTemplate.getSource().isKeySimple()) {
			policyTemplate.setSource(Source.getModuleSource(agentKey,
					policyTemplate.getSource().getKey()));
		}

		boolean result = validatePolicy(policyTemplate);

		if (!SimpleUtils.isNotNullAndNotEmpty(agentKey)) {
			result = false;
			throw new ServiceException("Agent name " + agentKey
					+ " can not be null or empty ");
		}

		final String moduleKey = policyTemplate.getSource().getSimpleKey(1);
		// Validate module
		final List<String> moduleNames = agentService
				.getAllModuleKeysByAgentKey(agentKey);
		String existingModuleName = null;
		for (final String moduleCandidate : moduleNames) {
			if (moduleCandidate.equalsIgnoreCase(moduleKey.trim())) {
				existingModuleName = moduleCandidate;
				break;
			}
		}
		if (existingModuleName == null) {
			result = false;
			throw new ServiceException("Cannot find module " + moduleKey
					+ " for agent " + agentKey);
		}

		return result;
	}

	private boolean validatePolicy(final MPolicy policy) {
		boolean result = true;

		if (!SimpleUtils.isNotNullAndNotEmpty(policy.getActions())) {
			result = false;
			throw new IllegalArgumentException(
					"Policy template actions can not be null or empty");
		}
		if (policy.getCondition() == null) {
			result = false;
			throw new IllegalArgumentException(
					"Policy template condition can not be null");
		}
		if (policy.getSource() == null) {
			result = false;
			throw new IllegalArgumentException(
					"Policy template source can not be null");
		}
		if (!SimpleUtils.isNotNullAndNotEmpty(policy.getSource().getKey())) {
			result = false;
			throw new IllegalArgumentException(
					"Policy template source key can not be null or empty");
		}
		if (policy.getSource().getType() == null) {
			result = false;
			throw new IllegalArgumentException(
					"Policy template source type can not be null");
		}
		if (!SimpleUtils.isNotNullAndNotEmpty(policy.getKey())) {
			result = false;
			throw new IllegalArgumentException(
					"Policy key can not be null or empty");
		}
		return result;
	}

	private void validatePolicySource(final MAgentTask task,
			final MPolicy policy) {
		final PolicyValidationMessages validationMessages = DefaultPolicyValidationMessages
				.getInstance();

		if (task.isDeleted()) {
			final String errorMessage;
			if (policy.getId() == null) {
				errorMessage = validationMessages
						.unableToCreatePolicyWithDeletedSource(task.getKey());
			} else {
				errorMessage = validationMessages
						.unableToUpdatePolicyWithDeletedSource(task.getKey());
			}
			throw new DeletedSourceException(errorMessage + ": " + policy);
		}

		if (task.isDisabled()) {
			final String errorMessage;
			if (policy.getId() == null) {
				errorMessage = validationMessages
						.unableToCreatePolicyWithDisabledSource(task.getKey());
			} else {
				errorMessage = validationMessages
						.unableToUpdatePolicyWithDisabledSource(task.getKey());
			}
			throw new DisabledSourceException(errorMessage + ": " + policy);
		}
	}
}

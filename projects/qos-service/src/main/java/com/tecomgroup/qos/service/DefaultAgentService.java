/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.*;

import com.tecomgroup.qos.service.rbac.AuthorizeService;
import com.tecomgroup.qos.util.AuditLogger;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MDivision;
import com.tecomgroup.qos.domain.MResultConfigurationTemplate;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.AgentEvent;
import com.tecomgroup.qos.exception.DeletedSourceException;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class DefaultAgentService extends AbstractService
		implements
			AgentService {

	private final static Logger LOGGER = Logger
			.getLogger(DefaultAgentService.class);

	private AuthorizeService authorizeService;

	private final Set<String> registeredAgents = new HashSet<String>();

	protected InternalTaskService taskService;

	protected PolicyConfigurationService policyConfigurationService;

	private WidgetDeleter widgetDeleter;

	private Criterion createAgentNotDeletedCriterionByAgentKey(
			final String agentKey) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("key", agentKey);
		criterion = query.and(criterion, query.eq("deleted", false));

		return criterion;
	}

	final protected Criterion createModuleCriterionByAgentKeyAndModuleKey(
			final String agentKey, final String moduleKey) {
		final CriterionQuery query = modelSpace.createCriterionQuery();

		return query.and(
				createModuleNotDeletedCriterionByAgentKey(agentKey, query),
				query.eq("key", moduleKey));
	}

	final protected Criterion createModuleNotDeletedCriterionByAgentKey(
			final String agentKey, final CriterionQuery providedQuery) {
		CriterionQuery query = providedQuery;
		if (query == null) {
			query = modelSpace.createCriterionQuery();
		}
		return query.and(query.eq("agent.key", agentKey),
				query.eq("agent.deleted", false));
	}

	@Override
	public void delete(final MAgent agent) {
		if (!agent.isDeleted()) {
			try {
				final List<MAgentTask> tasks = taskService.getAgentTasks(
						agent.getKey(), null, null, null, null, false, false);
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								agent.setDeleted(true);
								modelSpace.saveOrUpdate(agent);
								if (widgetDeleter != null) {
									widgetDeleter
											.clearSourceRelatedWidgets(agent);
								}

								taskService.deleteTasks(SimpleUtils
										.getKeys(tasks));

								if (LOGGER.isInfoEnabled()) {
									LOGGER.info("Delete agent: " + agent);
								}
								notifyListenersInTransaction(new AgentEvent(agent, EventType.DELETE));
								internalEventBroadcaster.broadcast(Arrays.asList(new AgentEvent(agent, EventType.DELETE)));
							}
						});
			} catch (final Exception ex) {
				throw new ServiceException("Unable to delete agent: " + agent,
						ex);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAgents(final Set<String> agentKeys) {
		try {
			final List<MAgent> agents = getAgentsByKeys(agentKeys);
			for (final MAgent agent : agents) {
				delete(agent);
			}
			AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK, "Probes deleted : {}", Arrays.toString(agentKeys.toArray(new String[agentKeys.size()])));
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.NOK,"Unable to delete probes : {}, reason :", Arrays.toString(agentKeys.toArray(new String[agentKeys.size()])),e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean doesAgentExist(final String agentKey) {
		return modelSpace.findUniqueEntity(MAgent.class,
				createAgentNotDeletedCriterionByAgentKey(agentKey)) != null;
	}

	@Override
	public boolean doesAgentPermitted(String agentKey) {
		return 	authorizeService.isPermittedProbes(Arrays.asList(agentKey));
	}

	@Override
	public List<String> getProbeKeysUserCanManage() {
		return authorizeService.getProbeKeysUserCanManage();
	}

	@Override
	@Transactional(readOnly = true)
	public MAgent getAgentByKey(final String agentKey) {
		MAgent agent = null;
		try {
			agent = modelSpace.findUniqueEntity(MAgent.class,
					createAgentNotDeletedCriterionByAgentKey(agentKey));
		} catch (final Exception ex) {
			throw new ServiceException("Unable to get agent by key: "
					+ agentKey, ex);
		}
		return agent;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgent> getAgentsByKeys(final Set<String> agentKeys) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.in("key", agentKeys);
		criterion = query.and(criterion, query.eq("deleted", false));
		return modelSpace.find(MAgent.class, criterion);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getAllAgentKeys() throws ServiceException {
		final List<String> agentNames = new LinkedList<String>();

		final List<MAgent> agents = getAllAgents();
		for (final MAgent agent : agents) {
			agentNames.add(agent.getKey());
		}

		Collections.sort(agentNames);
		return agentNames;
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getAllAgentKeysNoFiltering() throws ServiceException {
		final List<String> agentNames = new LinkedList<String>();

		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("deleted", false);
		final List<MAgent> agents = modelSpace.find(MAgent.class, criterion);
		for (final MAgent agent : agents) {
			agentNames.add(agent.getKey());
		}

		Collections.sort(agentNames);
		return agentNames;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgent> getAllAgents() throws ServiceException {
		List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();
		if(!agentKeys.isEmpty()) {
			final CriterionQuery query = modelSpace.createCriterionQuery();
			Criterion criterion = query.in("key", agentKeys);
			criterion = query.and(criterion, query.eq("deleted", false));
			return modelSpace.find(MAgent.class, criterion);
		} else {
			return new ArrayList<MAgent>();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<String> getAllModuleKeysByAgentKey(final String agentKey) {
		return (List<String>) modelSpace.findProperties(MAgentModule.class,
				createModuleNotDeletedCriterionByAgentKey(agentKey, null),
				"key");
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentModule> getAllModulesByAgentKey(final String agentKey) {
		return modelSpace.find(MAgentModule.class,
				createModuleNotDeletedCriterionByAgentKey(agentKey, null));
	}

	@Override
	@Transactional(readOnly = true)
	public MAgentModule getModule(final String agentKey, final String moduleKey) {
		return modelSpace
				.findUniqueEntity(
						MAgentModule.class,
						createModuleCriterionByAgentKeyAndModuleKey(agentKey,
								moduleKey));
	}

	@Override
	public Set<String> getRegisteredAgents() {
		Set<String> result = null;
		synchronized (registeredAgents) {
			result = new HashSet<String>(registeredAgents);
		}
		return result;
	}

	private MAgent registerAgent(final MAgent agent) {
		if (agent.getKey() == null) {
			throw new ServiceException("Cannot register agent without key");
		}

		final MAgent existingAgent = executeInTransaction(true,
				new TransactionCallback<MAgent>() {

					@Override
					public MAgent doInTransaction(final TransactionStatus status) {
						return modelSpace.findUniqueEntity(
								MAgent.class,
								modelSpace.createCriterionQuery().eq("key",
										agent.getKey()));
					}
				});

		// TODO check agent key
		final Date registrationDateTime = new Date();
		final MAgent registeredAgent;
		if (existingAgent == null) {
			agent.setCreationDateTime(registrationDateTime);
			agent.setModificationDateTime(registrationDateTime);
			executeInTransaction(false, new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					updateAgentReferences(agent);
					modelSpace.save(agent);
				}
			});
			registeredAgent = agent;
			LOGGER.info("Register new agent: " + registeredAgent);
		} else {
			// all operations with deleted agents are prohibited.
			if (existingAgent.isDeleted()) {
				throw new DeletedSourceException("Agent (" + agent.getKey() + ") was already registered and deleted");
			}
			if (existingAgent.updateSimpleFields(agent)) {
				existingAgent.setModificationDateTime(registrationDateTime);
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								modelSpace.saveOrUpdate(existingAgent);
							}
						});
				notifyListenersWithoutTransaction(new AgentEvent(existingAgent,
						EventType.UPDATE));
				LOGGER.info("Update agent: " + existingAgent);
			}
			registeredAgent = existingAgent;
		}

		synchronized (registeredAgents) {
			registeredAgents.add(registeredAgent.getKey());
		}

		return MAgent.copy(registeredAgent);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public MAgent registerAgent(final MAgent agent,
			final List<MAgentModule> modules) {
		final MAgent registeredAgent = registerAgent(agent);
		updateModules(agent.getKey(), modules);
		return registeredAgent;
	}

	/**
	 * @param policyConfigurationService
	 *            the policyConfigurationService to set
	 */
	public void setPolicyConfigurationService(
			final PolicyConfigurationService policyConfigurationService) {
		this.policyConfigurationService = policyConfigurationService;
	}

	/**
	 * @param taskService
	 *            the taskService to set
	 */
	public void setTaskService(final InternalTaskService taskService) {
		this.taskService = taskService;
	}

	public void setWidgetDeleter(final WidgetDeleter widgetDeleter) {
		this.widgetDeleter = widgetDeleter;
	}

	public void setAuthorizeService(AuthorizeService authorizeService) {
		this.authorizeService = authorizeService;
	}

	private void updateAgentReferences(final MAgent agent) {
		final MDivision division = agent.getDivision();
		if (division != null) {
			final MDivision existing = modelSpace.findUniqueEntity(
					MDivision.class,
					modelSpace.createCriterionQuery().eq("name",
							division.getName()));
			if (existing != null) {
				agent.setDivision(existing);
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateModules(final String agentKey,
			final List<MAgentModule> updatedModules) {
		final MAgent existingAgent = executeInTransaction(true,
				new TransactionCallback<MAgent>() {

					@Override
					public MAgent doInTransaction(final TransactionStatus status) {
						return modelSpace.findUniqueEntity(
								MAgent.class,
								modelSpace.createCriterionQuery().eq("key",
										agentKey));
					}
				});

		// all operations with deleted agents are prohibited.
		if (existingAgent.isDeleted()) {
			throw new DeletedSourceException("Agent (" + agentKey
					+ ") was already deleted");
		}

		final List<MAgentModule> existingModules = executeInTransaction(true,
				new TransactionCallback<List<MAgentModule>>() {

					@Override
					public List<MAgentModule> doInTransaction(
							final TransactionStatus status) {
						return getAllModulesByAgentKey(agentKey);
					}
				});
		final Map<String, MAgentModule> existingModulesMap = new HashMap<String, MAgentModule>();
		for (final MAgentModule module : existingModules) {
			existingModulesMap.put(module.getKey(), module);
		}
		for (final MAgentModule updatedModule : updatedModules) {
			final MAgentModule existingModule = existingModulesMap
					.get(updatedModule.getKey());

			if (existingModule != null) {
				boolean moduleIsUpdated = existingModule
						.updateSimpleFields(updatedModule);
				final MResultConfigurationTemplate existingResultConfigurationTemplate = existingModule
						.getTemplateResultConfiguration();
				final MResultConfigurationTemplate updatedResultConfigurationTemplate = updatedModule
						.getTemplateResultConfiguration();

				if (existingResultConfigurationTemplate != null
						&& updatedResultConfigurationTemplate != null) {

					moduleIsUpdated |= existingResultConfigurationTemplate
							.updateSimpleFields(updatedResultConfigurationTemplate);

					moduleIsUpdated |= taskService
							.updateModuleAndTaskParameterConfigurations(
									agentKey, existingModule.getKey(),
									existingModule
											.getTemplateResultConfiguration(),
									updatedModule
											.getTemplateResultConfiguration());
				}

				if (moduleIsUpdated) {
					executeInTransaction(false,
							new TransactionCallbackWithoutResult() {

								@Override
								protected void doInTransactionWithoutResult(
										final TransactionStatus status) {
									modelSpace.saveOrUpdate(existingModule);
								}
							});
					LOGGER.info("Update module: " + existingModule);
				}
			} else {
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								updatedModule.setParent(existingAgent);
								modelSpace.save(updatedModule);
							}
						});

				LOGGER.info("Register new module: " + updatedModule);
			}
		}
	}
}

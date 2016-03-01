/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.*;
import com.tecomgroup.qos.exception.*;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.EventBroadcastDispatcher;
import com.tecomgroup.qos.service.alert.AlertHistoryService;
import com.tecomgroup.qos.service.alert.PropagationAlertService;
import com.tecomgroup.qos.util.PolicyUtils;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.*;

/**
 * @author kunilov.p
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PolicyConfigurationServiceTest {

	private static final String SEND_ACTION_NAME_1 = "sendAction1";
	private static final String SEND_ACTION_NAME_2 = "sendAction2";
	private static final String SEND_ACTION_NAME_3 = "sendAction3";

	private static final String POLICY_ACTIONS_TEMPLATE_NAME = "policyActionsTemplate";

	private static final String POLICY_CONDITIONS_TEMPLATE_NAME = "policyConditionsTemplate";

	private static void assertConditionLevel(
			final ConditionLevel expectedResult,
			final ConditionLevel actualResult) {
		Assert.assertEquals(expectedResult.getCeaseLevel(),
				actualResult.getCeaseLevel());
		Assert.assertEquals(expectedResult.getCeaseDuration(),
				actualResult.getCeaseDuration());
		Assert.assertEquals(expectedResult.getRaiseLevel(),
				actualResult.getRaiseLevel());
		Assert.assertEquals(expectedResult.getRaiseDuration(),
				actualResult.getRaiseDuration());
	}

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TransactionTemplate readOnlyTransactionTemplate;

	@Autowired
	private InternalPolicyConfigurationService policyConfigurationService;

	private PropagationAlertService alertService;

	@Autowired
	private AlertHistoryService alertHistoryService;

	@Autowired
	private InternalTaskService taskService;

	@Autowired
	private EventBroadcastDispatcher eventBroadcastDispatcher;

	@Autowired
	private InternalSourceService sourceService;

	private MAgentTask task;
	private MAgentTask otherTask;

	private MPolicy policy;
	private MPolicy otherPolicy;

	private Source originator;
	private Source otherOriginator;

	private MPolicyActionWithContacts sendAction1;
	private MPolicyActionWithContacts sendAction2;
	private MPolicyActionWithContacts sendAction3;

	private final String policyManagerName = "testPolicyManager";

	private String agentName;

	private MAgent agent;

	private MUserGroup userGroup;

	private MPolicyAction getSendAlertAction(final MPolicy policy) {
		MPolicyAction result = null;
		for (final MPolicyAction action : policy.getActions()) {
			if (action instanceof MPolicySendAlert) {
				result = action;
				break;
			}
		}
		return result;
	}

	private PropagationAlertService initAlertService() {
		final PropagationAlertService alertService = new PropagationAlertService();
		alertService.setAlertHistoryService(alertHistoryService);
		alertService.setSourceService(sourceService);
		alertService.setModelSpace(modelSpace);
		alertService.setTransactionTemplate(transactionTemplate);
		alertService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		alertService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		alertService.setAlertTypesFile(new File(
				"../qos-gwt-media/config/alertTypes.config"));
		alertService.setPostProcessAlerts(false);
		alertService.setPropagationEnabled(false);
		alertService.init();

		return alertService;
	}

	@Before
	public void setUp() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				alertService = initAlertService();
				setUpLightWeightAgent();
			}
		});
	}

	private void setUpAndApplyPolicyActions() {
		final List<MUser> users = new ArrayList<>();
		userGroup = new MUserGroup();
		userGroup.setName("userGroup");
		userGroup.setUsers(users);
		modelSpace.saveOrUpdate(userGroup);

		sendAction1 = SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_1);
		sendAction2 = SharedModelConfiguration
				.createPolicySendSmsAction(SEND_ACTION_NAME_2);
		sendAction3 = SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_3);

		final List<MContactInformation> actionContacts = new ArrayList<>();
		actionContacts.add(userGroup);

		sendAction1.setContacts(actionContacts);
		sendAction2.setContacts(actionContacts);

		policy.addAction(sendAction1);
		policy.addAction(sendAction2);
		modelSpace.saveOrUpdate(policy);
	}

	private void setUpLightWeightAgent() {
		agentName = "testAgentName";
		agent = SharedModelConfiguration.createLightWeightAgent(agentName);
		modelSpace.save(agent);

		final MAgentModule module = SharedModelConfiguration
				.createAgentModule(agent);
		modelSpace.save(module);

		task = SharedModelConfiguration.createAgentTask(module);

		taskService.updateTaskConfiguration(task);
		task.getResultConfiguration().setParameterConfigurations(
				SharedModelConfiguration
						.createResultParameterConfigurations(null));

		modelSpace.save(task);
		otherTask = SharedModelConfiguration.createAgentTask(module);
		taskService.updateTaskConfiguration(otherTask);
		otherTask.getResultConfiguration().setParameterConfigurations(
				SharedModelConfiguration
						.createResultParameterConfigurations(null));
		modelSpace.save(otherTask);

		policy = SharedModelConfiguration
				.createPolicy(task, "policyActionName");
		modelSpace.save(policy);
		originator = Source.getSource(policy);

		otherPolicy = SharedModelConfiguration.createPolicy(otherTask,
				"otherPolicyActionName");
		modelSpace.save(otherPolicy);
		otherOriginator = Source.getSource(otherPolicy);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAddAgentToRegisteredPolicyManagerInfo() {
		policyConfigurationService.registerPolicyManager(policyManagerName,
				null);
		final String newAgentName = "testNewAgentName";
		policyConfigurationService.addAgentToRegisteredPMInfo(newAgentName);
		final Set<String> registeredPM = policyConfigurationService
				.getRegisteredPolicyManagersBySystemComponent(newAgentName);
		Assert.assertNotNull(registeredPM);
		Assert.assertTrue(registeredPM.contains(policyManagerName));
	}

	@Test
	@Transactional
	public void testAllPolicyGettersAfterDeletion() {
		final List<MPolicy> agentPolicies = policyConfigurationService
				.getAgentPolicies(agentName, null, null, null, null);

		policyConfigurationService.deletePolicies(SimpleUtils
				.getKeys(agentPolicies));

		final List<MPolicy> deletedAgentPolicies = policyConfigurationService
				.getAgentPolicies(agentName, null, null, null, null);
		Assert.assertTrue(deletedAgentPolicies.isEmpty());

		final List<MPolicy> deletedSourcePolicies = policyConfigurationService
				.getPolicies(Source.getAgentSource(agentName));
		Assert.assertTrue(deletedSourcePolicies.isEmpty());

		final List<MPolicy> deletedTaskPolicies = policyConfigurationService
				.getPolicies(Source.getTaskSource(task.getKey()));
		Assert.assertTrue(deletedTaskPolicies.isEmpty());

		for (final MResultParameterConfiguration parameterConfiguration : task
				.getResultConfiguration().getParameterConfigurations()) {
			final List<MPolicy> deletedParameterPolicies = policyConfigurationService
					.getTaskParameterPolicies(task,
							parameterConfiguration.getParameterIdentifier());
			Assert.assertTrue(deletedParameterPolicies.isEmpty());
		}
	}

	@Test
	@Transactional
	public void testApplyPolicyActionsTemplate() {
		final MPolicyAction policySendAlertAction = getSendAlertAction(policy);
		setUpAndApplyPolicyActions();

		modelSpace.save(SharedModelConfiguration.createPolicyActionsTemplate(
				POLICY_ACTIONS_TEMPLATE_NAME,
				Arrays.asList(sendAction2, sendAction3)));
		policyConfigurationService.applyPolicyActionsTemplate(
				POLICY_ACTIONS_TEMPLATE_NAME,
				SimpleUtils.getKeys(Arrays.asList(policy)));

		final MPolicy updatedPolicy = modelSpace.get(MPolicy.class,
				policy.getId());
		final List<MPolicyAction> updatedPolicyActions = updatedPolicy
				.getActions();
		Assert.assertEquals(POLICY_ACTIONS_TEMPLATE_NAME, updatedPolicy
				.getActionsTemplate().getName());
		Assert.assertEquals(3, updatedPolicyActions.size());

		final List<String> updatedPolicyActionNames = new ArrayList<>();
		for (final MPolicyAction action : updatedPolicyActions) {
			updatedPolicyActionNames.add(action.getName());
		}
		Assert.assertTrue(updatedPolicyActionNames
				.contains(policySendAlertAction.getName()));
		Assert.assertTrue(updatedPolicyActionNames.contains(sendAction2
				.getName()));
		Assert.assertTrue(updatedPolicyActionNames.contains(sendAction3
				.getName()));
		Assert.assertFalse(updatedPolicyActionNames.contains(sendAction1
				.getName()));

		Assert.assertNull(modelSpace.get(MPolicySendEmail.class,
				sendAction1.getId()));
	}

	@Test
	@Transactional
	public void testApplyPolicyConditionsTemplate() {
		final MPolicyConditionLevels conditionLevels = SharedModelConfiguration
				.createPolicyConditionLevels();
		modelSpace.save(SharedModelConfiguration
				.createPolicyConditionsTemplate(
						POLICY_CONDITIONS_TEMPLATE_NAME, conditionLevels));
		policyConfigurationService.applyPolicyConditionsTemplate(
				POLICY_CONDITIONS_TEMPLATE_NAME,
				SimpleUtils.getKeys(Arrays.asList(policy)));

		final MPolicy updatedPolicy = modelSpace.get(MPolicy.class,
				policy.getId());
		Assert.assertNotNull(updatedPolicy.getConditionsTemplate());
		Assert.assertEquals(POLICY_CONDITIONS_TEMPLATE_NAME, updatedPolicy
				.getConditionsTemplate().getName());
		Assert.assertTrue(PolicyUtils.arePolicyConditionLevelsEqual(
				conditionLevels, updatedPolicy.getConditionsTemplate()
						.getConditionLevels()));
	}

	@Test(expected = CompatibilityException.class)
	@Transactional
	public void testApplyPolicyConditionsTemplateForIncompatibleParameters() {
		final MPolicy policyForBoolParameter = SharedModelConfiguration
				.createPolicy(
						task,
						"boolParameterActionName",
						new ParameterIdentifier(
								SharedModelConfiguration.FAKE_STRONG_SIGNAL_BOOLEAN,
								null));
		modelSpace.save(policyForBoolParameter);
		final MPolicy policyForLevelParameter = SharedModelConfiguration
				.createPolicy(task, "levelParameterActionName",
						new ParameterIdentifier(
								SharedModelConfiguration.NICAM_LEVEL, null));
		modelSpace.save(policyForLevelParameter);

		modelSpace
				.save(SharedModelConfiguration.createPolicyConditionsTemplate(
						POLICY_CONDITIONS_TEMPLATE_NAME,
						SharedModelConfiguration.createPolicyConditionLevels()));
		policyConfigurationService.applyPolicyConditionsTemplate(
				POLICY_CONDITIONS_TEMPLATE_NAME,
				SimpleUtils.getKeys(Arrays.asList(policyForBoolParameter,
						policyForLevelParameter)));
	}

	@Test(expected = SourceNotFoundException.class)
	@Transactional
	public void testApplyPolicyConditionsTemplateForPolicyWithDeletedSource() {
		taskService.delete(task);

		modelSpace
				.save(SharedModelConfiguration.createPolicyConditionsTemplate(
						POLICY_CONDITIONS_TEMPLATE_NAME,
						SharedModelConfiguration.createPolicyConditionLevels()));
		policyConfigurationService.applyPolicyConditionsTemplate(
				POLICY_CONDITIONS_TEMPLATE_NAME,
				SimpleUtils.getKeys(Arrays.asList(policy, otherPolicy)));
	}

	@Test
	@Transactional
	public void testClearPolicyActions() {
		final MPolicyAction policySendAlertAction = getSendAlertAction(policy);

		setUpAndApplyPolicyActions();

		final MPolicy updatedPolicy = modelSpace.get(MPolicy.class,
				policy.getId());
		List<MPolicyAction> updatedPolicyActions = updatedPolicy.getActions();
		Assert.assertTrue(updatedPolicyActions.size() > 1);

		policyConfigurationService.clearPolicyActionsTemplates(SimpleUtils
				.getKeys(Arrays.asList(policy)));
		updatedPolicyActions = modelSpace.get(MPolicy.class, policy.getId())
				.getActions();
		Assert.assertNull(updatedPolicy.getActionsTemplate());
		Assert.assertTrue(updatedPolicyActions.size() == 1);
		Assert.assertEquals(policySendAlertAction, updatedPolicyActions.get(0));

		Assert.assertNull(modelSpace.get(MPolicySendEmail.class,
				sendAction1.getId()));
		Assert.assertNull(modelSpace.get(MPolicySendEmail.class,
				sendAction2.getId()));
	}

	@Test
	@Transactional
	public void testClearPolicyConditionsTemplates() {
		modelSpace
				.save(SharedModelConfiguration.createPolicyConditionsTemplate(
						POLICY_CONDITIONS_TEMPLATE_NAME,
						SharedModelConfiguration.createPolicyConditionLevels()));
		final Set<String> policyKeys = SimpleUtils.getKeys(Arrays.asList(
				policy, otherPolicy));
		policyConfigurationService.applyPolicyConditionsTemplate(
				POLICY_CONDITIONS_TEMPLATE_NAME, policyKeys);

		policyConfigurationService.clearPolicyConditionsTemplates(policyKeys);

		final MPolicy updatedPolicy = modelSpace.get(MPolicy.class,
				policy.getId());
		Assert.assertNull(updatedPolicy.getConditionsTemplate());
		final MPolicy updatedOtherPolicy = modelSpace.get(MPolicy.class,
				otherPolicy.getId());
		Assert.assertNull(updatedOtherPolicy.getConditionsTemplate());
	}

	@Test
	public void testCreateOrUpdatePolicy() {
		policyConfigurationService.saveOrUpdatePolicy(policy);

		final List<MPolicy> policies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.getAll(MPolicy.class);
					}
				});

		Assert.assertFalse(policies.isEmpty());
	}

	@Test
	@Transactional
	public void testDeletePolicies() {
		final List<MPolicy> policies = policyConfigurationService
				.getAllPolicies();

		policyConfigurationService
				.deletePolicies(SimpleUtils.getKeys(policies));

		final List<MPolicy> deletedPolicies = policyConfigurationService
				.getAllPolicies();
		Assert.assertTrue(deletedPolicies.isEmpty());

		final List<MPolicy> deletedAllPolicies = policyConfigurationService
				.getPolicies(null, null, null, null, null);
		Assert.assertTrue(deletedAllPolicies.isEmpty());

		final long allPoliciesCount = policyConfigurationService
				.getPoliciesCount(null, null);
		Assert.assertEquals(0, allPoliciesCount);
	}

	@Test
	@Transactional
	public void testDeletePolicy() {
		policyConfigurationService.delete(policy);

		final MPolicy deletedPolicy = policyConfigurationService
				.getPolicy(policy.getKey());
		Assert.assertNull(deletedPolicy);
	}

	@Test
	@Transactional
	public void testDetachDifferentTypePoliciesFromConditionsTemplate() {

		final ParameterIdentifier parameterIdentifier = new ParameterIdentifier(
				SharedModelConfiguration.FAKE_STRONG_SIGNAL_BOOLEAN, null);
		final MPolicy boolPolicy = SharedModelConfiguration.createPolicy(task,
				"boolActionName", parameterIdentifier);

		modelSpace.save(boolPolicy);

		final MPolicyConditionLevels conditionLevels = SharedModelConfiguration
				.createPolicyConditionLevels();
		final MPolicyConditionsTemplate conditionsTemplate = SharedModelConfiguration
				.createPolicyConditionsTemplate(
						POLICY_CONDITIONS_TEMPLATE_NAME, conditionLevels);
		modelSpace.save(conditionsTemplate);

		boolPolicy.setConditionsTemplate(conditionsTemplate);
		policy.setConditionsTemplate(conditionsTemplate);

		conditionsTemplate.setParameterType(ParameterType.BOOL);

		final Long boolPolicyId = boolPolicy.getId();
		final Long levelPolicyId = policy.getId();

		policyConfigurationService
				.detachDifferentTypePoliciesFromConditionsTemplate(conditionsTemplate);

		Assert.assertNotNull(modelSpace.get(MPolicy.class, boolPolicyId)
				.getConditionsTemplate());
		Assert.assertNull(modelSpace.get(MPolicy.class, levelPolicyId)
				.getConditionsTemplate());
	}

	@Test
	@Transactional
	public void testDisablePolicies() {
		final List<MPolicy> policies = policyConfigurationService
				.getAllPolicies();

		policyConfigurationService.disablePolicies(SimpleUtils
				.getKeys(policies));

		final List<MPolicy> deletedPolicies = policyConfigurationService
				.getAllPolicies();
		Assert.assertTrue(deletedPolicies.isEmpty());

		final List<MPolicy> disabledAllPolicies = policyConfigurationService
				.getPolicies(null, null, null, null, null);
		Assert.assertTrue(disabledAllPolicies.isEmpty());

		final long allPoliciesCount = policyConfigurationService
				.getPoliciesCount(null, null);
		Assert.assertEquals(0, allPoliciesCount);
	}

	@Test
	public void testDisablePolicy() {
		policyConfigurationService.disable(policy);

		final List<MPolicy> policies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.find(MPolicy.class, modelSpace
								.createCriterionQuery().eq("disabled", false));
					}
				});
		Assert.assertEquals(1, policies.size());
	}

	@Test
	@Transactional
	public void testDisablePolicyWithActiveAlerts() {
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				policy.getSource(), originator, "indicationSettings1"), "test");
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				policy.getSource(), originator, "indicationSettings2"), "test");
		alertService.activateAlert(
				SharedModelConfiguration.createIndication(
						SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
						otherPolicy.getSource(), otherOriginator,
						"indicationSettings1"), "test");
		alertService.activateAlert(
				SharedModelConfiguration.createIndication(
						SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
						otherPolicy.getSource(), otherOriginator,
						"indicationSettings2"), "test");

		final List<MAlert> policyActiveAlerts = alertService.getAlertsByOriginator(
				Source.getSource(policy), null, null, null);
		Assert.assertEquals(2, policyActiveAlerts.size());

		final List<MAlert> otherPolicyActiveAlerts = alertService
				.getAlertsByOriginator(Source.getSource(otherPolicy), null, null, null);
		Assert.assertEquals(2, otherPolicyActiveAlerts.size());

		testRemovePoliciesWithAlerts();
	}

	@Test
	@Transactional
	public void testDisablePolicyWithActiveAlertWhenItWasSubstitutedWithTheSameAlertOfOtherOriginator() {
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				policy.getSource(), originator, null), "test");
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				policy.getSource(), otherOriginator, null), "test");

		List<MAlert> activeAlerts = alertService.getAlertsByOriginator(
				originator, null, null, null);
		Assert.assertEquals(1, activeAlerts.size());
		List<MAlert> otherAlerts = alertService.getAlertsByOriginator(
				otherOriginator, null, null, null);
		Assert.assertEquals(1, otherAlerts.size());

		policyConfigurationService.disable(policy);
		final List<MPolicy> policies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.find(MPolicy.class, modelSpace
								.createCriterionQuery().eq("disabled", false));
					}
				});
		Assert.assertEquals(1, policies.size());

		activeAlerts = alertService.getAlertsByOriginator(otherOriginator, null,
				null, null);
		Assert.assertEquals(1, activeAlerts.size());
	}

	@Test
	public void testGetAllPolicies() {
		final List<MPolicy> policies = policyConfigurationService
				.getAllPolicies();
		Assert.assertFalse(policies.isEmpty());
	}

	@Test
	public void testGetAllPoliciesAfterDisable() {
		List<MPolicy> policies = policyConfigurationService.getAllPolicies();
		Assert.assertFalse(policies.isEmpty());

		for (final MPolicy policy : policies) {
			policyConfigurationService.disable(policy);
		}
		policies = policyConfigurationService.getAllPolicies();
		Assert.assertTrue(policies.isEmpty());
	}

	@Test
	public void testGetOnlyActivePolicy() {
		policyConfigurationService.disable(policy);

		MPolicy disabledPolicy = policyConfigurationService.getPolicy(
				policy.getKey(), false);
		Assert.assertNotNull(disabledPolicy);

		disabledPolicy = policyConfigurationService.getPolicy(policy.getKey(),
				true);
		Assert.assertNull(disabledPolicy);
	}

	@Test
	public void testGetPMConfigurations() {
		testRegisterPolicyManager();
		final Map<Source, PMConfiguration> pmConfigurations = policyConfigurationService
				.getPMConfigurations(policyManagerName);

		Assert.assertFalse(pmConfigurations.isEmpty());
	}

	@Test
	public void testGetPolicies() {
		final List<MPolicy> policies = policyConfigurationService
				.getPolicies(Source.getTaskSource(task.getKey()));
		Assert.assertFalse(policies.isEmpty());
	}

	@Test
	public void testGetPolicy() {
		final MPolicy policy = policyConfigurationService.getPolicy(this.policy
				.getKey());
		Assert.assertNotNull(policy);
	}

	@Test
	public void testGetRegisteredPolicyManagersBySystemComponent() {
		final Set<String> registeredSystemComponents = policyConfigurationService
				.registerPolicyManager(policyManagerName,
						Arrays.asList(agentName));

		for (final String systemComponentKey : registeredSystemComponents) {
			final Set<String> foundRegisteredPolicyManagers = policyConfigurationService
					.getRegisteredPolicyManagersBySystemComponent(systemComponentKey);
			Assert.assertEquals(1, foundRegisteredPolicyManagers.size());
			Assert.assertEquals(policyManagerName,
					foundRegisteredPolicyManagers.iterator().next());
		}
	}

	/**
	 * TODO please remove this when user will be able to set subject and body
	 * for messages being sent when policy triggers
	 */
	@Test
	@Transactional
	public void testPolicySendEmailActionDefaultValuesApplied() {
		final MPolicySendEmail sendEmailAction = (MPolicySendEmail) SharedModelConfiguration
				.createPolicySendEmailAction("nulledSendEmailAction");
		policy.addAction(sendEmailAction);
		policyConfigurationService.saveOrUpdatePolicy(policy);
	}

	@Test
	@Transactional
	public void testReapplyPolicyActionsTemplate() {
		final MPolicyActionWithContacts sendAction1 = SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_1);
		final MPolicyActionWithContacts sendAction2 = SharedModelConfiguration
				.createPolicySendSmsAction(SEND_ACTION_NAME_2);
		final MPolicyActionWithContacts sendAction3 = SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_3);

		final MPolicyActionsTemplate template = SharedModelConfiguration
				.createPolicyActionsTemplate(POLICY_ACTIONS_TEMPLATE_NAME,
						Arrays.asList(sendAction1, sendAction2));
		modelSpace.save(template);

		final Set<String> policyKeys = SimpleUtils.getKeys(Arrays.asList(
				policy, otherPolicy));
		policyConfigurationService.applyPolicyActionsTemplate(
				POLICY_ACTIONS_TEMPLATE_NAME, policyKeys);

		final List<MPolicy> foundPolicies = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		Assert.assertEquals(2, foundPolicies.size());
		for (final MPolicy foundPolicy : foundPolicies) {
			Assert.assertNotNull(foundPolicy.getActionsTemplate());
			Assert.assertEquals(POLICY_ACTIONS_TEMPLATE_NAME, foundPolicy
					.getActionsTemplate().getName());
			Assert.assertEquals(2, foundPolicy.getActionsWithContacts().size());

			Assert.assertEquals(SEND_ACTION_NAME_1, foundPolicy
					.getSendEmailActions().iterator().next().getName());
			Assert.assertEquals(SEND_ACTION_NAME_2, foundPolicy
					.getSendSmsActions().iterator().next().getName());
		}

		template.getActions().clear();
		template.getActions().add(sendAction3);
		policyConfigurationService.reapplyPolicyActionsTemplate(template);

		final List<MPolicy> updatedPolicies = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		Assert.assertEquals(2, updatedPolicies.size());
		for (final MPolicy updatedPolicy : updatedPolicies) {
			Assert.assertNotNull(updatedPolicy.getActionsTemplate());
			Assert.assertEquals(POLICY_ACTIONS_TEMPLATE_NAME, updatedPolicy
					.getActionsTemplate().getName());
			Assert.assertEquals(1, updatedPolicy.getActionsWithContacts()
					.size());

			Assert.assertEquals(SEND_ACTION_NAME_3, updatedPolicy
					.getSendEmailActions().iterator().next().getName());
			Assert.assertTrue(updatedPolicy.getSendSmsActions().isEmpty());
		}
	}

	@Test
	@Transactional
	public void testReapplyPolicyConditionsTemplate() {
		final MPolicyConditionLevels conditionLevels = SharedModelConfiguration
				.createPolicyConditionLevels();
		final MPolicyConditionsTemplate template = SharedModelConfiguration
				.createPolicyConditionsTemplate(
						POLICY_CONDITIONS_TEMPLATE_NAME, conditionLevels);

		modelSpace.save(template);

		final Set<String> policyKeys = SimpleUtils.getKeys(Arrays.asList(
				policy, otherPolicy));
		policyConfigurationService.applyPolicyConditionsTemplate(
				POLICY_CONDITIONS_TEMPLATE_NAME, policyKeys);

		final ConditionLevel newCriticalLevel = new ConditionLevel("100.0",
				10L, "120.0", 50L);
		conditionLevels.setCriticalLevel(newCriticalLevel);
		final ThresholdType newThresholdType = ThresholdType.GREATER_OR_EQUALS;
		conditionLevels.setThresholdType(newThresholdType);

		policyConfigurationService.reapplyPolicyConditionsTemplate(template);

		for (final String policyKey : policyKeys) {
			final MPolicy policy = policyConfigurationService
					.getPolicy(policyKey);

			final MPolicyConditionLevels updatedConditionLevels = (MPolicyConditionLevels) policy
					.getCondition();
			final ConditionLevel updatedConditionLevel = updatedConditionLevels
					.getCriticalLevel();
			Assert.assertEquals(newCriticalLevel.getCeaseLevel(),
					updatedConditionLevel.getCeaseLevel());
			Assert.assertEquals(newCriticalLevel.getCeaseDuration(),
					updatedConditionLevel.getCeaseDuration());
			Assert.assertEquals(newCriticalLevel.getRaiseLevel(),
					updatedConditionLevel.getRaiseLevel());
			Assert.assertEquals(newCriticalLevel.getRaiseDuration(),
					updatedConditionLevel.getRaiseDuration());

			Assert.assertEquals(newThresholdType,
					updatedConditionLevels.getThresholdType());
		}
	}

	@Test
	public void testRegisterDisabledPolicy() {
		final MPolicy registeringPolicy = SharedModelConfiguration
				.createPolicy(task, "policyActionName");
		policyConfigurationService.registerPolicy("testAgentName",
				registeringPolicy);

		final List<MPolicy> policies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.getAll(MPolicy.class);
					}
				});
		final MPolicy addedPolicy = policyConfigurationService
				.getPolicy(registeringPolicy.getKey());

		policyConfigurationService.disable(addedPolicy);
		policyConfigurationService.registerPolicy("testAgentName",
				registeringPolicy);

		final List<MPolicy> updatedPolicies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.getAll(MPolicy.class);
					}
				});
		Assert.assertEquals(policies.size(), updatedPolicies.size());
	}

	@Test
	public void testRegisterPolicyManager() {
		final List<String> agentsToRegister = new ArrayList<>(
				Arrays.asList(agentName));
		final Set<String> registeredAgents = policyConfigurationService
				.registerPolicyManager(policyManagerName, agentsToRegister);

		Assert.assertEquals(agentsToRegister.size(), registeredAgents.size());
		agentsToRegister.removeAll(registeredAgents);
		Assert.assertTrue(agentsToRegister.isEmpty());
	}

	@Test(expected = ServiceException.class)
	public void testRegisterPolicyManagerWithUnknownAgent() {
		policyConfigurationService.registerPolicyManager(policyManagerName,
				Arrays.asList(agentName, "fakeAgentName"));
	}

	@Test
	public void testRemoveAction() {
		policy.removeAction(policy.getActions().iterator().next());
		policyConfigurationService.saveOrUpdatePolicy(policy);
		final List<MPolicyAction> policyActions = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicyAction>>() {

					@Override
					public List<MPolicyAction> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.getAll(MPolicyAction.class);
					}
				});
		final MPolicy updatedPolicy = readOnlyTransactionTemplate
				.execute(new TransactionCallback<MPolicy>() {

					@Override
					public MPolicy doInTransaction(
							final TransactionStatus status) {
						return modelSpace.get(MPolicy.class, policy.getId());
					}
				});
		Assert.assertTrue(updatedPolicy.getActions().isEmpty());
		Assert.assertFalse(policyActions.isEmpty());
	}

	@Test
	public void testRemovePolicies() {
		final Set<String> policyKeys = new HashSet<>(Arrays.<String> asList(
				policy.getKey(), otherPolicy.getKey()));
		policyConfigurationService.disablePolicies(policyKeys);

		final List<MPolicy> policies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.find(MPolicy.class, modelSpace
								.createCriterionQuery().eq("disabled", false));
					}
				});
		Assert.assertTrue(policies.isEmpty());
	}

	@Test
	@Transactional
	public void testRemovePoliciesWithAlerts() {
		testRemovePolicies();

		final List<MAlert> policyActiveAlerts = alertService.getAlertsBySource(
				policy.getSource(), null, null, null, false, null);
		Assert.assertTrue(policyActiveAlerts.isEmpty());

		final List<MAlert> otherPolicyActiveAlerts = alertService
				.getAlertsBySource(otherPolicy.getSource(), null, null, null,
						false, null);
		Assert.assertTrue(otherPolicyActiveAlerts.isEmpty());
	}

	@Test
	@Transactional
	public void testRemovePolicyActionsWithContactInformation() {
		setUpAndApplyPolicyActions();

		final int allSendEmailActionsSize = modelSpace.getAll(
				MPolicyActionWithContacts.class).size();

		policyConfigurationService.removeSendActionsWithContact(userGroup);
		modelSpace.delete(userGroup);

		Assert.assertEquals(allSendEmailActionsSize - 2,
				modelSpace.getAll(MPolicyActionWithContacts.class).size());

	}

	@Test
	public void testResetDisabledPolicies() {
		testRemovePolicies();

		// reset policy
		policyConfigurationService.resetDisabledPolicies(task,
				((MContinuousThresholdFallCondition) policy.getCondition())
						.getParameterIdentifier());
		// reset otherPolicy
		policyConfigurationService
				.resetDisabledPolicies(otherTask,
						((MContinuousThresholdFallCondition) otherPolicy
								.getCondition()).getParameterIdentifier());
		final List<MPolicy> policies = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicy>>() {

					@Override
					public List<MPolicy> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.find(MPolicy.class, modelSpace
								.createCriterionQuery().eq("disabled", false));
					}
				});
		Assert.assertEquals(2, policies.size());
	}

	@Test
	@Transactional
	public void testResetDisabledPoliciesWithAlerts() {
		// activate alerts

		System.out.println(policy.getParent());
		System.out.println(policy.getParent().getParent());
		System.out.println(policy.getParent().getParent().getParent());
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				Source.getSource(policy.getParent()), Source.getSource(policy), "settings1"), "test");
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				Source.getSource(otherPolicy.getParent()),  Source.getSource(otherPolicy), "settings1"), "test");
		List<MAlert> policyActiveAlerts = alertService.getAlertsByOriginator(
				Source.getSource(policy), null, null, null);
		Assert.assertEquals(1, policyActiveAlerts.size());
		List<MAlert> otherPolicyActiveAlerts = alertService.getAlertsByOriginator(
				Source.getSource(otherPolicy), null, null, null);
		Assert.assertEquals(1, otherPolicyActiveAlerts.size());
		// disable policy and alerts
		testRemovePoliciesWithAlerts();

		// reset disabled policy
		testResetDisabledPolicies();

		policyActiveAlerts = alertService.getAlertsByOriginator(Source.getSource(policy),
				null, null, null);
		Assert.assertEquals(1, policyActiveAlerts.size());
		otherPolicyActiveAlerts = alertService.getAlertsByOriginator(
				Source.getSource(otherPolicy), null, null, null);
		Assert.assertEquals(1, otherPolicyActiveAlerts.size());
	}

	@Test
	public void testSaveOrUpdateAction() {
		final MPolicyAction action = policy.getActions().iterator().next();
		final String newActionName = "newActionName";
		action.setName(newActionName);
		policyConfigurationService.saveOrUpdatePolicy(policy);

		final List<MPolicyAction> policyActions = readOnlyTransactionTemplate
				.execute(new TransactionCallback<List<MPolicyAction>>() {

					@Override
					public List<MPolicyAction> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.getAll(MPolicyAction.class);
					}
				});
		Assert.assertFalse(policyActions.isEmpty());
		final MPolicyAction updatedAction = policyActions.iterator().next();
		Assert.assertEquals(newActionName, updatedAction.getName());
	}

	@Test(expected = DeletedSourceException.class)
	@Transactional
	public void testSaveOrUpdatePolicyWithDeletedSource() {
		taskService.delete(task);

		policyConfigurationService.saveOrUpdatePolicy(policy);
	}

	@Test(expected = DisabledSourceException.class)
	@Transactional
	public void testSaveOrUpdatePolicyWithDisabledSource() {
		taskService.disable(task);

		policyConfigurationService.saveOrUpdatePolicy(policy);
	}

	@Test(expected = UnknownSourceException.class)
	@Transactional
	public void testSaveOrUpdatePolicyWithUnknownSource() {
		policy.setSource(Source.getAgentSource(agentName));
		policyConfigurationService.saveOrUpdatePolicy(policy);
	}

	@Test
	@Transactional
	public void testUpdateAccumulatedThresholdFallCondition() {
		final AccumulatedLevel newCriticalLevel = new AccumulatedLevel("100.0",
				30L, "130.0", 30L, 100L);
		final AccumulatedLevel newWarningLevel = new AccumulatedLevel("50.0",
				10L, "80.0", 10L, 50L);

		final MAccumulatedThresholdFallCondition condition = new MAccumulatedThresholdFallCondition();
		condition.setParameterIdentifier(new ParameterIdentifier("signalLevel",
				null));
		condition.setThresholdType(ThresholdType.LESS);
		condition.setCriticalLevel(newCriticalLevel);
		condition.setWarningLevel(newWarningLevel);
		policy.setCondition(condition);
		modelSpace.saveOrUpdate(policy);

		final MPolicy updatedPolicy = modelSpace.get(MPolicy.class,
				policy.getId());

		final ConditionLevel updatedContinuousThresholdCriticalLevel = ((MContinuousThresholdFallCondition) updatedPolicy
				.getCondition()).getCriticalLevel();
		assertConditionLevel(newCriticalLevel,
				updatedContinuousThresholdCriticalLevel);

		final ConditionLevel updatedContinuousThresholdWarningLevel = ((MContinuousThresholdFallCondition) updatedPolicy
				.getCondition()).getWarningLevel();
		assertConditionLevel(newWarningLevel,
				updatedContinuousThresholdWarningLevel);

		final ConditionLevel updatedAccumulatedThresholdCriticalLevel = ((MAccumulatedThresholdFallCondition) updatedPolicy
				.getCondition()).getCriticalLevel();
		assertConditionLevel(newCriticalLevel,
				updatedAccumulatedThresholdCriticalLevel);

		final ConditionLevel updatedAccumulatedThresholdWarningLevel = ((MAccumulatedThresholdFallCondition) updatedPolicy
				.getCondition()).getWarningLevel();
		assertConditionLevel(newWarningLevel,
				updatedAccumulatedThresholdWarningLevel);
	}

	@Test
	public void testUpdateContinuousThresholdFallCondition() {
		final ConditionLevel newCriticalLevel = new ConditionLevel("100.0",
				20L, "130.0", 20L);
		final ConditionLevel newWarningLevel = new ConditionLevel("50.0", 10L,
				"80.0", 10L);
		final MContinuousThresholdFallCondition condition = (MContinuousThresholdFallCondition) policy
				.getCondition();
		condition.setCriticalLevel(newCriticalLevel);
		condition.setWarningLevel(newWarningLevel);
		policy.setCondition(condition);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				modelSpace.saveOrUpdate(policy);
			}
		});

		final MPolicy updatedPolicy = readOnlyTransactionTemplate
				.execute(new TransactionCallback<MPolicy>() {

					@Override
					public MPolicy doInTransaction(
							final TransactionStatus status) {
						return modelSpace.get(MPolicy.class, policy.getId());
					}
				});

		final ConditionLevel updatedPolicyCriticalLevel = ((MContinuousThresholdFallCondition) updatedPolicy
				.getCondition()).getCriticalLevel();
		assertConditionLevel(newCriticalLevel, updatedPolicyCriticalLevel);

		final ConditionLevel updatedPolicyWarningLevel = ((MContinuousThresholdFallCondition) updatedPolicy
				.getCondition()).getWarningLevel();
		assertConditionLevel(newWarningLevel, updatedPolicyWarningLevel);
	}

	@Test
	@Transactional
	public void testUpdatePolicyConfigurationByUser()
			throws InterruptedException {
		final List<MUser> users = modelSpace.find(MUser.class, null);
		final MUser user1 = users.get(0);
		final MUser user2 = users.get(1);
		for (final MPolicy p : Arrays.asList(policy, otherPolicy)) {
			final MPolicyActionWithContacts action = new MPolicySendEmail();
			action.setContacts(Arrays.<MContactInformation> asList(user1));
			p.getActions().add(action);
			modelSpace.save(p);
		}
		final List<MPolicy> policiesUser1 = policyConfigurationService
				.getPoliciesByUser(user1);
		Assert.assertEquals(policiesUser1.size(), 2);
		Assert.assertEquals(policiesUser1.contains(policy), true);
		Assert.assertEquals(policiesUser1.contains(otherPolicy), true);
		final List<MPolicy> policiesUser2 = policyConfigurationService
				.getPoliciesByUser(user2);
		Assert.assertEquals(policiesUser2.size(), 0);
	}

	@Test
	@Transactional
	public void testUpdatePolicyWithNotTheSameActionsAsInActionsTemplate() {
		final List<MPolicyActionWithContacts> newActions = new ArrayList<>();
		final List<MPolicyActionWithContacts> templateActions = new ArrayList<>();
		templateActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_2));
		templateActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_3));

		newActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_1));
		newActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_2));
		updateActionsOfPolicyWithAppliedTemplate(templateActions, newActions);
	}

	@Test
	@Transactional
	public void testUpdatePolicyWithTheSameActionsAsInActionsTemplate() {
		final List<MPolicyActionWithContacts> newActions = new ArrayList<>();
		final List<MPolicyActionWithContacts> templateActions = new ArrayList<>();
		templateActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_2));
		templateActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_3));

		newActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_2));
		newActions.add(SharedModelConfiguration
				.createPolicySendEmailAction(SEND_ACTION_NAME_3));
		updateActionsOfPolicyWithAppliedTemplate(templateActions, newActions);
	}

	private void updateActionsOfPolicyWithAppliedTemplate(
			final List<MPolicyActionWithContacts> templateActions,
			final List<MPolicyActionWithContacts> newActions) {

		modelSpace.save(SharedModelConfiguration.createPolicyActionsTemplate(
				POLICY_ACTIONS_TEMPLATE_NAME, templateActions));
		policyConfigurationService.applyPolicyActionsTemplate(
				POLICY_ACTIONS_TEMPLATE_NAME,
				SimpleUtils.getKeys(Arrays.asList(policy)));

		final MPolicy policyToUpdate = modelSpace.get(MPolicy.class,
				policy.getId());
		final List<MPolicyActionWithContacts> oldActions = policyToUpdate
				.getActionsWithContacts();

		final List<MPolicyAction> actionsToUpdate = new ArrayList<>();
		actionsToUpdate.addAll(policy.getSendAlertActions());
		actionsToUpdate.addAll(newActions);
		policyToUpdate.setActions(actionsToUpdate);
		policyConfigurationService.saveOrUpdatePolicy(policyToUpdate);

		final MPolicy updatedPolicy = modelSpace.get(MPolicy.class,
				policy.getId());

		if (!PolicyUtils.arePolicyActionsEqual(oldActions, newActions)) {
			Assert.assertNull(updatedPolicy.getActionsTemplate());
		} else {
			Assert.assertEquals(POLICY_ACTIONS_TEMPLATE_NAME, updatedPolicy
					.getActionsTemplate().getName());
		}
		final List<MPolicyAction> updatedActions = updatedPolicy.getActions();
		Assert.assertEquals(updatedActions.size(), actionsToUpdate.size());
		final List<String> updatedPolicyActionNames = new ArrayList<>();
		for (final MPolicyAction action : updatedActions) {
			updatedPolicyActionNames.add(action.getName());
		}

		for (final MPolicyAction actionToUpdate : actionsToUpdate) {
			Assert.assertTrue(updatedPolicyActionNames.contains(actionToUpdate
					.getName()));
		}
	}

}

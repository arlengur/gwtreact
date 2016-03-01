/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.*;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.PolicyUtils;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;
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
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * @author novohatskiy.r
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
public class PolicyComponentTemplateServiceTest {

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private PolicyComponentTemplateService policyComponentTemplateService;

	@Autowired
	private PolicyConfigurationService policyConfigurationService;

	@Autowired
	private InternalTaskService taskService;

	private MPolicyActionsTemplate actionsTemplate;

	private MPolicyConditionsTemplate conditionsTemplate;

	private MPolicy policy;

	private Set<String> applyActionsTemplateToPolicies() {
		final Set<String> policyKeys = new HashSet<String>();
		policyKeys.add(policy.getKey());

		policyConfigurationService.applyPolicyActionsTemplate(
				actionsTemplate.getName(), policyKeys);

		final List<MPolicy> foundPoliciesWithActionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithActionsTemplateReference) {
			Assert.assertNotNull(foundPolicy.getActionsTemplate());
		}
		return policyKeys;
	}

	private MPolicy createPolicy() {
		final MAgent agent = SharedModelConfiguration
				.createLightWeightAgent("testAgentName");
		modelSpace.save(agent);

		final MAgentModule module = SharedModelConfiguration
				.createAgentModule(agent);
		modelSpace.save(module);

		final MAgentTask task = SharedModelConfiguration
				.createAgentTask(module);
		taskService.updateTaskConfiguration(task);
		task.getResultConfiguration().setParameterConfigurations(
				SharedModelConfiguration
						.createResultParameterConfigurations(null));
		modelSpace.save(task);

		final MPolicy policy = SharedModelConfiguration.createPolicy(task,
				"policyActionName");
		modelSpace.save(policy);
		return policy;
	}

	private MPolicyActionsTemplate createPolicyActionsTemplate(
			final String name, final String... actionNames) {
		final List<MPolicyActionWithContacts> actions = new ArrayList<>();
		for (final String actionName : actionNames) {
			actions.add(SharedModelConfiguration
					.createPolicySendEmailAction(actionName));
		}
		return SharedModelConfiguration.createPolicyActionsTemplate(name,
				actions);
	}

	private MPolicyConditionsTemplate createPolicyConditionsTemplate(
			final String name, final ParameterIdentifier parameterIdentifier) {
		final MPolicyConditionsTemplate template = new MPolicyConditionsTemplate();
		template.setName(name);
		template.setParameterType(ParameterType.LEVEL);
		template.setConditionLevels(SharedModelConfiguration
				.createPolicyCondition(parameterIdentifier));

		return template;
	}

	@Before
	public void setUp() {
		actionsTemplate = createPolicyActionsTemplate("Actions template",
				"sendEmail1", "sendEmail2");
		conditionsTemplate = createPolicyConditionsTemplate(
				"Conditions template", new ParameterIdentifier(
						SharedModelConfiguration.SIGNAL_LEVEL, null));
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				policy = createPolicy();
				modelSpace.saveOrUpdate(actionsTemplate);
				modelSpace.saveOrUpdate(conditionsTemplate);
			}
		});
	}

	@Test
	@Transactional
	public void testCreateNewActionsTemplates() {
		final String templateName = "newActionsTemplate";
		final MPolicyActionsTemplate newTemplate = createPolicyActionsTemplate(
				templateName, "newSendEmail1", "newSendEmail2");

		final MPolicyActionsTemplate updatedTemplate = policyComponentTemplateService
				.saveOrUpdateTemplate(newTemplate, false);
		Assert.assertNotNull(updatedTemplate);
		Assert.assertEquals(templateName, updatedTemplate.getName());
		Assert.assertEquals(2, updatedTemplate.getActions().size());

		final MPolicyActionsTemplate foundTemplate = modelSpace.get(
				MPolicyActionsTemplate.class, updatedTemplate.getId());
		Assert.assertNotNull(foundTemplate);
		Assert.assertEquals(templateName, foundTemplate.getName());
		Assert.assertEquals(2, foundTemplate.getActions().size());
	}

	@Test
	@Transactional
	public void testCreateNewConditionsTemplates() {
		final String templateName = "newConditionsTemplate";
		final ParameterIdentifier parameterIdentifier = new ParameterIdentifier(
				SharedModelConfiguration.NICAM_LEVEL, null);
		final MPolicyConditionsTemplate newTemplate = createPolicyConditionsTemplate(
				templateName, parameterIdentifier);

		final MPolicyConditionsTemplate updatedTemplate = policyComponentTemplateService
				.saveOrUpdateTemplate(newTemplate, false);
		Assert.assertNotNull(updatedTemplate);
		Assert.assertEquals(templateName, updatedTemplate.getName());
		Assert.assertNotNull(updatedTemplate.getConditionLevels());
		Assert.assertEquals(parameterIdentifier,
				((MContinuousThresholdFallCondition) updatedTemplate
						.getConditionLevels()).getParameterIdentifier());

		final MPolicyConditionsTemplate foundTemplate = modelSpace.get(
				MPolicyConditionsTemplate.class, updatedTemplate.getId());
		Assert.assertNotNull(foundTemplate);
		Assert.assertEquals(templateName, foundTemplate.getName());
		Assert.assertNotNull(foundTemplate.getConditionLevels());
		Assert.assertEquals(parameterIdentifier,
				((MContinuousThresholdFallCondition) foundTemplate
						.getConditionLevels()).getParameterIdentifier());
	}

	@Test
	@Transactional
	public void testDoesActionsTemplateExist() {
		Assert.assertTrue(policyComponentTemplateService.doesTemplateExist(
				actionsTemplate.getName(), actionsTemplate.getClass().getName()));
		Assert.assertFalse(policyComponentTemplateService.doesTemplateExist(
				"fakeTemplate", MPolicyActionsTemplate.class.getName()));
		Assert.assertFalse(policyComponentTemplateService.doesTemplateExist(
				actionsTemplate.getName(),
				MPolicyConditionsTemplate.class.getName()));
	}

	@Test
	@Transactional
	public void testDoesConditionsTemplateExist() {
		Assert.assertTrue(policyComponentTemplateService.doesTemplateExist(
				conditionsTemplate.getName(), conditionsTemplate.getClass()
						.getName()));
		Assert.assertFalse(policyComponentTemplateService.doesTemplateExist(
				"fakeTemplate", MPolicyConditionsTemplate.class.getName()));
	}

	@Test
	@Transactional
	public void testGetActionsTemplateByName() {
		Assert.assertNotNull(policyComponentTemplateService.getTemplateByName(
				actionsTemplate.getName(), actionsTemplate.getClass().getName()));
		Assert.assertNull(policyComponentTemplateService.getTemplateByName(
				"fakeTemplate", MPolicyActionsTemplate.class.getName()));
	}

	@Test
	@Transactional
	public void testGetAllPolicyActionsTemlates() {
		final Collection<MPolicyActionsTemplate> allTemplates = policyComponentTemplateService
				.getAllActionsTemplates();

		Assert.assertNotNull(allTemplates);
		Assert.assertEquals(modelSpace.getAll(MPolicyActionsTemplate.class)
				.size(), allTemplates.size());
	}

	@Test
	@Transactional
	public void testGetAllPolicyConditionsTemlates() {
		final Collection<MPolicyConditionsTemplate> allTemplates = policyComponentTemplateService
				.getAllConditionsTemplates();

		Assert.assertNotNull(allTemplates);
		Assert.assertEquals(modelSpace.getAll(MPolicyConditionsTemplate.class)
				.size(), allTemplates.size());
	}

	@Test
	@Transactional
	public void testGetConditionsTemplateByName() {
		Assert.assertNotNull(policyComponentTemplateService.getTemplateByName(
				conditionsTemplate.getName(), conditionsTemplate.getClass()
						.getName()));
		Assert.assertNull(policyComponentTemplateService.getTemplateByName(
				"fakeTemplate", MPolicyConditionsTemplate.class.getName()));
	}
	@Test
	@Transactional
	public void testGetTemplateByName() {
		Assert.assertNotNull(policyComponentTemplateService.getTemplateByName(
				actionsTemplate.getName(), actionsTemplate.getClass().getName()));
		Assert.assertNull(policyComponentTemplateService.getTemplateByName(
				"fakeTemplate", MPolicyActionsTemplate.class.getName()));
	}

	@Test
	@Transactional
	public void testRemoveActionsTemplate() {
		final Set<String> policyKeys = new HashSet<String>();
		policyKeys.add(policy.getKey());
		policyConfigurationService.applyPolicyActionsTemplate(
				actionsTemplate.getName(), policyKeys);

		final List<MPolicy> foundPoliciesWithActionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithActionsTemplateReference) {
			Assert.assertNotNull(foundPolicy.getActionsTemplate());
		}

		final Collection<MPolicyActionsTemplate> previousAllTemplates = policyComponentTemplateService
				.getAllActionsTemplates();

		policyComponentTemplateService.removeTemplate(
				actionsTemplate.getName(),
				MPolicyActionsTemplate.class.getName());

		final List<MPolicy> foundPoliciesWithoutActionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithoutActionsTemplateReference) {
			Assert.assertNull(foundPolicy.getActionsTemplate());
		}

		final Collection<MPolicyActionsTemplate> currentAllTemplates = policyComponentTemplateService
				.getAllActionsTemplates();

		Assert.assertEquals(previousAllTemplates.size() - 1,
				currentAllTemplates.size());
		Assert.assertNull(policyComponentTemplateService.getTemplateByName(
				actionsTemplate.getName(), actionsTemplate.getClass().getName()));
	}

	@Test
	@Transactional
	public void testRemoveConditionsTemplate() {
		final Set<String> policyKeys = SimpleUtils.getKeys(Arrays
				.asList(policy));
		policyConfigurationService.applyPolicyConditionsTemplate(
				conditionsTemplate.getName(), policyKeys);

		final List<MPolicy> foundPoliciesWithConditionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithConditionsTemplateReference) {
			Assert.assertNotNull(foundPolicy.getConditionsTemplate());
		}

		final Collection<MPolicyConditionsTemplate> previousAllTemplates = policyComponentTemplateService
				.getAllConditionsTemplates();

		policyComponentTemplateService.removeTemplate(conditionsTemplate
				.getName(), conditionsTemplate.getClass().getName());

		final List<MPolicy> foundPoliciesWithoutConditionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithoutConditionsTemplateReference) {
			Assert.assertNull(foundPolicy.getConditionsTemplate());
		}

		final Collection<MPolicyConditionsTemplate> currentAllTemplates = policyComponentTemplateService
				.getAllConditionsTemplates();

		Assert.assertEquals(previousAllTemplates.size() - 1,
				currentAllTemplates.size());
		Assert.assertNull(policyComponentTemplateService.getTemplateByName(
				conditionsTemplate.getName(), conditionsTemplate.getClass()
						.getName()));
	}

	@Test
	@Transactional
	public void testRemoveTemplates() {
		policyComponentTemplateService.removeTemplates(
				Arrays.<String> asList(actionsTemplate.getName()),
				actionsTemplate.getClass().getName());

		final Collection<MPolicyComponentTemplate> allTemplates = new ArrayList<MPolicyComponentTemplate>();

		allTemplates.addAll(policyComponentTemplateService
				.getAllActionsTemplates());

		for (final MPolicyComponentTemplate template : allTemplates) {
			Assert.assertFalse(template.getName().equals(
					actionsTemplate.getName()));
		}

		Assert.assertNull(policyComponentTemplateService.getTemplateByName(
				actionsTemplate.getName(), actionsTemplate.getClass().getName()));
	}

	@Test
	public void testUpdateAndDetachConditionsTemplate() {
		final Set<String> policyKeys = SimpleUtils.getKeys(Arrays
				.asList(policy));

		policyConfigurationService.applyPolicyConditionsTemplate(
				conditionsTemplate.getName(), policyKeys);

		final List<MPolicy> foundPoliciesWithConditionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithConditionsTemplateReference) {
			Assert.assertNotNull(foundPolicy.getConditionsTemplate());
		}

		final ThresholdType newThresholdType = ThresholdType.GREATER;
		Assert.assertFalse(conditionsTemplate.getConditionLevels()
				.getThresholdType().equals(newThresholdType));
		conditionsTemplate.getConditionLevels().setThresholdType(
				newThresholdType);

		policyComponentTemplateService.saveOrUpdateTemplate(conditionsTemplate,
				false);

		final List<MPolicy> foundPoliciesWithoutConditionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithoutConditionsTemplateReference) {
			Assert.assertNull(foundPolicy.getConditionsTemplate());
		}
	}

	@Test
	public void testUpdateAndDetachTemplateActions() {
		final Set<String> policyKeys = applyActionsTemplateToPolicies();

		actionsTemplate.getActions().get(0).setName("newActionName");
		actionsTemplate.getActions().add(
				SharedModelConfiguration
						.createPolicySendEmailAction("newCreatedActionName2"));

		policyComponentTemplateService.saveOrUpdateTemplate(actionsTemplate,
				false);

		final List<MPolicy> foundPoliciesWithoutActionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithoutActionsTemplateReference) {
			Assert.assertNull(foundPolicy.getActionsTemplate());
		}
	}

	@Test
	public void testUpdateAndReapplyActionsTemplate() {
		final Set<String> policyKeys = applyActionsTemplateToPolicies();

		actionsTemplate.getActions().remove(0);
		actionsTemplate.getActions().get(0).setName("newActionName");
		actionsTemplate.getActions().add(
				SharedModelConfiguration
						.createPolicySendEmailAction("newCreatedActionName"));

		policyComponentTemplateService.saveOrUpdateTemplate(actionsTemplate,
				true);

		final List<MPolicy> updatePolicies = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : updatePolicies) {
			Assert.assertEquals(foundPolicy.getActionsWithContacts().size(),
					actionsTemplate.getActions().size());
		}
	}

	@Test
	public void testUpdateAndReapplyConditionsTemplate() {
		final Set<String> policyKeys = SimpleUtils.getKeys(Arrays
				.asList(policy));

		policyConfigurationService.applyPolicyConditionsTemplate(
				conditionsTemplate.getName(), policyKeys);

		final MPolicyConditionLevels newConditionLevels = SharedModelConfiguration
				.createPolicyConditionLevels();
		final ThresholdType newThresholdType = ThresholdType.GREATER;
		Assert.assertFalse(newConditionLevels.getThresholdType().equals(
				newThresholdType));
		newConditionLevels.setThresholdType(newThresholdType);

		final List<MPolicy> foundPoliciesWithConditionsTemplateReference = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy foundPolicy : foundPoliciesWithConditionsTemplateReference) {
			Assert.assertNotNull(foundPolicy.getConditionsTemplate());
			Assert.assertEquals(conditionsTemplate.getName(), foundPolicy
					.getConditionsTemplate().getName());

			final MPolicyCondition condition = foundPolicy.getCondition();
			if (condition instanceof MPolicyConditionLevels) {
				Assert.assertFalse(PolicyUtils.arePolicyConditionLevelsEqual(
						newConditionLevels, (MPolicyConditionLevels) condition));
			} else {
				Assert.fail("Not tested PolicyCondition class: "
						+ condition.getClass().getSimpleName());
			}
		}

		conditionsTemplate.setConditionLevels(newConditionLevels);

		policyComponentTemplateService.saveOrUpdateTemplate(conditionsTemplate,
				true);

		final List<MPolicy> updatePolicies = policyConfigurationService
				.getPolicies(
						CriterionQueryFactory.getQuery().in("key", policyKeys),
						null, null, null, null);
		for (final MPolicy updatedPolicy : updatePolicies) {
			Assert.assertNotNull(updatedPolicy.getConditionsTemplate());
			Assert.assertEquals(conditionsTemplate.getName(), updatedPolicy
					.getConditionsTemplate().getName());

			final MPolicyCondition condition = updatedPolicy.getCondition();
			if (condition instanceof MPolicyConditionLevels) {
				Assert.assertTrue(PolicyUtils.arePolicyConditionLevelsEqual(
						newConditionLevels, (MPolicyConditionLevels) condition));

			} else {
				Assert.fail("Not tested PolicyCondition class: "
						+ condition.getClass().getSimpleName());
			}
		}
	}

	@Test
	@Transactional
	public void testUpdateExistingPolicyActionsTemplate() {
		final MPolicyActionsTemplate existingTemplate = modelSpace
				.findUniqueEntity(
						MPolicyActionsTemplate.class,
						modelSpace.createCriterionQuery().eq("name",
								actionsTemplate.getName()));
		Assert.assertNotNull(existingTemplate);

		final String templateName = "newNameOfTheExistingTemplate";
		final int previousActionsSize = existingTemplate.getActions().size();

		existingTemplate.getActions().remove(0);
		existingTemplate.setName(templateName);

		final MPolicyActionsTemplate updatedTemplate = policyComponentTemplateService
				.saveOrUpdateTemplate(existingTemplate, false);
		Assert.assertNotNull(updatedTemplate);
		Assert.assertEquals(templateName, updatedTemplate.getName());
		Assert.assertEquals(previousActionsSize - 1, updatedTemplate
				.getActions().size());

		final MPolicyActionsTemplate foundTemplate = modelSpace
				.findUniqueEntity(MPolicyActionsTemplate.class, modelSpace
						.createCriterionQuery().eq("name", templateName));
		Assert.assertNotNull(foundTemplate);
		Assert.assertEquals(templateName, foundTemplate.getName());
		Assert.assertEquals(previousActionsSize - 1, foundTemplate.getActions()
				.size());
	}

	@Test
	@Transactional
	public void testUpdateExistingPolicyConditionsTemplate() {
		final MPolicyConditionsTemplate existingTemplate = modelSpace
				.findUniqueEntity(
						MPolicyConditionsTemplate.class,
						modelSpace.createCriterionQuery().eq("name",
								conditionsTemplate.getName()));
		Assert.assertNotNull(existingTemplate);

		final MPolicyConditionLevels conditionLevels = existingTemplate
				.getConditionLevels();
		final Long newWarningCeaseDuration = 1000l;
		conditionLevels.getWarningLevel().setCeaseDuration(
				newWarningCeaseDuration);
		final String newCriticalRaiseLevel = "100";
		conditionLevels.getCriticalLevel().setRaiseLevel(newCriticalRaiseLevel);
		final ThresholdType newThresholdType = ThresholdType.GREATER_OR_EQUALS;
		conditionLevels.setThresholdType(newThresholdType);
		final String templateName = "newTemplateName";
		existingTemplate.setName(templateName);

		final MPolicyConditionsTemplate updatedTemplate = policyComponentTemplateService
				.saveOrUpdateTemplate(existingTemplate, false);
		Assert.assertNotNull(updatedTemplate);
		Assert.assertEquals(templateName, updatedTemplate.getName());
		Assert.assertEquals(newWarningCeaseDuration, updatedTemplate
				.getConditionLevels().getWarningLevel().getCeaseDuration());
		Assert.assertEquals(newCriticalRaiseLevel, updatedTemplate
				.getConditionLevels().getCriticalLevel().getRaiseLevel());
		Assert.assertEquals(newThresholdType, updatedTemplate
				.getConditionLevels().getThresholdType());

		final MPolicyConditionsTemplate foundTemplate = modelSpace
				.findUniqueEntity(MPolicyConditionsTemplate.class, modelSpace
						.createCriterionQuery().eq("name", templateName));
		Assert.assertNotNull(foundTemplate);
		Assert.assertEquals(templateName, foundTemplate.getName());
		Assert.assertEquals(newWarningCeaseDuration, foundTemplate
				.getConditionLevels().getWarningLevel().getCeaseDuration());
		Assert.assertEquals(newCriticalRaiseLevel, foundTemplate
				.getConditionLevels().getCriticalLevel().getRaiseLevel());
		Assert.assertEquals(newThresholdType, foundTemplate
				.getConditionLevels().getThresholdType());
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserAlertsTemplate;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.EventBroadcastDispatcher;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.AgentService;
import com.tecomgroup.qos.service.DefaultAgentService;
import com.tecomgroup.qos.service.DefaultTaskService;
import com.tecomgroup.qos.service.InternalPolicyConfigurationService;
import com.tecomgroup.qos.service.PropertyUpdater;
import com.tecomgroup.qos.service.SpringUserService;
import com.tecomgroup.qos.service.StorageService;
import com.tecomgroup.qos.service.InternalEventBroadcaster;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@Transactional
@SuppressWarnings("unchecked")
public abstract class TemplateTest {

	@Autowired
	protected InternalPolicyConfigurationService policyConfigurationService;

	protected EventBroadcastDispatcher eventBroadcastDispatcher;

	protected InternalEventBroadcaster internalEventBroadcaster;

	protected PropertyUpdater propertyUpdater;

	protected StorageService storageService;

	protected DefaultTaskService taskService;

	protected AgentService agentService;

	protected String agentName;

	protected String moduleName;

	protected MAgentTask taskX;
	protected MAgentTask taskY;

	protected MAgent agent;
	protected MAgentModule module;
	protected MAgentTask task;

	@Autowired
	protected ModelSpace modelSpace;

	@Autowired
	protected TransactionTemplate transactionTemplate;

	@Autowired
	protected TransactionTemplate readOnlyTransactionTemplate;

	protected SpringUserService userService;

	protected MUser user;

	@Before
	public void before() throws Exception {
		init();
	}

	protected void expectTemplate(final TemplateKeeper keeper) {
		Map<String, TemplateKeeper> keepers = null;

		if (keeper != null && keeper.hasActualOrNew()) {
			final String name = keeper.getNew().getTemplate().getName();
			keepers = new HashMap<String, TemplateKeeper>();
			keepers.put(name, keeper);
		}

		expectTemplates(keepers);
	}

	protected void expectTemplates(final Map<String, TemplateKeeper> keepers) {
		if (hasChilds()) {
			expectTemplatesWithChilds(keepers);
		} else {
			expectTemplatesWithoutChilds(keepers);
		}
	}

	protected void expectTemplatesAreDistinct(final TemplateKeeper keeper) {
		final MUserAlertsTemplate oldTemplate = (MUserAlertsTemplate) keeper
				.getOld().get(0).getTemplate();
		final MUserAlertsTemplate actualTemplate = (MUserAlertsTemplate) keeper
				.getActual().getTemplate();
		final Long idX = oldTemplate.getId();
		final Long idY = actualTemplate.getId();

		Assert.assertTrue(idX != idY);
	}

	protected void expectTemplatesWithChilds(
			final Map<String, TemplateKeeper> keepers) {
		final List<MUserAbstractTemplate> actualTemplates = (List<MUserAbstractTemplate>) userService
				.getTemplates(getTemplateType(), user.getId());
		int expectedTemplatesCount = 0;
		int actualAllChildsCount = 0;
		final List<?> allChilds = modelSpace.getAll(getChildrenType());

		if (keepers != null) {
			for (final Map.Entry<String, TemplateKeeper> keeperEntry : keepers
					.entrySet()) {
				final TemplateKeeper keeper = keeperEntry.getValue();
				if (keeper.hasActualOrNew()) {
					expectedTemplatesCount++;
				}
			}
		}

		if (expectedTemplatesCount > 0) {
			Assert.assertEquals(expectedTemplatesCount, actualTemplates.size());
			for (final MUserAbstractTemplate actualTemplate : actualTemplates) {
				final TemplateKeeper keeper = keepers.get(actualTemplate
						.getName());
				final TemplateSnapshot snapshot = keeper.getActualOrNew();
				final MUserAbstractTemplate expectedTemplate = snapshot
						.getTemplate();

				final Collection<MAbstractEntity> expectedChilds = getChilds(expectedTemplate);

				final Collection<MAbstractEntity> actualChilds = getChilds(actualTemplate);

				int expectedChildsCountOfTemplate = 0;
				int actualChildsCountOfTemplate = 0;

				if (actualChilds != null) {
					actualChildsCountOfTemplate = actualChilds.size();
				}

				if (expectedChilds != null) {
					expectedChildsCountOfTemplate = expectedChilds.size();
				}

				if (expectedChildsCountOfTemplate > 0) {
					Assert.assertEquals(expectedChildsCountOfTemplate,
							actualChildsCountOfTemplate);
				} else {
					Assert.assertTrue(actualChilds == null
							|| actualChilds.isEmpty());
				}

				if (snapshot.isNew()) {
					keeper.actualize(snapshot, actualTemplate);
				}

				if (keeper.getSnapshots().size() > 1) {
					expectTemplatesAreDistinct(keeper);
				}

				actualAllChildsCount += actualChildsCountOfTemplate;
			}
			Assert.assertEquals(actualAllChildsCount, allChilds.size());
		} else {
			Assert.assertTrue(actualTemplates == null
					|| actualTemplates.isEmpty());
			Assert.assertEquals(0, allChilds.size());
		}
	}

	protected void expectTemplatesWithoutChilds(
			final Map<String, TemplateKeeper> keepers) {
		int expectedTemplatesCount = 0;
		final List<MUserAbstractTemplate> actualTemplates = (List<MUserAbstractTemplate>) userService
				.getTemplates(getTemplateType(), user.getId());

		if (keepers != null) {
			for (final Map.Entry<String, TemplateKeeper> keeperEntry : keepers
					.entrySet()) {
				final TemplateKeeper keeper = keeperEntry.getValue();
				if (keeper.hasActualOrNew()) {
					expectedTemplatesCount++;
				}
			}
		}

		if (expectedTemplatesCount > 0) {
			Assert.assertEquals(expectedTemplatesCount, actualTemplates.size());

			for (final MUserAbstractTemplate template : actualTemplates) {
				final TemplateKeeper keeper = keepers.get(template.getName());
				final TemplateSnapshot snapshot = keeper.getActualOrNew();

				if (snapshot.isNew()) {
					keeper.actualize(snapshot, template);
				}
			}
		} else {
			Assert.assertTrue(actualTemplates == null
					|| actualTemplates.isEmpty());
		}
	}

	protected Class<? extends MAbstractEntity> getChildrenType() {
		return null;
	}

	protected Collection<MAbstractEntity> getChilds(
			final MUserAbstractTemplate template) {
		return null;
	}

	protected abstract MUserAbstractTemplate getNewTemplate(
			final String templateName);

	protected abstract TemplateType getTemplateType();

	protected boolean hasChilds() {
		return false;
	}

	protected void init() throws Exception {
		initAgent();
		initModule();
		initTasks();
		initMocks();
		initUserService();
		initTaskService();
		initAgentService();
		initDependencies();
		user = userService.findUser("User");
	}

	protected void initAgent() {
		agent = SharedModelConfiguration.createLightWeightAgent("test-agent");
		modelSpace.save(agent);
		agentName = agent.getKey();
	}

	protected void initAgentService() {
		final DefaultAgentService defaultAgentService = new DefaultAgentService();

		defaultAgentService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		defaultAgentService.setTransactionTemplate(transactionTemplate);
		defaultAgentService
				.setPolicyConfigurationService(policyConfigurationService);
		defaultAgentService
				.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		defaultAgentService
				.setInternalEventBroadcaster(internalEventBroadcaster);
		defaultAgentService.setModelSpace(modelSpace);

		agentService = defaultAgentService;
	}

	protected void initDependencies() {
		((DefaultAgentService) agentService).setTaskService(taskService);
		taskService.setAgentService(agentService);
	}

	protected void initMocks() {
		eventBroadcastDispatcher = EasyMock.createMock(EventBroadcastDispatcher.class);
		internalEventBroadcaster = EasyMock.createMock(InternalEventBroadcaster.class);
		propertyUpdater = EasyMock.createMock(PropertyUpdater.class);
		storageService = EasyMock.createMock(StorageService.class);
	}

	protected void initModule() {
		module = SharedModelConfiguration.createAgentModule(agent);
		modelSpace.save(module);
		moduleName = module.getKey();
	}

	protected void initTasks() {
		task = SharedModelConfiguration.createAgentTask(module);
		modelSpace.save(task);

		taskX = SharedModelConfiguration.createAgentTask(module, "taskX");
		modelSpace.save(taskX);

		taskY = SharedModelConfiguration.createAgentTask(module, "taskY");
		modelSpace.save(taskY);
	}

	protected void initTaskService() {
		taskService = new DefaultTaskService();
		taskService.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		taskService.setTransactionTemplate(transactionTemplate);
		taskService.setPropertyUpdater(propertyUpdater);
		taskService.setModelSpace(modelSpace);
		taskService.setPolicyConfigurationService(policyConfigurationService);
		taskService.setTemplateDeleter(userService);
		taskService.setStorageService(storageService);
		taskService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
	}

	protected void initUserService() throws Exception {
		userService = new SpringUserService();
		userService.setModelSpace(modelSpace);
		userService.setTransactionTemplate(transactionTemplate);
		userService.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		userService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		userService.afterPropertiesSet();
	}

	protected void removeTemplate(final TemplateKeeper keeper) {
		final String templateName = keeper.getActual().getTemplate().getName();

		userService.removeTemplate(getTemplateType(), user.getId(),
				templateName);
		keeper.unactualize();
	}

	protected void removeTemplates(final Map<String, TemplateKeeper> keepers) {
		final TemplateType templateType = getTemplateType();
		if (keepers != null && templateType != null) {
			for (final Map.Entry<String, TemplateKeeper> keeperEntry : keepers
					.entrySet()) {
				final TemplateKeeper keeper = keeperEntry.getValue();
				final MUserAbstractTemplate template = keeper.getActual()
						.getTemplate();
				userService.removeTemplate(templateType, user.getId(),
						template.getName());
			}
		}
	}

	protected void saveTemplate(final TemplateKeeper keeper) {
		final TemplateSnapshot snapshot = keeper.getNew();
		if (snapshot != null) {
			final MUserAbstractTemplate template = snapshot.getTemplate();
			userService.saveTemplate(template);
		}

	}

	protected void saveTemplates(final Map<String, TemplateKeeper> keepers) {
		if (keepers != null) {
			for (final Map.Entry<String, TemplateKeeper> keeperEntry : keepers
					.entrySet()) {
				final TemplateKeeper keeper = keeperEntry.getValue();
				final MUserAbstractTemplate template = keeper.getNew()
						.getTemplate();
				userService.saveTemplate(template);
			}
		}
	}

	protected void testSaveTemplateFromExistToExist(final String nameX,
			final String nameY) {
		final MUserAbstractTemplate templateX = getNewTemplate(nameX);
		final MUserAbstractTemplate templateY = getNewTemplate(nameY);
		final TemplateKeeper keeperX = new TemplateKeeper(templateX);
		final TemplateKeeper keeperY = new TemplateKeeper(templateY);
		final Map<String, TemplateKeeper> keepers = new HashMap<String, TemplateKeeper>();

		keepers.put(nameX, keeperX);
		keepers.put(nameY, keeperY);
		saveTemplates(keepers);
		expectTemplates(keepers);
		updateFromTemplate(templateY, keeperX.getActual().getTemplate());
		keeperY.stage(templateY);

		saveTemplate(keeperY);
		expectTemplates(keepers);

		removeTemplates(keepers);
		expectTemplates(null);
	}

	protected void testSaveTemplateFromExistToNew(final String nameX,
			final String nameY) {
		final MUserAbstractTemplate templateX = getNewTemplate(nameX);
		final TemplateKeeper keeperX = new TemplateKeeper(templateX);
		final Map<String, TemplateKeeper> keepers = new HashMap<String, TemplateKeeper>();
		keepers.put(nameX, keeperX);

		saveTemplate(keeperX);
		expectTemplate(keeperX);

		final MUserAbstractTemplate templateY = getNewTemplate(nameY);
		updateFromTemplate(templateY, keeperX.getActual().getTemplate());
		final TemplateKeeper keeperY = new TemplateKeeper(templateY);
		keepers.put(nameY, keeperY);

		saveTemplate(keeperY);
		expectTemplates(keepers);

		removeTemplates(keepers);
		expectTemplates(null);
	}

	protected void testTemplate(final String templateName) {
		final MUserAbstractTemplate template = getNewTemplate(templateName);
		final TemplateKeeper keeper = new TemplateKeeper(template);

		saveTemplate(keeper);
		expectTemplate(keeper);

		removeTemplate(keeper);
		expectTemplate(null);
	}

	protected void testTemplateUpdate(final String templateName) {
		final MUserAbstractTemplate template = getNewTemplate(templateName);
		final TemplateKeeper keeper = new TemplateKeeper(template);

		saveTemplate(keeper);
		expectTemplate(keeper);

		updateTemplate(template);
		keeper.stage(template);

		saveTemplate(keeper);
		expectTemplate(keeper);

		removeTemplate(keeper);
		expectTemplate(null);
	}

	protected abstract void updateFromTemplate(
			MUserAbstractTemplate destination, MUserAbstractTemplate source);

	protected abstract void updateTemplate(MUserAbstractTemplate template);
}

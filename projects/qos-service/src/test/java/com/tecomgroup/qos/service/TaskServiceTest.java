/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MProperty.PropertyType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.exception.DeletedSourceException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.EventBroadcastDispatcher;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
@Transactional
public class TaskServiceTest {

	private DefaultTaskService taskService;

	private AgentService agentService;

	private InternalPolicyConfigurationService policyConfigurationService;

	private StorageService storageService;

	private PropertyUpdater propertyUpdater;

	private EventBroadcastDispatcher eventBroadcastDispatcher;

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TransactionTemplate readOnlyTransactionTemplate;

	private MAgent agent;
	private MAgentModule module;
	private MAgentTask task;

	private String agentName = "FirstChannel-NN";
	private String moduleName = "IT09AControlModule";

	private final List<MPolicy> policyEmptyList = Collections
			.unmodifiableList(new ArrayList<MPolicy>());

	private void createTaskParameterConfigurations() {
		task.setResultConfiguration(ConfigurationUtil.createFromTemplate(task
				.getModule().getTemplateResultConfiguration(), task));

		final List<MResultParameterConfiguration> taskParameterConfigurations = new ArrayList<MResultParameterConfiguration>();
		for (final MResultParameterConfiguration parameterConfiguration : module
				.getTemplateResultConfiguration().getParameterConfigurations()) {
			taskParameterConfigurations.add(new MResultParameterConfiguration(
					parameterConfiguration));
		}
		task.getResultConfiguration().setParameterConfigurations(
				taskParameterConfigurations);

		modelSpace.saveOrUpdate(task);
	}

	@Before
	public void setUp() {
		agent = SharedModelConfiguration.createLightWeightAgent(agentName);
		modelSpace.save(agent);

		module = SharedModelConfiguration.createAgentModule(agent);
		modelSpace.save(module);
		task = SharedModelConfiguration.createAgentTask(module);
		modelSpace.save(task);

		agentName = agent.getKey();
		moduleName = module.getKey();

		agentService = EasyMock.createMock(AgentService.class);
		policyConfigurationService = EasyMock
				.createMock(InternalPolicyConfigurationService.class);
		storageService = EasyMock.createMock(StorageService.class);
		eventBroadcastDispatcher = EasyMock
				.createMock(EventBroadcastDispatcher.class);
		propertyUpdater = EasyMock.createMock(PropertyUpdater.class);
		taskService = new DefaultTaskService();
		taskService.setAgentService(agentService);
		taskService.setPolicyConfigurationService(policyConfigurationService);
		taskService.setStorageService(storageService);
		taskService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		taskService.setModelSpace(modelSpace);
		taskService.setPropertyUpdater(propertyUpdater);
		taskService.setTransactionTemplate(transactionTemplate);
		taskService.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
	}

	@Test
	public void testDeleteTask() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(2);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.deletePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		final Set<String> moduleNames = new HashSet<String>();
		moduleNames.add(moduleName);
		List<MAgentTask> agentTasks = taskService.getAgentTasks(agentName,
				moduleNames, 0, 1000);
		Assert.assertEquals(1, agentTasks.size());
		Assert.assertEquals(task, agentTasks.iterator().next());

		taskService.delete(task);

		agentTasks = taskService.getAgentTasks(agentName, moduleNames, 0, 1000);
		Assert.assertTrue(agentTasks.isEmpty());

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
	}

	@Test
	public void testDeleteTasks() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(2);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.deletePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		final Set<String> moduleNames = new HashSet<String>();
		moduleNames.add(moduleName);
		List<MAgentTask> agentTasks = taskService.getAgentTasks(agentName,
				moduleNames, 0, 1000);
		Assert.assertEquals(1, agentTasks.size());
		Assert.assertEquals(task, agentTasks.iterator().next());

		final Set<String> taskKeys = new HashSet<String>();
		taskKeys.add(task.getKey());
		taskService.deleteTasks(taskKeys);

		agentTasks = taskService.getAgentTasks(agentName, moduleNames, 0, 1000);
		Assert.assertTrue(agentTasks.isEmpty());

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
	}

	@Test
	public void testDisableTask() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(2);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.disablePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		final Set<String> moduleNames = new HashSet<String>();
		moduleNames.add(moduleName);
		List<MAgentTask> agentTasks = taskService.getAgentTasks(agentName,
				moduleNames, 0, 1000);
		Assert.assertEquals(1, agentTasks.size());
		Assert.assertEquals(task, agentTasks.iterator().next());

		taskService.disable(task);

		agentTasks = taskService.getAgentTasks(agentName, moduleNames, 0, 1000);
		Assert.assertTrue(agentTasks.isEmpty());

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
	}

	@Test
	public void testGetAgentTasks() {

		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(1);
		EasyMock.replay(agentService);

		final Set<String> moduleNames = new HashSet<String>();
		moduleNames.add(moduleName);
		final List<MAgentTask> agentTasks = taskService.getAgentTasks(
				agentName, moduleNames, 0, 1000);
		Assert.assertFalse(agentTasks.isEmpty());
		EasyMock.verify(agentService);
	}

	@Test
	public void testGetAgentTasksOnlyWithParameters() {

		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(5);
		EasyMock.replay(agentService);

		final Set<String> moduleNames = new HashSet<String>();
		moduleNames.add(moduleName);
		List<MAgentTask> agentTasks = taskService.getAgentTasks(agentName,
				moduleNames, null, 0, 1000, true);
		Assert.assertTrue(agentTasks.isEmpty());

		createTaskParameterConfigurations();
		agentTasks = taskService.getAgentTasks(agentName, moduleNames, null, 0,
				1000, true);
		Assert.assertFalse(agentTasks.isEmpty());

		task.getResultConfiguration().setParameterConfigurations(
				new ArrayList<MResultParameterConfiguration>());
		modelSpace.saveOrUpdate(task);
		agentTasks = taskService.getAgentTasks(agentName, moduleNames, null, 0,
				1000, true);
		Assert.assertTrue(agentTasks.isEmpty());

		task.getResultConfiguration().setParameterConfigurations(null);
		modelSpace.saveOrUpdate(task);
		agentTasks = taskService.getAgentTasks(agentName, moduleNames, null, 0,
				1000, true);
		Assert.assertTrue(agentTasks.isEmpty());

		module.setTemplateResultConfiguration(null);
		modelSpace.saveOrUpdate(module);

		task.setResultConfiguration(null);
		modelSpace.saveOrUpdate(task);

		agentTasks = taskService.getAgentTasks(agentName, moduleNames, null, 0,
				1000, true);
		Assert.assertTrue(agentTasks.isEmpty());

		EasyMock.verify(agentService);
	}

	@Test
	public void testGetMultipleAgentsTasks() {
		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.disablePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(3);
		EasyMock.replay(agentService);

		List<MAgentTask> agentTasks = taskService.getAgentTasks(
				Arrays.asList(agentName), 0, 1000, true);
		Assert.assertFalse(agentTasks.isEmpty());

		final int initialTasksSize = agentTasks.size();
		Assert.assertEquals(task, agentTasks.iterator().next());

		taskService.disable(task);

		agentTasks = taskService.getAgentTasks(Arrays.asList(agentName), 0,
				1000, true);
		Assert.assertEquals(agentTasks.size(), initialTasksSize - 1);

		agentTasks = taskService.getAgentTasks(Arrays.asList(agentName), 0,
				1000, false);
		Assert.assertEquals(agentTasks.size(), initialTasksSize);

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
	}

	@Test
	public void testGetTasksByKeys() {
		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.disablePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		taskService.disable(task);

		final List<String> taskKeys = new ArrayList<String>();
		taskKeys.add(task.getKey());

		final List<MAgentTask> onlyActiveTasks = taskService.getTasksByKeys(
				taskKeys, true);
		final List<MAgentTask> allTasks = taskService.getTasksByKeys(taskKeys,
				false);
		Assert.assertEquals(onlyActiveTasks.size(), allTasks.size() - 1);

		EasyMock.verify(policyConfigurationService);
	}

	@Test(expected = DeletedSourceException.class)
	public void testRegisterDeletedTask() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(3);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.deletePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		final List<MAgentTask> previousTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertTrue(SimpleUtils.getKeys(previousTasks).contains(
				task.getKey()));

		taskService.delete(task);

		taskService.registerTasks(agentName, Arrays.asList(task));

		final List<MAgentTask> updatedTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertEquals(previousTasks.size() - 1, updatedTasks.size());
		Assert.assertFalse(SimpleUtils.getKeys(updatedTasks).contains(
				task.getKey()));

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
	}

	@Test
	public void testRegisterDisabledTask() {
		createTaskParameterConfigurations();

		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(3);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.disablePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		policyConfigurationService.resetDisabledPolicies(
				EasyMock.anyObject(MAgentTask.class),
				EasyMock.anyObject(ParameterIdentifier.class));
		EasyMock.expectLastCall().times(
				task.getResultConfiguration().getParameterConfigurations()
						.size());
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();

		eventBroadcastDispatcher.broadcastWithoutTransaction(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		EasyMock.expect(
				propertyUpdater.updateProperties(
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(PropertyType.class))).andReturn(
				false);
		EasyMock.replay(propertyUpdater);

		final List<MAgentTask> previousTasks = taskService.getAgentTasks(
				agentName, null, null, null);

		taskService.disable(task);

		taskService.registerTasks(agentName, Arrays.asList(task));

		final List<MAgentTask> updatedTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertEquals(previousTasks.size(), updatedTasks.size());

		final Map<String, MAgentTask> updatedTasksMap = SimpleUtils
				.getMap(updatedTasks);
		final MAgentTask updatedTask = updatedTasksMap.get(task.getKey());
		Assert.assertNotNull(updatedTask);
		Assert.assertEquals(task.getDisplayName(), updatedTask.getDisplayName());
		Assert.assertEquals(false, updatedTask.isDisabled());

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
		EasyMock.verify(propertyUpdater);
	}

	@Test
	public void testRegisterExistingModifiedTask() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(3);
		EasyMock.replay(agentService);

		eventBroadcastDispatcher.broadcastWithoutTransaction(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		EasyMock.expect(
				propertyUpdater.updateProperties(
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(PropertyType.class)))
				.andReturn(true);
		EasyMock.replay(propertyUpdater);

		final List<MAgentTask> previousTasks = taskService.getAgentTasks(
				agentName, null, null, null);

		task.setDisplayName("newTaskDisplayName");
		taskService.registerTasks(agentName, Arrays.asList(task));

		final List<MAgentTask> updatedTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertEquals(previousTasks.size(), updatedTasks.size());

		final Map<String, MAgentTask> updatedTasksMap = SimpleUtils
				.getMap(updatedTasks);
		Assert.assertTrue(updatedTasksMap.containsKey(task.getKey()));
		final MAgentTask updatedTask = updatedTasksMap.get(task.getKey());
		Assert.assertEquals(task.getKey(), updatedTask.getKey());
		Assert.assertEquals(task.getDisplayName(), updatedTask.getDisplayName());

		EasyMock.verify(agentService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(propertyUpdater);
	}

	@Test
	public void testRegisterNewTask() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(4);
		EasyMock.replay(agentService);

		eventBroadcastDispatcher.broadcastWithoutTransaction(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		policyConfigurationService.applyAgentPolicyTemplates(
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(MAgentTask.class));
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		EasyMock.expect(
				propertyUpdater.updateProperties(
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(PropertyType.class))).andReturn(
				false);
		EasyMock.replay(propertyUpdater);

		final List<MAgentTask> previousTasks = taskService.getAgentTasks(
				agentName, null, null, null);

		final MAgentTask newTask = SharedModelConfiguration
				.createAgentTask(module);
		taskService.registerTasks(agentName, Arrays.asList(task, newTask));

		final List<MAgentTask> updatedTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertEquals(previousTasks.size() + 1, updatedTasks.size());

		final Set<String> updatedTasksKeys = SimpleUtils.getKeys(updatedTasks);
		Assert.assertTrue(updatedTasksKeys.contains(task.getKey()));
		Assert.assertTrue(updatedTasksKeys.contains(newTask.getKey()));

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(propertyUpdater);
	}

	@Test
	public void testRegisterTaskToDisable() {
		createTaskParameterConfigurations();

		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(4);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.disablePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		final List<MAgentTask> previousTasks = taskService.getAgentTasks(
				agentName, null, null, null);

		taskService.registerTasks(agentName,
				Collections.<MAgentTask> emptyList());

		final List<MAgentTask> updatedTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertEquals(previousTasks.size() - 1, updatedTasks.size());
		Assert.assertFalse(SimpleUtils.getKeys(updatedTasks).contains(
				task.getKey()));

		final List<MAgentTask> allTasks = taskService.getAgentTasks(agentName,
				null, null, null, null, false, false);
		final Map<String, MAgentTask> allTasksMap = SimpleUtils
				.getMap(allTasks);
		Assert.assertTrue(allTasksMap.containsKey(task.getKey()));
		Assert.assertEquals(true, allTasksMap.get(task.getKey()).isDisabled());

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
	}

	@Test
	public void testRegisterTheSameExistingTask() {
		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(3);
		EasyMock.replay(agentService);

		EasyMock.expect(
				propertyUpdater.updateProperties(
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(MAgentTask.class),
						EasyMock.anyObject(PropertyType.class))).andReturn(
				false);
		EasyMock.replay(propertyUpdater);

		final List<MAgentTask> previousTasks = taskService.getAgentTasks(
				agentName, null, null, null);

		taskService.registerTasks(agentName, Arrays.asList(task));

		final List<MAgentTask> updatedTasks = taskService.getAgentTasks(
				agentName, null, null, null);
		Assert.assertEquals(previousTasks.size(), updatedTasks.size());

		final Set<String> updatedTasksKeys = SimpleUtils.getKeys(updatedTasks);
		Assert.assertTrue(updatedTasksKeys.contains(task.getKey()));

		EasyMock.verify(agentService);
		EasyMock.verify(propertyUpdater);
	}

	@Test
	public void testResetDisabledTask() {
		createTaskParameterConfigurations();

		EasyMock.expect(agentService.getAllModulesByAgentKey(agentName))
				.andReturn(Arrays.<MAgentModule> asList(module)).times(3);
		EasyMock.replay(agentService);

		EasyMock.expect(
				policyConfigurationService.getPolicies(EasyMock
						.anyObject(Source.class))).andReturn(policyEmptyList);
		policyConfigurationService.disablePolicies(EasyMock
				.<Set<String>> anyObject());
		EasyMock.expectLastCall();
		policyConfigurationService.resetDisabledPolicies(
				EasyMock.anyObject(MAgentTask.class),
				EasyMock.anyObject(ParameterIdentifier.class));
		EasyMock.expectLastCall().times(
				task.getResultConfiguration().getParameterConfigurations()
						.size());
		EasyMock.replay(policyConfigurationService);

		storageService.removeStorages(task);
		EasyMock.expectLastCall();
		EasyMock.replay(storageService);

		eventBroadcastDispatcher.broadcast(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();

		eventBroadcastDispatcher.broadcastWithoutTransaction(EasyMock
				.<Collection<AbstractEvent>> anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(eventBroadcastDispatcher);

		final Set<String> moduleNames = new HashSet<String>();
		moduleNames.add(moduleName);
		List<MAgentTask> agentTasks = taskService.getAgentTasks(agentName,
				moduleNames, 0, 1000);
		Assert.assertEquals(1, agentTasks.size());
		Assert.assertEquals(task, agentTasks.iterator().next());

		taskService.disable(task);

		agentTasks = taskService.getAgentTasks(agentName, moduleNames, 0, 1000);
		Assert.assertTrue(agentTasks.isEmpty());

		taskService.resetDisabledTask(task.getKey());

		agentTasks = taskService.getAgentTasks(agentName, moduleNames, 0, 1000);
		Assert.assertEquals(1, agentTasks.size());
		Assert.assertEquals(task, agentTasks.iterator().next());

		EasyMock.verify(agentService);
		EasyMock.verify(policyConfigurationService);
		EasyMock.verify(eventBroadcastDispatcher);
		EasyMock.verify(storageService);
	}
}

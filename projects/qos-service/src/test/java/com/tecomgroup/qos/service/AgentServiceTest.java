/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.exception.DeletedSourceException;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
public class AgentServiceTest extends AbstractAgentServiceTest {

	@Autowired
	private AgentService agentService;

	private MAgentModule module;

	@Override
	protected void init() {
		createAgent();
		module = SharedModelConfiguration.createAgentModule(agent);
		modelSpace.save(module);
		createTasks(module);
	}

	@Test
	public void testAllAgentGettersAfterDeletion() {
		final List<MAgent> agents = agentService.getAllAgents();
		agentService.deleteAgents(SimpleUtils.getKeys(agents));

		final List<MAgent> deletedAgents = agentService.getAllAgents();
		Assert.assertTrue(deletedAgents.isEmpty());

		final List<String> allAgentKeys = agentService.getAllAgentKeys();
		Assert.assertTrue(allAgentKeys.isEmpty());

		final List<String> allAgentModuleKeys = agentService
				.getAllModuleKeysByAgentKey(agentKey);
		Assert.assertTrue(allAgentModuleKeys.isEmpty());

		final List<MAgentModule> allAgentModules = agentService
				.getAllModulesByAgentKey(agentKey);
		Assert.assertTrue(allAgentModules.isEmpty());

		final MAgentModule existingModule = agentService.getModule(agentKey,
				module.getKey());
		Assert.assertNull(existingModule);

		Assert.assertFalse(agentService.doesAgentExist(agentKey));
	}

	@Test
	public void testDeleteAgent() {
		final MAgent agent = agentService.getAgentByKey(agentKey);
		agentService.delete(agent);

		final MAgent deletedAgent = agentService.getAgentByKey(agentKey);
		Assert.assertNull(deletedAgent);
	}

	@Test
	public void testDeleteAllAgents() {
		final List<MAgent> agents = agentService.getAllAgents();

		final Set<String> deletedAgentKeys = SimpleUtils.getKeys(agents);
		agentService.deleteAgents(deletedAgentKeys);

		final List<MAgent> deletedAgentsByKeys = agentService
				.getAgentsByKeys(deletedAgentKeys);
		Assert.assertTrue(deletedAgentsByKeys.isEmpty());
	}

	@Test
	public void testDoesAgentExist() {
		Assert.assertTrue(agentService.doesAgentExist(agentKey));
	}

	@Test
	public void testGetAllAgentKeys() {
		final List<String> agentKeys = agentService.getAllAgentKeys();
		Assert.assertEquals(1, agentKeys.size());
		Assert.assertEquals(agentKey, agentKeys.iterator().next());
	}

	@Test
	public void testGetAllAgents() {
		final List<MAgent> agents = agentService.getAllAgents();
		Assert.assertEquals(1, agents.size());
		Assert.assertEquals(agentKey, agents.iterator().next().getKey());
	}

	@Test
	public void testGetAllModuleKeysByAgentKey() {
		final List<String> allModuleKeys = agentService
				.getAllModuleKeysByAgentKey(agent.getKey());
		Assert.assertEquals(1, allModuleKeys.size());
		Assert.assertEquals(module.getKey(), allModuleKeys.iterator().next());
	}

	@Test
	public void testGetAllModulesByAgentKey() {
		final List<MAgentModule> allModules = agentService
				.getAllModulesByAgentKey(agent.getKey());
		Assert.assertEquals(1, allModules.size());
		Assert.assertEquals(module.getKey(), allModules.iterator().next()
				.getKey());
	}

	@Test
	public void testGetModule() {
		final MAgentModule existingModule = agentService.getModule(agentKey,
				module.getKey());

		Assert.assertNotNull(existingModule);
		Assert.assertEquals(module.getKey(), existingModule.getKey());
	}

	@Test(expected = DeletedSourceException.class)
	public void testRegisterDeletedAgent() {
		agentService.delete(agent);

		agentService.registerAgent(agent, Arrays.asList(module));
	}

	@Test
	public void testRegisterExistingAgent() {
		final MAgent registeredAgent = agentService.registerAgent(agent,
				Arrays.asList(module));
		Assert.assertEquals(agent.getId(), registeredAgent.getId());

		final Set<String> registeredAgents = agentService.getRegisteredAgents();
		Assert.assertEquals(1, registeredAgents.size());
		Assert.assertEquals(agent.getKey(), registeredAgents.iterator().next());

		final List<MAgentModule> registeredModules = agentService
				.getAllModulesByAgentKey(agent.getKey());
		Assert.assertEquals(1, registeredModules.size());
		Assert.assertEquals(module.getKey(), registeredModules.iterator()
				.next().getKey());
	}

	@Test
	public void testRegisterNewAgent() {
		final MAgent newAgent = SharedModelConfiguration
				.createLightWeightAgent("newAgentKey");
		final MAgentModule newModule = SharedModelConfiguration
				.createAgentModule(newAgent);

		final MAgent registeredAgent = agentService.registerAgent(newAgent,
				Arrays.asList(newModule));
		Assert.assertEquals(newAgent.getId(), registeredAgent.getId());

		final Set<String> registeredAgents = agentService.getRegisteredAgents();
		Assert.assertEquals(1, registeredAgents.size());
		Assert.assertEquals(newAgent.getKey(), registeredAgents.iterator()
				.next());

		final List<MAgentModule> registeredModules = agentService
				.getAllModulesByAgentKey(newAgent.getKey());
		Assert.assertEquals(1, registeredModules.size());
		Assert.assertEquals(newModule.getKey(), registeredModules.iterator()
				.next().getKey());
	}
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rrd4j.core.RrdDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class StorageServiceTest {

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private StorageService storageService;
	@Autowired
	private TaskService taskService;

	private MAgentTask agentTask = null;
	private MAgentModule module = null;
	private MAgent agent = null;
	private MResultConfiguration resultConfiguration = null;
	private MResultConfigurationTemplate resultConfigurationTemplate = null;

	@Before
	public void setUp() {

		storageService.setStorageHome(SharedModelConfiguration.STORAGE_HOME);

		SharedModelConfiguration.deleteFolder(SharedModelConfiguration.STORAGE_HOME);
		SharedModelConfiguration.createFolder(SharedModelConfiguration.STORAGE_HOME);

		agent = SharedModelConfiguration.createLightWeightAgent("resultTestAgentName");
		modelSpace.save(agent);
		module = SharedModelConfiguration.createAgentModule(agent);
		modelSpace.save(module);

		agentTask = SharedModelConfiguration.createAgentTask(module);
		resultConfiguration = SharedModelConfiguration.createResultConfiguration(agentTask);
		resultConfigurationTemplate = resultConfiguration.getTemplateResultConfiguration();
		agentTask.setResultConfiguration(resultConfiguration);
		modelSpace.save(agentTask);
	}

	@Test
	public void testInitStorageWithCreationWhenOtherStorageExists()
			throws Exception {
		MResultConfiguration updatedConfiguration = null;
		for (final MResultParameterConfiguration parameterConfigurationTemplate : new ArrayList<MResultParameterConfiguration>(
				resultConfigurationTemplate.getParameterConfigurations())) {
			final MResultParameterConfiguration parameterConfiguration = new MResultParameterConfiguration(
					parameterConfigurationTemplate, resultConfigurationTemplate);
			// create storage with one configuration
			storageService
					.initStorage(agentTask,
							parameterConfiguration.getParameterIdentifier(),
							true, null);

			resultConfiguration = agentTask.getResultConfiguration();
			// change storage configuration
			resultConfiguration
					.setSamplingRate((long) (Math.random() + 1) * 50);
			taskService.createOrUpdateTask(agentTask);

			// create storage with other configuration
			storageService
					.initStorage(agentTask,
							parameterConfiguration.getParameterIdentifier(),
							true, null);

			// check that a new parameter configuration was added to the
			// existing
			// configuration
			updatedConfiguration = (modelSpace.get(MAgentTask.class,
					agentTask.getId())).getResultConfiguration();
		}

		Assert.assertEquals(5, updatedConfiguration
				.getParameterConfigurations().size());

		// check that all parameter configurations were created
		if (!SharedModelConfiguration.checkWhetherStorageExists(
				updatedConfiguration, SharedModelConfiguration.STORAGE_HOME)) {
			throw new Exception("Storage was not created.");
		}
	}

	@Test
	public void testUpdateSamplingRate() throws Exception {
		for (final MResultParameterConfiguration paramConfigTemplate : resultConfigurationTemplate.getParameterConfigurations()) {
			final MResultParameterConfiguration paramConfig = new MResultParameterConfiguration(paramConfigTemplate, resultConfigurationTemplate);
			// create storage
			storageService.executeInRRD(agentTask, paramConfig.getParameterIdentifier(), true, false, null, null);
		}

		for (final MResultParameterConfiguration paramConfigTemplate : resultConfigurationTemplate.getParameterConfigurations()) {
			final MResultParameterConfiguration paramConfig = new MResultParameterConfiguration(paramConfigTemplate, resultConfigurationTemplate);
			RrdDb rrd = storageService.openRrdDb(agentTask.getResultConfiguration().findParameterConfiguration(paramConfig.getParameterIdentifier()).getLocation().getFullFilePath(SharedModelConfiguration.STORAGE_HOME));
			Assert.assertEquals(1L, rrd.getHeader().getStep());
		}

		long newSamplingRate = 50L;
		// update sampling rate
		List<Future<?>> futures = storageService.updateSamplingRate(agentTask, newSamplingRate);

		// wait when all threads are finished
		for (Future<?> future: futures) {
			future.get(60L, TimeUnit.SECONDS);
		}

		for (final MResultParameterConfiguration paramConfigTemplate : resultConfigurationTemplate.getParameterConfigurations()) {
			final MResultParameterConfiguration paramConfig = new MResultParameterConfiguration(paramConfigTemplate, resultConfigurationTemplate);
			RrdDb rrd = storageService.openRrdDb(agentTask.getResultConfiguration().findParameterConfiguration(paramConfig.getParameterIdentifier()).getLocation().getFullFilePath(SharedModelConfiguration.STORAGE_HOME));
			Assert.assertEquals(newSamplingRate, rrd.getHeader().getStep());
		}
	}

	@Test
	public void testInitStorageWithCreationWhenThereIsNoOtherStorage()
			throws Exception {

		for (final MResultParameterConfiguration parameterConfigurationTemplate : new ArrayList<MResultParameterConfiguration>(
				resultConfigurationTemplate.getParameterConfigurations())) {
			final MResultParameterConfiguration parameterConfiguration = new MResultParameterConfiguration(
					parameterConfigurationTemplate, resultConfigurationTemplate);

			storageService
					.initStorage(agentTask,
							parameterConfiguration.getParameterIdentifier(),
							true, null);
		}

		// check that all parameter configurations were created
		if (!SharedModelConfiguration.checkWhetherStorageExists(
				resultConfiguration, SharedModelConfiguration.STORAGE_HOME)) {
			throw new Exception("Storage was not created.");
		}
	}

	@Test
	public void testInitStorageWithoutCreationByUsingExistingOne()
			throws Exception {
		for (final MResultParameterConfiguration parameterConfigurationTemplate : new ArrayList<MResultParameterConfiguration>(
				resultConfigurationTemplate.getParameterConfigurations())) {
			final MResultParameterConfiguration parameterConfiguration = new MResultParameterConfiguration(
					parameterConfigurationTemplate, resultConfigurationTemplate);
			// create storage
			storageService
					.initStorage(agentTask,
							parameterConfiguration.getParameterIdentifier(),
							true, null);

			storageService
					.initStorage(agentTask,
							parameterConfiguration.getParameterIdentifier(),
							true, null);

			// check that there is no changes made during initialization of the
			// storage
			final MResultConfiguration updatedResultConfiguration = (modelSpace
					.get(MAgentTask.class, agentTask.getId()))
					.getResultConfiguration();

			Assert.assertTrue(ConfigurationUtil
					.areResultConfigurationsCompatible(resultConfiguration,
							updatedResultConfiguration));
		}

		// check that all parameter configurations were created
		if (!SharedModelConfiguration.checkWhetherStorageExists(
				resultConfiguration, SharedModelConfiguration.STORAGE_HOME)) {
			throw new Exception("Storage was not created.");
		}
	}

	@Test
	public void testRemoveStorage() throws Exception {
		final MResultParameterConfiguration parameterConfigurationTemplate = new MResultParameterConfiguration(
				resultConfigurationTemplate.getParameterConfigurations()
						.iterator().next(), resultConfigurationTemplate);
		final ParameterIdentifier parameterIdentifier = parameterConfigurationTemplate
				.getParameterIdentifier();

		storageService.initStorage(agentTask, parameterIdentifier, true, null);

		// remove both storages
		storageService.removeStorage(agentTask, parameterIdentifier);

		/*
		 * // check that all storages were removed if
		 * (!MediaModelConfiguration.checkWhetherStorageRemoved(
		 * resultConfiguration, MediaModelConfiguration.STORAGE_HOME)) { throw
		 * new Exception("Storages were not removed."); }
		 */
		if (!SharedModelConfiguration.checkWhetherStorageExists(
				resultConfiguration, SharedModelConfiguration.STORAGE_HOME,
				parameterIdentifier)) {
			throw new Exception("Storage was removed.");
		}
	}

	@Test
	public void testRemoveStorages() throws Exception {
		for (final MResultParameterConfiguration parameterConfigurationTemplate : new ArrayList<MResultParameterConfiguration>(
				resultConfigurationTemplate.getParameterConfigurations())) {
			final MResultParameterConfiguration parameterConfiguration = new MResultParameterConfiguration(
					parameterConfigurationTemplate, resultConfigurationTemplate);
			storageService
					.initStorage(agentTask,
							parameterConfiguration.getParameterIdentifier(),
							true, null);
		}

		// remove all storages
		storageService.removeStorages(agentTask);

		/*
		 * // check that all storages were removed if
		 * (!MediaModelConfiguration.checkWhetherStorageRemoved(
		 * resultConfiguration, MediaModelConfiguration.STORAGE_HOME)) { throw
		 * new Exception("Storages were not removed."); }
		 */

		if (!SharedModelConfiguration.checkWhetherStorageExists(
				resultConfiguration, SharedModelConfiguration.STORAGE_HOME)) {
			throw new Exception("Storage was removed.");
		}
	}

}

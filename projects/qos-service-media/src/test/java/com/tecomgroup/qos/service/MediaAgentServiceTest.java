/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kunilov.p
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/testServiceContext.xml"})
@ActiveProfiles(AbstractService.TEST_MEDIA_CONTEXT_PROFILE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MediaAgentServiceTest extends AbstractAgentServiceTest {

	@SuppressWarnings("unchecked")
	private static <T extends MStream> List<T> getModuleTemplateStreams(
			final Class<T> streamType, final MMediaAgentModule module) {
		final List<T> streams = new ArrayList<T>();
		for (final MStream stream : module.getTemplateStreams()) {
			if (streamType.isInstance(stream)) {
				streams.add((T) stream);
			}
		}
		return streams;
	}

	@Autowired
	private MediaAgentService agentService;

	private MMediaAgentModule module;

	@Override
	protected void init() {
		createAgent();
		module = MediaModelConfiguration.createMediaAgentModule(agent);
		modelSpace.save(module);
		createTasks(module);
	}

	@Test
	public void testAllAgentGettersAfterDeletion() {
		final List<MAgent> agents = agentService.getAllAgents();

		for (final MAgent agent : agents) {
			agentService.delete(agent);
		}

		final List<MRecordedStream> agentRecordedStreams = agentService
				.getAgentRecordedStreams(agentKey);
		Assert.assertTrue(agentRecordedStreams.isEmpty());

		final MMediaAgentModule existingMediaModule = agentService
				.getMediaModule(agentKey, module.getKey());
		Assert.assertNull(existingMediaModule);
	}

	@Test
	public void testClearAgentStreams() {
		List<MAgentModule> agentModules = agentService
				.getAllModulesByAgentKey(agentKey);
		Assert.assertEquals(1, agentModules.size());

		MMediaAgentModule mediaModule = (MMediaAgentModule) agentModules.get(0);
		final List<MLiveStream> liveStreams = getModuleTemplateStreams(
				MLiveStream.class, mediaModule);
		final List<MRecordedStream> recordedStreams = getModuleTemplateStreams(
				MRecordedStream.class, mediaModule);
		Assert.assertEquals(9, liveStreams.size());
		Assert.assertEquals(3, recordedStreams.size());

		mediaModule.getTemplateStreams().clear();
		agentService.updateModules(agent.getKey(), agentModules);

		agentModules = agentService.getAllModulesByAgentKey(agentKey);
		Assert.assertEquals(1, agentModules.size());
		mediaModule = (MMediaAgentModule) agentModules.get(0);
		Assert.assertEquals(0, mediaModule.getTemplateStreams().size());
	}

	@Test
	public void testGetAgentLiveStreams() {
		final List<MLiveStream> liveStreams = agentService
				.getAgentLiveStreams(agentKey);
		int taskSize = 0;
		if (tasks != null) {
			taskSize = tasks.size();
		}
		int streamSize = 0;
		if (module.getTemplateStreams() != null) {
			streamSize = getModuleTemplateStreams(MLiveStream.class, module)
					.size();
		}
		Assert.assertEquals(taskSize * streamSize, liveStreams.size());
	}

	@Test
	public void testGetAgetRecordedStreams() {
		final List<MRecordedStream> recordedStreams = agentService
				.getAgentRecordedStreams(agentKey);
		int taskSize = 0;
		if (tasks != null) {
			taskSize = tasks.size();
		}
		int streamSize = 0;
		if (module.getTemplateStreams() != null) {
			streamSize = getModuleTemplateStreams(MRecordedStream.class, module)
					.size();
		}
		Assert.assertEquals(taskSize * streamSize, recordedStreams.size());
	}

	@Test
	public void testGetMediaModule() {
		final MMediaAgentModule mediaModule = agentService.getMediaModule(
				agentKey, module.getKey());
		Assert.assertNotNull(mediaModule);
		Assert.assertEquals(module.getKey(), mediaModule.getKey());
	}
}

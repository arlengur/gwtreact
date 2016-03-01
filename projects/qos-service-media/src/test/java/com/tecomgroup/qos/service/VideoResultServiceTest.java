/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.VideoResult;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author kunilov.p
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/testServiceContext.xml"})
@ActiveProfiles(AbstractService.TEST_MEDIA_CONTEXT_PROFILE)
@Transactional
public class VideoResultServiceTest {

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private VideoResultService videoService;

	private MAgent agent;

	private MMediaAgentModule module;

	private final String agentName = "FirstChannel-NN";

	private final String streamKey = "recordedStream0";

	private Source streamSource;

	private MAgentTask task;

	private void createVideoResults() {
		final List<MVideoResult> results = MediaModelConfiguration
				.createVideoResults(streamSource);

		for (final MVideoResult result : results) {
			modelSpace.saveOrUpdate(result);
		}
	}

	@Before
	public void setUp() {
		agent = SharedModelConfiguration.createLightWeightAgent(agentName);
		modelSpace.save(agent);

		module = MediaModelConfiguration.createMediaAgentModule(agent);
		modelSpace.save(module);

		task = MediaModelConfiguration.createAgentTask(module);
		modelSpace.save(task);

		streamSource = Source.getStreamSource(task.getKey(), streamKey);
	}

	@Test
	public void testAddStreamResult() {
		final List<VideoResult> results = MediaModelConfiguration
				.createVideoResults(streamKey);

		final List<MVideoResult> resultsBefore = modelSpace
				.getAll(MVideoResult.class);

		videoService.addResults(task.getKey(), results);

		final List<MVideoResult> resultsAfter = modelSpace
				.getAll(MVideoResult.class);
		Assert.assertEquals(resultsBefore.size() + results.size(),
				resultsAfter.size());
	}

	@Test
	public void testGetStreamResults() {
		final Date startDateTime = new Date();
		// pre-condition
		createVideoResults();
		final Date endDateTime = new Date();

		final List<MVideoResult> videoResults = videoService.getResults(
				streamSource, TimeInterval.get(startDateTime, endDateTime),
				null, null);

		Assert.assertEquals(SharedModelConfiguration.VIDEO_RESULT_COUNT,
				videoResults.size());
	}

}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MMediaAgentModule;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.MediaModelConfiguration;

/**
 * @author abondin
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/testServiceContext.xml"})
@ActiveProfiles(AbstractService.TEST_MEDIA_CONTEXT_PROFILE)
public class MediaUserServiceTest {

	@Autowired
	private InternalUserService userService;

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	private MAgent agent;

	private MMediaAgentModule module;

	private MAgentTask task;

	private MUser user;

	private MLiveStream liveStream;

	@Before
	public void before() throws Exception {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				agent = MediaModelConfiguration
						.createLightWeightAgent("test-agent");
				modelSpace.save(agent);

				module = MediaModelConfiguration.createMediaAgentModule(agent);
				modelSpace.save(module);

				task = MediaModelConfiguration.createAgentTask(module);
				modelSpace.save(task);
			}
		});

		user = userService.findUser("User");

		for (final MStream stream : module.getTemplateStreams()) {
			if (stream instanceof MLiveStream) {
				liveStream = (MLiveStream) stream;
				break;
			}
		}
		Assert.assertNotNull("No streams configured for module", liveStream);
	}

	@Test
	public void testLiveStreamWidgetOnDashboard() {
		MDashboard dashboard = new MDashboard();
		dashboard.setUsername(user.getLogin());
		final LiveStreamWidget widget = new LiveStreamWidget();
		widget.setTitle("Simple Stream Widget");
		widget.setStreamKey(liveStream.getKey());
		widget.setTaskKey(task.getKey());
		dashboard.addWidget(widget);
		userService.updateDashboard(dashboard);

		dashboard = userService.getDashboard(user.getLogin());
		Assert.assertTrue(dashboard.getWidgets().size() == 1);
		Assert.assertTrue(((LiveStreamWidget) dashboard.getWidgets().values()
				.iterator().next()).getStream().getKey()
				.equals(liveStream.getKey()));
	}
}

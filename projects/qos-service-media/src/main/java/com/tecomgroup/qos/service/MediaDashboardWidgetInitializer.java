/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MLiveStream;

import java.util.Arrays;

/**
 * @author abondin
 * 
 */
@Component
public class MediaDashboardWidgetInitializer
		extends
			DefaultDashboardWidgetInitializer {

	private final Logger LOGGER = Logger
			.getLogger(MediaDashboardWidgetInitializer.class);

	@Autowired
	private MediaAgentService agentService;

	@Autowired
	private TaskRetriever taskRetriever;

	@Override
	public void setupWidget(final DashboardWidget widget) throws Exception {
		if (widget instanceof LiveStreamWidget) {
			final LiveStreamWidget liveStreamWidget = (LiveStreamWidget) widget;
			final MAgentTask task = taskRetriever.getTaskByKey(liveStreamWidget
					.getTaskKey());
			if (task == null) {
				throw new Exception("Unable to initialize widget " + widget.getKey()
						+ ": task " + liveStreamWidget.getTaskKey()
						+ " disabled or doesn't exist");
			} else {
				final MLiveStream stream = (MLiveStream) agentService
						.getStream(task, liveStreamWidget.getStreamKey());

				if(stream != null) {
					String agentKey = task.getModule().getAgent().getKey();
					if (!authorizeService.isPermittedProbes(Arrays.asList(agentKey))) {
						stream.setUrl("dumb_url");
						LOGGER.error("User is not permitted to see video from probe [" + agentKey + "]");
					}
				}
				liveStreamWidget.initialize(stream);
			}
		} else {
			super.setupWidget(widget);
		}
	}
}

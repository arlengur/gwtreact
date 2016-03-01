/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.communication.message.VideoResultMessage;
import com.tecomgroup.qos.communication.result.VideoResult;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.MediaModelConfiguration;

/**
 * @author kunilov.p
 * 
 */
@Component
public class SendVideoResult implements QoSTool {

	private final static Logger LOGGER = Logger
			.getLogger(SendVideoResult.class);

	@Value("${result.send.agent.name}")
	private String agentName;

	@Value("${task.key}")
	private String[] taskKeys;

	@Value("${video.result.message.count}")
	private Integer videoResultMessageCount;

	private final String streamKey = "recordedStream0";

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Override
	public void execute() {
		// Register agent
		// final SendRegisterAgent registerAgent = new SendRegisterAgent();
		// registerAgent.setAmqpTemplate(amqpTemplate);
		// registerAgent.setAgentName(agentName);
		// registerAgent.setTaskKeys(taskKeys);
		// registerAgent.execute();

		sendVideoResults();
	}

	@Override
	public String getDescription() {
		return "Send video result to the server";
	}

	private void sendVideoResults() {
		if (taskKeys.length > 0) {
			final String taskKey = taskKeys[0];
			final String moduleKey = MediaModelConfiguration
					.createModuleKey(agentName);

			for (int index = 0; index < videoResultMessageCount; index++) {
				final List<VideoResult> results = MediaModelConfiguration
						.createVideoResults(streamKey);
				final VideoResultMessage resultMessage = new VideoResultMessage();
				resultMessage.setResults(results);
				resultMessage.setTaskKey(MediaModelConfiguration.createTaskKey(
						null, moduleKey, taskKey));
				amqpTemplate.convertAndSend("qos.result", "agent-" + agentName,
						resultMessage);
				LOGGER.info("Send video result message: " + index);
			}
		}
	}
}

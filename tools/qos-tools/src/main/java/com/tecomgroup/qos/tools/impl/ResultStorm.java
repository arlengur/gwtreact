/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.communication.message.ResultMessage.ResultType;
import com.tecomgroup.qos.tools.QoSTool;

/**
 * 
 * Send tons of results from several agents
 * 
 * @author abondin
 * 
 */
@Component
public class ResultStorm implements QoSTool {
	@Value("${result.send.starttime}")
	private Long startTime;

	@Value("${result.storm.agent.count}")
	private int agentCount;
	@Value("${result.storm.task.count}")
	private int taskCount;
	@Value("${result.storm.register.only}")
	private boolean registerOnly = false;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Override
	public void execute() {
		if (startTime >= 0) {
			throw new IllegalArgumentException(
					"Only negative number supported for result.send.starttime");
		}
		for (int agentIndex = 1; agentIndex <= agentCount; agentIndex++) {
			final String agentName = "storm-agent-" + agentIndex;
			final List<String> tasks = new ArrayList<String>();
			for (int taskIndex = 1; taskIndex <= taskCount; taskIndex++) {
				tasks.add(agentName + "-" + taskIndex);
			}
			final SendResult sendResult = new SendResult();
			sendResult.setAmqpTemplate(amqpTemplate);
			sendResult.setTaskKeys(tasks.toArray(new String[0]));
			sendResult.setAgentName(agentName);
			if (registerOnly) {
				sendResult.setStartTime(startTime - 2);
				sendResult.setEndTime(startTime - 1);
			} else {
				sendResult.setStartTime(startTime);
				sendResult.setEndTime(0l);

			}
			sendResult.setInterval(1l);
			sendResult.setMode("random");
			sendResult.setSendGeneratedMessageCount(60);
			sendResult.setSendMessageCount(60);
			sendResult.setSleep(0l);
			sendResult.setResultType(ResultType.SINGLE_VALUE_RESULT);
			sendResult.execute();
		}

	}

	@Override
	public String getDescription() {
		return "Send result storm to the server"
				+ "\nSupported VM arguments:"
				+ "\n\tresult.storm.agent.count - number of agents to send results"
				+ "\n\tresult.storm.task.count - number of tasks for one agent"
				+ "\n\tresult.send.starttime - first message time in seconds. Only negative number supported. Now-n sec";
	}
	/**
	 * @param agentCount
	 *            the agentCount to set
	 */
	public void setAgentCount(final int agentCount) {
		this.agentCount = agentCount;
	}

	/**
	 * @param registerOnly
	 *            the registerOnly to set
	 */
	public void setRegisterOnly(final boolean registerOnly) {
		this.registerOnly = registerOnly;
	}

	/**
	 * @param taskCount
	 *            the taskCount to set
	 */
	public void setTaskCount(final int taskCount) {
		this.taskCount = taskCount;
	}

}

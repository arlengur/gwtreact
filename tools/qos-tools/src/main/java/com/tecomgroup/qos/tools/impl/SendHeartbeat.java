/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.communication.message.HeartbeatMessage;
import com.tecomgroup.qos.communication.response.RequestResponse.Status;
import com.tecomgroup.qos.tools.QoSTool;

/**
 * @author kunilov.p
 * 
 */
@Component
public class SendHeartbeat implements QoSTool {

	@Value("${result.send.agent.name}")
	private String agentName;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Override
	public void execute() {
		final HeartbeatMessage heartbeat = new HeartbeatMessage();
		heartbeat.setAgentKey(agentName);
		heartbeat.setStatus(Status.OK);
		amqpTemplate.convertAndSend("qos.service", "server", heartbeat);
	}

	@Override
	public String getDescription() {
		return "Send heartbeat message with provided agent key";
	}

}

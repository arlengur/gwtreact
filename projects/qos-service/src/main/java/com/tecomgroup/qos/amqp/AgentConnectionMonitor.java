/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.amqp;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.communication.message.HeartbeatMessage;
import com.tecomgroup.qos.service.SystemComponentStatisticService;

/**
 * Monitor a connection to the given agent. This class should resolve the
 * following issues:
 * 
 * 1) Communication problems<br>
 * For example: Restart server in isolated environment (no connection to the
 * agent)
 * 
 * 2) Connection loss<br>
 * 1.1) //TODO Raise alert if not heartbeats comes from the agent
 * 
 * @author abondin
 * 
 */
public class AgentConnectionMonitor implements AgentHeartbeatListener {

	private final Logger LOGGER = Logger
			.getLogger(AgentConnectionMonitor.class);

	private final Map<String, Long> sendRegisterMessageTimestamp = new HashMap<>();

	private SystemComponentStatisticService statisticService;

	private ServiceMessageListener serviceMessageListener;

	// @see agent.send.serverstarted.message.interval.min
	private Long sendRegisterMessageInterval = 5l;

	@Override
	public void onHeartbeat(final HeartbeatMessage heartbeat) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Heartbeat from " + heartbeat.getAgentKey() + " : "
					+ heartbeat.getStatus());
		}

		final String agent = heartbeat.getAgentKey();
		final AgentStatistic statistic = statisticService.getAgentsStatistic()
				.get(agent);
		if (statistic == null || statistic.getRegistrationTime() == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Agent " + agent
						+ " is not registered but sends heartbeats");
			}
			sendRegisterMessage(agent);
		}
	}

	/**
	 * @param agent
	 */
	private void sendRegisterMessage(final String agent) {
		final boolean send;
		synchronized (sendRegisterMessageTimestamp) {
			final Long currentTimestamp = System.currentTimeMillis();
			final Long timestamp = sendRegisterMessageTimestamp.get(agent);
			send = timestamp == null
					|| (currentTimestamp - timestamp) > sendRegisterMessageInterval
							* TimeConstants.MILLISECONDS_PER_SECOND;
			sendRegisterMessageTimestamp.put(agent, currentTimestamp);
		}
		if (send) {
			serviceMessageListener.sendServerStartedMessage(agent);
		}

	}

	/**
	 * @param sendRegisterMessageInterval
	 *            the sendRegisterMessageInterval to set
	 */
	public void setSendRegisterMessageInterval(
			final Long sendRegisterMessageInterval) {
		this.sendRegisterMessageInterval = sendRegisterMessageInterval;
	}

	/**
	 * Interval in minutes to send server started message if agent is not
	 * registered, but sends heartbeats
	 * 
	 * @param serviceMessageListener
	 *            the serviceMessageListener to set
	 */
	public void setServiceMessageListener(
			final ServiceMessageListener serviceMessageListener) {
		this.serviceMessageListener = serviceMessageListener;
	}

	public void setStatisticService(
			final SystemComponentStatisticService statisticService) {
		this.statisticService = statisticService;
	}

}

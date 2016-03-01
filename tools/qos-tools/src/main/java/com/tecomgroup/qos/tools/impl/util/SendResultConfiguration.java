/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author novohatskiy.r
 * 
 */
public class SendResultConfiguration {

	private String agent;
	private Long startTimeLong;
	private Long endTimeLong;
	private Long interval;
	private Long sleep;
	private String[] taskKeys;
	private List<Map<String, Double>> parametersByMessage = new ArrayList<Map<String, Double>>();

	public void addMessageParameters(final Map<String, Double> map) {
		parametersByMessage.add(map);
	}

	/**
	 * @return the agent
	 */
	public String getAgent() {
		return agent;
	}

	/**
	 * @return the endTimeLong
	 */
	public Long getEndTimeLong() {
		return endTimeLong;
	}

	/**
	 * @return the interval
	 */
	public Long getInterval() {
		return interval;
	}

	/**
	 * @return the parametersByMessage
	 */
	public List<Map<String, Double>> getParametersByMessage() {
		return parametersByMessage;
	}

	/**
	 * @return the sleep
	 */
	public Long getSleep() {
		return sleep;
	}

	/**
	 * @return the startTimeLong
	 */
	public Long getStartTimeLong() {
		return startTimeLong;
	}

	/**
	 * @return the taskKeys
	 */
	public String[] getTaskKeys() {
		return taskKeys;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(final String agent) {
		this.agent = agent;
	}

	/**
	 * @param endTimeLong
	 *            the endTimeLong to set
	 */
	public void setEndTimeLong(final Long endTimeLong) {
		this.endTimeLong = endTimeLong;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(final Long interval) {
		this.interval = interval;
	}

	/**
	 * @param parametersByMessage
	 *            the parametersByMessage to set
	 */
	public void setParametersByMessage(
			final List<Map<String, Double>> parametersByMessage) {
		this.parametersByMessage = parametersByMessage;
	}

	/**
	 * @param sleep
	 *            the sleep to set
	 */
	public void setSleep(final Long sleep) {
		this.sleep = sleep;
	}

	/**
	 * @param startTimeLong
	 *            the startTimeLong to set
	 */
	public void setStartTimeLong(final Long startTimeLong) {
		this.startTimeLong = startTimeLong;
	}

	/**
	 * @param taskKeys
	 *            the taskKeys to set
	 */
	public void setTaskKeys(final String[] taskKeys) {
		this.taskKeys = taskKeys;
	}
}

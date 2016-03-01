/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import com.tecomgroup.qos.communication.response.RequestResponse.Status;

/**
 * A heartbeat message is sent by Agent to Server to provided its {@link Status}
 * 
 * @author kunilov.p
 * 
 */
public class HeartbeatMessage extends QoSMessage {

	private String agentKey;

	private Status status;

	/**
	 * @return the agentKey
	 */
	public String getAgentKey() {
		return agentKey;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param agentKey
	 *            the agentKey to set
	 */
	public void setAgentKey(final String agentKey) {
		this.agentKey = agentKey;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.response;

import com.tecomgroup.qos.communication.request.RegisterAgent;

/**
 * Message respose for {@link RegisterAgent}
 * 
 * @author abondin
 * 
 */
public class RegisterAgentResponse extends RequestResponse {

	private String agentId;

	private String serverName;

	/**
	 * 
	 */
	public RegisterAgentResponse() {
		super();
	}

	/**
	 * @param ex
	 */
	public RegisterAgentResponse(final Throwable ex) {
		super(ex);
	}

	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param agentId
	 *            the agentId to set
	 */
	public void setAgentId(final String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}
}
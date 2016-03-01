/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.response;

/**
 * @author kunilov.p
 * 
 */
public class UpdateModulesResponse extends RequestResponse {

	private String serverName;

	public UpdateModulesResponse() {
		super();
	}

	public UpdateModulesResponse(final Throwable ex) {
		super(ex);
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}
}

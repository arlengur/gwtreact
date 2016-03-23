/*
 * Copyright (C) 2016 Qligent.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.response;

public class TaskStatusResponse extends RequestResponse {

	private String serverName;

	public TaskStatusResponse() {
		super();
	}

	public TaskStatusResponse(final Throwable ex) {
		super(ex);
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}
}
/*
 * Copyright (C) 2016 Qligent.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.response;

public class ProbeConfigSyncResponse extends RequestResponse {

	private String serverName;

	public ProbeConfigSyncResponse() {
		super();
	}

	public ProbeConfigSyncResponse(final Throwable ex) {
		super(ex);
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}
}
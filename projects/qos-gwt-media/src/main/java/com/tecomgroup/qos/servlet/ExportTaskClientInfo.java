/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

/**
 * @author sviyazov.a
 * 
 */
public class ExportTaskClientInfo {

	private final String sessionName;
	private final String remoteAddress;

	public ExportTaskClientInfo(final String sessionName,
			final String remoteAddress) {
		this.sessionName = sessionName;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public String toString() {
		return "{session: " + sessionName + ", clientAddress: " + remoteAddress
				+ "}";
	}
}

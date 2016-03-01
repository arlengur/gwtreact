/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

/**
 * Сообщение что сервер готов к работе
 * 
 * @author abondin
 * 
 */
public class ServerStarted extends QoSMessage {

	private String serverName;

	public static final String SERVER_NAME = "server";

	public static ServerStarted serverStarted(final String serverName) {
		final ServerStarted message = new ServerStarted();
		message.setServerName(serverName);
		return message;
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

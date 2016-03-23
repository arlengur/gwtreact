/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

public class UserLogoutEvent extends AbstractEvent {

	private static final long serialVersionUID = 7882313566234224126L;

	private String userLogin;

	public UserLogoutEvent() {
		super();
	}

	public UserLogoutEvent(final EventType eventType, String userLogin) {
		super(eventType);
		this.userLogin = userLogin;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
}

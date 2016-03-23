/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.UserLogoutEvent;

public class UserLogoutEventFilter implements QoSEventFilter {

	private String userLogin;

	public UserLogoutEventFilter() {
		super();
	}

	public UserLogoutEventFilter(
			final String userLogin) {
		super();
		this.userLogin = userLogin;
	}

	@Override
	public boolean accept(final AbstractEvent event) {
		if (event instanceof UserLogoutEvent) {
			UserLogoutEvent userLogoutEvent = (UserLogoutEvent) event;
			String eventUserLogin = userLogoutEvent.getUserLogin();

			if(eventUserLogin != null && userLogin.equals(eventUserLogin)) {
				return true;
			}
		}
		return false;
	}

}

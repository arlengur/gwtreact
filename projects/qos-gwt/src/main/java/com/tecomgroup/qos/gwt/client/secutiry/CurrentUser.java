/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.event.CurrentUserChangedEvent;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author abondin
 * 
 */
public class CurrentUser implements HasHandlers {
	private final EventBus eventBus;

	private MUser user;

	@Inject
	public CurrentUser(final EventBus eventBus) {
		this.eventBus = eventBus;
		AppUtils.registerCurrentUser(this);
	}

	@Override
	public void fireEvent(final GwtEvent<?> event) {
		eventBus.fireEvent(event);
	}

	/**
	 * @return the user
	 */
	public MUser getUser() {
		return user;
	}

	/**
	 * @param isAdmin
	 *            the isAdmin to set
	 */
	public void setUser(final MUser user) {
		this.user = user;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				eventBus.fireEvent(new CurrentUserChangedEvent(user));
			}
		});
	}
}

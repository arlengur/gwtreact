/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.user;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.event.user.AfterUserSavedEvent.AfterUserSavedEventHandler;

/**
 * Fires after {@link MUser} was saved on server.
 * 
 * @author meleshin.o
 */
public class AfterUserSavedEvent extends GwtEvent<AfterUserSavedEventHandler> {
	public static interface AfterUserSavedEventHandler extends EventHandler {
		void onAfterUserSaved(AfterUserSavedEvent event);
	}

	public final static Type<AfterUserSavedEventHandler> TYPE = new Type<AfterUserSavedEventHandler>();

	private final MUser user;

	public AfterUserSavedEvent(final MUser user) {
		super();
		this.user = user;
	}

	@Override
	protected void dispatch(final AfterUserSavedEventHandler handler) {
		handler.onAfterUserSaved(this);
	}

	@Override
	public Type<AfterUserSavedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public MUser getUser() {
		return user;
	}
}

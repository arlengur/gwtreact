/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent.BeforeLogoutEventHandler;

/**
 * Fired before user requests logout from application.
 * 
 * @author ivlev.e
 */
public class BeforeLogoutEvent extends GwtEvent<BeforeLogoutEventHandler> {

	public static interface BeforeLogoutEventHandler extends EventHandler {
		void onBeforeLogout(BeforeLogoutEvent event);
	}

	public final static Type<BeforeLogoutEventHandler> TYPE = new Type<BeforeLogoutEventHandler>();

	@Override
	protected void dispatch(final BeforeLogoutEventHandler handler) {
		handler.onBeforeLogout(this);
	}

	@Override
	public GwtEvent.Type<BeforeLogoutEventHandler> getAssociatedType() {
		return TYPE;
	}

}

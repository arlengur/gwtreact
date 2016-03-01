/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.alert.RefreshAlertGridViewEvent.RefreshAlertGridViewEventHandler;

/**
 * Event send request to refresh an alert grid
 * 
 * @author ivlev.e
 */
public class RefreshAlertGridViewEvent
		extends
			GwtEvent<RefreshAlertGridViewEventHandler> {

	public static interface RefreshAlertGridViewEventHandler
			extends
				EventHandler {
		void onRefreshEvent(RefreshAlertGridViewEvent event);
	}

	public final static Type<RefreshAlertGridViewEventHandler> TYPE = new Type<RefreshAlertGridViewEventHandler>();

	@Override
	protected void dispatch(final RefreshAlertGridViewEventHandler handler) {
		handler.onRefreshEvent(this);
	}

	@Override
	public Type<RefreshAlertGridViewEventHandler> getAssociatedType() {
		return TYPE;
	}

}

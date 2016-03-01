/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.event.NavigateToAlertDetailsEvent.NavigateToAlertDetailsEventHandler;

/**
 * Navigates UI to the AlertDetails page for given {@link MAlert}
 * 
 * @author novohatskiy.r
 * 
 */
public class NavigateToAlertDetailsEvent
		extends
			GwtEvent<NavigateToAlertDetailsEventHandler>
		implements
			HasPostActionCallback {

	public static interface NavigateToAlertDetailsEventHandler
			extends
				EventHandler {
		void onNavigateToAlertDetails(NavigateToAlertDetailsEvent event);
	}
	public final static Type<NavigateToAlertDetailsEventHandler> TYPE = new Type<NavigateToAlertDetailsEventHandler>();

	private final MAlert alert;
	private final PostActionCallback callback;

	public NavigateToAlertDetailsEvent(final MAlert alert,
			final PostActionCallback callback) {
		this.alert = alert;
		this.callback = callback;
	}

	@Override
	protected void dispatch(final NavigateToAlertDetailsEventHandler handler) {
		handler.onNavigateToAlertDetails(this);
	}

	/**
	 * @return the alert
	 */
	public MAlert getAlert() {
		return alert;
	}

	@Override
	public Type<NavigateToAlertDetailsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	public PostActionCallback getCallback() {
		return callback;
	}
}
/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.dashboard;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.RemoveWidgetFromDashboardEvent.RemoveWidgetFromDashboardEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class RemoveWidgetFromDashboardEvent
		extends
			GwtEvent<RemoveWidgetFromDashboardEventHandler> {

	public static interface RemoveWidgetFromDashboardEventHandler
			extends
				EventHandler {
		void onRemoveWidgetFromDashboard(RemoveWidgetFromDashboardEvent event);
	}

	private final String widgetKey;

	public final static Type<RemoveWidgetFromDashboardEventHandler> TYPE = new Type<RemoveWidgetFromDashboardEventHandler>();

	public RemoveWidgetFromDashboardEvent(final String widgetKey) {
		this.widgetKey = widgetKey;
	}

	@Override
	protected void dispatch(final RemoveWidgetFromDashboardEventHandler handler) {
		handler.onRemoveWidgetFromDashboard(this);
	}

	@Override
	public Type<RemoveWidgetFromDashboardEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the widgetKey
	 */
	public String getWidgetKey() {
		return widgetKey;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.dashboard;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetRemovedEvent.DashboardWidgetRemovedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class DashboardWidgetRemovedEvent
		extends
			GwtEvent<DashboardWidgetRemovedEventHandler> {

	public static interface DashboardWidgetRemovedEventHandler
			extends
				EventHandler {
		void onDashboardWidgetRemoved(DashboardWidgetRemovedEvent event);
	}

	private final String widgetKey;

	public final static Type<DashboardWidgetRemovedEventHandler> TYPE = new Type<DashboardWidgetRemovedEventHandler>();

	public DashboardWidgetRemovedEvent(final String widgetKey) {
		this.widgetKey = widgetKey;
	}

	@Override
	protected void dispatch(final DashboardWidgetRemovedEventHandler handler) {
		handler.onDashboardWidgetRemoved(this);
	}

	@Override
	public GwtEvent.Type<DashboardWidgetRemovedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getWidgetKey() {
		return widgetKey;
	}
}

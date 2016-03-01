/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.dashboard;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent.DashboardWidgetAddedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class DashboardWidgetAddedEvent
		extends
			GwtEvent<DashboardWidgetAddedEventHandler> {

	public static interface DashboardWidgetAddedEventHandler
			extends
				EventHandler {
		void onDashboardWidgetAdded(DashboardWidgetAddedEvent event);
	}

    private DashboardWidget widget;

	public final static Type<DashboardWidgetAddedEventHandler> TYPE = new Type<DashboardWidgetAddedEventHandler>();

	public DashboardWidgetAddedEvent() {
		super();
	}

	public DashboardWidgetAddedEvent(final DashboardWidget widget) {
		this();
        this.widget = widget;
	}

	@Override
	protected void dispatch(final DashboardWidgetAddedEventHandler handler) {
		handler.onDashboardWidgetAdded(this);
	}

	@Override
	public Type<DashboardWidgetAddedEventHandler> getAssociatedType() {
		return TYPE;
	}

    public DashboardWidget getWidget() {
        return widget;
    }

    public void setWidget(final DashboardWidget widget) {
        this.widget = widget;
    }
}

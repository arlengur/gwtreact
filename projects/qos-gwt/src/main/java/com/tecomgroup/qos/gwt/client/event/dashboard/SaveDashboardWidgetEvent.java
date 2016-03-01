/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.dashboard;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.SaveDashboardWidgetEvent.SaveDashboardWidgetEventHandler;

/**
 * An event is occured when it is necessary to save {@link DashboardWidget}.
 * 
 * @author kunilov.p
 * 
 */
public class SaveDashboardWidgetEvent
		extends
			GwtEvent<SaveDashboardWidgetEventHandler> {

	public static interface SaveDashboardWidgetEventHandler
			extends
				EventHandler {
		void onSaveWidget(SaveDashboardWidgetEvent event);
	}

	private DashboardWidget widget;

	public final static Type<SaveDashboardWidgetEventHandler> TYPE = new Type<SaveDashboardWidgetEventHandler>();

	public SaveDashboardWidgetEvent() {
		super();
	}

	public SaveDashboardWidgetEvent(final DashboardWidget widget) {
		this();
		this.widget = widget;
	}

	@Override
	protected void dispatch(final SaveDashboardWidgetEventHandler handler) {
		handler.onSaveWidget(this);
	}

	@Override
	public Type<SaveDashboardWidgetEventHandler> getAssociatedType() {
		return TYPE;
	}

	public DashboardWidget getWidget() {
		return widget;
	}

	public void setWidget(final DashboardWidget widget) {
		this.widget = widget;
	}
}

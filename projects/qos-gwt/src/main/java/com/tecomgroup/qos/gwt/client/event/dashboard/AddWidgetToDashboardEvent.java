/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.dashboard;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent.AddWidgetToDashboardHandler;

/**
 * @author abondin
 * 
 */
public class AddWidgetToDashboardEvent
		extends
			GwtEvent<AddWidgetToDashboardHandler> {

	public static interface AddWidgetToDashboardHandler extends EventHandler {
		void onAddWidgetToDashboard(AddWidgetToDashboardEvent event);
	}

	private DashboardWidget widget;

	public final static Type<AddWidgetToDashboardHandler> TYPE = new Type<AddWidgetToDashboardHandler>();

	/**
	 * @return the type
	 */
	public static Type<AddWidgetToDashboardHandler> getType() {
		return TYPE;
	}

	public AddWidgetToDashboardEvent() {
		super();
	}

	public AddWidgetToDashboardEvent(final DashboardWidget widget) {
		this();
		this.widget = widget;
	}

	@Override
	protected void dispatch(final AddWidgetToDashboardHandler handler) {
		handler.onAddWidgetToDashboard(this);
	}

	@Override
	public Type<AddWidgetToDashboardHandler> getAssociatedType() {
		return getType();
	}

	public DashboardWidget getWidget() {
		return widget;
	}

	public void setWidget(final DashboardWidget widget) {
		this.widget = widget;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.chart.BuildChartsEvent.BuildChartsEventHandler;

/**
 * An event raised immediately after the demand of build charts.
 * 
 * @author kshnyakin.m
 * 
 */
public class BuildChartsEvent extends GwtEvent<BuildChartsEventHandler> {

	public static interface BuildChartsEventHandler extends EventHandler {
		void onBuildCharts(BuildChartsEvent event);
	}

	public final static Type<BuildChartsEventHandler> TYPE = new Type<BuildChartsEventHandler>();

	public BuildChartsEvent() {
		super();
	}

	@Override
	protected void dispatch(final BuildChartsEventHandler handler) {
		handler.onBuildCharts(this);
	}

	@Override
	public Type<BuildChartsEventHandler> getAssociatedType() {
		return TYPE;
	}
}

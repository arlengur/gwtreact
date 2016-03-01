/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartBuiltEvent.ChartBuiltEventHandler;

/**
 * This event should be raised after chart was built.
 * 
 * @author novohatskiy.r
 * 
 */
public class ChartBuiltEvent extends GwtEvent<ChartBuiltEventHandler> {

	public static interface ChartBuiltEventHandler extends EventHandler {
		void onChartBuilt(ChartBuiltEvent event);
	}

	private final String chartName;

	public final static Type<ChartBuiltEventHandler> TYPE = new Type<ChartBuiltEventHandler>();

	public ChartBuiltEvent(final String chartName) {
		this.chartName = chartName;
	}

	@Override
	protected void dispatch(final ChartBuiltEventHandler handler) {
		handler.onChartBuilt(this);
	}

	@Override
	public Type<ChartBuiltEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getChartName() {
		return chartName;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesRemovedEvent.ChartSeriesRemovedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class ChartSeriesRemovedEvent
		extends
			ChartSeriesEvent<ChartSeriesRemovedEventHandler> {

	public static interface ChartSeriesRemovedEventHandler extends EventHandler {
		void onChartSeriesRemovedEvent(ChartSeriesRemovedEvent event);
	}

	private final List<String> seriesKeys;

	public final static Type<ChartSeriesRemovedEventHandler> TYPE = new Type<ChartSeriesRemovedEventHandler>();

	public ChartSeriesRemovedEvent(final List<String> seriesKeys) {
		this.seriesKeys = seriesKeys;
	}

	@Override
	protected void dispatch(final ChartSeriesRemovedEventHandler handler) {
		handler.onChartSeriesRemovedEvent(this);
	}

	@Override
	public Type<ChartSeriesRemovedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public List<String> getSeriesKeys() {
		return seriesKeys;
	}

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesAddedEvent.ChartSeriesAddedEventHandler;

/**
 * @author abondin
 * 
 */
public class ChartSeriesAddedEvent
		extends
			ChartSeriesEvent<ChartSeriesAddedEventHandler> {

	public static interface ChartSeriesAddedEventHandler extends EventHandler {
		void onChartSeriesAddedEvent(ChartSeriesAddedEvent event);
	}

	private final MChartSeries chartSeries;

	public final static Type<ChartSeriesAddedEventHandler> TYPE = new Type<ChartSeriesAddedEventHandler>();

	public ChartSeriesAddedEvent(final MChartSeries chartSeries) {
		this.chartSeries = chartSeries;
	}

	@Override
	protected void dispatch(final ChartSeriesAddedEventHandler handler) {
		handler.onChartSeriesAddedEvent(this);
	}

	@Override
	public Type<ChartSeriesAddedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the chartSeries
	 */
	public MChartSeries getChartSeries() {
		return chartSeries;
	}

}

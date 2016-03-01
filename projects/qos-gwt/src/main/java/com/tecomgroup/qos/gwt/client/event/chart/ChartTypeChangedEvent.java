/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartTypeChangedEvent.ChartTypeChangedEventHandler;

/**
 * An event of chart type change raised after chart type is changed.
 * 
 * @author kunilov.p
 * 
 */
public class ChartTypeChangedEvent
		extends
			GwtEvent<ChartTypeChangedEventHandler> {

	public static interface ChartTypeChangedEventHandler extends EventHandler {
		void onChartTypeChangedEvent(ChartTypeChangedEvent event);
	}

	private final String chartName;

	private String newType;

	private String oldType;

	public final static Type<ChartTypeChangedEventHandler> TYPE = new Type<ChartTypeChangedEventHandler>();

	public ChartTypeChangedEvent(final String chartName, final String newType,
			final String oldType) {
		super();
		this.chartName = chartName;
	}

	@Override
	protected void dispatch(final ChartTypeChangedEventHandler handler) {
		handler.onChartTypeChangedEvent(this);
	}

	@Override
	public Type<ChartTypeChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getChartName() {
		return chartName;
	}

	public String getNewType() {
		return newType;
	}

	public String getOldType() {
		return oldType;
	}
}

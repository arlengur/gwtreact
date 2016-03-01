/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import java.util.Date;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartZoomChangedEvent.ChartZoomChangedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class ChartZoomChangedEvent
		extends
			GwtEvent<ChartZoomChangedEventHandler> {

	public static interface ChartZoomChangedEventHandler extends EventHandler {
		void onChartZoomChanged(ChartZoomChangedEvent event);
	}

	private final Date startDate;

	private final Date endDate;

	private final String chartName;

	public final static Type<ChartZoomChangedEventHandler> TYPE = new Type<ChartZoomChangedEventHandler>();

	public ChartZoomChangedEvent(final String chartName, final Date startDate,
			final Date endDate) {
		this.chartName = chartName;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	protected void dispatch(final ChartZoomChangedEventHandler handler) {
		handler.onChartZoomChanged(this);
	}

	@Override
	public Type<ChartZoomChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getChartName() {
		return chartName;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getStartDate() {
		return startDate;
	}
}

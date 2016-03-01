/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartThresholdsOptionChangedEvent.ChartThresholdsOptionChangedEventHandler;

/**
 * An event of chart thresholds option state change raised after option state is
 * changed.
 * 
 * @author kunilov.p
 * 
 */
public class ChartThresholdsOptionChangedEvent
		extends
			ChartOptionStateChangedEvent<ChartThresholdsOptionChangedEventHandler> {

	public static interface ChartThresholdsOptionChangedEventHandler
			extends
				EventHandler {
		void onChartThresholdsOptionChangedEvent(
				ChartThresholdsOptionChangedEvent event);
	}

	public final static Type<ChartThresholdsOptionChangedEventHandler> TYPE = new Type<ChartThresholdsOptionChangedEventHandler>();

	/**
	 * @param optionState
	 *            true if option is enabled and false otherwise.
	 */
	public ChartThresholdsOptionChangedEvent(final String chartName,
			final boolean optionState) {
		super(chartName, optionState);
	}

	@Override
	protected void dispatch(
			final ChartThresholdsOptionChangedEventHandler handler) {
		handler.onChartThresholdsOptionChangedEvent(this);
	}

	@Override
	public Type<ChartThresholdsOptionChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}

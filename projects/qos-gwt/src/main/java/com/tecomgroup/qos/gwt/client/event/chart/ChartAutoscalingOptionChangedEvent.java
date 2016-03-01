/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartAutoscalingOptionChangedEvent.ChartAutoscalingOptionChangedEventHandler;

/**
 * An event of chart autoscaling option state change raised after option state
 * is changed.
 * 
 * @author kunilov.p
 * 
 */
public class ChartAutoscalingOptionChangedEvent
		extends
			ChartOptionStateChangedEvent<ChartAutoscalingOptionChangedEventHandler> {

	public static interface ChartAutoscalingOptionChangedEventHandler
			extends
				EventHandler {
		void onChartAutoscalingOptionChangedEvent(ChartAutoscalingOptionChangedEvent event);
	}

	public final static Type<ChartAutoscalingOptionChangedEventHandler> TYPE = new Type<ChartAutoscalingOptionChangedEventHandler>();

	/**
	 * @param optionState
	 *            true if option is enabled and false otherwise.
	 */
	public ChartAutoscalingOptionChangedEvent(final String chartName,
			final boolean optionState) {
		super(chartName, optionState);
	}

	@Override
	protected void dispatch(final ChartAutoscalingOptionChangedEventHandler handler) {
		handler.onChartAutoscalingOptionChangedEvent(this);
	}

	@Override
	public Type<ChartAutoscalingOptionChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}

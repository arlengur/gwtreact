/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartCaptionsOptionChangedEvent.ChartCaptionsOptionChangedEventHandler;

/**
 * An event of chart captions option state change raised after option state is
 * changed.
 * 
 * @author kunilov.p
 * 
 */
public class ChartCaptionsOptionChangedEvent
		extends
			ChartOptionStateChangedEvent<ChartCaptionsOptionChangedEventHandler> {

	public static interface ChartCaptionsOptionChangedEventHandler
			extends
				EventHandler {
		void onChartCaptionsOptionChangedEvent(
				ChartCaptionsOptionChangedEvent event);
	}

	public final static Type<ChartCaptionsOptionChangedEventHandler> TYPE = new Type<ChartCaptionsOptionChangedEventHandler>();

	/**
	 * @param optionState
	 *            true if option is enabled and false otherwise.
	 */
	public ChartCaptionsOptionChangedEvent(final String chartName,
			final boolean optionState) {
		super(chartName, optionState);
	}

	@Override
	protected void dispatch(final ChartCaptionsOptionChangedEventHandler handler) {
		handler.onChartCaptionsOptionChangedEvent(this);
	}

	@Override
	public Type<ChartCaptionsOptionChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}

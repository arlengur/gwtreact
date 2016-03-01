/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An abstract event of chart options state change raised after option state is
 * changed.
 * 
 * @author kunilov.p
 * 
 */
public abstract class ChartOptionStateChangedEvent<H extends EventHandler>
		extends
			GwtEvent<H> {

	protected String chartName;

	protected boolean optionState;

	/**
	 * @param optionState
	 *            true if option is enabled and false otherwise.
	 */
	public ChartOptionStateChangedEvent(final String chartName,
			final boolean optionState) {
		super();
		this.chartName = chartName;
		this.optionState = optionState;
	}

	public String getChartName() {
		return chartName;
	}

	/**
	 * @param return true if option is enabled and false otherwise.
	 */
	public boolean getOptionState() {
		return optionState;
	}
}

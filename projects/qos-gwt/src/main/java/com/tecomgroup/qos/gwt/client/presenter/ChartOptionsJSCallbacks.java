/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.Date;

import com.tecomgroup.qos.gwt.client.event.chart.ChartAutoscalingOptionChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartBuiltEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartCaptionsOptionChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartThresholdsOptionChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartTypeChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartZoomChangedEvent;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * Contains javascript callbacks of chart options operations.
 * 
 * @author kunilov.p
 * 
 */
public class ChartOptionsJSCallbacks {

	public static void chartAutoscalingOptionStateChanged(
			final String chartName, final boolean optionState) {
		AppUtils.getEventBus().fireEvent(
				new ChartAutoscalingOptionChangedEvent(chartName, optionState));
	}

	public static void chartBuilt(final String chartName) {
		AppUtils.getEventBus().fireEvent(new ChartBuiltEvent(chartName));
	}

	public static void chartCaptionsOptionStateChanged(final String chartName,
			final boolean optionState) {
		AppUtils.getEventBus().fireEvent(
				new ChartCaptionsOptionChangedEvent(chartName, optionState));
	}

	public static void chartThresholdsOptionStateChanged(
			final String chartName, final boolean optionState) {
		AppUtils.getEventBus().fireEvent(
				new ChartThresholdsOptionChangedEvent(chartName, optionState));
	}

	public static void chartTypeChanged(final String chartName,
			final String newType, final String oldType) {
		AppUtils.getEventBus().fireEvent(
				new ChartTypeChangedEvent(chartName, newType, oldType));
	}

	public static void timeIntervalChanged(final String chartName,
			final double startDate, final double endDate) {
		AppUtils.getEventBus().fireEvent(
				new ChartZoomChangedEvent(chartName, new Date(Double.valueOf(
						startDate).longValue()), new Date(Double.valueOf(
						endDate).longValue())));
	}
}

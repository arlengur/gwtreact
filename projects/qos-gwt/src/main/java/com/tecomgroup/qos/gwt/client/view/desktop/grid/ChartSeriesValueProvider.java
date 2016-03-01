/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author ivlev.e
 * 
 */
public class ChartSeriesValueProvider
		implements
			ValueProvider<MChartSeries, String> {

	private final DisabledTaskLabelProvider disabledTaskLabelProvider;

	public ChartSeriesValueProvider(final QoSMessages messages) {
		disabledTaskLabelProvider = new DisabledTaskLabelProvider(messages);
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public String getValue(final MChartSeries chartSeries) {
		final String taskLabel = disabledTaskLabelProvider.getLabel(chartSeries
				.getTask());
		return LabelUtils.createSeriesLabel(chartSeries.getAgent(), taskLabel,
				chartSeries.getParameter());
	}

	@Override
	public void setValue(final MChartSeries object, final String value) {
	}
}

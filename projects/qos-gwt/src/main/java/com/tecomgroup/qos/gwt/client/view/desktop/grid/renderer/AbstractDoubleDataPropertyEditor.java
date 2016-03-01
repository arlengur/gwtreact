/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractDoubleDataPropertyEditor
		extends
			AbstractDataPropertyEditor<Double> {

	public AbstractDoubleDataPropertyEditor(final String noDataMessage,
			final String startOfDataMessage, final String endOfDataMessage) {
		super(noDataMessage, startOfDataMessage, endOfDataMessage);
	}

	public AbstractDoubleDataPropertyEditor(final String noDataMessage,
			final String startOfDataMessage, final String endOfDataMessage,
			final boolean showNoData) {
		super(noDataMessage, startOfDataMessage, endOfDataMessage, showNoData);
	}

	@Override
	protected Double getNoDataValue() {
		return Double.NaN;
	}

	@Override
	protected boolean isEndOfDataValue(final Double value) {
		return value.equals(Double.POSITIVE_INFINITY);
	}

	@Override
	protected boolean isNoDataValue(final Double value) {
		return Double.isNaN(value);
	}

	@Override
	protected boolean isStartOfDataValue(final Double value) {
		return value.equals(Double.NEGATIVE_INFINITY);
	}
}

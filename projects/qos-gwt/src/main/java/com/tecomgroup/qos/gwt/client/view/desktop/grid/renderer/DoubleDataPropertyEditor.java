/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import com.google.gwt.i18n.client.NumberFormat;
import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author kshnyakin.m
 * 
 */
public class DoubleDataPropertyEditor extends AbstractDoubleDataPropertyEditor {

	public static class DoublePropertyCell extends PropertyDisplayCell<Double> {
		public DoublePropertyCell(final NumberFormat numberFormat,
				final QoSMessages messages, final boolean showNoData) {
			super(new DoubleDataPropertyEditor(numberFormat, messages.noData(),
					messages.startOfData(), messages.endOfData(), showNoData));
		}

		public DoublePropertyCell(final NumberFormat numberFormat,
				final String noDataMessage, final String startOfDataMessage,
				final String endOfDataMessage, final boolean showNoData) {
			super(new DoubleDataPropertyEditor(numberFormat, noDataMessage,
					startOfDataMessage, endOfDataMessage, showNoData));
		}
	}

	private final NumberFormat numberFormat;

	public DoubleDataPropertyEditor(final NumberFormat numberFormat,
			final String noDataMessage, final String startOfDataMessage,
			final String endOfDataMessage) {
		super(noDataMessage, startOfDataMessage, endOfDataMessage);
		this.numberFormat = numberFormat;
	}

	public DoubleDataPropertyEditor(final NumberFormat numberFormat,
			final String noDataMessage, final String startOfDataMessage,
			final String endOfDataMessage, final boolean showNoData) {
		super(noDataMessage, startOfDataMessage, endOfDataMessage, showNoData);
		this.numberFormat = numberFormat;
	}

	@Override
	protected String formatValue(final Double value) {
		return numberFormat.format(value);
	}

	@Override
	protected Double parseValue(final String value) {
		return Double.parseDouble(value);
	}
}

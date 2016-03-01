/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 * 
 */
public class DoubleAsBooleanDataPropertyEditor
		extends
			AbstractDoubleDataPropertyEditor {

	public static class DoubleAsBooleanPropertyCell
			extends
				PropertyDisplayCell<Double> {
		public DoubleAsBooleanPropertyCell(final QoSMessages messages,
				final boolean showNoData) {
			super(new DoubleAsBooleanDataPropertyEditor(messages, showNoData));
		}
		public DoubleAsBooleanPropertyCell(final String trueMessage,
				final String falseMessage, final String noDataMessage,
				final String startOfDataMessage, final String endOfDataMessage) {
			super(new DoubleAsBooleanDataPropertyEditor(trueMessage,
					falseMessage, noDataMessage, startOfDataMessage,
					endOfDataMessage));
		}
	}

	private final String trueMessage;
	private final String falseMessage;

	public DoubleAsBooleanDataPropertyEditor(final QoSMessages messages,
			final boolean showNoData) {
		super(messages.noData(), messages.startOfData(), messages.endOfData(),
				showNoData);
		this.trueMessage = messages.actionYes();
		this.falseMessage = messages.actionNo();
	}

	public DoubleAsBooleanDataPropertyEditor(final String trueMessage,
			final String falseMessage, final String noDataMessage,
			final String startOfDataMessage, final String endOfDataMessage) {
		super(noDataMessage, startOfDataMessage, endOfDataMessage);
		this.trueMessage = trueMessage;
		this.falseMessage = falseMessage;
	}

	@Override
	protected String formatValue(final Double value) {
		return SimpleUtils.doubleAsBoolean(value) ? trueMessage : falseMessage;
	}

	@Override
	protected Double parseValue(final String value) {
		return value.equals(trueMessage) ? 1.0 : 0.0;
	}
}

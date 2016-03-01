/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author abondin
 * 
 */
public class BooleanDataPropertyEditor
		extends
			AbstractDataPropertyEditor<Boolean> {

	public static class BooleanPropertyCell
			extends
				PropertyDisplayCell<Boolean> {
		public BooleanPropertyCell(final QoSMessages messages,
				final boolean showNoData) {
			super(new BooleanDataPropertyEditor(messages, showNoData));
		}
		public BooleanPropertyCell(final String trueMessage,
				final String falseMessage, final String noDataMessage,
				final String startOfDataMessage, final String endOfDataMessage) {
			super(new BooleanDataPropertyEditor(trueMessage, falseMessage,
					noDataMessage, startOfDataMessage, endOfDataMessage));
		}
	}

	private final String trueMessage;
	private final String falseMessage;

	public BooleanDataPropertyEditor(final QoSMessages messages,
			final boolean showNoData) {
		super(messages.noData(), messages.startOfData(), messages.endOfData(),
				showNoData);
		this.trueMessage = messages.actionYes();
		this.falseMessage = messages.actionNo();
	}

	public BooleanDataPropertyEditor(final String trueMessage,
			final String falseMessage, final String noDataMessage,
			final String startOfDataMessage, final String endOfDataMessage) {
		super(noDataMessage, startOfDataMessage, endOfDataMessage);
		this.trueMessage = trueMessage;
		this.falseMessage = falseMessage;
	}

	@Override
	protected String formatValue(final Boolean value) {
		return value ? trueMessage : falseMessage;
	}

	@Override
	protected Boolean parseValue(final String value) {
		return value.equals(trueMessage) ? true : false;
	}
}

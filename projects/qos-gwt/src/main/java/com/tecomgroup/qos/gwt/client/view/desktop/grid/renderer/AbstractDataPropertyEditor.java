/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.text.ParseException;

import com.sencha.gxt.widget.core.client.form.PropertyEditor;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractDataPropertyEditor<T> extends PropertyEditor<T> {

	protected final String noDataMessage;

	protected final String startOfDataMessage;

	protected final String endOfDataMessage;

	public AbstractDataPropertyEditor(final String noDataMessage,
			final String startOfDataMessage, final String endOfDataMessage) {
		super();
		this.noDataMessage = noDataMessage;
		this.startOfDataMessage = startOfDataMessage;
		this.endOfDataMessage = endOfDataMessage;
	}

	public AbstractDataPropertyEditor(final String noDataMessage,
			final String startOfDataMessage, final String endOfDataMessage,
			final boolean showNoData) {
		this(showNoData ? noDataMessage : null, startOfDataMessage,
				endOfDataMessage);
	}

	protected abstract String formatValue(final T value);

	protected T getNoDataValue() {
		return null;
	}

	protected boolean isEndOfDataValue(final T value) {
		return false;
	}

	protected boolean isNoDataValue(final T value) {
		return value == null;
	}

	protected boolean isStartOfDataValue(final T value) {
		return false;
	}

	@Override
	public T parse(final CharSequence text) throws ParseException {
		return (text == null || "".equals(text.toString())) ? null : text
				.equals(noDataMessage)
				|| text.equals(startOfDataMessage)
				|| text.equals(endOfDataMessage)
				? getNoDataValue()
				: parseValue(text.toString());
	}

	protected abstract T parseValue(final String value);

	@Override
	public String render(final T value) {
		return value == null ? "" : (isNoDataValue(value)
				? noDataMessage
				: (isStartOfDataValue(value)
						? startOfDataMessage
						: (isEndOfDataValue(value)
								? endOfDataMessage
								: formatValue(value))));
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.google.gwt.i18n.client.NumberFormat;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AlertsHistoryGridWidgetView;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Provides formatted <code>oldValue</code> and <code>newValue</code> fields of
 * {@link MAlertUpdate} for {@link AlertsHistoryGridWidgetView}
 * 
 * @author kshnyakin.m
 * 
 */
public abstract class AlertUpdateValueProvider
		extends
			ValueProviderWithPath<MAlertUpdate, String> {

	public static class AlertUpdateNewValueProvider
			extends
				AlertUpdateValueProvider {

		public AlertUpdateNewValueProvider(final String path) {
			super(path);
		}

		@Override
		public String getStringValue(final MAlertUpdate alertUpdate) {
			return alertUpdate.getNewValue();
		}
	}

	public static class AlertUpdateOldValueProvider
			extends
				AlertUpdateValueProvider {

		public AlertUpdateOldValueProvider(final String path) {
			super(path);
		}

		@Override
		public String getStringValue(final MAlertUpdate alertUpdate) {
			return alertUpdate.getOldValue();
		}
	}

	private final NumberFormat numberFormat = NumberFormat
			.getFormat(SimpleUtils.NUMBER_FORMAT);

	public AlertUpdateValueProvider(final String path) {
		super(path);
	}

	public abstract String getStringValue(MAlertUpdate alertUpdate);

	@Override
	public String getValue(final MAlertUpdate alertUpdate) {
		String result = null;
		final String value = getStringValue(alertUpdate);
		final String field = alertUpdate.getField();
		if (field != null && value != null && field.equals("detectionValue")) {
			result = numberFormat.format(Double.parseDouble(value));
		} else {
			result = value;
		}
		return result;
	}
}

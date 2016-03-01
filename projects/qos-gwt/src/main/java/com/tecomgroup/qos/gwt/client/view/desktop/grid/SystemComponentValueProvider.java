/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MSystemComponent;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public abstract class SystemComponentValueProvider<M>
		extends
			ValueProviderWithPath<M, String> {

	public static class AlertReportSystemComponentValueProvider
			extends
				SystemComponentValueProvider<MAlertReport> {

		public AlertReportSystemComponentValueProvider(final String path) {
			super(path);
		}

		@Override
		protected MSource getSource(final MAlertReport alertReport) {
			return alertReport.getAlert().getSource();
		}

	}

	public static class AlertSystemComponentValueProvider
			extends
				SystemComponentValueProvider<MAlert> {

		public AlertSystemComponentValueProvider(final String path) {
			super(path);
		}

		@Override
		protected MSource getSource(final MAlert alert) {
			return alert.getSource();
		}

	}

	public static class AlertUpdateSystemComponentValueProvider
			extends
				SystemComponentValueProvider<MAlertUpdate> {

		public AlertUpdateSystemComponentValueProvider(final String path) {
			super(path);
		}

		@Override
		protected MSource getSource(final MAlertUpdate alertUpdate) {
			return alertUpdate.getAlert().getSource();
		}

	}

	public SystemComponentValueProvider(final String path) {
		super(path);
	}

	protected abstract MSource getSource(M object);

	@Override
	public String getValue(final M object) {
		final MSource source = getSource(object);
		String result = null;
		try {
			final MSystemComponent systemComponent = SimpleUtils
					.findSystemComponent(source);
			if (systemComponent != null) {
				result = systemComponent.getDisplayName();
			}
		} catch (final Exception ex) {
			// ignore
		}
		return result;
	}
}

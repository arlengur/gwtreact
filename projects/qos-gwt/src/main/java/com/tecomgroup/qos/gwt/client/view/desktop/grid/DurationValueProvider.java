/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;

/**
 * @author kunilov.p
 * 
 */
public abstract class DurationValueProvider<M>
		extends
			ValueProviderWithPath<M, String> {

	public static class AlertDurationValueProvider
			extends
				DurationValueProvider<MAlert> {

		public AlertDurationValueProvider(final QoSMessages messages) {
			super(messages, "duration");

		}

		@Override
		public Long getDuration(final MAlert alert) {
			return alert.getDuration();
		}
	}

	public static class AlertReportDurationValueProvider
			extends
				DurationValueProvider<MAlertReport> {

		public AlertReportDurationValueProvider(final QoSMessages messages) {
			super(messages, "duration");
		}

		@Override
		public Long getDuration(final MAlertReport alertReport) {
			return alertReport.getDuration();
		}
	}

	private final QoSMessages messages;

	public DurationValueProvider(final QoSMessages messages, final String path) {
		super(path);
		this.messages = messages;
	}

	public abstract Long getDuration(final M entity);

	@Override
	public String getValue(final M entity) {
		String result = messages.unknown();

		final Long duration = getDuration(entity);
		if (duration != null) {
			result = DateUtils.formatDuration(duration, messages);
		}

		return result;
	}
}

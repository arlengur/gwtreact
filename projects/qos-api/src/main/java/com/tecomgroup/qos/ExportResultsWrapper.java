/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.tecomgroup.qos.TimeInterval.TimeZoneType;

/**
 * @author kshnyakin.m
 * 
 */
public class ExportResultsWrapper
		extends
			CommonExportDataWrapper<ExportResultsWrapper.I18nResultLabels> {
	public static class I18nResultLabels
			extends
				CommonExportDataWrapper.I18nCommonExportLabels {
		private static final long serialVersionUID = 8645681317389282611L;

		public List<String> parameterDisplayNames;

		public String noData;

		public String trueMessage;

		public String falseMessage;

		public String endOfDataLabel;

		public String startOfDataLabel;

		public I18nResultLabels() {
			super();
		}

		public I18nResultLabels(final String reportTitle, final String agents,
				final String startDateTime, final String endDateTime,
				final String timeZone,
				final List<String> parameterDisplayNames, final String noData,
				final String trueMessage, final String falseMessage,
				final String startOfDataLabel, final String endOfDataLabel) {
			super(reportTitle, agents, startDateTime, endDateTime, timeZone);
			this.parameterDisplayNames = parameterDisplayNames;
			this.noData = noData;
			this.trueMessage = trueMessage;
			this.falseMessage = falseMessage;
			this.startOfDataLabel = startOfDataLabel;
			this.endOfDataLabel = endOfDataLabel;
		}
	}

	private static final long serialVersionUID = 6209309268489311342L;

	public List<String> taskKeys;

	public List<String> parameterIdentifiers;

	public boolean rawData;

	public String timeZone;

	public String clientTimeZone;

	public TimeZoneType timeZoneType;

	public boolean booleanResults;

	public ExportResultsWrapper() {
		super();
	}

	public ExportResultsWrapper(final List<String> taskKeys,
			final List<String> parameterIdentifiers, final Date startDate,
			final Date endDate, final String timeZone,
			final String clientTimeZone, final TimeZoneType timeZoneType,
			final I18nResultLabels labels,
			final Collection<String> agentDisplayNames,
			final String timeZoneLabel, final String dateFormat,
			final String locale, final boolean booleanResults,
			final boolean rawData) {
		super(agentDisplayNames, labels, timeZoneLabel, dateFormat, locale);
		this.taskKeys = taskKeys;
		this.parameterIdentifiers = parameterIdentifiers;
		this.startDateTime = startDate;
		this.endDateTime = endDate;
		this.timeZone = timeZone;
		this.clientTimeZone = clientTimeZone;
		this.timeZoneType = timeZoneType;
		this.booleanResults = booleanResults;
		this.rawData = rawData;
	}
}

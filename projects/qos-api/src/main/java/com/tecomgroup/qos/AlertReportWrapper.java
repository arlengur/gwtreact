/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.util.Collection;
import java.util.Map;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MUserReportsTemplate;

/**
 * Объект для передачи всей информации для генерации файла отчёта
 * 
 * @author abondin
 * 
 */
public class AlertReportWrapper
		extends
			CommonExportDataWrapper<AlertReportWrapper.I18nReportLabels> {
	/**
	 * All i18n labels to generate export file
	 * 
	 * @author abondin
	 * 
	 */
	public static class I18nReportLabels
			extends
				CommonExportDataWrapper.I18nCommonExportLabels {
		private static final long serialVersionUID = 6923027486359195883L;

		public String severity;

        public String comments;

		public Map<PerceivedSeverity, String> severities;

		public Map<String, String> columnDisplayNames;

		public I18nReportLabels() {
			super();
		}

		/**
		 * @param reportTitle
		 * @param agents
		 * @param severity
		 * @param startDateTime
		 * @param endDateTime
		 * @param columnDisplayNames
		 */
		public I18nReportLabels(final String reportTitle, final String agents,
				final String severity, final String startDateTime,
				final String endDateTime, final String timeZone, final String comments,
				final Map<PerceivedSeverity, String> severities,
				final Map<String, String> columnDisplayNames) {
			super(reportTitle, agents, startDateTime, endDateTime, timeZone);
			this.severity = severity;
			this.severities = severities;
			this.columnDisplayNames = columnDisplayNames;
            this.comments = comments;
		}

	}

	private static final long serialVersionUID = -2523105276514832240L;

	public MUserReportsTemplate template;

	public AlertReportWrapper() {
		super();
	}

	/**
	 * 
	 * @param template
	 *            - sources + user filters + order + hidden columns + selected
	 *            dates
	 * @param labels
	 *            - i18n labels
	 * @param dateFormat
	 *            - i18n format for dates
	 * @param locale
	 */
	public AlertReportWrapper(final MUserReportsTemplate template,
			final I18nReportLabels labels,
			final Collection<String> agentDisplayNames,
			final String timeZoneLabel, final String dateFormat,
			final String locale) {
		super(agentDisplayNames, labels, timeZoneLabel, dateFormat, locale);
		this.template = template;
	}
}

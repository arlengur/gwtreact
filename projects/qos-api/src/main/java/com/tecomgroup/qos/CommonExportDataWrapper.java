/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author kshnyakin.m
 * 
 */

public abstract class CommonExportDataWrapper<T extends CommonExportDataWrapper.I18nCommonExportLabels>
		implements
			Serializable {

	public static abstract class I18nCommonExportLabels implements Serializable {

		private static final long serialVersionUID = -7364151122671883555L;

		public String reportTitle;

		public String agents;

		public String startDateTime;

		public String endDateTime;

		public String timeZone;

		public I18nCommonExportLabels() {
			super();
		}

		public I18nCommonExportLabels(final String reportTitle,
				final String agents, final String startDateTime,
				final String endDateTime, final String timeZone) {
			this();
			this.reportTitle = reportTitle;
			this.agents = agents;
			this.startDateTime = startDateTime;
			this.endDateTime = endDateTime;
			this.timeZone = timeZone;
		}
	}

	private static final long serialVersionUID = 7970610304319213439L;

	public Collection<String> agentDisplayNames;

	public String dateFormat;

	public String timeZoneLabel;

	public String locale;

	public Date startDateTime;

	public Date endDateTime;

	public T labels;

	public CommonExportDataWrapper() {
		super();
	}

	public CommonExportDataWrapper(final Collection<String> agentDisplayNames,
			final T labels, final String timeZoneLabel,
			final String dateFormat, final String locale) {
		this();
		this.agentDisplayNames = agentDisplayNames;
		this.labels = labels;
		this.timeZoneLabel = timeZoneLabel;
		this.dateFormat = dateFormat;
		this.locale = locale;
	}
}

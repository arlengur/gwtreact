/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.report;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance.AlertsGridResources;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertReportProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.SeverityGridViewConfig;

/**
 * @author kunilov.p
 * 
 */
public class ReportsGridViewConfig extends SeverityGridViewConfig<MAlertReport> {

	protected AlertReportProperties alertReportProperties;

	public ReportsGridViewConfig(final AlertsGridResources resources,
			final AppearanceFactory appearanceFactory,
			final AlertReportProperties alertReportProperties) {
		super(resources, appearanceFactory);
		this.alertReportProperties = alertReportProperties;
	}

	@Override
	public String getColStyle(final MAlertReport alertReport,
			final ValueProvider<? super MAlertReport, ?> valueProvider,
			final int rowIndex, final int colIndex) {
		final String colStyle = super.getColStyle(alertReport, valueProvider,
				rowIndex, colIndex);
		return colStyle;
	}

	@Override
	public PerceivedSeverity getPerceivedSeverity(final MAlertReport alertReport) {
		return alertReport.getPerceivedSeverity();
	}

	@Override
	public String getRowStyle(final MAlertReport alertReport, final int rowIndex) {
		String rowStyle = super.getRowStyle(alertReport, rowIndex);
		if (alertReport.getEndDateTime() != null) {
			rowStyle += " " + style.alertCleared();
		}
		return rowStyle;
	}
}

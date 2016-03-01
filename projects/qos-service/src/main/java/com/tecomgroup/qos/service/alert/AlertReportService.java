/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.util.Date;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.service.AlertReportRetriever;

/**
 * A service to perform {@link MAlertReport} processing. It is internal server
 * API.
 * 
 * @author kunilov.p
 * 
 */
public interface AlertReportService extends AlertReportRetriever {

	/**
	 * Closes last {@link MAlertReport} associated with provided alert. It is
	 * called when provided {@link MAlert} changes its {@link Status} to
	 * {@link Status#CLEARED} or provided {@link MAlert} changes its
	 * {@link PerceivedSeverity} by receiving {@link UpdateType#SEVERITY_CHANGE}
	 * .
	 * 
	 * @param alert
	 *            An alert related to the report. The parameter is only
	 *            necessary to find the last report to update.
	 * @param endDateTime
	 *            An end date time of the report. It MUST NOT be null.
	 */
	void closeAlertReport(MAlert alert, Date endDateTime);

	/**
	 * Opens new {@link MAlertReport} assosiated with provided alert. It is
	 * called when provided {@link MAlert} changes its status to
	 * {@link Status#ACTIVE} or provided {@link MAlert} changes its
	 * {@link PerceivedSeverity} by receiving {@link UpdateType#SEVERITY_CHANGE}
	 * .
	 * 
	 * <b>IMPORTANT</b> <br/>
	 * Only one opened report (report with NULL as {@link #endDateTime}) is
	 * possible for every alert. Before an addition another opened report, it is
	 * obligatory to close previous one.
	 * 
	 * @param alert
	 *            An alert related to the report.
	 * @param startDateTime
	 *            A start date time of the report. It MUST NOT be null.
	 */
	void openAlertReport(MAlert alert, Date startDateTime);

	/**
	 * Process alert history event - open new or close existing report
	 * 
	 * @param alertUpdate
	 */
	void processAlertUpdateEvent(final MAlertUpdate alertUpdate);
}

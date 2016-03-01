/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.service.alert.AlertHistoryService;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.HttpRequestHandler;

import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.service.alert.AlertReportService;

/**
 * @author zamkin.a
 * 
 */
public class ExportReportServletHandler implements HttpRequestHandler {

	private final static Logger LOGGER = Logger.getLogger(ExportReportServletHandler.class);

	private AlertReportService alertReportService;

    private AlertHistoryService alertHistoryService;

	private final static String REPORT_REQUEST = "reportRequest";

	private final long stepCount = 5000;

	private String createFileName(final String dateFormat,
								  final String locale,
								  final Date startDateTime,
								  final Date endDateTime) {
		final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(dateFormat)
				                                              .withLocale(Locale.forLanguageTag(locale));

		return "ExportReport(" + dateFormatter.print(startDateTime.getTime()) + "-"
				               + dateFormatter.print(endDateTime.getTime()) + ").xlsx";
	}

	private AlertReportWrapper getAlertReportWrapper(final BufferedReader reader) throws IOException {
		String data = "";
		try {
			data = FileCopyUtils.copyToString(reader);
		} catch (final IOException e) {
			LOGGER.error("Cannot read: ", e);
		}
		return alertReportService.deserializeBean(data.toString());
	}

	private void handleGetRequest(final HttpServletRequest request,
								  final HttpServletResponse response) throws IOException {
		final HttpSession session = request.getSession(false);
		if (session != null) {
			final AlertReportWrapper alertReportWrapper = (AlertReportWrapper) session.getAttribute(REPORT_REQUEST);
			final TimeInterval timeInterval = alertReportWrapper.template.getTimeInterval();
			final String exportTimeZone = timeInterval.getTimeZone();

            alertReportWrapper.startDateTime = timeInterval.getStartDateTime();
            alertReportWrapper.endDateTime = timeInterval.getEndDateTime();

			final String fileName = createFileName(alertReportWrapper.dateFormat,
					                               alertReportWrapper.locale,
					                               timeInterval.getStartDateTime(),
					                               timeInterval.getEndDateTime());
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""	+ fileName + "\"");

			try (ReportXlsxWriter reportWriter = new ReportXlsxWriter(response.getOutputStream(), alertReportWrapper, exportTimeZone)) {
				writeReportData(reportWriter, alertReportWrapper);
			} catch (final Exception ex) {
				response.sendError(500, "Error occurred: " + ex.getMessage());
			} finally {
				response.flushBuffer();
			}
		}
	}

	private void handlePostRequest(final HttpServletRequest request) throws IOException {
		final AlertReportWrapper alertReportWrapper = getAlertReportWrapper(request.getReader());
		request.getSession(true).setAttribute(REPORT_REQUEST, alertReportWrapper);
	}

	@Override
	public void handleRequest(final HttpServletRequest request,
							  final HttpServletResponse response) throws ServletException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			handlePostRequest(request);
		} else if ("GET".equalsIgnoreCase(request.getMethod())) {
			handleGetRequest(request, response);
		}
	}

	public void setAlertReportService(final AlertReportService alertReportService) {
		this.alertReportService = alertReportService;
	}

    public void setAlertHistoryService(AlertHistoryService alertHistoryService) {
        this.alertHistoryService = alertHistoryService;
    }

	private void writeReportData(final ReportXlsxWriter reportWriter,
								 final AlertReportWrapper alertReportWrapper) throws Exception {

		for (int startPosition = 0;; startPosition += stepCount) {
            final TimeInterval interval = alertReportWrapper.template.getTimeInterval();

			final List<MAlertReport> reports = alertReportService.getAlertReports(alertReportWrapper.template.getSourceKeys(),
                                                                                  interval,
							                                                      alertReportWrapper.template.getCriterion(),
							                                                      alertReportWrapper.template.getOrder(),
							                                                      startPosition,
					                                                              (int) stepCount);
            final Map<Long, List<MAlertUpdate>> comments = alertHistoryService.getAlertReportComments(reports,
					                                                                                  interval.getStartDateTime(),
					                                                                                  interval.getEndDateTime());
			reportWriter.insertRecords(reports, comments);
			if (reports.size() < stepCount) {
				break;
			}
		}
	}
}

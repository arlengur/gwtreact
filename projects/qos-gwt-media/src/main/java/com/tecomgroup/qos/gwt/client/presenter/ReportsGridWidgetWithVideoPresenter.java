/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.Date;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent.DownloadVideoEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.ExportVideoPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.AddAnalyticsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.ReportsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.AlertReportRetrieverAsync;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author kunilov.p
 *
 */
public class ReportsGridWidgetWithVideoPresenter
		extends
			ReportsGridWidgetPresenter implements DownloadVideoEventHandler {

	public static interface MyView extends ReportsGridWidgetPresenter.MyView {

		void showPlayerDialog(MRecordedStream stream, Date startDateTime,
				Date endDateTime);
	}

	private final ExportVideoPresenter exportVideoPresenter;

	private final MediaAgentServiceAsync mediaAgentService;

	private static final String MAX_RECORD_DURATION_IN_MIN_FOR_REPORT_PROPERTY = "client.max.record.duration.in.min.for.report";

	private static final String RECORD_SHIFT_IN_SEC_FOR_REPORT_PROPERTY = "client.record.shift.in.sec.for.report";

	private final long maxRecordDurationInMin;

	private final long recordShift;

	@Inject
	public ReportsGridWidgetWithVideoPresenter(
			final EventBus eventBus,
			final MyView view,
			final AlertReportRetrieverAsync alertReportRetriever,
			final ExportVideoPresenter exportVideoPresenter,
			final MediaAgentServiceAsync mediaAgentService,
			final QoSMessages messages,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final UserServiceAsync userService,
			final TaskRetrieverAsync taskRetriever,
			final AddAnalyticsToDashboardWidgetPresenter addAnalyticsToDashboardWidgetPresenter) {
		super(eventBus, view, alertReportRetriever, messages,
				loadTemplatePresenter, saveTemplatePresenter, userService,
				taskRetriever, addAnalyticsToDashboardWidgetPresenter);
		this.mediaAgentService = mediaAgentService;
		this.exportVideoPresenter = exportVideoPresenter;

		getView().setUiHandlers(this);

		final Map<String, Object> clientProperties = AppUtils
				.getClientProperties();
		maxRecordDurationInMin = Long.parseLong(((String) clientProperties
				.get(MAX_RECORD_DURATION_IN_MIN_FOR_REPORT_PROPERTY)).trim());

		recordShift = Long.parseLong(((String) clientProperties
				.get(RECORD_SHIFT_IN_SEC_FOR_REPORT_PROPERTY)).trim())
				* TimeConstants.MILLISECONDS_PER_SECOND;

		addHandler(ExportVideoEvent.TYPE, this);
	}

	public void loadRelatedVideo(final MAlertReport alertReport) {
		mediaAgentService
				.getRelatedStream(
						alertReport.getAlert(),
						new AutoNotifyingAsyncCallback<MRecordedStream>(
								messages.unableToLoadRecordedStreamAssociatedWithAlert(),
								true) {
							@Override
							protected void success(final MRecordedStream stream) {
								if (stream != null) {
									// form start date
									final long alertReportStartTimestamp = alertReport
											.getStartDateTime().getTime();
									final Date startDateTime = new Date(
											alertReportStartTimestamp
													- recordShift);

									// form end date
									long alertReportEndTimestamp;
									if (alertReport.getEndDateTime() != null) {
										alertReportEndTimestamp = alertReport
												.getEndDateTime().getTime();
									} else {
										alertReportEndTimestamp = System
												.currentTimeMillis();
									}

									final long maxRecordDuration = maxRecordDurationInMin
											* TimeConstants.MILLISECONDS_PER_MINUTE;
									if (alertReport.getDuration() > maxRecordDuration) {
										alertReportEndTimestamp = alertReportStartTimestamp
												+ maxRecordDuration;
										AppUtils.showInfoMessage(messages
												.videoLengthIsMoreThanDefault(maxRecordDurationInMin));
									}

									final Date endDateTime = new Date(
											alertReportEndTimestamp
													+ recordShift);

									getView().<MyView> cast().showPlayerDialog(
											stream, startDateTime, endDateTime);
								} else {
									AppUtils.showInfoWithConfirmMessage(messages
											.noRecordedStreamAssociatedWithAlert());
								}
							}
						});
	}

	@Override
	public void onExport(final ExportVideoEvent event) {
		if (isVisible()) {
			exportVideoPresenter.showDialog(event.getUrl(), event.getTaskKey(), event.getTaskDisplayName());
			addToPopupSlot(exportVideoPresenter, false);
		}
	}
}

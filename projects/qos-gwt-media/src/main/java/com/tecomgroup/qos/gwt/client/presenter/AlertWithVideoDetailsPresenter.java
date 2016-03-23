/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.Date;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent.DownloadVideoEventHandler;
import com.tecomgroup.qos.gwt.client.presenter.AlertWithVideoDetailsPresenter.MyProxy;
import com.tecomgroup.qos.gwt.client.presenter.AlertWithVideoDetailsPresenter.MyView;
import com.tecomgroup.qos.gwt.client.presenter.widget.ExportVideoPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AlertCommentsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.SingleAlertHistoryGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.AlarmsGatekeeper;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;

/**
 * @author abondin
 * 
 */
public class AlertWithVideoDetailsPresenter
		extends
			AlertDetailsPresenter<MyView, MyProxy>
		implements
			DownloadVideoEventHandler {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.alertDetails)
	@UseGatekeeper(AlarmsGatekeeper.class)
	public static interface MyProxy
			extends
				ProxyPlace<AlertWithVideoDetailsPresenter> {

	}

	public static interface MyView
			extends
				AlertDetailsPresenter.AlertDetailsView {

		void destroyPlayer();

		void showPlayerDialog(MRecordedStream stream, Date startDateTime,
				Date endDateTime);
	}

	private final MediaAgentServiceAsync mediaAgentService;

	private static final String MAX_RECORD_DURATION_IN_MIN_FOR_ALERT_PROPERTY = "client.max.record.duration.in.min.for.alert";

	private static final String RECORD_SHIFT_IN_SEC_FOR_ALERT_PROPERTY = "client.record.shift.in.sec.for.alert";

	private final long maxRecordDurationInMin;

	private final long recordShift;

	private final ExportVideoPresenter exportVideoPresenter;

	@Inject
	public AlertWithVideoDetailsPresenter(
			final EventBus eventBus,
			final MyView view,
			final MyProxy proxy,
			final SingleAlertHistoryGridWidgetPresenter singleAlertHistoryGridWidgetPresenter,
			final ChartWidgetPresenter chartWidgetPresenter,
			final AlertCommentsGridWidgetPresenter alertCommentsGridWidgetPresenter,
			final PropertyGridWidgetPresenter propertiesGridWidgetPresenter,
			final ExportVideoPresenter exportVideoPresenter,
			final MediaAgentServiceAsync mediaAgentService,
			@Named("clientProperties") final Map<String, Object> clientProperties,
			final AlertServiceAsync alertService,
			final TaskRetrieverAsync taskRetriever) {
		super(eventBus, view, proxy, singleAlertHistoryGridWidgetPresenter,
				chartWidgetPresenter, alertCommentsGridWidgetPresenter,
				propertiesGridWidgetPresenter, alertService, taskRetriever,
				clientProperties);
		this.mediaAgentService = mediaAgentService;
		this.exportVideoPresenter = exportVideoPresenter;

		maxRecordDurationInMin = Long.parseLong(((String) clientProperties.get(MAX_RECORD_DURATION_IN_MIN_FOR_ALERT_PROPERTY)).trim());

		recordShift = Long.parseLong(((String) clientProperties.get(RECORD_SHIFT_IN_SEC_FOR_ALERT_PROPERTY)).trim()) * TimeConstants.MILLISECONDS_PER_SECOND;

		addHandler(ExportVideoEvent.TYPE, this);
	}

	@Override
	protected void onAlertLoaded(final MAlert alert) {
		super.onAlertLoaded(alert);

		if (initialAlertLoad) {
			mediaAgentService.getRelatedStream(
					alert,
					new AutoNotifyingAsyncLogoutOnFailureCallback<MRecordedStream>(messages
							.unableToLoadRecordedStreamAssociatedWithAlert(),
							true) {
						@Override
						protected void success(final MRecordedStream stream) {
							if (stream != null) {
								// form start date
								final long alertStartTimestamp = alert.getSeverityChangeDateTime().getTime();
								final Date startDateTime = new Date(alertStartTimestamp	- recordShift);

								// form end date
								long alertReportEndTimestamp = alertStartTimestamp + alert.getDuration();

								final long maxRecordDuration = maxRecordDurationInMin * TimeConstants.MILLISECONDS_PER_MINUTE;
								if (alert.getDuration() > maxRecordDuration) {
									alertReportEndTimestamp = alertStartTimestamp + maxRecordDuration;
									AppUtils.showInfoMessage(messages.videoLengthIsMoreThanDefault(maxRecordDurationInMin));
								}

								final Date endDateTime = new Date(alertReportEndTimestamp + recordShift);

								getView().showPlayerDialog(stream, startDateTime, endDateTime);
							}
						}
					});
		}
	}

	@Override
	public void onExport(final ExportVideoEvent event) {
		if (isVisible()) {
			exportVideoPresenter.showDialog(event.getUrl(), event.getTaskKey(), event.getTaskDisplayName());
			addToPopupSlot(exportVideoPresenter, false);
		}
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().destroyPlayer();
	}
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.info.Info;
import com.tecomgroup.qos.gwt.client.event.AddNavigationLinkEvent;
import com.tecomgroup.qos.gwt.client.gin.desktop.DesktopQoSMediaGinjector;
import com.tecomgroup.qos.gwt.client.i18n.MediaMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;

import com.tecomgroup.qos.domain.rbac.PermissionScope;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DesktopQoSMedia extends QoSEntryPoint {

	public static native void exportNativeMethods() /*-{ 
		$wnd.printDefaultThresholdColorsMessage = $entry(@com.tecomgroup.qos.gwt.client.view.desktop.ResultsAnalyticsView::showDefaultThresholdColorsMessage());
		$wnd.printIncorrectTimeIntervalMessage = $entry(@com.tecomgroup.qos.gwt.client.view.desktop.ResultsAnalyticsView::showIncorrectTimeIntervalMessage(D));		
		$wnd.printMessage = $entry(@com.tecomgroup.qos.gwt.client.utils.AppUtils::showInfoMessage(Ljava/lang/String;));
		$wnd.timeIntervalChanged = $entry(@com.tecomgroup.qos.gwt.client.presenter.ChartOptionsJSCallbacks::timeIntervalChanged(Ljava/lang/String;DD));
		$wnd.chartTypeChanged = $entry(@com.tecomgroup.qos.gwt.client.presenter.ChartOptionsJSCallbacks::chartTypeChanged(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;));
		$wnd.chartAutoscalingOptionStateChanged = $entry(@com.tecomgroup.qos.gwt.client.presenter.ChartOptionsJSCallbacks::chartAutoscalingOptionStateChanged(Ljava/lang/String;Z));
		$wnd.chartCaptionsOptionStateChanged = $entry(@com.tecomgroup.qos.gwt.client.presenter.ChartOptionsJSCallbacks::chartCaptionsOptionStateChanged(Ljava/lang/String;Z));
		$wnd.chartThresholdsOptionStateChanged = $entry(@com.tecomgroup.qos.gwt.client.presenter.ChartOptionsJSCallbacks::chartThresholdsOptionStateChanged(Ljava/lang/String;Z));
		$wnd.chartBuilt = $entry(@com.tecomgroup.qos.gwt.client.presenter.ChartOptionsJSCallbacks::chartBuilt(Ljava/lang/String;));
        $wnd.changeDateTimeCallback = $entry(@com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeWidget::changeDateTimeCallback(Ljava/lang/String;Ljava/lang/String;));
	}-*/;

	public static void printInfoMessage(final String message) {
		Info.display(messages.message(), message);
	}

	public final DesktopQoSMediaGinjector ginjector = GWT
			.create(DesktopQoSMediaGinjector.class);

	protected static MediaMessages messages = GWT.create(MediaMessages.class);

	@Override
	public DesktopQoSMediaGinjector getInjector() {
		return ginjector;
	}

	@Override
	protected void loadNavigationLinks(final EventBus eventBus) {
		super.loadNavigationLinks(eventBus);

		if (AppUtils.isPermitted(PermissionScope.POLICIES) 
			|| AppUtils.isPermitted(PermissionScope.POLICIES_ADVANCED)) {
			final AddNavigationLinkEvent addPoliciesLinkEvent = new AddNavigationLinkEvent();
			addPoliciesLinkEvent.setPath(QoSNameTokens.policies);
			addPoliciesLinkEvent.setDisplayName(messages.policies());
			addPoliciesLinkEvent.setMinScreenWidth(ClientConstants.MOBILE_SCREEN_WIDTH);

			eventBus.fireEvent(addPoliciesLinkEvent);
		}

		final AddNavigationLinkEvent event = new AddNavigationLinkEvent();

		if(AppUtils.isPermitted(PermissionScope.RECORDING_SCHEDULE)) {
			event.setPath(QoSNameTokens.recordSchedule);
			event.setDisplayName(messages.navigationRecordSchedule());
			eventBus.fireEvent(event);
		}

		if(AppUtils.isPermitted(PermissionScope.REPORTS)) {
			event.setPath(QoSNameTokens.reports);
			event.setDisplayName(messages.reports());
			eventBus.fireEvent(event);
		}

		if(AppUtils.isShowRecordedVideoPage() 
		   && AppUtils.isPermitted(PermissionScope.RECORDED_VIDEO)) {
			event.setPath(QoSMediaNameTokens.recordedVideo);
			event.setDisplayName(messages.navigationRecorded());
			event.setIcon(MediaIcons.VIDEO);
			eventBus.fireEvent(event);
		}

		if (AppUtils.isShowLiveVideoPage() 
			&& AppUtils.isPermitted(PermissionScope.LIVE_VIDEO)) {
			event.setPath(QoSMediaNameTokens.mediaPlayer);
			event.setDisplayName(messages.navigationVideo());
			event.setIcon(MediaIcons.VIDEO);
			eventBus.fireEvent(event);
		}

		if(AppUtils.isPermitted(PermissionScope.CHARTS)) {
			event.setPath(QoSMediaNameTokens.chartResults);
			event.setDisplayName(messages.navigationAnalytics());
			event.setIcon(GeneralIcons.CHART);
			eventBus.fireEvent(event);
		}

		if(AppUtils.isPermitted(PermissionScope.ALERTS)) {
			event.setPath(QoSNameTokens.alerts);
			event.setDisplayName(messages.alerts());
			eventBus.fireEvent(event);
		}

		if(AppUtils.isPermitted(PermissionScope.MAP)) {
			event.setPath(QoSNameTokens.gis);
			event.setDisplayName(messages.navigationMap());
			eventBus.fireEvent(event);
		}

		if(AppUtils.isShowChannelViewPage() 
		   && AppUtils.isPermitted(PermissionScope.CHANNEL_VIEW)) {
			event.setPath(QoSNameTokens.channelView);
			event.setDisplayName(messages.navigationChannelView());
			eventBus.fireEvent(event);
		}

		if(AppUtils.isPermitted(PermissionScope.MAIN)) {
			event.setPath(QoSNameTokens.dashboard);
			event.setDisplayName(messages.navigationDashboard());
			eventBus.fireEvent(event);
		}

	}

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		exportNativeMethods();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				load();
			}
		});
	}

}

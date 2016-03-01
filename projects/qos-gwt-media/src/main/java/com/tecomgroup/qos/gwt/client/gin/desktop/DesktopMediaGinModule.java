/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin.desktop;

import com.google.inject.Singleton;
import com.tecomgroup.qos.gwt.client.DashboardWidgetFactory;
import com.tecomgroup.qos.gwt.client.MediaDashboardWidgetFactory;
import com.tecomgroup.qos.gwt.client.gin.MediaGinModule;
import com.tecomgroup.qos.gwt.client.model.template.MediaTemplateFactory;
import com.tecomgroup.qos.gwt.client.model.template.TemplateFactory;
import com.tecomgroup.qos.gwt.client.player.DefaultPlayerFactory;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.presenter.*;
import com.tecomgroup.qos.gwt.client.presenter.widget.ExportVideoPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.ReportsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.view.desktop.*;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ExportVideoView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.report.ReportsGridWidgetWithVideoView;

/**
 * @author ivlev.e
 * 
 */
public class DesktopMediaGinModule extends MediaGinModule {

	@Override
	protected void configure() {
		bindPresenter(LiveVideoPresenter.class,
				LiveVideoPresenter.MyView.class, LiveVideoView.class,
				LiveVideoPresenter.MyProxy.class);
		bindPresenter(RecordedVideoPresenter.class,
				RecordedVideoPresenter.MyView.class, RecordedVideoView.class,
				RecordedVideoPresenter.MyProxy.class);

		bindPresenterWidget(AddLiveVideoPresenter.class,
				AddLiveVideoPresenter.MyView.class, AddLiveVideoView.class);

		bindPresenterWidget(AddRecordedVideoPresenter.class,
				AddRecordedVideoPresenter.MyView.class,
				AddRecordedVideoView.class);

		bindPresenterWidget(ExportVideoPresenter.class,
							ExportVideoPresenter.MyView.class,
							ExportVideoView.class);

		bind(PlayerFactory.class).to(DefaultPlayerFactory.class).in(
				Singleton.class);
		bind(TemplateFactory.class).to(MediaTemplateFactory.class).in(
				Singleton.class);
		bind(DashboardWidgetFactory.class)
				.to(MediaDashboardWidgetFactory.class).in(Singleton.class);

		bindPresenter(AlertWithVideoDetailsPresenter.class,
				AlertWithVideoDetailsPresenter.MyView.class,
				AlertWithVideoDetailsView.class,
				AlertWithVideoDetailsPresenter.MyProxy.class);

		bindPresenterWidget(ReportsGridWidgetWithVideoPresenter.class,
				ReportsGridWidgetWithVideoPresenter.MyView.class,
				ReportsGridWidgetWithVideoView.class);

		bindPresenter(MediaUserProfilePresenter.class,
				UserProfilePresenter.MyView.class, UserProfileView.class,
				MediaUserProfilePresenter.MyProxy.class);

		// use ReportsGridWidgetWithVideoPresenter implementation of
		// ReportsGridWidgetPresenter in MediaModule
		bind(ReportsGridWidgetPresenter.class).to(
				ReportsGridWidgetWithVideoPresenter.class);
	}
}

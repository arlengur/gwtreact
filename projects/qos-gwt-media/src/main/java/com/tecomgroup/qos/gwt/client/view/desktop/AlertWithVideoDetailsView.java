/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.Date;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.presenter.AlertWithVideoDetailsPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.PlayerWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedPlayerWidget;

/**
 * @author abondin
 * 
 */
public class AlertWithVideoDetailsView extends SimpleAlertDetailsView
		implements
			AlertWithVideoDetailsPresenter.MyView {

	private static String ALERT_DETAILS_PLAYER_ID = "qos.alert.details.player";

	private final RecordedPlayerWidget playerWidget;

	private final BorderLayoutData playerWidgetLayout;

	/**
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param messages
	 */
	@Inject
	public AlertWithVideoDetailsView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final PlayerFactory playerFactory, final QoSMessages messages) {
		super(appearanceFactoryProvider, dialogFactory, messages);
		playerWidget = new RecordedPlayerWidget(ALERT_DETAILS_PLAYER_ID,
				playerFactory, appearanceFactoryProvider.get(), messages);
		playerWidgetLayout = createPlayerWidgetLayout();
	}

	private BorderLayoutData createPlayerWidgetLayout() {
		final BorderLayoutData playerWidgetLayout = new BorderLayoutData(
				PlayerWidget.VIDEO_PANEL_WIDTH);
		playerWidgetLayout.setMinSize(PlayerWidget.VIDEO_PANEL_WIDTH);
		playerWidgetLayout.setMaxSize(PlayerWidget.VIDEO_PANEL_WIDTH);
		playerWidgetLayout.setFloatable(false);
		playerWidgetLayout.setMargins(new Margins(0, 0, 0, 5));
		return playerWidgetLayout;
	}

	@Override
	public void destroyPlayer() {
		hidePlayerWidget();
		playerWidget.destroyPlayer();
	}

	private void hidePlayerWidget() {
		final Widget playerWidgetContainter = northInnerBorderLayoutContainer
				.getWestWidget();
		if (playerWidgetContainter != null) {
			northInnerBorderLayoutContainer.remove(playerWidgetContainter);
		}
	}

	@Override
	public void showPlayerDialog(final MRecordedStream stream,
			final Date startDateTime, final Date endDateTime) {
		if (stream != null) {
			showPlayerWidget();
			playerWidget.show(stream, startDateTime, endDateTime);
		}
	}

	private void showPlayerWidget() {
		northInnerBorderLayoutContainer.setWestWidget(
				playerWidget.getPlayerContainer(), playerWidgetLayout);
		northInnerBorderLayoutContainer.forceLayout();
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.inject.Inject;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.TileContentElement;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class MediaDashboardWidgetFactory extends DefaultDashboardWidgetFactory {

	private final PlayerFactory playerFactory;

	@Inject
	public MediaDashboardWidgetFactory(final PlayerFactory playerFactory,
			final QoSEventService eventService,
			final UserServiceAsync userService,
			final AgentServiceAsync agentService,
			final AlertServiceAsync alertService, final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory) {
		super(eventService, userService, agentService, alertService, messages,
				appearanceFactoryProvider, dialogFactory);
		this.playerFactory = playerFactory;
	}

	private TileContentElement createLiveStreamWidget(
			final LiveStreamWidget model) {
		return new LiveVideoPlayerDelegate(model, playerFactory,
				appearanceFactory, messages);
	}

	@Override
	public TileContentElement createWidget(final DashboardWidget model) {
		TileContentElement tileContentElement = super.createWidget(model);
		if (tileContentElement == null) {
			if (model instanceof LiveStreamWidget) {
				tileContentElement = createLiveStreamWidget((LiveStreamWidget) model);
			}
		}
		return tileContentElement;
	}
}

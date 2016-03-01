/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.gis;

import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.dashboard.DashboardMapWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AddAgentsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.MapInfo;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 *
 */
public class AddMapToDashboardWidgetPresenter
		extends
			AddAgentsToDashboardWidgetPresenter {

	public static interface MyView
			extends
				AddAgentsToDashboardWidgetPresenter.MyView {
	}

	private MapInfo mapInfo;

	@Inject
	public AddMapToDashboardWidgetPresenter(final EventBus eventBus,
			final MyView view, final AgentServiceAsync agentService,
			final QoSMessages messages) {
		super(eventBus, view, agentService, messages);
	}

	public void actionCreateWidget(final Set<String> agentKeys) {
		final DashboardMapWidget widget = new DashboardMapWidget();
		widget.setAgentKeys(agentKeys);
		fillWidgetSize(widget);
		fillWidgetTitle(widget);
		widget.setForAllAgents(!SimpleUtils.isNotNullAndNotEmpty(agentKeys));
		// don't save current center and zoom. They will be updated by user
		// on the dashboard page.
		// widget.setCenter(mapInfo.getCenter());
		// widget.setZoom(mapInfo.getZoom());
		getEventBus().fireEvent(new AddWidgetToDashboardEvent(widget));
	}

	public void setMapInfo(final MapInfo mapInfo) {
		this.mapInfo = mapInfo;
	}
}

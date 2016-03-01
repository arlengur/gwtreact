/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.inject.Inject;
import com.tecomgroup.qos.dashboard.*;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.EmergencyAgentsTopClientWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.LatestAlertsClientWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.chart.DashboardBitrateClientWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.chart.DashboardChartClientWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.TileContentElement;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.DashboardMapClientWidget;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class DefaultDashboardWidgetFactory implements DashboardWidgetFactory {

	protected QoSEventService eventService;

	protected final UserServiceAsync userService;

	protected final AgentServiceAsync agentService;

	protected final AlertServiceAsync alertService;

	protected final AppearanceFactory appearanceFactory;

	protected final DialogFactory dialogFactory;

	protected final QoSMessages messages;

	@Inject
	public DefaultDashboardWidgetFactory(final QoSEventService eventService,
			final UserServiceAsync userService,
			final AgentServiceAsync agentService,
			final AlertServiceAsync alertService, final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory) {
		this.eventService = eventService;
		this.userService = userService;
		this.agentService = agentService;
		this.alertService = alertService;
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.dialogFactory = dialogFactory;
	}

	private TileContentElement createEmergencyAgentsTopWidget(
			final EmergencyAgentsTopWidget model) {
		return new EmergencyAgentsTopClientWidget(userService, model, messages);
	}

	private TileContentElement createLatestAlertsWidget(
			final LatestAlertsWidget latestAlertsWidget) {
		return new LatestAlertsClientWidget(userService, eventService,
				latestAlertsWidget, messages, appearanceFactory);
	}

	private TileContentElement createMapWidget(
			final DashboardMapWidget mapWidget) {
		return new DashboardMapClientWidget(eventService, agentService,
				alertService, mapWidget, new AgentGisWidget(dialogFactory,
						messages));
	}

    private TileContentElement createChartWidget(
            final DashboardChartWidget dashboardChartWidget) {
        return new DashboardChartClientWidget(dashboardChartWidget, userService, messages);
    }

    private TileContentElement createBitrateWidget(
            final DashboardBitrateWidget model) {
        return new DashboardBitrateClientWidget(model);
    }

	@Override
	public TileContentElement createWidget(final DashboardWidget dashboardWidget) {
		TileContentElement tileContentElement = null;
		if (dashboardWidget instanceof LatestAlertsWidget) {
			tileContentElement = createLatestAlertsWidget((LatestAlertsWidget) dashboardWidget);
		} else if (dashboardWidget instanceof DashboardMapWidget) {
			tileContentElement = createMapWidget((DashboardMapWidget) dashboardWidget);
		} else if (dashboardWidget instanceof EmergencyAgentsTopWidget) {
			tileContentElement = createEmergencyAgentsTopWidget((EmergencyAgentsTopWidget) dashboardWidget);
		} else if (dashboardWidget instanceof DashboardChartWidget) {
            tileContentElement = createChartWidget((DashboardChartWidget) dashboardWidget);
        } else if (dashboardWidget instanceof DashboardBitrateWidget) {
            tileContentElement = createBitrateWidget((DashboardBitrateWidget) dashboardWidget);
        }

		return tileContentElement;
	}

}

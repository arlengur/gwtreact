/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.gis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.Statefull;
import com.tecomgroup.qos.dashboard.DashboardMapWidget;
import com.tecomgroup.qos.domain.GISPosition;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.event.StatusEvent;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectionListener;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.AbstractWidgetTileContentElement;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.gwt.shared.event.filter.AgentStatusEventFilter;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * A GUI widget to show {@link DashboardMapWidget}.
 *
 * @author kunilov.p
 *
 */
public class DashboardMapClientWidget
		extends
			AbstractWidgetTileContentElement<DashboardMapWidget>
		implements
			QoSEventListener,
			AgentSelectionListener,
			Statefull {

	private final QoSEventService eventService;

	private final AgentGisWidget agentGisWidget;

	private final AgentServiceAsync agentService;

	private final AlertServiceAsync alertService;

	private final AsyncCallback<List<MAgent>> loadMapDataCallback = new AsyncCallback<List<MAgent>>() {

		@Override
		public void onFailure(final Throwable caught) {
			AppUtils.showInfoMessage("Cannot load data for widget: "
					+ model.getKey());
		}

		@Override
		public void onSuccess(final List<MAgent> agents) {
			agentGisWidget.updateAgents(agents);
			updateAgentStatuses(Source.convertAgentsToSources(agents));
			loadState();
		}
	};

	public DashboardMapClientWidget(final QoSEventService eventService,
			final AgentServiceAsync agentService,
			final AlertServiceAsync alertService,
			final DashboardMapWidget model, final AgentGisWidget agentGisWidget) {
		super(model);
		this.eventService = eventService;
		this.alertService = alertService;
		this.agentService = agentService;
		this.agentGisWidget = agentGisWidget;
		agentGisWidget.setParentContainerKey(model.getKey());
	}

	@Override
	public void agentSelected(final MAgent agent) {
		AgentGisWidget.navigateToProbeStatusPage(agent.getKey());
	}

	@Override
	public void clearState() {
		model.setCenter(null);
		model.setZoom(null);
	}

	@Override
	public void dispose() {
		eventService.unsubscribe(StatusEvent.class, this);
		agentGisWidget.removeListener(this);
	}

	@Override
	public Widget getContentElement() {
		return agentGisWidget.asWidget();
	}

	@Override
	public void initialize() {
		loadMapData();
		eventService
				.subscribe(
						StatusEvent.class,
						this,
						new AgentStatusEventFilter.MultipleOrAllAgentsStatusEventFilter(
								model.getAgentKeys()));
		agentGisWidget.addListener(this);
	}

	private void loadMapData() {
		final Set<String> agentKeys = model.getAgentKeys();
		if (SimpleUtils.isNotNullAndNotEmpty(agentKeys)) {
			agentService.getAgentsByKeys(agentKeys, loadMapDataCallback);
		} else {
			agentService.getAllAgents(loadMapDataCallback);
		}
	}

	@Override
	public void loadState() {
		final GISPosition center = model.getCenter();
		if (center != null) {
			agentGisWidget.setCenter(center);
		}

		final Integer zoomLevel = model.getZoom();
		if (zoomLevel != null) {
			agentGisWidget.zoomTo(zoomLevel);
		}
	}

	@Override
	public void onServerEvent(final AbstractEvent event) {
		final StatusEvent statusEvent = (StatusEvent) event;
		agentGisWidget.updateAgent(statusEvent.getSourceKey(),
				statusEvent.getSeverity());
	}

	@Override
	public void refresh() {
		agentGisWidget.refresh();
	}

	@Override
	public void saveState() {
		model.setCenter(agentGisWidget.getCenter());
		model.setZoom(agentGisWidget.getZoom());
	}

	private void updateAgentStatuses(final Collection<Source> agents) {
		alertService.getStatus(agents, true,
				new AsyncCallback<Map<Source, PerceivedSeverity>>() {

					@Override
					public void onFailure(final Throwable caught) {
						AppUtils.showInfoMessage("Cannot update agent statuses for widget: "
								+ model.getKey());
					}

					@Override
					public void onSuccess(
							final Map<Source, PerceivedSeverity> agentStatuses) {
						agentGisWidget.updateAgentStatuses(agentStatuses);
					}
				});
	}
}

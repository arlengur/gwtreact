/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.domain.GISPosition;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.event.StatusEvent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.presenter.widget.gis.AddMapToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.ProbeMapGatekeeper;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectionListener;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.MapInfo;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.gwt.shared.event.filter.AgentStatusEventFilter;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertServiceAsync;

/**
 * 
 * Показывает Блоки Контроля на карте
 * 
 * @author abondin
 * 
 */
public class GisPresenter
		extends
			Presenter<GisPresenter.MyView, GisPresenter.MyProxy>
		implements
			UiHandlers,
			QoSEventListener,
			AgentSelectionListener,
			MapInfo{

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.gis)
	@UseGatekeeper(ProbeMapGatekeeper.class)
	public static interface MyProxy extends ProxyPlace<GisPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<GisPresenter>,
				MapInfo {
		void bind();

		/**
		 * 
		 * @param agentName
		 * @param severity
		 */
		void updateAgent(String agentName, PerceivedSeverity severity);

		/**
		 * Обновление блоков контроля на карте
		 * 
		 * @param agents
		 */
		void updateAgents(List<MAgent> agents);

		/**
		 * Обновление статусов блоков контроля
		 * 
		 * @param agentStatuses
		 */
		void updateAgentStatuses(Map<Source, PerceivedSeverity> agentStatuses);
	}

	private final AgentServiceAsync agentService;

	private final AlertServiceAsync alertService;

	private final QoSEventService eventService;

	private final AddMapToDashboardWidgetPresenter addMapToDashboardWidget;

	@Inject
	public GisPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final AgentServiceAsync agentService,
			final AlertServiceAsync alertService,
			final QoSEventService eventService,
			final AddMapToDashboardWidgetPresenter addMapToDashboardWidget) {
		super(eventBus, view, proxy);
		this.agentService = agentService;
		this.alertService = alertService;
		this.eventService = eventService;
		this.addMapToDashboardWidget = addMapToDashboardWidget;
		addMapToDashboardWidget.setMapInfo(this);
		getView().setUiHandlers(this);
	}

	@Override
	public void agentSelected(final MAgent agent) {
		AgentGisWidget.navigateToProbeStatusPage(agent.getKey());
	}

	public void displayAddAgentsToDashboardDialog() {
		addToPopupSlot(addMapToDashboardWidget, false);
	}

	@Override
	public GISPosition getCenter() {
		return getView().getCenter();
	}

	@Override
	public int getZoom() {
		return getView().getZoom();
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().bind();
	}

	@Override
	protected void onHide() {
		super.onHide();
		eventService.unsubscribe(StatusEvent.class, this);
	}

	@Override
	protected void onReset() {
		super.onReset();
		updateAgents();
	}

	@Override
	public void onServerEvent(final AbstractEvent event) {
		final StatusEvent statusEvent = (StatusEvent) event;
		getView().updateAgent(statusEvent.getSourceKey(),
				statusEvent.getSeverity());
	}

	@Override
	protected void revealInParent() {
		eventService.subscribe(StatusEvent.class, this,
				new AgentStatusEventFilter());
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}

	/**
	 * Обновление местоположений блоков контроля на карте
	 */
	private void updateAgents() {
		agentService
				.getAllAgents(new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgent>>() {
					@Override
					protected void success(final List<MAgent> agents) {
						getView().updateAgents(agents);
						updateAgentStatuses(Source
								.convertAgentsToSources(agents));
					}
				});
	}

	/**
	 * Обновление статусов блоков контроля
	 */
	private void updateAgentStatuses(final Collection<Source> agents) {
		// stub method
		// TODO implement status getting
		alertService
				.getStatus(
						agents,
						true,
						new AutoNotifyingAsyncLogoutOnFailureCallback<Map<Source, PerceivedSeverity>>() {

							@Override
							protected void success(
									final Map<Source, PerceivedSeverity> agentStatuses) {
								getView().updateAgentStatuses(agentStatuses);
							}
						});

	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.event.StatusEvent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.GridPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentResultsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AgentAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.AgentPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.gwt.shared.event.filter.AgentStatusEventFilter;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class AgentStatusPresenter
		extends
			Presenter<AgentStatusPresenter.MyView, AgentStatusPresenter.MyProxy>
		implements
			UiHandlers,
			QoSEventListener {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.agentStatus)
	public static interface MyProxy extends ProxyPlace<AgentStatusPresenter> {
	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<AgentStatusPresenter> {

		void displayAgent(MAgent agent);

		/**
		 * 
		 * @param agentName
		 * @param severity
		 */
		void updateAgent(String agentName, PerceivedSeverity severity);

	}

	private final Map<String, GridPresenter> tabs = new LinkedHashMap<String, GridPresenter>();

	private String agentName;

	private String selectedTab;

	private final AgentServiceAsync agentService;

	private final AlertServiceAsync alertService;

	private final QoSEventService eventService;

	private final QoSMessages messages;

	private final AgentAlertsGridWidgetPresenter alertsGridWidgetPresenter;

	private final AgentResultsGridWidgetPresenter resultsGridWidgetPresenter;

	private final AgentPoliciesGridWidgetPresenter policiesGridWidgetPresenter;

	@Inject
	public AgentStatusPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final QoSMessages messages,
			final AgentServiceAsync agentService,
			final QoSEventService eventService,
			final AlertServiceAsync alertService,
			final AgentAlertsGridWidgetPresenter alertsGridWidgetPresenter,
			final AgentResultsGridWidgetPresenter resultsGridWidgetPresenter,
			final AgentPoliciesGridWidgetPresenter policiesGridWidgetPresenter) {
		super(eventBus, view, proxy);
		this.agentService = agentService;
		this.eventService = eventService;
		this.alertService = alertService;
		this.messages = messages;
		this.alertsGridWidgetPresenter = alertsGridWidgetPresenter;
		this.resultsGridWidgetPresenter = resultsGridWidgetPresenter;
		this.policiesGridWidgetPresenter = policiesGridWidgetPresenter;
		tabs.put(messages.alerts(), alertsGridWidgetPresenter);
		tabs.put(messages.currentResults(), resultsGridWidgetPresenter);
		tabs.put(messages.policies(), policiesGridWidgetPresenter);
		view.setUiHandlers(this);
	}

	public void actionSelectGridPresenter(final String tabKey) {
		selectedTab = tabKey;
		final GridPresenter presenter = tabs.get(tabKey);
		if (presenter != null && agentName != null) {
			if (presenter instanceof AgentResultsGridWidgetPresenter) {
				resultsGridWidgetPresenter.reload(false);
			} else {
				resultsGridWidgetPresenter.stopResultPolling();
				presenter.reload(false);
			}
		}
	}

	private void loadAgent(final String agentKey) {
		agentService.getAgentByKey(
				agentKey,
				new AutoNotifyingAsyncLogoutOnFailureCallback<MAgent>(messages
						.agentLoadingFail(), true) {

					@Override
					protected void success(final MAgent agent) {
						getView().displayAgent(agent);
						updateAgentStatus();
					}
				});
	}

	@Override
	protected void onBind() {
		super.onBind();
		for (final Map.Entry<String, GridPresenter> entry : tabs.entrySet()) {
			setInSlot(entry.getKey(), (PresenterWidget<?>) entry.getValue());
		}
	}

	@Override
	protected void onHide() {
		super.onHide();
		eventService.unsubscribe(StatusEvent.class, this);
		agentName = null;
	}

	@Override
	public void onServerEvent(final AbstractEvent event) {
		final StatusEvent statusEvent = (StatusEvent) event;
		getView().updateAgent(statusEvent.getSourceKey(),
				statusEvent.getSeverity());
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
		agentName = request.getParameter(RequestParams.agentName, null);
	}

	@Override
	protected void revealInParent() {
		eventService.subscribe(StatusEvent.class, this,
				new AgentStatusEventFilter.SingleAgentStatusEventFilter(
						agentName));
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);

		alertsGridWidgetPresenter.setAgentName(agentName);
		resultsGridWidgetPresenter.setAgentName(agentName);
		policiesGridWidgetPresenter.setAgentName(agentName);
		actionSelectGridPresenter(selectedTab);

		loadAgent(agentName);
	}

	/**
	 * Обновление статуса БК
	 */
	private void updateAgentStatus() {

		final Source agentSource = Source.getAgentSource(agentName);
		alertService
				.getStatus(
						Arrays.asList(agentSource),
						true,
						new AutoNotifyingAsyncLogoutOnFailureCallback<Map<Source, PerceivedSeverity>>() {
							@Override
							protected void success(
									final Map<Source, PerceivedSeverity> result) {

								if (result.size() > 0) {
									getView().updateAgent(agentName,
											result.get(agentSource));
								}
							}
						});

	}
}

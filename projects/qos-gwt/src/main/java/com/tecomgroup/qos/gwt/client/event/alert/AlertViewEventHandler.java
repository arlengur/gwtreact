/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MSystemComponent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.HasPostActionCallback;
import com.tecomgroup.qos.gwt.client.event.HasPostActionCallback.PostActionCallback;
import com.tecomgroup.qos.gwt.client.event.NavigateToAlertDetailsEvent;
import com.tecomgroup.qos.gwt.client.event.NavigateToAlertDetailsEvent.NavigateToAlertDetailsEventHandler;
import com.tecomgroup.qos.gwt.client.event.NavigateToResultDetailsEvent;
import com.tecomgroup.qos.gwt.client.event.NavigateToResultDetailsEvent.NavigateToResultDetailsEventHandler;
import com.tecomgroup.qos.gwt.client.event.NavigateToSourceEvent;
import com.tecomgroup.qos.gwt.client.event.NavigateToSourceEvent.NavigateToSourceEventHandler;
import com.tecomgroup.qos.gwt.client.event.alert.AcknowledgeAlertsEvent.AcknowledgeAlertsEventHandler;
import com.tecomgroup.qos.gwt.client.event.alert.ClearAlertsEvent.ClearAlertsEventHandler;
import com.tecomgroup.qos.gwt.client.event.alert.CommentAlertsEvent.CommentAlertsEventHandler;
import com.tecomgroup.qos.gwt.client.event.alert.UnacknowledgeAlertsEvent.UnacknowledgeAlertsEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AlertDetailsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.TableResultPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.SourceServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Handles alert events such as acknowledge/unacknowledge, clear, goto source
 * etc.
 * 
 * @author novohatskiy.r
 * 
 */
public class AlertViewEventHandler
		implements
			AcknowledgeAlertsEventHandler,
			UnacknowledgeAlertsEventHandler,
			ClearAlertsEventHandler,
			CommentAlertsEventHandler,
			NavigateToSourceEventHandler,
			NavigateToAlertDetailsEventHandler,
			NavigateToResultDetailsEventHandler {

	private final EventBus eventBus;
	private final PlaceManager placeManager;
	private final QoSMessages messages;

	private final AlertServiceAsync alertService;
	private final SourceServiceAsync sourceService;

	@Inject
	public AlertViewEventHandler(final EventBus eventBus,
			final PlaceManager placeManager,
			final AlertServiceAsync alertService,
			final SourceServiceAsync sourceService, final QoSMessages messages) {
		this.eventBus = eventBus;
		this.placeManager = placeManager;
		this.messages = messages;
		this.alertService = alertService;
		this.sourceService = sourceService;
		registerHandler();
	}

	private void actionNavigate(final PlaceRequest request) {
		placeManager.revealPlace(request);
	}

	private void invokeCallback(final HasPostActionCallback event,
			final Object executionResult) {
		final PostActionCallback callback = event.getCallback();
		if (callback != null) {
			callback.actionPerformed(executionResult);
		}
	}

	@Override
	public void onAcknowledgeAlerts(final AcknowledgeAlertsEvent event) {
		alertService.acknowledgeAlerts(
				SimpleUtils.alertsToIndications(event.getAlerts(),
						UpdateType.ACK),
				event.getComment(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages
						.alertAcknowledgeFail(), true) {
					@Override
					public void success(final Void result) {
						invokeCallback(event, result);
					}

					@Override
					protected void failure(Throwable caught) {
						super.failure(caught);
					}
				});
	}

	@Override
	public void onClearAlerts(final ClearAlertsEvent event) {
		alertService.clearAlerts(SimpleUtils.alertsToIndications(
				event.getAlerts(), UpdateType.OPERATOR_CLEARED), event
				.getComment(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages.alertClearFail(),
						true) {
					@Override
					protected void success(final Void result) {
						invokeCallback(event, result);
					}

					@Override
					protected void failure(Throwable caught) {
						super.failure(caught);
					}
				});
	}

	@Override
	public void onCommentAlers(final CommentAlertsEvent event) {
		alertService.commentAlerts(
				SimpleUtils.alertsToIndications(event.getAlerts(),
						UpdateType.COMMENT),
				event.getComment(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages
						.alertCommentFail(), true) {
					@Override
					protected void success(final Void result) {
						invokeCallback(event, result);
					}

					@Override
					protected void failure(Throwable caught) {
						super.failure(caught);
					}
				});
	}

	@Override
	public void onNavigateToAlertDetails(final NavigateToAlertDetailsEvent event) {
		final MAlert alert = event.getAlert();
		final PlaceRequest request = AlertDetailsPresenter
				.createAlertDetailsRequest(alert);
		actionNavigate(request);
		invokeCallback(event, null);
	}

	@Override
	public void onNavigateToResultDetails(
			final NavigateToResultDetailsEvent event) {
		final PlaceRequest request = TableResultPresenter
				.createResultRequest(event.getSource().getKey(), event
						.getParameterIdentifier().createParameterStorageKey(),
						event.getStartDateTime(), event.getEndDateTime(), event
								.getTimeZone(), event.getTimeZoneType(), event
								.getChartName());
		actionNavigate(request);
		invokeCallback(event, null);
	}

	@Override
	public void onNavigateToSource(final NavigateToSourceEvent event) {
		sourceService.getSystemComponent(
				event.getSource(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<MSystemComponent>(messages
						.rootSystemComponentLoadingFail(), true) {
					@Override
					protected void success(
							final MSystemComponent systemComponent) {
						if (systemComponent instanceof MAgent) {
							final MAgent agent = (MAgent) systemComponent;
							PlaceRequest request = new PlaceRequest(
									QoSNameTokens.agentStatus);
							request = request.with(RequestParams.agentName,
									agent.getKey());
							actionNavigate(request);
						} else {
							AppUtils.showErrorMessage("Status Page is not implemented for system component "
									+ systemComponent);
						}
						invokeCallback(event, systemComponent);
					}
				});
	}

	@Override
	public void onUnacknowledgeAlerts(final UnacknowledgeAlertsEvent event) {
		alertService.unAcknowledgeAlerts(
				SimpleUtils.alertsToIndications(event.getAlerts(),
						UpdateType.UNACK),
				event.getComment(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages
						.alertUnacknowledgeFail(), true) {
					@Override
					protected void success(final Void result) {
						invokeCallback(event, result);
					}

					@Override
					protected void failure(Throwable caught) {
						super.failure(caught);

					}
				});
	}

	protected void registerHandler() {
		eventBus.addHandler(AcknowledgeAlertsEvent.TYPE, this);
		eventBus.addHandler(UnacknowledgeAlertsEvent.TYPE, this);
		eventBus.addHandler(ClearAlertsEvent.TYPE, this);
		eventBus.addHandler(CommentAlertsEvent.TYPE, this);
		eventBus.addHandler(NavigateToSourceEvent.TYPE, this);
		eventBus.addHandler(NavigateToAlertDetailsEvent.TYPE, this);
		eventBus.addHandler(NavigateToResultDetailsEvent.TYPE, this);
	}
}

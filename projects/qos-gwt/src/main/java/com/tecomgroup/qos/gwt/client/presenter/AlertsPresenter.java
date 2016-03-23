/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.alert.RefreshAlertGridViewEvent;
import com.tecomgroup.qos.gwt.client.event.alert.RefreshAlertGridViewEvent.RefreshAlertGridViewEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.GridPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AlertsHistoryGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.DefaultAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.AlarmsGatekeeper;

/**
 * @author abondin
 * 
 */
public class AlertsPresenter
		extends
			Presenter<AlertsPresenter.MyView, AlertsPresenter.MyProxy>
		implements
			UiHandlers,
			RefreshAlertGridViewEventHandler {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.alerts)
	@UseGatekeeper(AlarmsGatekeeper.class)
	public static interface MyProxy extends ProxyPlace<AlertsPresenter> {

	}
	public static interface MyView extends View, HasUiHandlers<AlertsPresenter> {
	}

	public static Logger LOGGER = Logger.getLogger(AlertsPresenter.class
			.getName());

	private final Map<String, GridPresenter> tabs = new LinkedHashMap<String, GridPresenter>();

	private final DefaultAlertsGridWidgetPresenter alertsGridWidgetPresenter;

	private final PlaceManager placeManager;

	/**
	 * @param eventBus
	 * @param view
	 */
	@Inject
	public AlertsPresenter(
			final EventBus eventBus,
			final MyView view,
			final MyProxy proxy,
			final DefaultAlertsGridWidgetPresenter alertsGridWidgetPresenter,
			final AlertsHistoryGridWidgetPresenter alertsHistoryGridWidgetPresenter,
			final QoSMessages messages, final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.placeManager = placeManager;
		tabs.put(messages.alerts(), alertsGridWidgetPresenter);
		tabs.put(messages.alertHistory(), alertsHistoryGridWidgetPresenter);
		this.alertsGridWidgetPresenter = alertsGridWidgetPresenter;
		view.setUiHandlers(this);
		getEventBus().addHandler(RefreshAlertGridViewEvent.TYPE, this);
	}

	public void actionSelectGridPreseter(final String tabKey) {
		final GridPresenter presenter = tabs.get(tabKey);
		if (presenter != null) {
			if (presenter instanceof AlertsHistoryGridWidgetPresenter) {
				presenter.reload(true);
			} else {
				presenter.reload(false);
			}
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		for (final Map.Entry<String, GridPresenter> entry : tabs.entrySet()) {
			setInSlot(entry.getKey(), (PresenterWidget<?>) entry.getValue());
		}
	}

	@Override
	public void onRefreshEvent(final RefreshAlertGridViewEvent event) {
		if (QoSNameTokens.alerts.equals(placeManager.getCurrentPlaceRequest()
				.getNameToken())) {
			alertsGridWidgetPresenter.loadFirstPage();
		}
	}
	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
		final String templateName = request.getParameter(
				RequestParams.template, null);
		// if templateName is null, then not overwrite selectedTemplateName
		// because overwriting selectedTemplateName with null
		// disappear load/save template widget dialog substitution
		if (templateName != null) {
			alertsGridWidgetPresenter.setSelectedTemplateName(templateName);
		}
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}

}

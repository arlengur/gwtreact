/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.tecomgroup.qos.gwt.client.QoSEntryPoint;
import com.tecomgroup.qos.gwt.client.event.alert.AlertViewEventHandler;
import com.tecomgroup.qos.gwt.client.presenter.AgentListPresenter;
import com.tecomgroup.qos.gwt.client.presenter.GisPresenter;
import com.tecomgroup.qos.gwt.client.presenter.MainPagePresenter;
import com.tecomgroup.qos.gwt.client.presenter.SystemInformationPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.*;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;

/**
 * @author abondin
 * 
 */
public interface QoSGinjector extends Ginjector {

	AdminGatekeeper getAdminGatekeeper();

	AlarmsGatekeeper getAlarmsGatekeeper();

	ChartsGatekeeper getChartsGatekeeper();

	PoliciesGatekeeper getPoliciesGatekeeper();

	ProbeMapGatekeeper getProbeMapGatekeeper();

	ProbesGatekeeper getProbesGatekeeper();

	RecordedVideoGatekeeper getRecordedVideoGatekeeper();

	ReportsGatekeeper getReportsGatekeeper();

	UsersGatekeeper getUsersGatekeeper();

	TranslationsGatekeeper getTranslationsGatekeeper();


	AsyncProvider<AgentListPresenter> getAgentListPresenter();

	AlertViewEventHandler getAlertViewEventHandler();

	EventBus getEventBus();

	AsyncProvider<GisPresenter> getGisPresenter();

	Provider<MainPagePresenter> getMainPagePresenter();

	PlaceManager getPlaceManager();

	QoSEventService getQoSEventService();

	AsyncProvider<SystemInformationPresenter> getSystemInformationPresenter();

	/**
	 * Инжектит поля в EntryPoint
	 * 
	 * @param entryPoint
	 */
	void injectEntryPoint(QoSEntryPoint entryPoint);
}

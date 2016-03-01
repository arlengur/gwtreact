/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.emitrom.touch4j.client.core.InitHandler;
import com.emitrom.touch4j.client.core.Touch;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.gwt.client.event.AddNavigationLinkEvent;
import com.tecomgroup.qos.gwt.client.gin.mobile.MobileQoSMediaGinjector;
import com.tecomgroup.qos.gwt.client.i18n.MediaMessages;

/**
 * @author ivlev.e
 * 
 */
public class MobileQoSMedia extends QoSEntryPoint {

	public final MobileQoSMediaGinjector ginjector = GWT
			.create(MobileQoSMediaGinjector.class);

	protected MediaMessages messages = GWT.create(MediaMessages.class);

	@Override
	public MobileQoSMediaGinjector getInjector() {
		return ginjector;
	}

	@Override
	protected void loadNavigationLinks(final EventBus eventBus) {
		super.loadNavigationLinks(eventBus);
		final AddNavigationLinkEvent event = new AddNavigationLinkEvent();
		event.setPath(QoSMediaNameTokens.mediaPlayer);
		event.setDisplayName(messages.navigationVideo());
		event.setIcon(MediaIcons.VIDEO);
		eventBus.fireEvent(event);

		event.setPath(QoSNameTokens.tableResults);
		event.setDisplayName(messages.navigationAnalytics());
		event.setIcon(GeneralIcons.CHART);
		eventBus.fireEvent(event);

		// event = new AddNavigationLinkEvent();
		// event.setPath(QoSMediaNameTokens.resultsTvIt09a());
		// event.setIcon(GeneralIcons.CHART);
		// event.setDisplayName(MESSAGES.navigationResults());
		// eventBus.fireEvent(event);
		//
		// event = new AddNavigationLinkEvent();
		// event.setPath(QoSMediaNameTokens.monitoringTvIt09a());
		// event.setDisplayName(MESSAGES.navigationAlerts());
		// event.setIcon(GeneralIcons.ALERTS);
		// event.setMenuIndex(2);
		// eventBus.fireEvent(event);

	}

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		Touch.init(new InitHandler() {
			@Override
			protected void onInit() {
				load();
			}
		});
	}

}

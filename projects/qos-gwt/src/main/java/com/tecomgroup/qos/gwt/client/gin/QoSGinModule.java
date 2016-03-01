/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.tecomgroup.qos.gwt.client.QoSPlaceManager;
import com.tecomgroup.qos.gwt.client.event.alert.AlertViewEventHandler;
import com.tecomgroup.qos.gwt.client.secutiry.CurrentUser;
import com.tecomgroup.qos.gwt.shared.event.DefaultEventService;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;

/**
 * @author abondin
 * 
 */
public class QoSGinModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		bind(CurrentUser.class).in(Singleton.class);
		bind(AlertViewEventHandler.class).in(Singleton.class);
		bind(QoSEventService.class).to(DefaultEventService.class).in(
				Singleton.class);
		bind(new TypeLiteral<Map<String, Object>>() {
		}).annotatedWith(Names.named("clientProperties"))
				.to(new TypeLiteral<HashMap<String, Object>>() {
				}).in(Singleton.class);
		install(new DefaultModule(QoSPlaceManager.class));
	}
}

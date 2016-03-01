/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.gin.QoSGinjector;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class QoSBBM extends QoSEntryPoint {

	public final QoSBBMGinjector ginjector = GWT.create(QoSBBMGinjector.class);

	@Override
	public QoSGinjector getInjector() {
		return ginjector;
	}

	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
	}
}

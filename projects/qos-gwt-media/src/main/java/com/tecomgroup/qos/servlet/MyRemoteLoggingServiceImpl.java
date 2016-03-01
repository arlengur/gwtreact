/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
public class MyRemoteLoggingServiceImpl extends RemoteLoggingServiceImpl {

	public MyRemoteLoggingServiceImpl() {
		super();
		setSymbolMapsDirectory("WEB-INF/deploy/DesktopQosMedia/symbolMaps/");
	}
}

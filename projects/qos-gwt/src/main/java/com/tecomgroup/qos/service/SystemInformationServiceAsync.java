/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.BuildInfo;
import com.tecomgroup.qos.TimeZoneWrapper;

/**
 * @author abondin
 * 
 */
public interface SystemInformationServiceAsync {
	/**
	 * 
	 * @param callback
	 */
	void getBuildInfo(AsyncCallback<BuildInfo> callback);

	/**
	 * 
	 * @param callback
	 */
	void getClientProperties(AsyncCallback<Map<String, Object>> callback);

	/**
	 * 
	 * @param callback
	 */
	void getTimeZoneList(AsyncCallback<List<TimeZoneWrapper>> callback);

}

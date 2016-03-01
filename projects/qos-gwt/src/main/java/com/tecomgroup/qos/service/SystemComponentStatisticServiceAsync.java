/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.AgentStatistic;

/**
 * @author sviyazov.a
 * @see SystemComponentStatisticService
 * 
 */
public interface SystemComponentStatisticServiceAsync {
	void getAgentsStatistic(
			AsyncCallback<Map<String, AgentStatistic>> asyncCallback);
}

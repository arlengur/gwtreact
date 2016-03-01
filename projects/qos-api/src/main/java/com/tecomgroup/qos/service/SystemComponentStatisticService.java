/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.AgentStatistic;

/**
 * Provides information about system components and their statistics
 * 
 * @author sviyazov.a
 * 
 */
@RemoteServiceRelativePath("springServices/systemComponentStatisticService")
public interface SystemComponentStatisticService extends Service, RemoteService {
	/**
	 * Provides statistic of only active agents
	 * 
	 * @return
	 */
	Map<String, AgentStatistic> getAgentsStatistic();

}

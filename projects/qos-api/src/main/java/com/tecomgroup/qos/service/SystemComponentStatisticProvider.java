/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Date;
import java.util.List;

import com.tecomgroup.qos.AgentStatistic;

/**
 * Provides aggregated system component statistic and interface for listening
 * for agent registration events.
 * 
 * @author sviyazov.a
 * 
 */
public interface SystemComponentStatisticProvider {
	/**
	 * @see SystemComponentStatisticProvider#registerListener(SystemComponentEventListener)
	 */
	interface SystemComponentEventListener {
		/**
		 * Fires when agent was successfully deleted.
		 * 
		 * @param agentStatistic
		 */
		void onAgentDeletion(AgentStatistic agentStatistic);

		/**
		 * Fires when new agent was successfully registered.
		 */
		void onAgentRegister(AgentStatistic agentStatistic);

		/**
		 * Fires when listener was successfully registered. Sends already
		 * registered agents.
		 */
		void onListenerRegistration(List<AgentStatistic> agentStatistics);
	}
	Date getAgentResultsStartTime();
	Long getHandledAgentResultsCount();
	void registerListener(SystemComponentEventListener listener);
	AgentStatistic getAgentsStatisticByComponentKey(String key);
	void resetAgentStatistics(final String agentKey);
}

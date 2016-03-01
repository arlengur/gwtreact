/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.communication.pm.PMTaskConfiguration;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * Класс для хранения настроек Policy Manager
 * 
 * @author abondin
 * 
 */
public class PolicyManagerConfiguration extends QoSMessage {

	private Map<Source, PMConfiguration> configurations = new HashMap<Source, PMConfiguration>();

	private final Set<String> agents = new HashSet<String>();

	private Date lastUpdateTime;

	public PolicyManagerConfiguration() {
		super();
	}

	public PolicyManagerConfiguration(final Set<String> agents,
			final Map<Source, PMConfiguration> configurations) {
		this.agents.addAll(agents);
		this.configurations.putAll(configurations);
	}

	public void addTaskConfiguration(final PMTaskConfiguration configuration) {
		configurations.put(configuration.getSource(), configuration);
	}

	public void addTaskConfiguration(final String agentName, final String taskKey,
                                     final MResultConfiguration taskConfiguration,
                                     final MPolicy... policies) {
		final Source taskSource = Source.getTaskSource(taskKey);
        final Source systemComponent = Source.getAgentSource(agentName);
		PMTaskConfiguration configuration = (PMTaskConfiguration) configurations
				.get(taskSource);
		if (configuration != null) {
			configuration.setConfiguration(taskConfiguration);
			if (policies != null) {
				if (configuration.getPolicies() != null) {
					configuration.getPolicies().addAll(
							new ArrayList<MPolicy>(Arrays.asList(policies)));
				} else {
					configuration.setPolicies(new ArrayList<MPolicy>(Arrays
							.asList(policies)));
				}
			}
		} else {
			configuration = new PMTaskConfiguration();
            configuration.setSource(taskSource);
			configuration.setConfiguration(taskConfiguration);
            configuration.setSystemComponenet(systemComponent);
			if (policies != null) {
				configuration.setPolicies(new ArrayList<MPolicy>(Arrays
						.asList(policies)));
			}
		}
		configurations.put(taskSource, configuration);
	}

	/**
	 * @return the agents
	 */
	public Set<String> getAgents() {
		return agents;
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	public PMTaskConfiguration getConfiguration(final Source source) {
		return (PMTaskConfiguration) configurations.get(source);
	}

	/**
	 * @return the taskConfigurations
	 */
	public Map<Source, PMConfiguration> getConfigurations() {
		return configurations;
	}

	/**
	 * @return the lastUpdateTime
	 */
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * @param configurations
	 *            the taskConfigurations to set
	 */
	public void setConfigurations(
			final Map<Source, PMConfiguration> configurations) {
		this.configurations = configurations;
	}

	/**
	 * @param lastUpdateTime
	 *            the lastUpdateTime to set
	 */
	public void setLastUpdateTime(final Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}

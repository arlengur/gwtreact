/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.dashboard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A widget to store agent keys.
 * 
 * @author kunilov.p
 * 
 */
public abstract class DashboardAgentsWidget extends DashboardWidget {

	private static final long serialVersionUID = -8054273489033882813L;

	protected Set<String> agentKeys;

	protected boolean forAllAgents = false;

	public DashboardAgentsWidget() {
		super();
		agentKeys = new HashSet<String>();
	}

	public Set<String> getAgentKeys() {
		return agentKeys;
	}

	@Override
	@Transient
	@JsonIgnore
	public String getKey() {
		final String[] agentKeysArray = agentKeys.toArray(new String[0]);
		Arrays.sort(agentKeysArray);
		return DashboardAgentsWidget.class.getName()
				+ "{ agents: "
				+ (agentKeysArray.length == 0 ? "all" : Arrays
						.toString(agentKeysArray)) + " }";
	}

	@Override
	public boolean isEmpty() {
		return (forAllAgents == false)
				&& (agentKeys == null || agentKeys.isEmpty());
	}

	public boolean isForAllAgents() {
		return forAllAgents;
	}

	public void setAgentKeys(final Set<String> agentKeys) {
		this.agentKeys.clear();
		if (agentKeys != null && !agentKeys.isEmpty()) {
			this.agentKeys = agentKeys;
		}
	}

	public void setForAllAgents(final boolean forAllAgents) {
		this.forAllAgents = forAllAgents;
	}
}

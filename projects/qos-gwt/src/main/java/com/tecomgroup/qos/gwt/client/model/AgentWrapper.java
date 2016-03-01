/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model;

import java.io.Serializable;
import java.util.Date;

import com.tecomgroup.qos.domain.MAgent;

/**
 * A wrapper for {@link MAgent}
 * 
 * @author kunilov.p
 * 
 */
public class AgentWrapper implements Serializable {

	private static final long serialVersionUID = 3513545019179565759L;

	private final String agent;

	private String displayName;

	private String description;

	private Date registrationTime;

	private Date lastResultTime;

	private String state;

	public AgentWrapper(final String agent) {
		super();
		this.agent = agent;
		this.displayName = agent;
	}

	public AgentWrapper(final String agentName, final String agentDisplayName) {
		this(agentName);
		this.displayName = agentDisplayName;
	}

	public AgentWrapper(final String agentName, final String agentDisplayName,
			final Date registrationTime, final Date lastResultTime) {
		this(agentName, agentDisplayName);
		this.registrationTime = registrationTime;
		this.lastResultTime = lastResultTime;
	}

	public AgentWrapper(final String agentName, final String agentDisplayName,
			final String description, final Date registrationTime,
			final Date lastResultTime, final String state) {
		this(agentName, agentDisplayName, registrationTime, lastResultTime);
		this.setDescription(description);
		this.setState(state);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof AgentWrapper) {
			return agent.equals(((AgentWrapper) obj).getAgent());
		}
		return false;
	}

	public String getAgent() {
		return agent;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Date getLastResultTime() {
		return lastResultTime;
	}

	public Date getRegistrationTime() {
		return registrationTime;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setLastResultTime(final Date lastResultTime) {
		this.lastResultTime = lastResultTime;
	}

	public void setRegistrationTime(final Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}

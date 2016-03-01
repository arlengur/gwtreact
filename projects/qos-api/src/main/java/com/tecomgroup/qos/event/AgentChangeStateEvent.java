/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;

import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class AgentChangeStateEvent extends AbstractEvent {
	private String agentKey;
	private Long agentID;
	private MAgent.AgentRegistrationState state;
	private Date date;
	private MAgent agent;
	private String agentVersion;
	private List<MAgentTask> tasks;

	public AgentChangeStateEvent(final MAgent.AgentRegistrationState state, MAgent magent,Date date) {
		super();
		this.agent=magent;
		this.state = state;
		this.agentID = magent.getId();
		this.agentKey = magent.getKey();
		this.date=date;
	}
	public AgentChangeStateEvent(final MAgent.AgentRegistrationState state, MAgent magent,String version,Date date) {
		super();
		this.agent=magent;
		this.state = state;
		this.agentID = magent.getId();
		this.agentKey = magent.getKey();
		this.date=date;
		this.agentVersion=version;
	}

	public AgentChangeStateEvent(final MAgent.AgentRegistrationState state, MAgent magent,Date date,List<MAgentTask> tasks) {
		this(state,magent,date);
		this.tasks=tasks;
	}

	public AgentChangeStateEvent(final MAgent.AgentRegistrationState state, MAgent magent,String version,Date date,List<MAgentTask> tasks) {
		this(state,magent,version,date);
		this.tasks=tasks;
	}

	public Long getAgentID() {
		return agentID;
	}
	public MAgent.AgentRegistrationState getState() {
		return state;
	}

	public Date getDate() {
		return date;
	}

	public String getAgentKey() {
		return agentKey;
	}

	public MAgent getAgent() {
		return agent;
	}

	public List<MAgentTask> getTasks() {
		return tasks;
	}

	public String getAgentVersion() {
		return agentVersion;
	}
}


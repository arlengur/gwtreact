/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.MAgent;

/**
 * An event for notifications of operations with agents.
 * 
 * @author kunilov.p
 * 
 */
public class AgentEvent extends AbstractEvent {
	private static final long serialVersionUID = -8278166321220957541L;

	private MAgent agent;

	public AgentEvent() {
		super();
	}

	public AgentEvent(final MAgent agent, final EventType eventType) {
		super(eventType);
		this.agent = agent;
	}

	public MAgent getAgent() {
		return agent;
	}

	public void setAgent(final MAgent agent) {
		this.agent = agent;
	}
}

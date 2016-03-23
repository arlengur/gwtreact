/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.probestatus.MProbeEvent;

public class AgentActionStatusEvent extends AbstractEvent {

	private static final long serialVersionUID = 7881313566234224126L;

	private MProbeEvent event;

	public AgentActionStatusEvent() {
		super();
	}

	public AgentActionStatusEvent(final EventType eventType, MProbeEvent event) {
		super(eventType);
		this.event = event;
	}

	public MProbeEvent getEvent() {
		return event;
	}

	public void setEvent(MProbeEvent event) {
		this.event = event;
	}
}

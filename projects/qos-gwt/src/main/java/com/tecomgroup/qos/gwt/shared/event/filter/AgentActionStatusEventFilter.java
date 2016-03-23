/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.ActivateAlertEvent;
import com.tecomgroup.qos.event.AgentActionStatusEvent;
import com.tecomgroup.qos.event.QoSEventFilter;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
public class AgentActionStatusEventFilter implements QoSEventFilter {

	private String userLogin;

	public AgentActionStatusEventFilter() {
		super();
	}

	public AgentActionStatusEventFilter(
			final String userLogin) {
		super();
		this.userLogin = userLogin;
	}

	@Override
	public boolean accept(final AbstractEvent event) {
		if (event instanceof AgentActionStatusEvent) {
			AgentActionStatusEvent agentStatusEvent = (AgentActionStatusEvent) event;
			MProbeEvent agentEvent = agentStatusEvent.getEvent();

			if(agentEvent.getUserLogin() != null && agentEvent.getUserLogin().equals(userLogin)) {
				return true;
			}
		}
		return false;
	}

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import java.util.Set;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.Source.Type;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.StatusEvent;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class AgentStatusEventFilter implements QoSEventFilter {

	/**
	 * Filters {@link StatusEvent} if the status is related to {@link MAgent}
	 * contained in {@link MultipleOrAllAgentsStatusEventFilter#agentKeys} or if
	 * {@link MultipleOrAllAgentsStatusEventFilter#agentKeys} is null or empty.
	 * 
	 * @author kunilov.p
	 * 
	 */
	public static class MultipleOrAllAgentsStatusEventFilter
			extends
				AgentStatusEventFilter {

		private Set<String> agentKeys;

		private MultipleOrAllAgentsStatusEventFilter() {
			super();
		}

		public MultipleOrAllAgentsStatusEventFilter(final Set<String> agentKeys) {
			this();
			this.agentKeys = agentKeys;
		}

		@Override
		public boolean accept(final AbstractEvent event) {
			boolean result = super.accept(event);
			if (result && SimpleUtils.isNotNullAndNotEmpty(agentKeys)) {
				final String eventAgentKey = ((StatusEvent) event).getSourceKey();
				result = agentKeys.contains(eventAgentKey);
			}
			return result;
		}
	}

	public static class SingleAgentStatusEventFilter
			extends
				AgentStatusEventFilter {

		private String agentName;

		private SingleAgentStatusEventFilter() {
			super();
		}

		public SingleAgentStatusEventFilter(final String agentName) {
			this();
			this.agentName = agentName;
		}

		@Override
		public boolean accept(final AbstractEvent event) {
			boolean result = super.accept(event);
			if (result) {
				final StatusEvent statusEvent = (StatusEvent) event;
				result = statusEvent.getSourceKey().equals(agentName);
			}
			return result;
		}
	}

	@Override
	public boolean accept(final AbstractEvent event) {
		boolean result = false;
		if (event instanceof StatusEvent) {
			final StatusEvent statusEvent = (StatusEvent) event;
			if(statusEvent.getSourceKey()!=null)
			{
				result =true;
			}
		}
		return result;
	}
}

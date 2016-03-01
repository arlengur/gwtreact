/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.dashboard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertEvent;
import com.tecomgroup.qos.event.QoSEventFilter;

/**
 * Widget to show <b>n</b> latest alerts for given agent/agents, all agents.
 *
 * NOTE: You can use LatestAlertsWidget as {@link QoSEventFilter} to filter
 * server events on a client
 *
 * @author abondin
 *
 */
public class LatestAlertsWidget extends DashboardAgentsWidget
		implements
			QoSEventFilter,
			HasUpdatableData<MAlert> {

	/**
	 * Used for construct unique domain name for this widget
	 */
	public static final int DEFAULT_VISIBLE_ALERT_COUNT = 20;

	public static final String EVENT_SERVICE_DOMAIN_PREFIX = "LatestAlertsWidget_";

	private static final long serialVersionUID = 8160797964670258503L;

	private int visibleAlertCount = DEFAULT_VISIBLE_ALERT_COUNT;

	private Set<PerceivedSeverity> severities;

	public LatestAlertsWidget() {
		super();
		agentKeys = new HashSet<String>();
		severities = new HashSet<PerceivedSeverity>();
	}

	@Override
	public boolean accept(final AbstractEvent event) {
		if (event instanceof AlertEvent) {
			// TODO: implicitly use systemComponentKey instead of
			// alert.getSource().getParent().getParent().getKey()
			if (agentKeys.isEmpty()
					|| agentKeys.contains(((AlertEvent) event).getAgentKey())) {
				return true;
				// TODO Do not ignore severity in filtering
				// if (severities.isEmpty()
				// || severities.contains(alert.getPerceivedSeverity())) {
				// return true;
				// }
			}
		}
		return false;
	}

	@Override
	public String getKey() {
		final String[] agentKeysArray = agentKeys.toArray(new String[0]);
		Arrays.sort(agentKeysArray);
		final PerceivedSeverity[] severitiesArray = severities
				.toArray(new PerceivedSeverity[0]);
		Arrays.sort(severitiesArray);
		return LatestAlertsWidget.class.getName()
				+ " : "
				+ (agentKeysArray.length == 0 ? "All" : Arrays
						.toString(agentKeysArray))
				+ " : "
				+ (severitiesArray.length == 0 ? "All" : Arrays
						.toString(severitiesArray));
	}

	/**
	 * @return the severities
	 */
	public Set<PerceivedSeverity> getSeverities() {
		return severities;
	}

	/**
	 * @return the visibleAlertCount
	 */
	public int getVisibleAlertCount() {
		return visibleAlertCount;
	}

	/**
	 * @param severities
	 *            the severities to set
	 */
	public void setSeverities(final Set<PerceivedSeverity> severities) {
		if (severities == null) {
			this.severities.clear();
		} else {
			this.severities = severities;
		}
	}

	/**
	 * @param visibleAlertCount
	 *            the visibleAlertCount to set
	 */
	public void setVisibleAlertCount(final int visibleAlertCount) {
		this.visibleAlertCount = visibleAlertCount;
	}

}

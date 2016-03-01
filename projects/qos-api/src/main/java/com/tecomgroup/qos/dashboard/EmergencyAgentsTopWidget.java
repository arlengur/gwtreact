/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.dashboard.EmergencyAgentsTopWidget.ChartData;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;

/**
 * Widget shows top of agents ordered by summary time of their emergencies. A
 * number of agents is controlled by {@link EmergencyAgentsTopWidget#topSize}
 * 
 * @author ivlev.e
 */
public class EmergencyAgentsTopWidget extends DashboardWidget
		implements
			HasUpdatableData<ChartData> {

	/**
	 * One agent's data for the chart
	 * 
	 * @author ivlev.e
	 */
	public static class ChartData implements WidgetData, Serializable {

		private static final long serialVersionUID = 1827322704172955755L;

		private String displayName;

		/**
		 * Summary duration of agent's alerts (in percent with respect to all
		 * agents)
		 */
		private int summaryDuration;

		public ChartData() {
			super();
		}

		public ChartData(final String displayName, final int duration) {
			this();
			this.displayName = displayName;
			this.summaryDuration = duration;
		}

		public String getDisplayName() {
			return displayName;
		}

		public int getSummaryDuration() {
			return summaryDuration;
		}

		public void setDisplayName(final String displayName) {
			this.displayName = displayName;
		}

		public void setSummaryDuration(final int duration) {
			this.summaryDuration = duration;
		}
	}

	public static final String OTHERS_AGENTS = "others";

	private static final long serialVersionUID = 8713606981598763659L;

	private int topSize;

	private Type intervalType;

	private Set<PerceivedSeverity> severities;

	private List<ChartData> data = new ArrayList<ChartData>();

	public EmergencyAgentsTopWidget() {
		super();
		severities = new HashSet<PerceivedSeverity>();
	}

	@Transient
	@JsonIgnore
	public List<ChartData> getData() {
		return data;
	}

	public Type getIntervalType() {
		return intervalType;
	}

	@Override
	@Transient
	@JsonIgnore
	public String getKey() {
		final PerceivedSeverity[] severitiesArray = severities
				.toArray(new PerceivedSeverity[0]);
		Arrays.sort(severitiesArray);

		return EmergencyAgentsTopWidget.class.getName()
				+ ": { topSize: "
				+ topSize
				+ ", intervalType: "
				+ intervalType
				+ ", severities: "
				+ (severitiesArray.length == 0 ? "all" : Arrays
						.toString(severitiesArray)) + " } ";
	}

	public Set<PerceivedSeverity> getSeverities() {
		return severities;
	}

	public int getTopSize() {
		return topSize;
	}

	@Transient
	@JsonIgnore
	public void setData(final List<ChartData> data) {
		this.data = data;
	}

	public void setIntervalType(final Type intervalType) {
		this.intervalType = intervalType;
	}

	public void setSeverities(final Set<PerceivedSeverity> severities) {
		this.severities.clear();
		if (severities != null && !severities.isEmpty()) {
			this.severities = severities;
		}
	}

	public void setTopSize(final int topSize) {
		this.topSize = topSize;
	}
}

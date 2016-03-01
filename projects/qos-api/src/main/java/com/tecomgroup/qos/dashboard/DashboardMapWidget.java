/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.dashboard;

import java.util.Arrays;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.domain.GISPosition;

/**
 * A widget to store map options.
 * 
 * @author kunilov.p
 * 
 */
public class DashboardMapWidget extends DashboardAgentsWidget {

	private static final long serialVersionUID = -441811369581323276L;

	private GISPosition center;

	private Integer zoom;

	private long creationTimestamp;

	public DashboardMapWidget() {
		super();
		creationTimestamp = System.currentTimeMillis();
	}

	public GISPosition getCenter() {
		return center;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	@Transient
	@JsonIgnore
	@Override
	public String getKey() {
		final String[] agentKeysArray = agentKeys.toArray(new String[0]);
		Arrays.sort(agentKeysArray);

		return DashboardMapWidget.class.getName()
				+ ": { agents: "
				+ (agentKeysArray.length == 0 ? "all" : Arrays
						.toString(agentKeysArray)) + ", creationTimestamp: "
				+ creationTimestamp + " } ";
	}

	public Integer getZoom() {
		return zoom;
	}

	@Transient
	@JsonIgnore
	public void setCenter(final double longitude, final double latitude) {
		center = new GISPosition(longitude, latitude);
	}

	public void setCenter(final GISPosition center) {
		this.center = center;
	}

	public void setCreationTimestamp(final long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public void setZoom(final Integer zoom) {
		this.zoom = zoom;
	}

	@Override
	public String toString() {
		final String[] agentKeysArray = agentKeys.toArray(new String[0]);
		Arrays.sort(agentKeysArray);

		String toString = DashboardMapWidget.class.getName()
				+ ": { agents: "
				+ (agentKeysArray.length == 0 ? "all" : Arrays
						.toString(agentKeysArray)) + ", creationTimestamp: "
				+ creationTimestamp;

		if (center != null) {
			toString += ", center: { longitude: " + center.getLongitude()
					+ ", latitude: " + center.getLatitude() + "}, zoom: "
					+ zoom;
		}

		return toString + " }";
	}
}

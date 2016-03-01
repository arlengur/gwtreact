/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.dashboard;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MStreamWrapper;

/**
 * Widget for live video
 * 
 * @author abondin
 * 
 */
public class LiveStreamWidget extends DashboardWidget {
	private static final long serialVersionUID = -317128061597619042L;

	private String streamKey;

	private String taskKey;

	@JsonIgnore
	private MLiveStream stream;

	@Override
	@Transient
	@JsonIgnore
	public String getKey() {
		return MStreamWrapper.generateUniqueKey(taskKey, streamKey);
	}

	/**
	 * @return the stream
	 */
	public MLiveStream getStream() {
		return stream;
	}

	/**
	 * @return the streamKey
	 */
	public String getStreamKey() {
		return streamKey;
	}

	/**
	 * @return the taskKey
	 */
	public String getTaskKey() {
		return taskKey;
	}

	public void initialize(final MLiveStream stream) {
		this.stream = stream;
	}

	/**
	 * @param streamKey
	 *            the streamKey to set
	 */
	public void setStreamKey(final String streamKey) {
		this.streamKey = streamKey;
	}

	/**
	 * @param taskKey
	 *            the taskKey to set
	 */
	public void setTaskKey(final String taskKey) {
		this.taskKey = taskKey;
	}

}

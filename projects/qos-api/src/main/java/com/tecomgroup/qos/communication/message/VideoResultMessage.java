/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import java.util.List;

import com.tecomgroup.qos.communication.result.VideoResult;

/**
 * @author kunilov.p
 * 
 */
public class VideoResultMessage extends QoSMessage {

	private String taskKey;

	private List<VideoResult> results;

	/**
	 * @return the results
	 */
	public List<VideoResult> getResults() {
		return results;
	}

	/**
	 * @return the taskKey
	 */
	public String getTaskKey() {
		return taskKey;
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public void setResults(final List<VideoResult> results) {
		this.results = results;
	}

	/**
	 * @param taskKey
	 *            the taskKey to set
	 */
	public void setTaskKey(final String taskKey) {
		this.taskKey = taskKey;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.MAgentTask;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class TaskEvent extends AbstractEvent {

	private MAgentTask task;

	public TaskEvent() {
		super();
	}

	/**
	 * @param eventType
	 */
	public TaskEvent(final MAgentTask task, final EventType eventType) {
		super(eventType);
		this.task = task;
	}

	/**
	 * @return the task
	 */
	public MAgentTask getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(final MAgentTask task) {
		this.task = task;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

/**
 * @author sviyazov.a
 * 
 */
public class TaskStatusInfo implements StatusReporter {

	private final TaskStatus status;

	private final int percentagesDone;

	public TaskStatusInfo(final TaskStatus status, final int percentagesDone) {
		this.status = status;
		this.percentagesDone = percentagesDone;
	}

	/**
	 * @return the percentagesDone
	 */
	@Override
	public int getPercentDone() {
		return percentagesDone;
	}

	/**
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}
}
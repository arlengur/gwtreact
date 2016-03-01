/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;

/**
 * @author kunilov.p
 * 
 */
public class QoSTaskExecutor implements TaskExecutor {

	private TaskExecutor executor;

	private static Logger LOGGER = Logger.getLogger(QoSTaskExecutor.class);

	@Override
	public void execute(final Runnable task) {
		try {
			executor.execute(task);
		} catch (final TaskRejectedException ex) {
			LOGGER.error("TaskPool capacity is low", ex);
		} catch (final Exception ex) {
			LOGGER.error(ex);
		}
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutor(final TaskExecutor executor) {
		this.executor = executor;
	}
}

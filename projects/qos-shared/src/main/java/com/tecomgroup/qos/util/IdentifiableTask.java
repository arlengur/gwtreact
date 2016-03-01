/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import org.apache.log4j.Logger;

import com.tecomgroup.qos.TimeConstants;

/**
 * @author sviyazov.a
 * 
 */
public class IdentifiableTask<R> implements CallableTask<R> {
	private final long id;
	private final CallableTask<R> callable;

	private final static Logger LOGGER = Logger
			.getLogger(IdentifiableTask.class);

	public IdentifiableTask(final CallableTask<R> callable, final long id) {
		assert (callable != null);
		this.id = id;
		this.callable = callable;
	}

	@Override
	public R call() throws Exception {
		LOGGER.info("Start task execution: " + this);

		final long startTimestamp = System.currentTimeMillis();
		final R result = callable.call();
		final long executionTime = System.currentTimeMillis() - startTimestamp;

		LOGGER.info("End task execution: " + this);
		LOGGER.info("Task " + this + " was executed for "
				+ (executionTime / TimeConstants.MILLISECONDS_PER_SECOND)
				+ " seconds.");
		return result;
	}

	public long getId() {
		return id;
	}

	@Override
	public int getPercentDone() {
		return callable.getPercentDone();
	}

	@Override
	public String toString() {
		return "{id = " + id + ", callable = " + callable + "}";
	}
}

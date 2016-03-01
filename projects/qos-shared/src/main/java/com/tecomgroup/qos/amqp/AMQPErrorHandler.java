/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import org.apache.log4j.Logger;
import org.springframework.util.ErrorHandler;

/**
 * @author abondin
 * 
 */
public class AMQPErrorHandler implements ErrorHandler {
	private final static Logger LOGGER = Logger
			.getLogger(AMQPErrorHandler.class);

	@Override
	public void handleError(final Throwable t) {
		LOGGER.error("Amqp error: ", t);
		// TODO Implement me
	}
}

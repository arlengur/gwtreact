/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * 
 * Superclass for all QoS exceptions
 * 
 * @author abondin
 * 
 */
public abstract class QOSException extends RuntimeException {
	private static final long serialVersionUID = 2315230942892506855L;

	/**
	 * 
	 */
	public QOSException() {
		super();
	}

	/**
	 * @param message
	 */
	public QOSException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public QOSException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public QOSException(final Throwable cause) {
		super(cause);
	}

}

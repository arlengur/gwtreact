/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * @author abondin
 * 
 */
public class AlertException extends QOSException {
	private static final long serialVersionUID = 4583080525864510512L;

	/**
	 * 
	 */
	public AlertException() {
		super();
	}

	/**
	 * @param message
	 */
	public AlertException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AlertException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public AlertException(final Throwable cause) {
		super(cause);
	}

}

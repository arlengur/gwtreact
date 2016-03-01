/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * An exception which should be thrown when unknown source is detected in the
 * system.
 * 
 * @author kunilov.p
 * 
 */
public class UnknownSourceException extends QOSException {
	private static final long serialVersionUID = 7653503942121814842L;

	public UnknownSourceException() {
		super();
	}

	/**
	 * @param message
	 */
	public UnknownSourceException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnknownSourceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public UnknownSourceException(final Throwable cause) {
		super(cause);
	}

}

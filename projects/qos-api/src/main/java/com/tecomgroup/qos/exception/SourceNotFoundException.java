/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * An exception which should be thrown when source is not found in the system.
 * 
 * @author kunilov.p
 * 
 */
public class SourceNotFoundException extends QOSException {
	private static final long serialVersionUID = 1222549893347433886L;

	public SourceNotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public SourceNotFoundException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SourceNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public SourceNotFoundException(final Throwable cause) {
		super(cause);
	}

}

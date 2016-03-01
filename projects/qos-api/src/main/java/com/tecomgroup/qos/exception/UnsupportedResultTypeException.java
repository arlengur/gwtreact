/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * @author kunilov.p
 * 
 */
public class UnsupportedResultTypeException extends QOSException {
	private static final long serialVersionUID = -3845457309269048512L;

	/**
	 * 
	 */
	public UnsupportedResultTypeException() {
		super();
	}

	/**
	 * @param message
	 */
	public UnsupportedResultTypeException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnsupportedResultTypeException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public UnsupportedResultTypeException(final Throwable cause) {
		super(cause);
	}
}

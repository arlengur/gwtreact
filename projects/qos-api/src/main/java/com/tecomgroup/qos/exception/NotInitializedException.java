/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * @author kunilov.p
 * 
 */
public class NotInitializedException extends QOSException {
	private static final long serialVersionUID = 7970189801879576157L;

	public NotInitializedException() {
		super();
	}

	public NotInitializedException(final String message) {
		super(message);
	}

	public NotInitializedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NotInitializedException(final Throwable cause) {
		super(cause);
	}

}

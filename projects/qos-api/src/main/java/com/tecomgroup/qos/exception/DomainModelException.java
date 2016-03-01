/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * 
 * Domain model exception
 * 
 * @author kunilov.p
 * 
 */
public class DomainModelException extends QOSException {
	private static final long serialVersionUID = 7338365195402891531L;

	/**
	 * 
	 */
	public DomainModelException() {
		super();
	}

	/**
	 * @param message
	 */
	public DomainModelException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DomainModelException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DomainModelException(final Throwable cause) {
		super(cause);
	}
}

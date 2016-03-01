/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * @author abondin
 * 
 */
public class ServiceException extends QOSException {
	private static final long serialVersionUID = 8683529310308019291L;

	public ServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public ServiceException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ServiceException(final Throwable cause) {
		super(cause);
	}

}

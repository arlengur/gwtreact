/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * Error in modelspace level
 * 
 * @author abondin
 * 
 */
public class ModelSpaceException extends QOSException {
	private static final long serialVersionUID = 8048531486568091228L;

	/**
	 * 
	 */
	public ModelSpaceException() {
		super();
	}

	/**
	 * @param message
	 */
	public ModelSpaceException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ModelSpaceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ModelSpaceException(final Throwable cause) {
		super(cause);
	}

}

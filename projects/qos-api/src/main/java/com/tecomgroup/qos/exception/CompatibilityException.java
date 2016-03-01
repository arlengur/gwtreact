/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * @author tabolin.a
 *
 */
public class CompatibilityException extends ServiceException {

	private static final long serialVersionUID = -6185622953080980394L;

	public CompatibilityException() {
		super();
	}

	public CompatibilityException(final String message) {
		super(message);
	}

	public CompatibilityException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CompatibilityException(final Throwable cause) {
		super(cause);
	}
}

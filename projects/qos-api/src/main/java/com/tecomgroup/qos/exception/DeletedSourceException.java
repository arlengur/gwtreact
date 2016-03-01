/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * An exception is thrown when any operation is performed on deleted source.
 * 
 * @author kunilov.p
 * 
 */
public class DeletedSourceException extends QOSException {
	private static final long serialVersionUID = -1412914031186079369L;

	public DeletedSourceException() {
		super();
	}

	public DeletedSourceException(final String message) {
		super(message);
	}

	public DeletedSourceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DeletedSourceException(final Throwable cause) {
		super(cause);
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * An exception is thrown when any operation is performed on disabled source.
 * 
 * @author kunilov.p
 * 
 */
public class DisabledSourceException extends QOSException {
	private static final long serialVersionUID = -1412914031186079369L;

	public DisabledSourceException() {
		super();
	}

	public DisabledSourceException(final String message) {
		super(message);
	}

	public DisabledSourceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DisabledSourceException(final Throwable cause) {
		super(cause);
	}
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * 
 * Ошибки авторизации/аутентификации, изменения пароля
 * 
 * @author abondin
 * 
 */
public class SecurityException extends QOSException {
	public enum Reason {
		INCORRECT_LOGIN, NO_GRANT, INCORRECT_OLD_PASSWORD, USER_NOT_FOUND;
	}

	private static final long serialVersionUID = 6826841958667422325L;

	private Reason reason;

	/**
	 * 
	 */
	public SecurityException() {
		super();
		this.reason = Reason.INCORRECT_LOGIN;
	}

	/**
	 * 
	 */
	public SecurityException(final Reason reason) {
		super();
		this.reason = reason;
	}

	/**
	 * @param message
	 */
	public SecurityException(final Reason reason, final String message) {
		super(message);
		this.reason = reason;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SecurityException(final Reason reason, final String message,
			final Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	/**
	 * @param cause
	 */
	public SecurityException(final Reason reason, final Throwable cause) {
		super(cause);
		this.reason = reason;
	}

	/**
	 * @return the reason
	 */
	public Reason getReason() {
		return reason;
	}

	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(final Reason reason) {
		this.reason = reason;
	}

}

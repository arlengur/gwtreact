package com.tecomgroup.qos.exception;

/**
 * @author meleshin.o
 */
public class UserValidationException extends QOSException {
	public enum Reason {
		INCORRECT_LOGIN_FORMAT, INCORRECT_EMAIL_FORMAT, INCORRECT_PHONE_NUMBER_FORMAT
	}

	private Reason reason;

	public UserValidationException() {
		super();
	}

	public UserValidationException(final Reason reason) {
		super();
		this.reason = reason;
	}

	public UserValidationException(final Reason reason, final String message) {
		super(message);
		this.reason = reason;
	}

	public UserValidationException(final Reason reason, final String message,
			final Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	public UserValidationException(final Reason reason, final Throwable cause) {
		super(cause);
		this.reason = reason;
	}

	public UserValidationException(final String message) {
		super(message);
	}

	public Reason getReason() {
		return reason;
	}

	public void setReason(final Reason reason) {
		this.reason = reason;
	}
}

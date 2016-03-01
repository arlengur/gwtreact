/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper exception containing list of minor registration errors.
 * 
 * @author sviyazov.a
 * 
 */
public class RegistrationMinorErrorsException extends QOSException {
	private static final long serialVersionUID = -2568006879597192109L;

	private final List<Throwable> errors = new ArrayList<Throwable>();
	private static final String ERROR_LIST_SEPARATOR = ", ";

	public RegistrationMinorErrorsException(final List<Throwable> errors) {
		super();
		this.errors.addAll(errors);
	}

	/**
	 * 
	 * @return unmodifiable list of the registration errors
	 */
	public List<Throwable> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	@Override
	public String toString() {
		String result = this.getClass().getName() + " [";

		for (final Iterator<Throwable> iterator = errors.iterator(); iterator
				.hasNext();) {
			result += "{ " + iterator.next().toString() + " }";
			if (iterator.hasNext()) {
				result += ERROR_LIST_SEPARATOR;
			}
		}

		return result + "]";
	}
}

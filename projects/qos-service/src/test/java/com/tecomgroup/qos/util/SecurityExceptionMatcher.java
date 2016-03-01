/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

import com.tecomgroup.qos.exception.SecurityException;
import com.tecomgroup.qos.exception.SecurityException.Reason;

/**
 * @author meleshin.o
 * 
 */
public class SecurityExceptionMatcher
		extends
			TypeSafeMatcher<SecurityException> {

	private final Reason expectedReason;

	private final String descriptionMessage;

	public SecurityExceptionMatcher(final Reason expectedReason,
			final String descriptionMessage) {
		super();
		this.expectedReason = expectedReason;
		this.descriptionMessage = descriptionMessage;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText(descriptionMessage);

	}

	@Override
	public boolean matchesSafely(final SecurityException exception) {
		return exception.getReason() == expectedReason;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import java.text.ParseException;

import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;

/**
 * @author kunilov.p
 * 
 */
public enum PolicyActionType {
	EMAIL("E-mail"), SMS("SMS");

	public static PolicyActionType getByActionClass(
			final Class<? extends MPolicyActionWithContacts> actionClass) {
		PolicyActionType result = null;
		if (actionClass != null) {
			if (MPolicySendEmail.class.equals(actionClass)) {
				result = EMAIL;
			} else if (MPolicySendSms.class.equals(actionClass)) {
				result = SMS;
			} else {
				throw new ClassCastException(actionClass.getName()
						+ " is not supported");
			}
		}
		return result;
	}

	public static PolicyActionType parseString(final String object)
			throws ParseException {
		PolicyActionType result = null;
		if (object != null) {
			final String candidate = object.toUpperCase();
			if (EMAIL.toString().equals(candidate)) {
				result = EMAIL;
			} else if (SMS.toString().equals(candidate)) {
				result = SMS;
			} else {
				throw new ParseException(object + " could not be parsed", 0);
			}
		}
		return result;
	}

	private String text;

	private PolicyActionType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}

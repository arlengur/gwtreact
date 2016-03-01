/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.i18n;

import java.io.Serializable;

import com.google.gwt.i18n.client.Messages.DefaultMessage;

/**
 * Messages for various validators
 * 
 * @author kshnyakin.m
 */
public interface ValidationMessages extends Serializable {
	@DefaultMessage("This field may not be empty")
	String emptyFieldError();

	@DefaultMessage("Field \"{0}\" does not match field \"{1}\"")
	String fieldsDoNotMatch(String fieldNameX, String fieldNameY);

	@DefaultMessage("Email format is incorrect")
	String incorrectEmailFormat();

	@DefaultMessage("Login may contain uppercase or lowercase latin alphabet characters, digits or nonalphanumeric characters \"_\", \".\", \"-\"")
	String incorrectLoginFormat();

	@DefaultMessage("Not a number")
	String NaN();

	@DefaultMessage("Only 0 or 1 allowed")
	String onlyBooleanAllowed();

	@DefaultMessage("Only nonnegative numbers (0, 1, 2 ...) are allowed")
	String onlyNaturalNumberAllowed();

	@DefaultMessage("Only numbers from 0 to 100 are allowed.")
	String onlyPercentageAllowed();
}

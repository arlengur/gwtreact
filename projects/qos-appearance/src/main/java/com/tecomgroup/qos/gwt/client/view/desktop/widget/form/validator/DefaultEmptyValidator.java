/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator;

import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;
import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;

/**
 * @author kunilov.p
 * 
 */
public class DefaultEmptyValidator<T> extends EmptyValidator<T> {

	protected final ValidationMessages messages;

	public DefaultEmptyValidator(final ValidationMessages messages) {
		this.messages = messages;
		setDefaultMessage(messages.emptyFieldError());
	}

	/**
	 * Changes default message
	 */
	public void setDefaultMessage(final String message) {
		setMessages(new EmptyMessages() {
			@Override
			public String blankText() {
				return message;
			}
		});
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator;

import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;

/**
 * @author meleshin.o
 * 
 */
public class LoginValidator extends RegExValidator {

	public LoginValidator(final ValidationMessages messages) {
		super(MUser.LOGIN_VALID_PATTERN, messages.incorrectLoginFormat());
	}
}

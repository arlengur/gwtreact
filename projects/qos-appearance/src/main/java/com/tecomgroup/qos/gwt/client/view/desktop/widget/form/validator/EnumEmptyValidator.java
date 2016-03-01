/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator;

import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;

/**
 * @author kunilov.p
 * 
 */
public class EnumEmptyValidator<T extends Enum<?>>
		extends
			DefaultEmptyValidator<T> {

	public EnumEmptyValidator(final ValidationMessages messages) {
		super(messages);
	}
}

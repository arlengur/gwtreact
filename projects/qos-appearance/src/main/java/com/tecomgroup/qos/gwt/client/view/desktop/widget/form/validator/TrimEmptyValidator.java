/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;

/**
 * EmptyValidator that trims string value before validation.
 * 
 * @author novohatskiy.r
 * 
 */
public class TrimEmptyValidator extends DefaultEmptyValidator<String> {

	public TrimEmptyValidator(final ValidationMessages messages) {
		super(messages);
	}

	@Override
	public List<EditorError> validate(final Editor<String> editor,
			final String value) {
		final String trimmedValue = value != null ? value.trim() : null;
		return super.validate(editor, trimmedValue);
	}
}

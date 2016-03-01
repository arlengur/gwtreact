/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;
import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;

/**
 * Validator which checks values of field A and B for equality
 * 
 * @author meleshin.o
 * 
 */
public class FieldMatchValidator extends AbstractValidator<String> {

	private final ValidationMessages messages;

	private final ValueBaseField<String> otherField;

	private final String ownFieldName;

	private final String otherFieldName;

	/**
	 * @param otherField
	 *            - field with which validator compare the value of own field.
	 * */
	public FieldMatchValidator(final ValueBaseField<String> otherField,
			final String ownFieldName, final String otherFieldName,
			final ValidationMessages messages) {
		this.otherField = otherField;
		this.ownFieldName = ownFieldName;
		this.otherFieldName = otherFieldName;
		this.messages = messages;
	}

	@Override
	public List<EditorError> validate(final Editor<String> editor,
			final String value) {
		final String fieldValue = otherField.getValue();
		List<EditorError> errors = null;
		if ((value == null && fieldValue != null)
				|| (value != null && !value.equals(fieldValue))) {
			errors = createError(new DefaultEditorError(otherField,
					messages.fieldsDoNotMatch(ownFieldName, otherFieldName),
					value));
		}

		return errors;
	}
}

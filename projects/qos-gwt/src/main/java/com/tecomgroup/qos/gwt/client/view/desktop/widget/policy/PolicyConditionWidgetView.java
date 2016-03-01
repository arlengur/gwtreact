/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.validator.MinNumberValidator;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.PolicyConditionWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.TimeUnitComboBox;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.TimeUnitComboBox.TimeUnits;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.EnumEmptyValidator;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 * 
 */
public class PolicyConditionWidgetView
		extends
			ViewWithUiHandlers<PolicyConditionWidgetPresenter>
		implements
			PolicyConditionWidgetPresenter.MyView {

	private static class BooleanAsNumberValidator extends NumberValueValidator {

		private final List<Integer> allowedNumbers = Arrays.asList(0, 1);

		public BooleanAsNumberValidator(final ValidationMessages messages,
				final NumberField<Double> durationField) {
			super(messages, durationField);
		}

		@Override
		public List<EditorError> validateNumber(final Editor<String> editor,
				final String value) {
			List<EditorError> errors = null;

			final Integer intValue = Integer.valueOf(value);
			if (!allowedNumbers.contains(intValue)) {
				errors = createError(editor, messages.onlyBooleanAllowed(),
						value);
			}

			return errors;
		}
	}

	public static class NaturalNumberValidator extends NumberValueValidator {

		public NaturalNumberValidator(final ValidationMessages messages,
				final NumberField<Double> durationField) {
			super(messages, durationField);
		}

		@Override
		public List<EditorError> validateNumber(final Editor<String> editor,
				final String value) {
			List<EditorError> errors = null;

			final Integer intValue = Integer.valueOf(value);
			if (intValue < 0) {
				errors = createError(editor,
						messages.onlyNaturalNumberAllowed(), value);
			}

			return errors;
		}
	}

	public static class NumberValueValidator
			extends
				ValueFieldTrimEmptyValidator {

		public NumberValueValidator(final ValidationMessages messages,
				final NumberField<Double> durationField) {
			super(messages, durationField);
		}

		protected List<EditorError> createError(final Editor<?> editor,
				final String message, final Object value) {
			return createError(new DefaultEditorError(editor, message, value));
		}

		protected List<EditorError> createError(final EditorError... errors) {
			final List<EditorError> list = new ArrayList<EditorError>();
			for (final EditorError error : errors) {
				list.add(error);
			}
			return list;
		}

		@Override
		public List<EditorError> validate(final Editor<String> editor,
				final String value) {
			List<EditorError> errors = super.validate(editor, value);
			if ((errors == null || errors.isEmpty())
					&& SimpleUtils.isNotNullAndNotEmpty(value)) {
				try {
					errors = validateNumber(editor, value);
				} catch (final NumberFormatException ex) {
					errors = createError(editor, messages.NaN(), value);
				}
			}

			return errors;
		}

		protected List<EditorError> validateNumber(final Editor<String> editor,
				final String value) {
			// just parse double as default validation
			Double.valueOf(value);

			return null;
		}
	}

	private enum ParameterValidationMode {
		NATURAL, BOOLEAN, PERCENTAGE, ANY
	}

	private static class PercentNumberValidator extends NumberValueValidator {

		public PercentNumberValidator(final ValidationMessages messages,
				final NumberField<Double> durationField) {
			super(messages, durationField);
		}

		@Override
		public List<EditorError> validateNumber(final Editor<String> editor,
				final String value) {
			List<EditorError> errors = null;

			final Double doubleValue = Double.valueOf(value);
			if (doubleValue < 0 || doubleValue > 100) {
				errors = createError(editor, messages.onlyPercentageAllowed(),
						value);
			}

			return errors;
		}
	}

	private static class TimeUnitsEmptyValidator
			extends
				EnumEmptyValidator<TimeUnits> {

		private final NumberField<Double> durationField;

		public TimeUnitsEmptyValidator(final ValidationMessages messages,
				final NumberField<Double> durationField) {
			super(messages);
			this.durationField = durationField;
		}

		@Override
		public List<EditorError> validate(final Editor<TimeUnits> editor,
				final TimeUnits value) {
			List<EditorError> errors = Collections.<EditorError> emptyList();
			if (durationField.getValue() != null
					&& durationField.getValue() > 0) {
				errors = super.validate(editor, value);
			}
			return errors;
		}
	}

	private static class ValueFieldTrimEmptyValidator
			extends
				TrimEmptyValidator {

		private final NumberField<Double> durationField;

		public ValueFieldTrimEmptyValidator(final ValidationMessages messages,
				final NumberField<Double> durationField) {
			super(messages);
			this.durationField = durationField;
		}

		@Override
		public List<EditorError> validate(final Editor<String> editor,
				final String value) {
			List<EditorError> errors = Collections.<EditorError> emptyList();
			if (durationField.getValue() != null
					&& durationField.getValue() > 0) {
				errors = super.validate(editor, value);
			}
			return errors;
		}
	}

	private ParameterValidationMode currentValidationMode = ParameterValidationMode.ANY;

	private Widget widget;

	private final QoSMessages messages;

	private final AppearanceFactory appearanceFactory;

	private Field<String> raiseValue;
	private Field<String> ceaseValue;

	private Field<String> numericRaiseValue;
	private Field<String> numericCeaseValue;

	private Field<String> booleanRaiseValue;
	private Field<String> booleanCeaseValue;

	private SimpleContainer raiseValueWrapper;
	private SimpleContainer ceaseValueWrapper;

	private NumberField<Double> raiseDuration;
	private NumberField<Double> ceaseDuration;

	private TimeUnitComboBox raiseTimeUnit;
	private TimeUnitComboBox ceaseTimeUnit;

	private static final double MIN_DURATION_VALUE = 0d;

	private Map<ParameterValidationMode, List<Validator<String>>> validators;

	private final static String TRUE_VALUE = "1";

	private final static String FALSE_VALUE = "0";

	@Inject
	public PolicyConditionWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
	}

	private void addValueValidators(final ParameterValidationMode mode) {
		final List<Validator<String>> validatorsToAdd = validators.get(mode);

		raiseValue.addValidator(validatorsToAdd.get(0));
		ceaseValue.addValidator(validatorsToAdd.get(1));

		if (ParameterValidationMode.BOOLEAN != mode) {
			raiseValue.validate();
			ceaseValue.validate();
		}
		currentValidationMode = mode;
	}

	private boolean areConditionFieldsValid() {
		return raiseValue.validate() & raiseDuration.validate()
				& raiseTimeUnit.validate() & ceaseValue.validate()
				& ceaseDuration.validate() & ceaseTimeUnit.validate();
	}

	private boolean areConditionLevelsValid() {
		boolean isRaiseLevelValid = false;
		if (!isRaiseConditionEmpty()) {
			if (isRaiseConditionValid()) {
				isRaiseLevelValid = true;
			} else {
				AppUtils.showErrorMessage(messages
						.levelIsNotCompletelyDefined(messages.raise()));
			}
		}

		boolean isCeaseLevelValid = false;
		if (!isCeaseConditionEmpty()) {
			if (isCeaseConditionValid()) {
				isCeaseLevelValid = true;
			} else {
				AppUtils.showErrorMessage(messages
						.levelIsNotCompletelyDefined(messages.cease()));
			}
		}

		if (isRaiseConditionEmpty()
				&& (!isCeaseConditionEmpty() && isCeaseLevelValid)) {
			AppUtils.showErrorMessage(messages.levelIsNotDefined(messages
					.raise()));
		} else if (isCeaseConditionEmpty()
				&& (!isRaiseConditionEmpty() && isRaiseLevelValid)) {
			AppUtils.showErrorMessage(messages.levelIsNotDefined(messages
					.cease()));
		}

		return isRaiseLevelValid && isCeaseLevelValid;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private void clearInvalid() {
		clearInvalid(raiseValue);
		clearInvalid(raiseDuration);
		clearInvalid(raiseTimeUnit);
		clearInvalid(ceaseValue);
		clearInvalid(ceaseDuration);
		clearInvalid(ceaseTimeUnit);
	}

	private void clearInvalid(final Field<?> field) {
		field.clearInvalid();
	}

	private Field<String> createBooleanValueField() {
		final ListStore<String> store = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(final String item) {
						return item;
					}
				});
		store.add(TRUE_VALUE);
		store.add(FALSE_VALUE);

		final ComboBox<String> field = new ComboBox<String>(store,
				new LabelProvider<String>() {
					@Override
					public String getLabel(final String item) {
						String label = null;

						final Boolean value = SimpleUtils.stringAsBoolean(item);
						if (value != null) {
							if (value) {
								label = messages.actionYes();
							} else {
								label = messages.actionNo();
							}
						}
						return label;
					}
				});

		field.setWidth(60);
		field.setTriggerAction(ComboBoxCell.TriggerAction.ALL);
		field.setForceSelection(true);
		return field;
	}

	private NumberField<Double> createDurationField() {
		final NumberField<Double> field = new NumberField<Double>(
				new DoublePropertyEditor());
		field.setWidth(60);
		field.addValidator(new MinNumberValidator<Double>(MIN_DURATION_VALUE));
		field.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(final KeyPressEvent event) {
				final Character ch = event.getCharCode();
				final int code = event.getUnicodeCharCode();
				// code == 0 is fix for firefox. Somehow delete and backspace
				// buttons are considered as 0 here, only in firefox.
				if (!Character.isDigit(ch) && ch != '.' && code != 0) {
					event.stopPropagation();
					event.preventDefault();
				}
			}
		});
		return field;
	}

	protected FieldLabel createLabelField(final Widget widget,
			final String label) {
		final FieldLabel fieldLabel = new FieldLabel(widget, label);
		fieldLabel.addStyleName(appearanceFactory.resources().css()
				.textMainColor());

		return fieldLabel;
	}

	private TextField createNumericValueField() {
		final TextField value = new TextField();
		value.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(final KeyPressEvent event) {
				final Character ch = event.getCharCode();
				final int code = event.getUnicodeCharCode();
				// code == 0 is fix for firefox. Somehow delete and backspace
				// buttons are considered as 0 here, only in firefox.
				if (!Character.isDigit(ch) && ch != '.' && ch != '-'
						&& code != 0) {
					event.stopPropagation();
					event.preventDefault();
				}
			}
		});
		value.setWidth(60);
		return value;
	}

	private Widget createRow(final Container valueFieldWrapper,
			final NumberField<Double> duration, final TimeUnitComboBox timeUnit) {
		final CssFloatLayoutContainer row = new CssFloatLayoutContainer();
		row.add(getWidgetWithMargins(valueFieldWrapper, 10));
		final Label label = new Label(messages.within());
		label.setStyleName(appearanceFactory.resources().css().textMainColor());
		row.add(getWidgetWithMargins(label, 10));
		row.add(getWidgetWithMargins(duration, 10));
		row.add(timeUnit);
		return row;
	}

	private TimeUnitComboBox createTimeUnitField(
			final NumberField<Double> validationField) {
		final TimeUnitComboBox field = new TimeUnitComboBox(messages);
		field.addValidator(new TimeUnitsEmptyValidator(messages,
				validationField));
		return field;
	}

	private Map<ParameterValidationMode, List<Validator<String>>> createValidators(
			final NumberField<Double> raiseDuration,
			final NumberField<Double> ceaseDuration) {
		final Map<ParameterValidationMode, List<Validator<String>>> validators = new HashMap<ParameterValidationMode, List<Validator<String>>>();

		final List<Validator<String>> boolValidators = new ArrayList<Validator<String>>();
		boolValidators
				.add(new BooleanAsNumberValidator(messages, raiseDuration));
		boolValidators
				.add(new BooleanAsNumberValidator(messages, ceaseDuration));
		validators.put(ParameterValidationMode.BOOLEAN, boolValidators);

		final List<Validator<String>> anyValidators = new ArrayList<Validator<String>>();
		anyValidators.add(new NumberValueValidator(messages, raiseDuration));
		anyValidators.add(new NumberValueValidator(messages, ceaseDuration));
		validators.put(ParameterValidationMode.ANY, anyValidators);

		final List<Validator<String>> naturalValidators = new ArrayList<Validator<String>>();
		naturalValidators.add(new NaturalNumberValidator(messages,
				raiseDuration));
		naturalValidators.add(new NaturalNumberValidator(messages,
				ceaseDuration));
		validators.put(ParameterValidationMode.NATURAL, naturalValidators);

		final List<Validator<String>> percentageValidators = new ArrayList<Validator<String>>();
		percentageValidators.add(new PercentNumberValidator(messages,
				raiseDuration));
		percentageValidators.add(new PercentNumberValidator(messages,
				ceaseDuration));
		validators
				.put(ParameterValidationMode.PERCENTAGE, percentageValidators);

		return validators;
	}

	public String getTextOrNull(final Field<String> field) {
		String value = null;
		if (field.getValue() != null) {
			value = field.getValue().trim();
			if (value.isEmpty()) {
				value = null;
			}
		}
		return value;
	}

	private Widget getWidgetWithMargins(final Widget widget, final int margin) {
		widget.getElement().getStyle().setMarginRight(margin, Unit.PX);
		return widget;
	}

	@Override
	public boolean hasErrors() {
		return !((isRaiseConditionEmpty() || isRaiseConditionValid()) && (isCeaseConditionEmpty() || isCeaseConditionValid()))
				|| (isRaiseConditionEmpty() && !isCeaseConditionEmpty() && isCeaseConditionValid())
				|| (isCeaseConditionEmpty() && !isRaiseConditionEmpty() && isRaiseConditionValid());
	}

	private void initBooleanFields() {
		booleanRaiseValue = createBooleanValueField();
		booleanCeaseValue = createBooleanValueField();
		setBooleanFieldHandlers((ComboBox<String>) booleanRaiseValue,
				booleanCeaseValue);
		setBooleanFieldHandlers((ComboBox<String>) booleanCeaseValue,
				booleanRaiseValue);
	}

	@Inject
	protected void initialize() {
		raiseDuration = createDurationField();
		ceaseDuration = createDurationField();

		validators = createValidators(raiseDuration, ceaseDuration);

		raiseValueWrapper = new SimpleContainer();
		ceaseValueWrapper = new SimpleContainer();

		raiseTimeUnit = createTimeUnitField(raiseDuration);
		ceaseTimeUnit = createTimeUnitField(ceaseDuration);

		final VerticalLayoutContainer widget = new VerticalLayoutContainer();
		widget.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		widget.add(createRow(raiseValueWrapper, raiseDuration, raiseTimeUnit));
		final VerticalLayoutData layoutData = new VerticalLayoutData();
		layoutData.setMargins(new Margins(10, 0, 0, 0));
		widget.add(createRow(ceaseValueWrapper, ceaseDuration, ceaseTimeUnit),
				layoutData);
		widget.getElement().setPadding(new Padding(8));

		initNumericFields();
		initBooleanFields();
		setDefaultValueFields();

		addValueValidators(ParameterValidationMode.ANY);

		setValueFields(raiseValue, ceaseValue);

		this.widget = widget;
	}

	private void initNumericFields() {
		numericRaiseValue = createNumericValueField();
		numericCeaseValue = createNumericValueField();
	}

	private boolean isCeaseConditionEmpty() {
		return isConditionEmpty(ceaseValue, ceaseDuration);
	}

	private boolean isCeaseConditionValid() {
		return isConditionValid(ceaseValue, ceaseDuration, ceaseTimeUnit);
	}

	private boolean isConditionEmpty(final Field<String> conditionField,
			final NumberField<Double> durationField) {
		return !SimpleUtils.isNotNullAndNotEmpty(conditionField.getValue())
				&& isDurationFieldEmpty(durationField);
	}

	private boolean isConditionValid(final Field<String> conditionField,
			final NumberField<Double> durationField,
			final TimeUnitComboBox timeUnitField) {
		return conditionField.validate()
				&& SimpleUtils.isNotNullAndNotEmpty(conditionField.getValue())
				&& isDurationFieldValid(durationField, timeUnitField);
	}

	private boolean isDurationFieldEmpty(final NumberField<Double> durationField) {
		return durationField.getValue() == null
				|| durationField.getValue().equals(0.0);
	}

	private boolean isDurationFieldValid(
			final NumberField<Double> durationField,
			final TimeUnitComboBox timeUnitField) {
		return isDurationFieldEmpty(durationField)
				|| (durationField.getValue() > 0.0 && timeUnitField.getValue() != null);
	}

	private boolean isRaiseConditionEmpty() {
		return isConditionEmpty(raiseValue, raiseDuration);
	}

	private boolean isRaiseConditionValid() {
		return isConditionValid(raiseValue, raiseDuration, raiseTimeUnit);
	}

	@Override
	public void refreshCondition() {
		clearInvalid();
		((VerticalLayoutContainer) widget).forceLayout();

		final ConditionLevel conditionLevel = getUiHandlers()
				.getConditionLevel();
		if (conditionLevel == null) {
			numericRaiseValue.setValue(null, true);
			numericCeaseValue.setValue(null, true);
			booleanRaiseValue.setValue(null, true);
			booleanCeaseValue.setValue(null, true);

			raiseDuration.setValue(0.0, true);
			ceaseDuration.setValue(0.0, true);
			raiseTimeUnit.setValue(TimeUnits.SECONDS, true);
			ceaseTimeUnit.setValue(TimeUnits.SECONDS, true);
		} else {
			raiseValue.setValue(conditionLevel.getRaiseLevel(), true);
			ceaseValue.setValue(conditionLevel.getCeaseLevel(), true);
			raiseTimeUnit
					.setDuration(raiseDuration, SimpleUtils
							.safeFromLong(conditionLevel.getRaiseDuration()));
			ceaseTimeUnit
					.setDuration(ceaseDuration, SimpleUtils
							.safeFromLong(conditionLevel.getCeaseDuration()));
		}
	}

	private void removeValueValidators(final ParameterValidationMode mode) {
		final List<Validator<String>> validatorsToRemove = validators.get(mode);

		raiseValue.removeValidator(validatorsToRemove.get(0));
		ceaseValue.removeValidator(validatorsToRemove.get(1));
		clearInvalid(raiseValue);
		clearInvalid(ceaseValue);
	}

	@Override
	public void saveCondition() {
		if (areConditionFieldsValid() && areConditionLevelsValid()) {
			ConditionLevel conditionLevel = getUiHandlers().getConditionLevel();
			if (conditionLevel == null) {
				conditionLevel = new ConditionLevel();
				getUiHandlers().setConditionLevel(conditionLevel);
			}
			conditionLevel.setCeaseDuration(ceaseTimeUnit
					.getDuration(ceaseDuration));
			conditionLevel.setRaiseDuration(raiseTimeUnit
					.getDuration(raiseDuration));
			conditionLevel.setCeaseLevel(getTextOrNull(ceaseValue));
			conditionLevel.setRaiseLevel(getTextOrNull(raiseValue));
			conditionLevel.setCeaseLevelDouble(null);
			conditionLevel.setRaiseLevelDouble(null);
		} else {
			getUiHandlers().setConditionLevel(null);
		}
	}

	private void setBooleanFieldHandlers(final ComboBox<String> field,
			final Field<String> pairField) {
		field.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(final ValueChangeEvent<String> event) {
				// sync fields
				pairField.setValue(event.getValue(), true);
			}
		});

		field.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(final SelectionEvent<String> event) {
				// sync fields
				pairField.setValue(event.getSelectedItem(), true);
			}
		});
	}

	private void setBooleanValueFields() {
		raiseValue = booleanRaiseValue;
		ceaseValue = booleanCeaseValue;

		setValueFields(raiseValue, ceaseValue);
	}

	private void setDefaultValueFields() {
		raiseValue = numericRaiseValue;
		ceaseValue = numericCeaseValue;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		raiseValue.setEnabled(enabled);
		ceaseValue.setEnabled(enabled);
		raiseDuration.setEnabled(enabled);
		ceaseDuration.setEnabled(enabled);
		raiseTimeUnit.setEnabled(enabled);
		ceaseTimeUnit.setEnabled(enabled);
	}

	private void setNumericValueFields() {
		raiseValue = numericRaiseValue;
		ceaseValue = numericCeaseValue;

		setValueFields(raiseValue, ceaseValue);
	}

	@Override
	public void setParameterType(final ParameterType type) {
		removeValueValidators(currentValidationMode);
		switch (type) {
			case PERCENTAGE :
				setNumericValueFields();
				addValueValidators(ParameterValidationMode.PERCENTAGE);
				break;
			case COUNTER :
				setNumericValueFields();
				addValueValidators(ParameterValidationMode.NATURAL);
				break;
			case BOOL :
				setBooleanValueFields();
				addValueValidators(ParameterValidationMode.BOOLEAN);
				break;
			default :
				setNumericValueFields();
				addValueValidators(ParameterValidationMode.ANY);
		}
	}

	private void setValueFields(final Field<String> raiseField,
			final Field<String> ceaseField) {
		raiseValue.setEnabled(true);
		raiseValueWrapper.clear();
		raiseValueWrapper.add(raiseField);

		ceaseValue.setEnabled(true);
		ceaseValueWrapper.clear();
		ceaseValueWrapper.add(ceaseField);
	}
}

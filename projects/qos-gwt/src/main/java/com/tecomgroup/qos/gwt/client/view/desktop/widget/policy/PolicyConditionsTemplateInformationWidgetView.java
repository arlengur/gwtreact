/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplateInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ParameterTypeLabelProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.EnumEmptyValidator;

/**
 * @author ivlev.e
 * 
 */
public class PolicyConditionsTemplateInformationWidgetView
		extends
			AbstractPolicyComponentTemplateInformationWidgetView<MPolicyConditionsTemplate, PolicyConditionsTemplateInformationWidgetPresenter>
		implements
			PolicyConditionsTemplateInformationWidgetPresenter.MyView {

	private ComboBox<ParameterType> parameterTypeCombo;

	private final Dialog.DialogMessages typeChangedSaveDialogMessages = new BaseDialog.BaseDialogMessages(
			messages);

	@Inject
	public PolicyConditionsTemplateInformationWidgetView(
			final EventBus eventBus, final DialogFactory dialogFactory) {
		super(eventBus, dialogFactory);
	}

	private ComboBox<ParameterType> createParameterTypeCombo() {
		final ComboBox<ParameterType> parameterTypeCombo = new ComboBox<ParameterType>(
				new ListStore<ParameterType>(
						new ModelKeyProvider<ParameterType>() {

							@Override
							public String getKey(final ParameterType item) {
								return item.name();
							}

						}), new ParameterTypeLabelProvider(messages));

		parameterTypeCombo.addValidator(new EnumEmptyValidator<ParameterType>(
				messages));
		parameterTypeCombo.setTriggerAction(TriggerAction.ALL);
		parameterTypeCombo.setForceSelection(true);

		ListStore<ParameterType> store = parameterTypeCombo.getStore();
		store.add(ParameterType.LEVEL);
		store.add(ParameterType.COUNTER);
		store.add(ParameterType.PERCENTAGE);
		store.add(ParameterType.BOOL);

		parameterTypeCombo
				.addValueChangeHandler(new ValueChangeHandler<MResultParameterConfiguration.ParameterType>() {

					@Override
					public void onValueChange(
							final ValueChangeEvent<ParameterType> event) {
						if (event.getValue() != null) {
							getUiHandlers().onParameterTypeChange(
									event.getValue());
						}
					}
				});

		parameterTypeCombo
				.addSelectionHandler(new SelectionHandler<ParameterType>() {
                    @Override
                    public void onSelection(SelectionEvent<ParameterType> event) {
                        if (event.getSelectedItem() != null) {
                            getUiHandlers().onParameterTypeChange(
                                    event.getSelectedItem());
                        }
                    }
                });

		return parameterTypeCombo;
	}

	private ConfirmationDialog createTypeChangedSaveDialog() {
		final ConfirmationDialog typeChangedSaveDialog = dialogFactory
				.createWarningDialog(messages.actionSave(), messages
						.policyConditionTemplateSaveTypeChangedConfirm(),
						new ConfirmationDialog.ConfirmationHandler() {

							@Override
							public void onCancel() {
								// return to editing
							}

							@Override
							public void onConfirm(final String comment) {
								getUiHandlers().saveOrUpdateTemplate(false);
							}
						}, typeChangedSaveDialogMessages, 320);
		return typeChangedSaveDialog;
	}

	@Override
	protected int getDefaultDialogHeight() {
		return 185;
	}

	@Override
	protected int getDefaultDialogWidth() {
		return 570;
	}

	@Override
	protected Widget getDialogContent() {
		final Widget content = super.getDialogContent();

		if (parameterTypeCombo == null) {
			parameterTypeCombo = createParameterTypeCombo();
			final FieldLabel label = new FieldLabel(parameterTypeCombo,
					messages.parameterType());
			label.setLabelWidth(115);

			final VerticalLayoutData layoutData = new VerticalLayoutData(1, -1,
					new Margins(10, 5, 3, 5));
			verticalLayoutContainer.add(label, layoutData);
		}

		return content;
	}

	@Override
	public ParameterType getParameterType() {
		return parameterTypeCombo.getValue();
	}

	@Override
	public void reset() {
		templateNameField.reset();
		parameterTypeCombo.reset();
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		verticalLayoutContainer.add(content, new VerticalLayoutData(1, 1,
				new Margins(10, 5, -5, 5)));
	}

	@Override
	public void setParameterType(final ParameterType type) {
		parameterTypeCombo.setValue(type);
	}

	@Override
	public void showTypeChangedSaveDialog() {
		createTypeChangedSaveDialog().show();
	}

	@Override
	public boolean validate() {
		return templateNameField.validate() && parameterTypeCombo.validate();
	}
}

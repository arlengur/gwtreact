/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Dialog.DialogMessages;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.AbstractPolicyComponentTemplateInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractEntityEditorDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;

/**
 * @author ivlev.e
 *
 */
public abstract class AbstractPolicyComponentTemplateInformationWidgetView<M extends MPolicyComponentTemplate, U extends AbstractPolicyComponentTemplateInformationWidgetPresenter<M, ?>>
		extends
			AbstractEntityEditorDialogView<M, U>
		implements
			AbstractPolicyComponentTemplateInformationWidgetPresenter.MyView<M, U> {

	protected TextField templateNameField;

	protected VerticalLayoutContainer verticalLayoutContainer;

	protected final DialogFactory dialogFactory;

	protected final DialogMessages updateModeSelectDialogMessages = new BaseDialog.BaseDialogMessages(
			messages) {

		@Override
		public String cancel() {
			return messages.actionNo();
		}

		@Override
		public String ok() {
			return messages.actionYes();
		}
	};

	private SimpleContainer parentContainer;

	private final static int DEFAULT_DIALOG_HEIGHT = 400;

	private final static int DEFAULT_DIALOG_WIDTH = 500;

	@Inject
	public AbstractPolicyComponentTemplateInformationWidgetView(
			final EventBus eventBus, final DialogFactory dialogFactory) {
		super(eventBus, AppUtils.getMessages());
		this.dialogFactory = dialogFactory;
	}

	@Override
	protected void actionOkButtonPressed() {
		getUiHandlers().actionOkButtonPressed();
	}

	@Override
	protected QoSDialog createDialog() {
		final QoSDialog dialog = super.createDialog();
		dialog.addStyleName(ClientConstants.QOS_POLICY_ACTIONS_INFORMATION_DIALOG);
		return dialog;
	}

	private ConfirmationDialog createUpdateModeSelectionDialog() {
		final ConfirmationDialog updateModeSelectionDialog = dialogFactory
				.createWarningDialog(messages.applyNotificationTemplate(),
						messages.policyComponentTemplateReapplyConfirm(),
						new ConfirmationHandler() {

							@Override
							public void onCancel() {
								getUiHandlers().saveOrUpdateTemplate(false);
							}

							@Override
							public void onConfirm(final String comment) {
								getUiHandlers().saveOrUpdateTemplate(true);
							}
						}, updateModeSelectDialogMessages, 320);
		return updateModeSelectionDialog;
	}

	@Override
	protected String getCreationDialogTitle() {
		return messages.createNewPolicyActionsTemplate();
	}

	protected int getDefaultDialogHeight() {
		return DEFAULT_DIALOG_HEIGHT;
	}

	protected int getDefaultDialogWidth() {
		return DEFAULT_DIALOG_WIDTH;
	}

	@Override
	protected Widget getDialogContent() {
        parentContainer = new SimpleContainer();
        parentContainer.setWidth(getDefaultDialogWidth());
        parentContainer.setHeight(getDefaultDialogHeight());
        verticalLayoutContainer = new VerticalLayoutContainer();
        parentContainer.add(verticalLayoutContainer);

        templateNameField = new TextField();
        templateNameField.addValidator(new TrimEmptyValidator(messages));

        final FieldLabel label = new FieldLabel(templateNameField,
                messages.templateName());
        label.setLabelWidth(115);

        final VerticalLayoutData layoutData = new VerticalLayoutData(1, -1,
                new Margins(10, 5, 3, 5));
        verticalLayoutContainer.add(label, layoutData);

		return parentContainer;
	}

	@Override
	public String getName() {
		return templateNameField.getValue();
	}

	@Override
	protected String getUpdateDialogTitle() {
		return messages.editPolicyActionsTemplate();
	}

	@Override
	public void reset() {
		templateNameField.reset();
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		verticalLayoutContainer.add(content, new VerticalLayoutData(1, 1,
				new Margins(0, 5, 0, 5)));
	}

	@Override
	public void setPolicyComponentTemplateName(final String name) {
		templateNameField.setValue(name);
	}

	@Override
	public void showTemplateUpdateModeSelectionDialog() {
		createUpdateModeSelectionDialog().show();
	}

	@Override
	public boolean validate() {
		return templateNameField.validate();
	}
}

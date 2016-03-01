/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.DialogMessages;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog.BaseDialogMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;

/**
 * @author meleshin.o
 * 
 */
public class SaveTemplateWidgetView
		extends
			AbstractTemplateWidgetView<SaveTemplatePresenterWidget>
		implements
			SaveTemplatePresenterWidget.MyView {

	private final DialogFactory dialogFactory;

	private TextField templateControl;

	@Inject
	public SaveTemplateWidgetView(final EventBus eventBus,
			final AppearanceFactory appearanceFactory,
			final DialogFactory dialogFactory, final QoSMessages messages) {
		super(eventBus, appearanceFactory, messages);
		this.dialogFactory = dialogFactory;
	}

	private MUserAbstractTemplate createTemplate(final String templateName) {
		return getUiHandlers().createTemplate(templateName);
	}

	private DialogMessages getDefaultDialogMessages() {
		return new BaseDialogMessages(messages) {
			@Override
			public String ok() {
				return messages.actionSave();
			}
		};
	}

	private Map<String, MUserAbstractTemplate> getTemplates() {
		return getUiHandlers().getTemplates();
	}

	@Inject
	public void initialize() {
		dialog = new QoSDialog(appearanceFactory, messages,
				getDefaultDialogMessages()) {

			@Override
			protected TextButton getButtonPressedOnEnter() {
				return getSaveButton();
			}

			protected TextButton getSaveButton() {
				return (TextButton) getButtonBar().getItemByItemId(
						PredefinedButton.OK.name());
			}

			@Override
			protected String getTitleText(final QoSMessages messages) {
				return messages.tempalteSavingHeader();
			}

			@Override
			protected void initializeComponents() {
				templateControl = new TextField();
				templateControl.setAllowBlank(false);
				templateControl.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
				templateControl.addValidator(new MaxLengthValidator(
						ClientConstants.TEMPLATE_NAME_MAX_LENGTH));
				final FieldLabel label = new FieldLabel(templateControl,
						messages.name());
				label.setLabelWidth(50);
				add(label, new MarginData(5));
			}

			@Override
			protected void onButtonPressed(final TextButton button) {
				if (button == getCancelButton()) {
					hide();
				} else if (button == getSaveButton()) {
					// call for finishEditing is necessary for correct
					// templateControl validation
					templateControl.finishEditing();

					final String templateName = templateControl.getText()
							.trim();

					if (!templateName.isEmpty() && templateControl.isValid()) {
						final MUserAbstractTemplate template;
						final Map<String, MUserAbstractTemplate> templates = getTemplates();
						if (templates.get(templateName) == null) {
							template = createTemplate(templateName);
							saveTemplate(template);
							hide();
						} else {
							template = templates.get(templateName);
							dialogFactory.createConfirmationDialog(
									new ConfirmationHandler() {

										@Override
										public void onCancel() {
										}

										@Override
										public void onConfirm(
												final String comment) {
											saveTemplate(template);
											dialog.hide();
										}
									}, messages.tempalteSavingHeader(),
									messages.templateSavingConfirmation(),
									CommentMode.DISABLED).show();
						}
					}
				}
			}
		};
		dialog.setPredefinedButtons(PredefinedButton.OK,
				PredefinedButton.CANCEL);
	}

	private void saveTemplate(final MUserAbstractTemplate template) {
		getUiHandlers().saveTemplate(template);
	}

	@Override
	public void setTemplate(final MUserAbstractTemplate template) {
		if (template == null) {
			templateControl.reset();
		} else {
			templateControl.setValue(template.getName(), true);
		}
	}

	@Override
	public void show() {
		super.show();
		dialog.setFocusWidget(templateControl);
	}
}

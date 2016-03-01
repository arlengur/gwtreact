/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog.DialogMessages;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog.BaseDialogMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.UserTemplateProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomComboBox;

/**
 * @author meleshin.o
 * 
 */
public class LoadTemplateWidgetView
		extends
			AbstractTemplateWidgetView<LoadTemplatePresenterWidget>
		implements
			LoadTemplatePresenterWidget.MyView {

	private UserTemplateProperties properties;

	private CustomComboBox<MUserAbstractTemplate> templateControl;

	@Inject
	public LoadTemplateWidgetView(final EventBus eventBus,
			final QoSMessages messages,
			final AppearanceFactory appearanceFactory) {
		super(eventBus, appearanceFactory, messages);
	}

	private DialogMessages getDefaultDialogMessages() {
		return new BaseDialogMessages(messages) {
			@Override
			public String ok() {
				return messages.actionSelect();
			}
		};
	}

	private UserTemplateProperties getProperties() {
		return getUiHandlers().getProperties();
	}

	private ListStore<MUserAbstractTemplate> getStore() {
		return getUiHandlers().getStore();
	}

	@Override
	@Inject
	public void initialize() {
		dialog = new QoSDialog(appearanceFactory, messages,
				getDefaultDialogMessages()) {

			@Override
			protected TextButton getButtonPressedOnEnter() {
				return getLoadButton();
			}

			protected TextButton getLoadButton() {
				return (TextButton) getButtonBar().getItemByItemId(
						PredefinedButton.OK.name());
			}

			@Override
			protected String getTitleText(final QoSMessages messages) {
				return messages.templateLoadingHeader();
			}

			@Override
			protected void initializeComponents() {
				properties = getProperties();
				templateControl = new CustomComboBox<MUserAbstractTemplate>(
						getStore(), properties.name());
				templateControl.setUpdateValueOnSelection(true);
				templateControl.setAllowBlank(false);
				templateControl.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
				templateControl.setEmptyText(messages.emptyTemplateText());
				templateControl.setForceSelection(true);
				templateControl.setValidateOnBlur(false);
				templateControl.setTypeAhead(true);
				templateControl.setTriggerAction(TriggerAction.ALL);
				setFocusWidget(templateControl);
				final FieldLabel label = new FieldLabel(templateControl,
						messages.name());
				label.setLabelWidth(50);
				add(label, new MarginData(5));
			}

			@Override
			protected void onButtonPressed(final TextButton button) {
				if (button == getCancelButton()) {
					hide();
				} else if (button == getLoadButton()) {
					final MUserAbstractTemplate template = templateControl
							.getValue();
					if (template != null) {
						loadTemplate(template);
						hide();
					}
				}
			}

		};
		dialog.setPredefinedButtons(PredefinedButton.OK,
				PredefinedButton.CANCEL);
	}

	private void loadTemplate(final MUserAbstractTemplate template) {
		getUiHandlers().loadTemplate(template);
	}

	@Override
	public void setTemplate(final MUserAbstractTemplate template) {
		if (template == null) {
			templateControl.reset();
		} else {
			templateControl.setValue(template, true);
		}
	}

	@Override
	public void show() {
		super.show();
		dialog.setFocusWidget(templateControl);
	}

}

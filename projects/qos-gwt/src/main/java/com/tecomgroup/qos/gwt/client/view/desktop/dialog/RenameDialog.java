/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.ResultsAnalyticsPresenter.MyView;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;

/**
 * @author ivlev.e
 * 
 */
public class RenameDialog extends QoSDialog {

	public static interface RenameHandler {
		void rename(String oldName, String newName);
	}

	private final String oldName;

	private final TextField textField;

	private final MyView view;

	private final RenameHandler handler;

	public RenameDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final RenameHandler handler,
			final String oldName, final MyView view) {
		super(appearanceFactory, messages);
		this.view = view;
		this.oldName = oldName;
		this.handler = handler;
		setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		textField = new TextField();
		textField.setValue(oldName, true);
		textField.addValidator(new TrimEmptyValidator(messages));
	}

	@Override
	protected TextButton getButtonPressedOnEnter() {
		return getOkButton();
	}

	protected TextButton getOkButton() {
		return (TextButton) getButtonBar().getItemByItemId(
				PredefinedButton.OK.name());
	}

	@Override
	protected String getTitleText(final QoSMessages messages) {
		return oldName;
	}

	@Override
	protected void initializeComponents() {
		final FieldLabel label = new FieldLabel(textField,
				messages.chartRenamePrompt());
		label.setLabelAlign(LabelAlign.TOP);
		textField.setWidth(190);
		textField.setAllowBlank(false);
		add(label, new MarginData(5));
	}

	@Override
	protected void onButtonPressed(final TextButton button) {
		if (button == getCancelButton()) {
			hide();
		} else if (button == getOkButton()) {
			if (textField.validate()) {
				final String newName = textField.getValue().trim();
				if (!newName.isEmpty() && !newName.equals(oldName)
						&& view.renameChart(oldName, newName)) {
					handler.rename(oldName, newName);
					hide();
				}

			}
		}
	}

}

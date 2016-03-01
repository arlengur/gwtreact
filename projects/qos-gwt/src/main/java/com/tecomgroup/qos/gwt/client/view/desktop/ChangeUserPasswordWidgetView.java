/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.ChangeUserPasswordWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog.BaseDialogMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.FieldMatchValidator;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author meleshin.o
 * 
 */
public class ChangeUserPasswordWidgetView
		extends
			SenchaPopupView<ChangeUserPasswordWidgetPresenter>
		implements
			ChangeUserPasswordWidgetPresenter.MyView {

	interface ViewUiBinder
			extends
				UiBinder<Widget, ChangeUserPasswordWidgetView> {
	}

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private static final int CHANGE_PASSWORD_DIALOG_HEIGHT = 199;

	@UiField(provided = true)
	protected QoSDialog dialog;

	@UiField
	protected PasswordField oldPasswordField;

	@UiField
	protected PasswordField newPasswordField;

	@UiField(provided = true)
	protected ContentPanel changePasswordContentPanel;

	@UiField
	protected VerticalLayoutContainer verticalContainer;

	@UiField
	protected PasswordField confirmPasswordField;

	private final QoSMessages messages;

	private final AppearanceFactory appearanceFactory;

	@Inject
	public ChangeUserPasswordWidgetView(final EventBus eventBus,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(eventBus);
		this.appearanceFactory = appearanceFactory;
		this.messages = messages;

		initializeUI();
	}

	@Override
	public Widget asWidget() {
		return dialog;
	}

	private void initializeUI() {
		dialog = new QoSDialog(appearanceFactory, messages,
				new BaseDialogMessages(messages) {
					@Override
					public String ok() {
						return messages.actionSave();
					}
				}) {

			@Override
			protected TextButton createDialogButton(final String text,
					final String itemId) {
				final TextButton textButton = new TextButton(
						new TextButtonCell(
								appearanceFactory
										.<String> buttonCellHugeAppearance()),
						text);
				textButton.setWidth(130);
				textButton.setItemId(itemId);
				return textButton;
			}

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
				return messages.changePassword();
			}

			@Override
			protected void initializeComponents() {
			}

			@Override
			protected void onButtonPressed(final TextButton button) {
				if (button == getCancelButton() || button == getCloseButton()) {
					hide();
				} else if (button == getSaveButton()) {
					if (isFormValid()) {
						updatePassword(oldPasswordField.getText(),
								newPasswordField.getText());
					}
				}
			}
		};
		dialog.setPredefinedButtons(PredefinedButton.OK,
				PredefinedButton.CANCEL);

		changePasswordContentPanel = new ContentPanel(
				appearanceFactory.lightFramedPanelAppearance());
		StyleUtils.configureNoHeaders(changePasswordContentPanel);

		UI_BINDER.createAndBindUi(this);

		confirmPasswordField.addValidator(new FieldMatchValidator(
				newPasswordField, messages.confirmPassword(), messages
						.loginPassword(), messages));
		verticalContainer.addStyleName(appearanceFactory.resources().css()
				.textDisabledColor());
	}

	private boolean isFormValid() {
		return SimpleUtils.isNotNullAndNotEmpty(oldPasswordField.getValue())
				&& confirmPasswordField.isValid();
	}

	@Override
	public void show() {
		oldPasswordField.clear();
		newPasswordField.clear();
		confirmPasswordField.clear();
		dialog.setHeight(CHANGE_PASSWORD_DIALOG_HEIGHT);
		dialog.show();
	}

	private void updatePassword(final String oldPassword,
			final String newPassword) {
		getUiHandlers().updatePassword(oldPassword, newPassword);
	}
}

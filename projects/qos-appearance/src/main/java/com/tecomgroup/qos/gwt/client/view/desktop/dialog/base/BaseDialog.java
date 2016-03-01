/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog.base;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.tecomgroup.qos.gwt.client.i18n.CommonMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * @author abondin
 * 
 */
public abstract class BaseDialog<T extends CommonMessages> extends Dialog {

	public static class BaseDialogMessages implements DialogMessages {
		private final CommonMessages messages;

		/**
		 * @param messages
		 */
		public BaseDialogMessages(final CommonMessages messages) {
			super();
			this.messages = messages;
		}

		@Override
		public String cancel() {
			return messages.actionCancel();
		}

		@Override
		public String close() {
			return messages.actionClose();
		}

		@Override
		public String no() {
			return messages.actionNo();
		}

		@Override
		public String ok() {
			return messages.actionOk();
		}

		@Override
		public String yes() {
			return messages.actionYes();
		}
	}

	protected final AppearanceFactory appearanceFactory;

	protected final T messages;

	protected final SelectHandler handler = new SelectHandler() {

		@Override
		public void onSelect(final SelectEvent event) {
			onButtonPressed((TextButton) event.getSource());
		}
	};

	protected boolean initialized = false;

	public BaseDialog(final AppearanceFactory appearanceFactory,
			final T messages) {
		this(appearanceFactory, messages, null);
	}

	public BaseDialog(final AppearanceFactory appearanceFactory,
			final T messages, DialogMessages dialogMessages) {
		super(appearanceFactory.dialogAppearanceWithHeader());
		this.messages = messages;
		this.appearanceFactory = appearanceFactory;
		setMinWidth(100);

		if (dialogMessages == null) {
			dialogMessages = getDefaultDialogMessages(messages);
		}
		setDialogMessages(dialogMessages);

		addStyleName(AppearanceFactory.DIALOG_WITH_HEADER);
		setPredefinedButtons(PredefinedButton.CLOSE);
		setClosable(false);
		setOnEsc(true);
		setModal(true);
		setResizable(false);
	}

	@Override
	protected void createButtons() {
		getButtonBar().clear();
		getButtonBar().setEnableOverflow(false);
		setFocusWidget(null);

		for (int i = 0; i < getPredefinedButtons().size(); i++) {
			final PredefinedButton button = getPredefinedButtons().get(i);
			final TextButton textButton = createDialogButton(getText(button),
					button.name());
			textButton.addSelectHandler(handler);
			if (i == 0) {
				setFocusWidget(textButton);
			}
			addButton(textButton);
		}
	}

	protected TextButton createDialogButton(final String text,
			final String itemId) {
		final TextButton textButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellLightAppearance()), text);
		textButton.setWidth(100);
		textButton.setItemId(itemId);
		return textButton;
	}

	/**
	 * Returns button that will be pressed on ENTER key press event. Null by
	 * default.
	 */
	protected TextButton getButtonPressedOnEnter() {
		return null;
	}

	protected TextButton getCancelButton() {
		return (TextButton) getButtonBar().getItemByItemId(
				PredefinedButton.CANCEL.name());
	}

	protected TextButton getCloseButton() {
		return (TextButton) getButtonBar().getItemByItemId(
				PredefinedButton.CLOSE.name());
	}

	/**
	 * @param messages
	 * @return
	 */
	protected DialogMessages getDefaultDialogMessages(final T messages) {
		return new BaseDialogMessages(messages);
	}

	protected abstract String getTitleText(T messages);

	protected abstract void initializeComponents();

	@Override
	protected void onKeyPress(final Event we) {
		final int keyCode = we.getKeyCode();

		if (getElement().isOrHasChild(we.getEventTarget().<Element> cast())) {

			if (isOnEsc() && keyCode == KeyCodes.KEY_ESCAPE) {
				hide();
			}

			if (keyCode == KeyCodes.KEY_ENTER
					&& getButtonPressedOnEnter() != null) {
				onButtonPressed(getButtonPressedOnEnter());
			}

		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (!initialized) {
			initializeComponents();
			initialized = true;
		}
	}

	/**
	 * Assigns height and min height of dialog
	 */
	@Override
	public void setHeight(final int height) {
		super.setHeight(height);
		setMinHeight(height);
	}

	@Override
	public void setPredefinedButtons(final PredefinedButton... buttons) {
		// Skip first invocation from super constuctor
		if (appearanceFactory != null) {
			super.setPredefinedButtons(buttons);
		}
	}

	/**
	 * Assigns width and min width of dialog
	 */
	@Override
	public void setWidth(final int width) {
		super.setWidth(width);
		setMinWidth(width);
	}

	@Override
	public void show() {
		setHeadingText(getTitleText(messages));
		super.show();
		setFocusWidget(null);
		center();
	}
}

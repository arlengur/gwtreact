/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 * 
 */
public class ConfirmationDialog extends QoSDialog {

	/**
	 * Manages comment presence in confirmation dialog
	 */
	public enum CommentMode {
		DISABLED, OPTIONAL, REQUIRED
	}

	public static interface ConfirmationHandler {
		void onCancel();
		void onConfirm(String comment);
	}

	protected final String title;
	protected final String message;
	protected final CommentMode commentMode;
	protected final ConfirmationHandler handler;
	private final int width;

	protected TextArea commentArea;

	/**
	 * Minimal width that can be set for dialog in constructor.
	 */
	private final static int MIN_WIDTH = 100;
	private final static int CONTENT_HORIZONTAL_MARGIN = 15;

	/**
	 * To manage comment presence please use {@link CommentMode}
	 * 
	 * @param appearanceFactory
	 * @param messages
	 * @param handler
	 * @param title
	 * @param message
	 * @param commentMode
	 */
	public ConfirmationDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final ConfirmationHandler handler,
			final String title, final String message,
			final CommentMode commentMode) {
		this(appearanceFactory, messages, handler, title, message, commentMode,
				null);
	}

	public ConfirmationDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final ConfirmationHandler handler,
			final String title, final String message,
			final CommentMode commentMode, final DialogMessages dialogMessages) {
		this(appearanceFactory, messages, handler, title, message, commentMode,
				dialogMessages, MIN_WIDTH);
	}

	public ConfirmationDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final ConfirmationHandler handler,
			final String title, final String message,
			final CommentMode commentMode, final DialogMessages dialogMessages,
			final int width) {
		super(appearanceFactory, messages, dialogMessages);
		this.title = title;
		this.message = message;
		this.commentMode = commentMode;
		this.handler = handler;
		this.width = width < MIN_WIDTH ? MIN_WIDTH : width;
		setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		setMinWidth(width);
	}

	@Override
	protected TextButton getButtonPressedOnEnter() {
		return getOkButton();
	}

	public String getComment() {
		final String comment = isCommentEnabled() ? commentArea.getText()
				.trim() : null;
		if (SimpleUtils.isNotNullAndNotEmpty(comment)) {
			return comment;
		} else {
			return null;
		}
	}

	protected TextButton getOkButton() {
		return (TextButton) getButtonBar().getItemByItemId(
				PredefinedButton.OK.name());
	}

	@Override
	protected String getTitleText(final QoSMessages messages) {
		return title;
	}

	@Override
	protected void initializeComponents() {
		final VerticalLayoutContainer container = new VerticalLayoutContainer();
		final VerticalLayoutData margings = new VerticalLayoutData();
		margings.setMargins(new Margins(5, 5, 15, 5));
		if (message != null) {
			final Label label = new Label(message);
			label.addStyleName(appearanceFactory.resources().css()
					.textAlignCenter());
			label.setWidth((width - CONTENT_HORIZONTAL_MARGIN * 2) + "px");
			label.setWordWrap(true);
			container.add(label, margings);
		}
		if (isCommentEnabled()) {
			container.add(new Label(messages.comment() + ":"), margings);
			commentArea = new TextArea();
			commentArea.addStyleName(appearanceFactory.resources().css()
					.textAlignCenter());
			commentArea.setSize("400px", "150px");
			container.add(commentArea, margings);
			// FIXME change this part when implementing
			// http://rnd.tecom.nnov.ru/issues/2614
			commentArea.addValidator(new MaxLengthValidator(255));
			if (commentMode == CommentMode.REQUIRED) {
				commentArea.addValidator(new TrimEmptyValidator(messages));
			}
		}
		add(container);
	}

	private boolean isCommentEnabled() {
		return ((commentMode == CommentMode.OPTIONAL) || (commentMode == CommentMode.REQUIRED));
	}

	@Override
	protected void onButtonPressed(final TextButton button) {
		if (button == getCancelButton()) {
			handler.onCancel();
			hide();
		} else if (button == getOkButton()) {
			if (isCommentEnabled() && !commentArea.validate()) {
				return;
			}
			handler.onConfirm(getComment());
			hide();
		}
	}
}

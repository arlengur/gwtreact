/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * Provides dialog with "Attention" icon. Used as {@link ConfirmationDialog} or
 * as {@link ErrorDialog} with icon
 * 
 * @author kshnyakin.m
 * 
 */
public class WarningDialog extends ConfirmationDialog {

	final int WARNING_DIALOG_MIN_WIDTH = 270;

	public WarningDialog(final String title, final String message,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		this(title, message, appearanceFactory, messages, null);
	}

	public WarningDialog(final String title, final String message,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final ConfirmationHandler handler) {
		super(appearanceFactory, messages, handler, title, message,
				CommentMode.DISABLED);
		initialize();
	}

	public WarningDialog(final String title, final String message,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final ConfirmationHandler handler,
			final DialogMessages dialogMessages, final int dialogWidth) {
		super(appearanceFactory, messages, handler, title, message,
				CommentMode.DISABLED, dialogMessages, dialogWidth);
		initialize();
	}

	@Override
	protected TextButton createDialogButton(final String text,
			final String itemId) {
		final TextButton textButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()), text);
		textButton.setWidth(120);
		textButton.setItemId(itemId);
		return textButton;
	}

	private void initialize() {
		setButtonAlign(BoxLayoutPack.CENTER);
		final int width = getMinWidth() < WARNING_DIALOG_MIN_WIDTH
				? WARNING_DIALOG_MIN_WIDTH
				: getMinWidth();
		setWidth(width);
		setMinHeight(170);
	}

	@Override
	protected void initializeComponents() {
		final HBoxLayoutContainer container = new HBoxLayoutContainer();
		container.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		final Image image = new Image(appearanceFactory.resources().attention());
		final Label label = new Label(message);
		label.setWidth(getMinWidth() - 100 + "px");
		label.setWordWrap(true);
		final BoxLayoutData layoutData = new BoxLayoutData(new Margins(15, 10,
				20, 10));
		container.add(image, layoutData);
		container.add(label, layoutData);
		add(container);
	}

	@Override
	protected void onButtonPressed(final TextButton button) {
		if (handler != null) {
			if (button == getCancelButton()) {
				handler.onCancel();
			} else if (button == getOkButton()) {
				handler.onConfirm(null);
			}
		}
		hide();
	}
}

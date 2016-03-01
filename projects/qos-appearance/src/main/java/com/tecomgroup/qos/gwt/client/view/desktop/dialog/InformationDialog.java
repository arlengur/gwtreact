/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.tecomgroup.qos.gwt.client.i18n.CommonMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog;

/**
 * @author abondin
 * 
 */
public class InformationDialog extends BaseDialog<CommonMessages> {

	protected final String message;
	protected Label messageLabel;
	/**
	 * @param appearanceFactory
	 * @param messages
	 */
	public InformationDialog(final AppearanceFactory appearanceFactory,
			final CommonMessages messages, final String message) {
		super(appearanceFactory, messages);
		this.message = message;
	}

	@Override
	protected String getTitleText(final CommonMessages messages) {
		return messages.message();
	}

	@Override
	protected void initializeComponents() {
		messageLabel = new Label(message);
		messageLabel.addStyleName(appearanceFactory.resources().css()
				.textAlignCenter());
		messageLabel.setWordWrap(true);
		add(messageLabel, new MarginData(5));
        messageLabel.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.PRE); // for message line break handling
	}
}

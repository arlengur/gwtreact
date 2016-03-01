/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog;

import com.tecomgroup.qos.gwt.client.i18n.CommonMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * The dialog shows provided error message.
 * 
 * @author kshnyakin.m
 * 
 */
public class ErrorDialog extends InformationDialog {

	public ErrorDialog(final AppearanceFactory appearanceFactory,
			final CommonMessages messages, final String message) {
		super(appearanceFactory, messages, message);
	}

	@Override
	protected String getTitleText(final CommonMessages messages) {
		return messages.error();
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog.base;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * @author ivlev.e
 * 
 */
public abstract class QoSDialog extends BaseDialog<QoSMessages> {

	public QoSDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(appearanceFactory, messages);
	}

	public QoSDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final DialogMessages dialogMessages) {
		super(appearanceFactory, messages, dialogMessages);
	}
}

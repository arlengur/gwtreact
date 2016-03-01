/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.messages;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author meleshin.o
 * 
 */
public abstract class AbstractLocalizedMessages {
	protected QoSMessages messages;

	public AbstractLocalizedMessages(final QoSMessages messages) {
		this.messages = messages;
	}
}

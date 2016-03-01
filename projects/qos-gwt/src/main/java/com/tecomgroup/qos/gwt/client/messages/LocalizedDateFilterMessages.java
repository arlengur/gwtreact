/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.messages;

import com.sencha.gxt.widget.core.client.grid.filters.DateFilter.DateFilterMessages;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author meleshin.o
 * 
 */
public class LocalizedDateFilterMessages extends AbstractLocalizedMessages
		implements
			DateFilterMessages {

	/**
	 * @param messages
	 */
	public LocalizedDateFilterMessages(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String afterText() {
		return messages.after();
	}

	@Override
	public String beforeText() {
		return messages.before();
	}

	@Override
	public String onText() {
		return messages.on();
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.messages;

import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter.BooleanFilterMessages;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author meleshin.o
 * 
 */
public class LocalizedBooleanFilterMessages extends AbstractLocalizedMessages
		implements
			BooleanFilterMessages {

	public LocalizedBooleanFilterMessages(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String noText() {
		return messages.actionNo();
	}

	@Override
	public String yesText() {
		return messages.actionYes();
	}

}

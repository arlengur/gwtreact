/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.messages;

import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter.NumericFilterMessages;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author meleshin.o
 * 
 */
public class LocalizedNumericFilterMessages extends AbstractLocalizedMessages
		implements
			NumericFilterMessages {

	/**
	 * @param messages
	 */
	public LocalizedNumericFilterMessages(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String emptyText() {
		return messages.emptyFilterText();
	}

}

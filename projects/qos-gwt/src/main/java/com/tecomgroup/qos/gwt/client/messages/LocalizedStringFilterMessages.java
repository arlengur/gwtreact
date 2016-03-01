/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.messages;

import com.sencha.gxt.widget.core.client.grid.filters.StringFilter.StringFilterMessages;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author meleshin.o
 * 
 */
public class LocalizedStringFilterMessages extends AbstractLocalizedMessages
		implements
			StringFilterMessages {

	/**
	 * @param messages
	 */
	public LocalizedStringFilterMessages(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String emptyText() {
		return messages.emptyFilterText();
	}

}

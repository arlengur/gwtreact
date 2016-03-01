/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author kunilov.p
 * 
 */
public abstract class DisabledEntityLabelProvider<T extends Disabled>
		implements
			LabelProvider<T> {

	private final QoSMessages messages;

	public DisabledEntityLabelProvider(final QoSMessages messages) {
		this.messages = messages;
	}

	protected abstract String getDisplayName(T entity);

	@Override
	public String getLabel(final T entity) {
		String label = getDisplayName(entity);
		if (entity.isDisabled()) {
			label += " - (" + messages.disabled() + ")";
		}
		return label;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author sviyazov.a
 * 
 */
public class DisabledTaskValueProvider
		extends
			ValueProviderWithPath<MAgentTask, String> {

	private final DisabledTaskLabelProvider disabledTaskLabelProvider;

	public DisabledTaskValueProvider(final QoSMessages messages) {
		this(messages, null);

	}

	/**
	 * @param messages
	 * @param path
	 *            may be null, if it is not necessary to sort by this column
	 *            otherwise it must be provided
	 */
	public DisabledTaskValueProvider(final QoSMessages messages,
			final String path) {
		super(path);
		disabledTaskLabelProvider = new DisabledTaskLabelProvider(messages);
	}

	@Override
	public String getValue(final MAgentTask agentTask) {
		return disabledTaskLabelProvider.getLabel(agentTask);
	}
}
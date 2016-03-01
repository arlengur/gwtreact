/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author kunilov.p
 * 
 */
public class DisabledTaskLabelProvider
		extends
			DisabledEntityLabelProvider<MAgentTask> {

	public DisabledTaskLabelProvider(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String getDisplayName(final MAgentTask task) {
		return task.getDisplayName();
	}
}

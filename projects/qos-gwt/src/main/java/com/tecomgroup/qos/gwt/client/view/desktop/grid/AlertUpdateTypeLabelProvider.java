/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Map;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author kunilov.p
 * 
 */
public class AlertUpdateTypeLabelProvider implements LabelProvider<UpdateType> {

	private final Map<UpdateType, String> labels;

	public AlertUpdateTypeLabelProvider(final QoSMessages messages) {
		labels = LabelUtils.getUpdateTypeLabels(messages);
	}

	@Override
	public String getLabel(final UpdateType updateType) {
		return labels.get(updateType);
	}
}

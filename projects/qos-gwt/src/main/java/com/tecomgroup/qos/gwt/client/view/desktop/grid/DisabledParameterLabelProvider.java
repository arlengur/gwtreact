/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class DisabledParameterLabelProvider
		extends
			DisabledEntityLabelProvider<MResultParameterConfiguration> {

	public DisabledParameterLabelProvider(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String getDisplayName(final MResultParameterConfiguration parameter) {
		String displayName = parameter.getParsedDisplayFormat();

		if (SimpleUtils.isNotNullAndNotEmpty(parameter.getUnits())) {
			displayName = displayName + " (" + parameter.getUnits() + ")";
		}
		return displayName;
	}
}

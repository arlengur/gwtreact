/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author sviyazov.a
 * 
 */
public class ParameterTypeLabelProvider implements LabelProvider<ParameterType> {

	public static String getLabel(final QoSMessages messages,
			final ParameterType parameterType) {
		String label;
		switch (parameterType) {
			case LEVEL :
				label = messages.numeric();
				break;
			case PROPERTY :
				label = messages.property();
				break;
			case COUNTER :
				label = messages.counter();
				break;
			case PERCENTAGE :
				label = messages.percentage();
				break;
			case BOOL :
				label = messages.bool();
				break;
			default :
				label = parameterType.name();
		}
		return label;
	}

	private final QoSMessages messages;

	public ParameterTypeLabelProvider(final QoSMessages messages) {
		this.messages = messages;
	}

	@Override
	public String getLabel(final ParameterType parameterType) {
		return ParameterTypeLabelProvider.getLabel(messages, parameterType);
	}
}

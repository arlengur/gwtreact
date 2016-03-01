/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;

/**
 * @author kunilov.p
 * 
 */
public class ParameterModelKeyProvider
		implements
			ModelKeyProvider<MResultParameterConfiguration> {

	@Override
	public String getKey(
			final MResultParameterConfiguration parameterConfiguration) {
		return parameterConfiguration.getParameterIdentifier()
				.createParameterStorageKey();
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;

/**
 * @author kunilov.p
 * 
 */
public interface ParameterProperties
		extends
			PropertyAccess<MResultParameterConfiguration> {
	@Path("parsedDisplayFormat")
	ValueProvider<MResultParameterConfiguration, String> displayFormat();

	@Path("parsedDisplayFormat")
	LabelProvider<MResultParameterConfiguration> label();
}

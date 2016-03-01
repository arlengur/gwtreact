/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MAgent;

/**
 * @author ivlev.e
 * 
 */
public interface AgentProperties extends PropertyAccess<MAgent> {
	@Path("displayName")
	LabelProvider<MAgent> displayName();

	@Path("displayName")
	ValueProvider<MAgent, String> displayNameValue();

	@Path("id")
	ModelKeyProvider<MAgent> key();

}

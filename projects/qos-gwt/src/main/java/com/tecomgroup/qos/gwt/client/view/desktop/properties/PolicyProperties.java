/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author ivlev.e
 * 
 */
public interface PolicyProperties extends PropertyAccess<MPolicy> {

	@Path("actionsTemplate.name")
	ValueProvider<MPolicy, String> actionsTemplateName();

	@Path("conditionsTemplate.name")
	ValueProvider<MPolicy, String> conditionsTemplateName();

	@Path("displayName")
	ValueProvider<MPolicy, String> displayName();

	@Path("id")
	ModelKeyProvider<MPolicy> key();
	@Path("source.key")
	ValueProvider<MPolicy, String> source();
}

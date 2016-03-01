/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;

/**
 * @author sviyazov.a
 * 
 */
public interface PolicyConditionsTemplatesProperties
		extends
			PropertyAccess<MPolicyConditionsTemplate> {
	@Path("id")
	ModelKeyProvider<MPolicyConditionsTemplate> key();

	@Path("name")
	ValueProvider<MPolicyConditionsTemplate, String> name();

	@Path("name")
	LabelProvider<MPolicyConditionsTemplate> nameLabel();
}

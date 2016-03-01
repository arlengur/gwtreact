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
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;

/**
 * @author sviyazov.a
 * 
 */
public interface PolicyActionsTemplateProperties
		extends
			PropertyAccess<MPolicyActionsTemplate> {
	@Path("id")
	ModelKeyProvider<MPolicyActionsTemplate> key();

	@Path("name")
	ValueProvider<MPolicyActionsTemplate, String> name();

	@Path("name")
	LabelProvider<MPolicyActionsTemplate> nameLabel();
}

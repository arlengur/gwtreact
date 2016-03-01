/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;

/**
 * @author ivlev.e
 * 
 */
public interface UserTemplateProperties
		extends
			PropertyAccess<MUserAbstractTemplate> {

	@Path("id")
	ModelKeyProvider<MUserAbstractTemplate> key();

	LabelProvider<MUserAbstractTemplate> name();
}

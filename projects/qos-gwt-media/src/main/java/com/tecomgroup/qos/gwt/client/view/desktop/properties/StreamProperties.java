/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author ivlev.e
 * 
 */
public interface StreamProperties
		extends
			PropertyAccess<StreamClientWrapper<?>> {

	@Path("wrapper.stream.displayName")
	ValueProvider<StreamClientWrapper<?>, String> displayName();

}

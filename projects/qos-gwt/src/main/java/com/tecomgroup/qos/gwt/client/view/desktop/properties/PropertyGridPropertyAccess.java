/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter.Property;

/**
 * @author novohatskiy.r
 * 
 */
public interface PropertyGridPropertyAccess extends PropertyAccess<Property> {

	ModelKeyProvider<Property> key();

	@Path("key")
	ValueProvider<Property, String> name();

	ValueProvider<Property, Object> value();

}

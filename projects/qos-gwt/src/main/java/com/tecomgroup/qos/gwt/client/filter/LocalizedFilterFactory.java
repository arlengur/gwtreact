/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.filter;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.EnumerationPropertyEditor;

/**
 * @author meleshin.o
 * 
 */
public interface LocalizedFilterFactory extends FilterFactory {
	<M, V extends Enum<?>> ListFilter<M, V> createEnumListFilter(
			ValueProvider<M, V> valueProvider,
			EnumerationPropertyEditor<V> propertyEditor);
}

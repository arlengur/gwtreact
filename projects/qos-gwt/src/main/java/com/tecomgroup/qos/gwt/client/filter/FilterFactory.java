/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.filter;

import java.util.Date;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.DateFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;

/**
 * @author kunilov.p
 * 
 */
public interface FilterFactory {

	<M> BooleanFilter<M> createBooleanFilter(
			ValueProvider<M, Boolean> valueProvider);

	<M> DateFilter<M> createDateFilter(ValueProvider<M, Date> valueProvider);

	<M, V> ListFilter<M, V> createListFilter(ValueProvider<M, V> valueProvider,
			ListStore<V> store);

	<M, V extends Number> NumericFilter<M, V> createNumericFilter(
			ValueProvider<M, V> valueProvider,
			NumberPropertyEditor<V> propertyEditor);

	<M> StringFilter<M> createStringFilter(
			ValueProvider<M, String> valueProvider);
}

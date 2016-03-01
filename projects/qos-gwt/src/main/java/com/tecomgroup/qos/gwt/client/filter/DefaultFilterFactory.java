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
import com.tecomgroup.qos.gwt.client.view.desktop.widget.SafeDateFilterHandler;

/**
 * @author kunilov.p
 * 
 */
public class DefaultFilterFactory implements FilterFactory {
	private final SafeDateFilterHandler safeDateFilterHandler = new SafeDateFilterHandler();

	@Override
	public <M> BooleanFilter<M> createBooleanFilter(
			final ValueProvider<M, Boolean> valueProvider) {
		return new BooleanFilter<M>(valueProvider);
	}

	@Override
	public <M> DateFilter<M> createDateFilter(
			final ValueProvider<M, Date> valueProvider) {
		final DateFilter<M> filter = new DateFilter<M>(valueProvider);
		filter.setHandler(safeDateFilterHandler);
		return filter;
	}

	@Override
	public <M, V> ListFilter<M, V> createListFilter(
			final ValueProvider<M, V> valueProvider, final ListStore<V> store) {
		return new ListFilter<M, V>(valueProvider, store);
	}

	@Override
	public <M, V extends Number> NumericFilter<M, V> createNumericFilter(
			final ValueProvider<M, V> valueProvider,
			final NumberPropertyEditor<V> propertyEditor) {
		return new NumericFilter<M, V>(valueProvider, propertyEditor);
	}

	@Override
	public <M> StringFilter<M> createStringFilter(
			final ValueProvider<M, String> valueProvider) {
		return new StringFilter<M>(valueProvider);
	}

}

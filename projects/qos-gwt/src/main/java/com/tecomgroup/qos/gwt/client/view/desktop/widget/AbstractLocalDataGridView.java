/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Collection;
import java.util.List;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;

/**
 * Grid with local (client-side) filtering and ordering.
 * 
 * @author sviyazov.a
 * 
 */
public abstract class AbstractLocalDataGridView<M, U extends AbstractGridWidgetPresenter<M, ?>>
		extends
			AbstractDataGridView<M, U> {

	@Inject
	public AbstractLocalDataGridView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
	}

	@Override
	protected void addFilters(final List<Filter<M, ?>> filters) {
		for (final Filter<M, ?> filter : filters) {
			this.filters.addFilter(filter);
		}
	}

	@Override
	protected GridFilters<M> createGridFilters() {
		return new GridFilters<M>();
	}

	@Override
	@Inject
	public void initialize() {
		super.initialize();
		filters.setLocal(true);
	}

	public void loadData(final Collection<M> data) {
		clearFilters(false);

		setStoreData(data);
	}

	protected void setStoreData(final Collection<M> data) {
		store.clear();
		store.addAll(data);
	}
}

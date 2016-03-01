/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.tecomgroup.qos.gwt.client.event.GridGroupSelectedEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupSelectedEvent.GridGroupSelectedEventHandler;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author ivlev.e
 * 
 */
public class GroupingGrid<M> extends Grid<M>
		implements
			GridGroupSelectedEventHandler<M> {

	public GroupingGrid(final ListStore<M> store, final ColumnModel<M> cm,
			final GridView<M> view) {
		super(store, cm, view);
		AppUtils.getEventBus().addHandler(GridGroupSelectedEvent.TYPE, this);
	}

	@Override
	public void onGroupSelected(final GridGroupSelectedEvent<M> event) {

	}

}

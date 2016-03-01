/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.grid.GridView;

/**
 * Class works faster on inserting big amount of data. It doesn't refresh grid
 * view every time when {@link FastButtonedGrouingView#onAdd(List, int)} is
 * called. It needs to invoke {@link GridView#refresh(boolean)} after the
 * insertion of data in {@link Store}
 * 
 * @author ivlev.e
 */
public class FastButtonedGrouingView<M> extends ButtonedGroupingView<M> {

	public FastButtonedGrouingView() {
		super();
	}

	public FastButtonedGrouingView(final GridAppearance appearance,
			final GroupingViewAppearance groupingAppearance) {
		super(appearance, groupingAppearance);
	}

	public FastButtonedGrouingView(final GroupingViewAppearance groupAppearance) {
		super(groupAppearance);
	}

	@Override
	protected void onAdd(final List<M> models, final int index) {
		if (enableGrouping) {
			final Point ss = getScrollState();
			restoreScroll(ss);
		} else {
			super.onAdd(models, index);
		}
	}

}

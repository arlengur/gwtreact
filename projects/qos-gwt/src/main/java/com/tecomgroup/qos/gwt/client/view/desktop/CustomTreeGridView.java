/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;
import com.tecomgroup.qos.gwt.client.style.common.grid.FilteredColumnHeaderAppearance;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;

/**
 * @author ivlev.e
 * 
 */
public class CustomTreeGridView<M> extends TreeGridView<M> {

	private final FilteredColumnHeaderAppearance columnHeaderAppearance;

	public CustomTreeGridView(
			final FilteredColumnHeaderAppearance columnHeaderAppearance,
			boolean preventScrollToTopOnRefresh) {
		this(columnHeaderAppearance);
		setPreventScrollToTopOnRefresh(preventScrollToTopOnRefresh);
	}
	public CustomTreeGridView(
			final FilteredColumnHeaderAppearance columnHeaderAppearance) {
		super();
		this.columnHeaderAppearance = columnHeaderAppearance;
	}
	
	public void setPreventScrollToTopOnRefresh(boolean preventScrollToTopOnRefresh) {
		super.preventScrollToTopOnRefresh = preventScrollToTopOnRefresh;
	}

	@Override
	protected void initColumnHeader() {
		header = new ColumnHeader<M>(grid, cm, columnHeaderAppearance) {

			@Override
			protected Menu getContextMenu(final int column) {
				return createContextMenu(column);
			}

			@Override
			protected void onColumnSplitterMoved(final int colIndex,
					final int width) {
				super.onColumnSplitterMoved(colIndex, width);
				CustomTreeGridView.this.onColumnSplitterMoved(colIndex, width);
			}

			@Override
			protected void onHeaderClick(final Event ce, final int column) {
				super.onHeaderClick(ce, column);
				CustomTreeGridView.this.onHeaderClick(column);
			}

			@Override
			protected void onKeyDown(final Event ce, final int index) {
				ce.stopPropagation();
				// auto select on key down
				if (grid.getSelectionModel() instanceof CellSelectionModel<?>) {
					final CellSelectionModel<?> csm = (CellSelectionModel<?>) grid
							.getSelectionModel();
					csm.selectCell(0, index);
				} else {
					grid.getSelectionModel().select(0, false);
				}
			}

		};
		header.addStyleName(ClientConstants.QOS_GRID_HEADER_STYLE);
		header.setSplitterWidth(splitterWidth);
		header.setMinColumnWidth(grid.getMinColumnWidth());
	}

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.Head;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.tecomgroup.qos.gwt.client.style.common.grid.FilteredColumnHeaderAppearance;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;

/**
 * @author ivlev.e
 * 
 */
public class CustomGridView<M> extends GridView<M> {

	protected FilteredColumnHeaderAppearance columnHeaderAppearance;

	protected GridFilters<M> filters;

	public CustomGridView(final GridAppearance gridAppearance,
			final FilteredColumnHeaderAppearance columnHeaderAppearance) {
		super(gridAppearance);
		this.columnHeaderAppearance = columnHeaderAppearance;
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
				CustomGridView.this.onColumnSplitterMoved(colIndex, width);
			}

			@Override
			protected void onHeaderClick(final Event ce, final int column) {
				super.onHeaderClick(ce, column);
				CustomGridView.this.onHeaderClick(column);
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

	@Override
	public void refresh(final boolean headerToo) {
		refreshFilterIcons();
		super.refresh(headerToo);
	}

	@SuppressWarnings("rawtypes")
	protected void refreshFilterIcons() {
		if (grid != null && filters != null) {
			for (int index = 0; index < grid.getColumnModel().getColumnCount(); index++) {
				final ColumnConfig<M, ?> config = grid.getColumnModel()
						.getColumn(index);
				final String path = config.getPath();
				final Filter<M, ?> filter = filters.getFilter(path);
				if (filter != null) {
					final Head head = header.getHead(index);
					if (head != null) {
						final String filterStyle = columnHeaderAppearance
								.getStyle().filterIcon();
						if (filter.isActive()) {
							head.addStyleName(filterStyle);
						} else {
							head.removeStyleName(filterStyle);
						}
					}
				}
			}
		}
	}

	@Override
	protected void renderUI() {
		refreshFilterIcons();
		super.renderUI();
	}

	/**
	 * Я не нашёл способа добраться до фильта в классичесом GridView
	 * 
	 * @author abondin
	 * @param filters
	 *            the filters to set
	 */
	// FIXME Find a way to get filters through standart GridView api
	public void setFilters(final GridFilters<M> filters) {
		this.filters = filters;
	}

}

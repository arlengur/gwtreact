/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard;

import com.tecomgroup.qos.dashboard.DashboardWidget;

/**
 *
 * @author ivlev.e
 */
public class DashboardGridCell {

	private final int colIndex;

	private final int rowIndex;

	private final int rowspan;

	private final int colspan;

	private final DashboardWidget widget;

	public DashboardGridCell(final int column, final int row,
			final DashboardWidget widget) {
		this(column, row, 1, 1, widget);
	}

	public DashboardGridCell(final int column, final int row,
			final int rowspan, final int colspan, final DashboardWidget widget) {
		this.rowspan = rowspan;
		this.colspan = colspan;
		this.colIndex = column;
		this.rowIndex = row;
		this.widget = widget;
	}

	public int getColIndex() {
		return colIndex;
	}

	public int getColspan() {
		return colspan;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getRowspan() {
		return rowspan;
	}

	public DashboardWidget getWidget() {
		return widget;
	}
}
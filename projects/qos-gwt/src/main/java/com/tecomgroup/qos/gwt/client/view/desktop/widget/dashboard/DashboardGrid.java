/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.DashboardLogicalGrid.CellIndex;

/**
 * A physical two-dimensional grid which has constraints by number of cells. The
 * grid has {@link Widget} in its cells. Widget can consume several cells with
 * constraint that shape of such widget is rectangle. The grid supports paging
 * model. Each page may have fixed number of cells. Number of widgets on
 * specified page depends on size of widget. The grid builds list of
 * {@link DashboardLogicalGrid}. Each item in this list represents page in
 * paging model. The grid has ability to build physical grid by pageNumber.
 *
 * @author ivlev.e
 */
public class DashboardGrid extends FlexTable {
	private final static int CELL_WIDTH_IN_PERCENT = 33;
	private final static int CELL_HEIGHT_IN_PERCENT = 50;

	private List<DashboardLogicalGrid> pages;

	public DashboardGrid() {
		super();
	}

	private boolean addLogicalWidget(final DashboardWidget widget,
			final int colspan, final int rowspan,
			final DashboardLogicalGrid model) {
		boolean result = false;
		outerloop : for (int rowIndex = 0; rowIndex < model.rowSize(); rowIndex++) {
			for (int colIndex = 0; colIndex < model.columnSize(); colIndex++) {
				if (model.isWidgetFit(rowIndex, rowspan, colIndex, colspan)) {
					model.insert(new DashboardGridCell(colIndex, rowIndex,
							rowspan, colspan, widget));
					result = true;
					break outerloop;
				}
			}
		}
		return result;
	}

	public void createLogicalStructure(final List<DashboardWidget> widgets,
			final int rowNumber, final int colNumber) {
		pages = new ArrayList<DashboardLogicalGrid>();
		final List<DashboardWidget> widgetsToAdd = new LinkedList<DashboardWidget>(
				widgets);
		DashboardLogicalGrid currentPage = createNewPage(rowNumber, colNumber);
		while (!widgetsToAdd.isEmpty()) {
			DashboardWidget addedWidget = null;
			for (final DashboardWidget widget : widgetsToAdd) {
				if (addLogicalWidget(widget, widget.getColspan(),
						widget.getRowspan(), currentPage)) {
					addedWidget = widget;
					break;
				}
			}

			if (addedWidget != null) {
				widgetsToAdd.remove(addedWidget);
			} else {
				currentPage = createNewPage(rowNumber, colNumber);
			}
		}
	}

	private DashboardLogicalGrid createNewPage(final int rowNumber,
			final int colNumber) {
		final DashboardLogicalGrid model = new DashboardLogicalGrid(rowNumber,
				colNumber);
		pages.add(model);
		return model;
	}

	public List<CellIndex> getAvailableCellsIndexes(final int pageNumber) {
		return pages.get(pageNumber).getAvailableCellIndexes();
	}

	public int getPageCount() {
		return pages != null ? pages.size() : 0;
	}

	public List<DashboardGridCell> getPageWidgets(final int pageNumber) {
		final DashboardLogicalGrid model = pages.get(pageNumber);
		return model != null ? model.currentTiles : null;
	}

	public boolean hasAvailableCells(final int pageNumber) {
		return pages.get(pageNumber).hasAvailableCell();
	}

	public void setCellSize(final int row, final int column, final int rowspan,
			final int colspan) {
		final Element tableCell = getFlexCellFormatter()
				.getElement(row, column);
		tableCell.getStyle()
				.setWidth(CELL_WIDTH_IN_PERCENT * colspan, Unit.PCT);
		tableCell.getStyle().setHeight(CELL_HEIGHT_IN_PERCENT * rowspan,
				Unit.PCT);

	}

	public void setTile(final DashboardGridCell cell, final Tile tile) {
		setWidget(cell.getRowIndex(), cell.getColIndex(), tile);
		setCellSize(cell.getRowIndex(), cell.getColIndex(), cell.getRowspan(),
				cell.getColspan());
		getFlexCellFormatter().setColSpan(cell.getRowIndex(),
				cell.getColIndex(), cell.getColspan());
		getFlexCellFormatter().setRowSpan(cell.getRowIndex(),
				cell.getColIndex(), cell.getRowspan());
		getFlexCellFormatter().setAlignment(cell.getRowIndex(),
				cell.getColIndex(), HasHorizontalAlignment.ALIGN_CENTER,
				HasVerticalAlignment.ALIGN_MIDDLE);
	}

}

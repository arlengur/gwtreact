/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ivlev.e
 */
public class DashboardLogicalGrid {
	public static class CellIndex {
		private final int row;
		private final int column;

		public CellIndex(final int row, final int column) {
			this.row = row;
			this.column = column;
		}

		public int getColumn() {
			return column;
		}

		public int getRow() {
			return row;
		}
	}

	final List<DashboardGridCell> currentTiles;

	private final boolean[][] availableCells;

	public DashboardLogicalGrid(final int rowNumber, final int columnNumber) {
		currentTiles = new ArrayList<DashboardGridCell>();
		availableCells = new boolean[rowNumber][columnNumber];
		for (int i = 0; i < availableCells.length; i++) {
			Arrays.fill(availableCells[i], true);
		}
	}

	public int columnSize() {
		return availableCells[0].length;
	}

	public List<CellIndex> getAvailableCellIndexes() {
		final List<CellIndex> list = new LinkedList<DashboardLogicalGrid.CellIndex>();
		for (int i = 0; i < availableCells.length; i++) {
			for (int j = 0; j < availableCells[i].length; j++) {
				if (availableCells[i][j]) {
					list.add(new CellIndex(i, j));
				}
			}
		}
		return list;

	}

	public boolean hasAvailableCell() {
		boolean result = false;
		outer : for (int i = 0; i < availableCells.length; i++) {
			for (int j = 0; j < availableCells[i].length; j++) {
				if (availableCells[i][j]) {
					result = true;
					break outer;
				}
			}
		}
		return result;
	}

	public void insert(final DashboardGridCell cell) {
		currentTiles.add(cell);
		markUnavailableCells(cell);
	}

	public boolean isWidgetFit(final int rowIndex, final int rowspan,
			final int colIndex, final int colspan) {
		boolean result = (rowIndex + rowspan) <= rowSize()
				&& (colIndex + colspan) <= columnSize();
		if (result) {
			final int rowEnd = rowIndex + rowspan;
			final int colEnd = colIndex + colspan;
			outer : for (int i = rowIndex; i < rowEnd; i++) {
				for (int j = colIndex; j < colEnd; j++) {
					if (!availableCells[i][j]) {
						result = false;
						break outer;
					}
				}
			}
		}
		return result;
	}

	private void markUnavailableCells(final DashboardGridCell cell) {
		final int rowEnd = cell.getRowIndex() + cell.getRowspan();
		final int colEnd = cell.getColIndex() + cell.getColspan();
		for (int i = cell.getRowIndex(); i < rowEnd; i++) {
			for (int j = cell.getColIndex(); j < colEnd; j++) {
				availableCells[i][j] = false;
			}
		}
	}

	public int rowSize() {
		return availableCells.length;
	}

}
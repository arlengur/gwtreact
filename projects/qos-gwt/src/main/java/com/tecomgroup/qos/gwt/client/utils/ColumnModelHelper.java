/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.tecomgroup.qos.gwt.client.ActionListener;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;

/**
 * @author ivlev.e
 * 
 */
public class ColumnModelHelper {

	public static <M, N> ColumnConfig<M, N> createIconedColumn(
			final String columnDisplayName, final ImageResource icon,
			final ActionListener<N> callback,
			final ValueProvider<M, N> valueProvider) {

		final IconedActionCell<N> deletionActionCell = new IconedActionCell<N>(
				AppearanceFactoryProvider
						.instance()
						.<N> iconedActionCellAppearance(icon, columnDisplayName),
				columnDisplayName, new ActionCell.Delegate<N>() {

					@Override
					public void execute(final N treeGridRow) {
						callback.onActionPerformed(treeGridRow);
					}
				});
		final ColumnConfig<M, N> deletionActionColumn = new ColumnConfig<M, N>(
				valueProvider, 5);
		deletionActionColumn.setCell(deletionActionCell);

		return deletionActionColumn;
	}
}

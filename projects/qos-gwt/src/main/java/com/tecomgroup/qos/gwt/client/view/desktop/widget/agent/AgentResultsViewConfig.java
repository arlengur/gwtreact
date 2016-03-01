/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.tecomgroup.qos.gwt.client.model.results.ParameterRow;
import com.tecomgroup.qos.gwt.client.model.results.ResultRow;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;

/**
 * @author ivlev.e
 * 
 */
public class AgentResultsViewConfig implements GridViewConfig<ResultRow> {

	@Override
	public String getColStyle(final ResultRow modelRow,
			final ValueProvider<? super ResultRow, ?> valueProvider,
			final int rowIndex, final int colIndex) {
		String colStyle = "";
		if (TreeGridFields.VALUE.toString().equals(valueProvider.getPath())) {
			if (modelRow instanceof ParameterRow) {
				final ParameterRow parameterRow = ((ParameterRow) modelRow);

				colStyle = ChartResultUtils.applyThresholdToValue(
						parameterRow.getThreshold(), parameterRow.getValue());
			}
		}
		return colStyle;
	}

	@Override
	public String getRowStyle(final ResultRow modelRow, final int rowIndex) {
		return "";
	}
}

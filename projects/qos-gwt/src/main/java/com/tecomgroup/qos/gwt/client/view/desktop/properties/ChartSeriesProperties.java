/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MChartSeries;

public interface ChartSeriesProperties
		extends
			PropertyAccess<MChartSeries> {
	@Path("agent.displayName")
	ValueProvider<MChartSeries, String> agentName();

	ValueProvider<MChartSeries, String> chartName();

	@Path("parameter.parsedDisplayFormat")
	ValueProvider<MChartSeries, String> paramDisplayName();

	@Path("task.key")
	ValueProvider<MChartSeries, String> taskKey();
}
/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance;

/**
 * @author abondin
 * 
 */
public class DarkAlertsGridAppearance extends DarkGridStandardAppearance
		implements
			AlertsGridAppearance {
	public interface DarkAlertsGridResources
			extends
				GridResources,
				AlertsGridResources {

		@Source({
				"DarkGridStandard.css",
				"com/tecomgroup/qos/gwt/client/style/theme/dark/grid/DarkAlertSeverityStyle.css",
				"DarkAlertsGrid.css"})
		@Override
		DarkAlertsGridStandardStyle css();
	}

	public static interface DarkAlertsGridStandardStyle
			extends
				GridStyle,
				AlertsGridStyle {
	}

	public DarkAlertsGridAppearance() {
		this(
				GWT.<DarkAlertsGridResources> create(DarkAlertsGridResources.class));
	}

	public DarkAlertsGridAppearance(final GridResources resources) {
		super(resources);
	}

	@Override
	public AlertsGridResources getResources() {
		return (AlertsGridResources) resources;
	}
}

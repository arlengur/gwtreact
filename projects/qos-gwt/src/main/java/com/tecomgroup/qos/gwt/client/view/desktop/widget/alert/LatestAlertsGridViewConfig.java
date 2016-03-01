/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance.AlertsGridResources;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * @author ivlev.e
 * 
 */
public class LatestAlertsGridViewConfig extends SeverityGridViewConfig<MAlert> {

	public LatestAlertsGridViewConfig(final AlertsGridResources resources,
			final AppearanceFactory appearanceFactory) {
		super(resources, appearanceFactory);
	}

	@Override
	public PerceivedSeverity getPerceivedSeverity(final MAlert alert) {
		return alert.getPerceivedSeverity();
	}

	@Override
	public String getRowStyle(final MAlert model, final int rowIndex) {
		String rowStyle = super.getRowStyle(model, rowIndex);
		rowStyle += " " + appearanceFactory.resources().css().cursorPointer();
		return rowStyle;
	}

}

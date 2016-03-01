/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance.AlertsGridResources;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertProperties;

/**
 * @author abondin
 * 
 */
public class AlertsGridViewConfig extends SeverityGridViewConfig<MAlert> {

	protected AlertProperties alertProperties;

	public AlertsGridViewConfig(final AlertsGridResources resources,
			final AppearanceFactory appearanceFactory,
			final AlertProperties alertProperties) {
		super(resources, appearanceFactory);
		this.alertProperties = alertProperties;
	}

	@Override
	public PerceivedSeverity getPerceivedSeverity(final MAlert alert) {
		return alert.getPerceivedSeverity();
	}

	@Override
	public String getRowStyle(final MAlert alert, final int rowIndex) {
		String rowStyle = super.getRowStyle(alert, rowIndex);
		if (alert.getStatus() == Status.CLEARED) {
			rowStyle += " " + style.alertCleared();
		}
		return rowStyle;
	}

}

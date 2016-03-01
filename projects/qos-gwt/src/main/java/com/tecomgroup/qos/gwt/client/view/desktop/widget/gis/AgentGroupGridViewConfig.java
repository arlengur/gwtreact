/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.gis;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance.AlertsGridResources;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.SeverityGridViewConfig;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGroupSelectionDialog.AgentAnchorWrapper;

/**
 * @author kshnyakin.m
 * 
 */
public class AgentGroupGridViewConfig
		extends
			SeverityGridViewConfig<AgentAnchorWrapper> {

	/**
	 * @param resources
	 * @param appearanceFactory
	 */
	public AgentGroupGridViewConfig(final AlertsGridResources resources,
			final AppearanceFactory appearanceFactory) {
		super(resources, appearanceFactory);
	}

	@Override
	public String getColStyle(final AgentAnchorWrapper agent,
			final ValueProvider<? super AgentAnchorWrapper, ?> valueProvider,
			final int rowIndex, final int colIndex) {
		final String colStyle = appearanceFactory.resources().css()
				.gridNavigation();
		return colStyle;
	}

	@Override
	public PerceivedSeverity getPerceivedSeverity(final AgentAnchorWrapper model) {
		return model.getStatus();
	}

}

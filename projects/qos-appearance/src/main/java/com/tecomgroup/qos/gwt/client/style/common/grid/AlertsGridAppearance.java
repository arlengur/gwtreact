/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.grid;

import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;

/**
 * @author abondin
 * 
 */
public interface AlertsGridAppearance extends GridAppearance {
	public static interface AlertsGridResources {
		AlertsGridStyle css();
	}
	public static interface AlertsGridStyle extends AlertSeverityStyle {
		String alertCleared();
		String alertrHighlightedColumn();
	}

	AlertsGridResources getResources();
}

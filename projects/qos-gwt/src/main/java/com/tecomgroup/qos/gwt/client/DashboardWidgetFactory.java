/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.TileContentElement;

/**
 * @author ivlev.e
 * 
 */
public interface DashboardWidgetFactory {

	TileContentElement createWidget(DashboardWidget model);
}

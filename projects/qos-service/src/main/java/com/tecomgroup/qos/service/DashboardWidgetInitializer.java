/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;

/**
 * 
 * Interface to initialize dashboard widget
 * 
 * @author abondin
 * 
 */
public interface DashboardWidgetInitializer {

	/**
	 * Load data for the widger
	 * 
	 * @param widget
	 * @return
	 */
	<M extends WidgetData> List<M> loadData(HasUpdatableData<M> widget);

	/**
	 * Load all data from database to the widget before send it to client
	 * 
	 * @param widget
	 */
	void setupWidget(final DashboardWidget widget) throws Exception;
}

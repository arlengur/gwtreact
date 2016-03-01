/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard;

import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.Destroyable;
import com.tecomgroup.qos.Disposable;
import com.tecomgroup.qos.Initializable;
import com.tecomgroup.qos.Refreshable;
import com.tecomgroup.qos.dashboard.DashboardWidget;

/**
 * @author ivlev.e
 *
 */
public interface TileContentElement
		extends
			Initializable,
			Disposable,
			Destroyable,
			Refreshable {

	Widget getContentElement();

	DashboardWidget getModel();
}

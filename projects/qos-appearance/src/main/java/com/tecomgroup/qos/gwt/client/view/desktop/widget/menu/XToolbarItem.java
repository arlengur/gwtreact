/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.menu;

import com.sencha.gxt.widget.core.client.Window;

/**
 * @author ivlev.e
 * 
 */
public interface XToolbarItem {

	String getMenuTitle();

	String getPlaceToken();

	Window getPopupWindow();
}

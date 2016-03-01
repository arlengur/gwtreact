/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.gis;

import com.tecomgroup.qos.domain.GISPosition;

/**
 * Contains general map information.
 * 
 * @author kunilov.p
 * 
 */
public interface MapInfo {

	/**
	 * @return current center of the map.
	 */
	GISPosition getCenter();

	/**
	 * @return current zoom of the map.
	 */
	int getZoom();
}

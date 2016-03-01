/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ivlev.e
 * 
 */
public interface HasIcon {

	/**
	 * URI to default image if for example icon not found. Can be null
	 * 
	 * @return
	 */
	SafeUri getDefaultImageUri();

	Image getIcon();
}

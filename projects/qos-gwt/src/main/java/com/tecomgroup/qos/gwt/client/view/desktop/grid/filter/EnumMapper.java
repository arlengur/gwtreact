/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.filter;

/**
 * @author ivlev.e
 * 
 */
public interface EnumMapper {
	Object tryConvertToEnum(String unqualifiedFiledName, String value);
}

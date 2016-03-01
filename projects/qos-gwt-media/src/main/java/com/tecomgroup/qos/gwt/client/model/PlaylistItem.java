/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model;

/**
 * Single row for download video dialog grid.
 * 
 * @author meleshin.o
 */
public interface PlaylistItem {
	Long getEndTime();

	String getName();

	Long getStartTime();
}

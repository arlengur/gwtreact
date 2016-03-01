/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model;

import java.io.Serializable;

import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

/**
 * Общая модель данных для {@link TreeGrid}
 * 
 * @author meleshin.o
 */
public interface TreeGridRow extends Serializable {
	String getKey();

	String getName();
}

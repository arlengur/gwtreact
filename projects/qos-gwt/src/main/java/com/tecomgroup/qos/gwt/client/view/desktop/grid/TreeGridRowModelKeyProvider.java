/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

/**
 * @author ivlev.e
 * 
 */
public class TreeGridRowModelKeyProvider
		implements
			ModelKeyProvider<TreeGridRow> {

	@Override
	public String getKey(final TreeGridRow row) {
		return row.getKey();
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author ivlev.e
 * 
 */
public class StringModelKeyProvider implements ModelKeyProvider<String> {

	@Override
	public String getKey(final String item) {
		return item;
	}

}

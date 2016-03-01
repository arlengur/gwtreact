/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Map;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author ivlev.e
 * 
 */
public class DynamicModelKeyProvider
		implements
			ModelKeyProvider<Map<String, Object>> {

	private final String keyName;

	public DynamicModelKeyProvider(final String keyName) {
		this.keyName = keyName;
	}

	@Override
	public String getKey(final Map<String, Object> item) {
		return item.get(keyName).toString();
	}
}

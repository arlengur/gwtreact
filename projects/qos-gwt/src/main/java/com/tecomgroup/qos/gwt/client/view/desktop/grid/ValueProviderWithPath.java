/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author kunilov.p
 * 
 */
public abstract class ValueProviderWithPath<T, V>
		implements
			ValueProvider<T, V> {

	private String path = "";

	public ValueProviderWithPath() {
		super();
	}

	public ValueProviderWithPath(final String path) {
		this();
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public void setValue(final T object, final V value) {
		// do nothing
	}
}

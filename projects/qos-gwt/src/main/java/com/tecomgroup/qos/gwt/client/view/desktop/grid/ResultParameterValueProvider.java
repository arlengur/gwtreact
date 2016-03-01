/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Map;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author ivlev.e
 * 
 */
public class ResultParameterValueProvider<T>
		implements
			ValueProvider<Map<String, Object>, T> {

	private final String paramName;

	public ResultParameterValueProvider(final String paramName) {
		this.paramName = paramName;
	}

	@Override
	public String getPath() {
		return paramName;
	}

	@Override
	public T getValue(final Map<String, Object> object) {
		return (T) object.get(paramName);
	}

	@Override
	public void setValue(final Map<String, Object> object, final T value) {
		object.put(paramName, value);
	}
}

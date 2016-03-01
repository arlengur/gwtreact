/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Date;

/**
 * @author meleshin.o
 * 
 */
public abstract class DateValueProvider<T>
		extends
			ValueProviderWithPath<T, Date> {

	public DateValueProvider(final String path) {
		super(path);
	}

	protected abstract Long getTimestamp(T object);

	@Override
	public Date getValue(final T object) {
		Date result = null;
		final Long dateTimestamp = getTimestamp(object);
		if (dateTimestamp != null) {
			result = new Date(dateTimestamp);
		}

		return result;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author abondin
 * 
 */
public class SimpleValueContainer<T> implements HasValue<T> {
	T value;

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<T> handler) {
		return null;
	}

	@Override
	public void fireEvent(final GwtEvent<?> event) {
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(final T value) {
		this.value = value;
	}

	@Override
	public void setValue(final T value, final boolean fireEvents) {
		setValue(value);
	}

}

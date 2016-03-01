/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Map;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Creates value providers based on storageKey as provider model key.
 * 
 * @author ivlev.e
 * @author abondin
 * 
 */

public abstract class StorageKeyValueProviderFactory {

	private static abstract class StorageKeyValueProvider<M>
			implements
				ValueProvider<Map<String, Object>, M> {
		protected final String storageKey;

		public StorageKeyValueProvider(final String storageKey) {
			this.storageKey = storageKey;
		}

		@Override
		public String getPath() {
			return storageKey;
		}
		@Override
		public void setValue(final Map<String, Object> object, final M value) {
		}
	}

	/**
	 * Double value as boolean
	 * 
	 * @param storageKey
	 * @return
	 */
	public static ValueProvider<Map<String, Object>, Boolean> getDoubleAsBooleanValueProvider(
			final String storageKey) {
		return new StorageKeyValueProvider<Boolean>(storageKey) {
			@Override
			public Boolean getValue(final Map<String, Object> object) {
				final Object value = object.get(storageKey);
				return SimpleUtils.doubleAsBoolean((Double) value);
			}
		};
	}

	/**
	 * Value as double
	 * 
	 * @param storageKey
	 * @return
	 */
	public static ValueProvider<Map<String, Object>, Double> getDoubleValueProvider(
			final String storageKey) {
		return new StorageKeyValueProvider<Double>(storageKey) {
			@Override
			public Double getValue(final Map<String, Object> object) {
				return (Double) object.get(storageKey);
			}
		};
	}
}
/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author abondin
 * 
 */
public abstract class EnumerationPropertyEditor<T extends Enum<?>>
		extends
			PropertyEditor<T> {

	private final Map<T, String> labels = new HashMap<T, String>();

	private final ListStore<T> store;

	protected final QoSMessages messages;
	public EnumerationPropertyEditor(final QoSMessages messages) {
		this.messages = messages;
		populateLabels(labels);
		store = new ListStore<T>(new ModelKeyProvider<T>() {
			@Override
			public String getKey(final T item) {
				return item.toString();
			}
		});
		for (final T item : getAllEnumerationValues()) {
			store.add(item);
			if (!labels.containsKey(item)) {
				labels.put(item, item.toString());
			}
		}
	}

	/**
	 * @param store
	 */
	protected abstract Collection<T> getAllEnumerationValues();

	public String getLabel(final T key) {
		return labels.get(key);
	}

	/**
	 * @return the store
	 */
	public ListStore<T> getStore() {
		return store;
	}

	@Override
	public T parse(final CharSequence text) throws ParseException {
		for (final T item : labels.keySet()) {
			if (labels.get(item).equals(text)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * @param labels
	 */
	protected abstract void populateLabels(Map<T, String> labels);

	@Override
	public String render(final T object) {
		if (object == null) {
			return null;
		}
		final String label = labels.get(object);
		return label == null ? object.toString() : label;
	}
}

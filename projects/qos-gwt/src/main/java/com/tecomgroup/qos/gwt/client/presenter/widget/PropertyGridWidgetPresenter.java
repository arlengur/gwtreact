/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import java.util.List;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter.MyView;

/**
 * Represents a list of [String key = Object value] pairs, wrapped by
 * {@link Property} objects.
 * 
 * @see Property
 * 
 * @author novohatskiy.r
 * 
 */
public class PropertyGridWidgetPresenter extends PresenterWidget<MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				View,
				HasUiHandlers<PropertyGridWidgetPresenter> {
		void addProperty(Property property, int index);

		void setProperties(List<Property> properties);
	}

	/**
	 * Property has String key and Object value
	 */
	public static class Property {
		private String key;
		private Object value;

		public Property(final String key, final Object value) {
			this.key = key;
			this.value = value;
		}
		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}
		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
		/**
		 * @param key
		 *            the key to set
		 */
		public void setKey(final String key) {
			this.key = key;
		}
		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final Object value) {
			this.value = value;
		}

	}

	@Inject
	public PropertyGridWidgetPresenter(final EventBus eventBus,
			final MyView view) {
		super(eventBus, view);
		view.setUiHandlers(this);
	}

	public void addProperty(final Property property, final int index) {
		getView().addProperty(property, index);
	}

	public void setProperties(final List<Property> properties) {
		getView().setProperties(properties);
	}

}

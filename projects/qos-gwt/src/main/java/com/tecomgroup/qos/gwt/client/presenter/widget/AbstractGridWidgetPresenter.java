/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import java.util.Map;
import java.util.Set;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

/**
 * @author sviyazov.a
 * 
 */
public abstract class AbstractGridWidgetPresenter<M, V extends AbstractGridWidgetPresenter.MyView<M, ?>>
		extends
			PresenterWidget<V> implements UiHandlers, GridPresenter {

	public interface MyView<M, U extends AbstractGridWidgetPresenter<M, ?>>
			extends
				View,
				HasUiHandlers<U> {

		void addItem(M item);

		/**
		 * 
		 * @return <path, column header text> map
		 */
		Map<String, String> getColumnNames();

		String[] getHiddenColumns();

		void removeItem(M item);

		void removeItems(Set<String> keys);

		void updateItem(final M item);

	}

	public AbstractGridWidgetPresenter(final EventBus eventBus, final V view) {
		super(eventBus, view);
	}

	/**
	 * 
	 * @return <path, column header text> map
	 */
	public Map<String, String> getColumnNames() {
		return getView().getColumnNames();
	}

	public String[] getHiddenColumns() {
		return getView().getHiddenColumns();
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import java.util.Collection;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author sviyazov.a
 * 
 */
public abstract class AbstractLocalDataGridWidgetPresenter<M, V extends AbstractLocalDataGridWidgetPresenter.MyView<M, ?>>
		extends
			AbstractGridWidgetPresenter<M, V> {

	public static interface MyView<M, U extends AbstractLocalDataGridWidgetPresenter<M, ?>>
			extends
				AbstractGridWidgetPresenter.MyView<M, U> {

		/**
		 * Clears grid and adds new data
		 * 
		 * @param data
		 */
		void loadData(Collection<M> data);
	}

	public AbstractLocalDataGridWidgetPresenter(final EventBus eventBus,
			final V view) {
		super(eventBus, view);
	}
}
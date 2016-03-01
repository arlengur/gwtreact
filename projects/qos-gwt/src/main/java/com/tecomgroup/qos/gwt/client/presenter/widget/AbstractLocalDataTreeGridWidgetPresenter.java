/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * Base abstract class for {@link PresenterWidget} containing GXT
 * {@link TreeGrid} without pagination
 * 
 * @author ivlev.e
 */
public abstract class AbstractLocalDataTreeGridWidgetPresenter<M, V extends AbstractLocalDataTreeGridWidgetPresenter.MyView<M, ?>>
		extends
			PresenterWidget<V> implements UiHandlers, GridPresenter {

	public interface MyView<M, U extends AbstractLocalDataTreeGridWidgetPresenter<M, ?>>
			extends
				View,
				HasUiHandlers<U> {
		void initialize();
	}

	protected final QoSMessages messages;

	public AbstractLocalDataTreeGridWidgetPresenter(final EventBus eventBus,
			final V view) {
		super(eventBus, view);
		messages = AppUtils.getMessages();
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().initialize();
	}

}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent.DashboardWidgetAddedEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author ivlev.e
 */
public abstract class AddNamedWidgetToDashboardWidgetPresenter
		extends
			PresenterWidget<AddNamedWidgetToDashboardWidgetPresenter.MyView>
		implements
			UiHandlers,
			DashboardWidgetAddedEventHandler {

	public static interface MyView
			extends
				PopupView,
				HasUiHandlers<AddNamedWidgetToDashboardWidgetPresenter> {
		void closeDialog();

		int getColspan();

		int getRowspan();

		String getTitle();

		void initialize();

		void setName(String name);
	}

	protected final QoSMessages messages;

	@Inject
	public AddNamedWidgetToDashboardWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages) {
		super(eventBus, view);
		this.messages = messages;
		getView().setUiHandlers(this);
	}

	@SuppressWarnings("unchecked")
	public <X> X cast() {
		return (X) this;
	}

	protected void fillWidgetSize(final DashboardWidget widget) {
		widget.setColspan(getView().getColspan());
		widget.setRowspan(getView().getRowspan());
	}

	protected void fillWidgetTitle(final DashboardWidget widget) {
		widget.setTitle(getView().getTitle());
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().initialize();
		getEventBus().addHandler(DashboardWidgetAddedEvent.TYPE, this);
	}

	@Override
	public void onDashboardWidgetAdded(final DashboardWidgetAddedEvent event) {
		getView().closeDialog();
	}

	public void setName(final String name) {
		getView().setName(name);
	}
}

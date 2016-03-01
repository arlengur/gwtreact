/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author tabolin.a
 *
 */
public class AddWidgetToDashboardWidgetPresenter
		extends
			AddNamedWidgetToDashboardWidgetPresenter {

	public static interface MyView
	extends
	AddNamedWidgetToDashboardWidgetPresenter.MyView {

	}

	private DashboardWidget widget;

	@Inject
	public AddWidgetToDashboardWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages) {
		super(eventBus, view, messages);
	}

	public void actionCreateWidget() {
		fillWidgetTitle(widget);
		fillWidgetSize(widget);
		getEventBus().fireEvent(new AddWidgetToDashboardEvent(widget));
	}

	public void setDashboardWidget(final DashboardWidget widget) {
		this.widget = widget;
		setName(widget.getTitle());
	}

}

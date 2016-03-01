/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.chart;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddNamedWidgetToDashboardWidgetPresenter;

/**
 * @author tabolin.a
 *
 */
public class AddChartToDashboardWidgetPresenter
		extends
			AddNamedWidgetToDashboardWidgetPresenter {

	public static interface MyView
			extends
				AddNamedWidgetToDashboardWidgetPresenter.MyView {

	}

	private DashboardChartWidget widget;

	@Inject
	public AddChartToDashboardWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages) {
		super(eventBus, view, messages);
	}

	public void actionCreateWidget(final boolean hasLegend) {
		fillWidgetTitle(widget);
		fillWidgetSize(widget);
		widget.setLegendEnabled(hasLegend);
		getEventBus().fireEvent(new AddWidgetToDashboardEvent(widget));
	}

	public void setDashboardChartWidget(final DashboardChartWidget widget) {
		this.widget = widget;
		setName(widget.getTitle());
	}
}

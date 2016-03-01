/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.report;

import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.dashboard.EmergencyAgentsTopWidget;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddNamedWidgetToDashboardWidgetPresenter;

/**
 * @author ivlev.e
 *
 */
public class AddAnalyticsToDashboardWidgetPresenter
		extends
			AddNamedWidgetToDashboardWidgetPresenter {

	public static interface MyView
			extends
				AddNamedWidgetToDashboardWidgetPresenter.MyView {
	}

	@Inject
	public AddAnalyticsToDashboardWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages) {
		super(eventBus, view, messages);
	}

	public void actionCreateWidget(final Set<PerceivedSeverity> severities,
			final Type intervalType, final Integer topSize) {
		final EmergencyAgentsTopWidget widget = new EmergencyAgentsTopWidget();
		fillWidgetTitle(widget);
		fillWidgetSize(widget);
		widget.setSeverities(severities);
		widget.setIntervalType(intervalType);
		widget.setTopSize(topSize);
		getEventBus().fireEvent(new AddWidgetToDashboardEvent(widget));
	}
}

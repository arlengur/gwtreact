/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.dashboard.LatestAlertsWidget;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AddAgentsToDashboardWidgetPresenter;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 *
 */
public class AddAlertsToDashboardWidgetPresenter
		extends
			AddAgentsToDashboardWidgetPresenter {

	public static interface MyView
			extends
				AddAgentsToDashboardWidgetPresenter.MyView {
	}

	@Inject
	public AddAlertsToDashboardWidgetPresenter(final EventBus eventBus,
			final MyView view, final AgentServiceAsync agentService,
			final QoSMessages messages) {
		super(eventBus, view, agentService, messages);
		getView().setUiHandlers(this);
	}

	public void actionCreateWidget(final Set<PerceivedSeverity> severities,
			final Set<String> agentKeys) {
		final LatestAlertsWidget widget = new LatestAlertsWidget();
		fillWidgetTitle(widget);
		fillWidgetSize(widget);
		widget.setVisibleAlertCount(LatestAlertsWidget.DEFAULT_VISIBLE_ALERT_COUNT
				* widget.getRowspan());
		widget.setAgentKeys(agentKeys);
		widget.setForAllAgents(!SimpleUtils.isNotNullAndNotEmpty(agentKeys));
		widget.setSeverities(severities);
		getEventBus().fireEvent(new AddWidgetToDashboardEvent(widget));
	}
}

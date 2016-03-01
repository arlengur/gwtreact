/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class AgentAlertsGridWidgetPresenter
		extends
			AbstractAlertsGridWidgetPresenter {

	public interface MyView extends AbstractAlertsGridWidgetPresenter.MyView {

	}

	private Source source;

	@Inject
	public AgentAlertsGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final AlertServiceAsync alertService,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final AddAlertsToDashboardWidgetPresenter addAlertsToDashboardWidget,
			final UserServiceAsync userService,
			final TaskRetrieverAsync taskRetriever, final QoSMessages messages) {
		super(eventBus, view, alertService, loadTemplatePresenter,
				saveTemplatePresenter, addAlertsToDashboardWidget, userService);
	}

	public void actionLoadAlerts(final Order order, final Criterion criterion,
			final AsyncCallback<List<MAlert>> callback) {
		alertService.getAlertsBySource(source, order, 0, Integer.MAX_VALUE,
				true, criterion, callback);
	}

	public Source getSource() {
		return source;
	}

	@Override
	protected void onReveal() {
		getView().loadFirstPage();
	}

	public void setAgentName(final String agentName) {
		source = Source.getAgentSource(agentName);
	}
}

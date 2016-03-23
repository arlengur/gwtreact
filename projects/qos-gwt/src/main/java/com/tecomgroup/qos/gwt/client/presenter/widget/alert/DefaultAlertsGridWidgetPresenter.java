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
public class DefaultAlertsGridWidgetPresenter
		extends
			AbstractAlertsGridWidgetPresenter{

	public interface MyView extends AbstractAlertsGridWidgetPresenter.MyView {
	}

	@Inject
	public DefaultAlertsGridWidgetPresenter(
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

	public void actionLoadAlerts(final Criterion criterion, final Order order,
			final int startPosition, final int size,
			final AsyncCallback<List<MAlert>> callback) {
		alertService.getAlerts(criterion, order, startPosition, size, callback);
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().onBind();
	}
}

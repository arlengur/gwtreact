/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.service.PolicyConfigurationServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;

/**
 * @author ivlev.e
 * 
 */
public class DefaultPoliciesGridWidgetPresenter
		extends
			AbstractPoliciesGridWidgetPresenter {

	public interface MyView extends AbstractPoliciesGridWidgetPresenter.MyView {

	}

	@Inject
	public DefaultPoliciesGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PlaceManager placeManager,
			final PolicyConfigurationServiceAsync policyConfigService,
			final TaskRetrieverAsync taskRetriever,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final QoSMessages messages,
			final PolicyActionsTemplatesEditorWidgetPresenter policyActionsTemplatesEditorWidgetPresenter,
			final PolicyConditionsTemplatesEditorWidgetPresenter
                    policyConditionsTemplatesEditorWidgetPresenter,
            final PolicyItemWidgetPresenter policyItemWidgetPresenter) {
		super(eventBus, view, placeManager, policyConfigService, taskRetriever,
				policyComponentTemplateService, messages,
				policyActionsTemplatesEditorWidgetPresenter,
				policyConditionsTemplatesEditorWidgetPresenter, policyItemWidgetPresenter);
	}

	public void actionLoadPolicies(final Criterion criterion,
			final Order order, final int startPosition, final int size,
			final AsyncCallback<List<PolicyWrapper>> callback) {
		policyConfigService.getPolicies(criterion, order, startPosition,
                size, getSearchText(),
				loadPoliciesCallback(callback));
	}

}

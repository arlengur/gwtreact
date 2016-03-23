/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;

/**
 * @author kunilov.p
 *
 */
public class PolicyActionsTemplatesEditorGridWidgetPresenter
		extends
			AbstractPolicyComponentTemplatesEditorGridWidgetPresenter<MPolicyActionsTemplate, PolicyActionsTemplatesEditorGridWidgetPresenter.MyView, PolicyActionsTemplateInformationWidgetPresenter> {

	public static interface MyView
			extends
				AbstractPolicyComponentTemplatesEditorGridWidgetPresenter.MyView<MPolicyActionsTemplate, PolicyActionsTemplatesEditorGridWidgetPresenter> {
	}

	@Inject
	public PolicyActionsTemplatesEditorGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PolicyActionsTemplateInformationWidgetPresenter actionsTemplateInformationWidgetPresenter,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService) {
		super(eventBus, view, actionsTemplateInformationWidgetPresenter,
				policyComponentTemplateService);
		getView().setUiHandlers(this);
	}

	@Override
	protected void loadPolicyComponentTemplates() {
		policyComponentTemplateService
				.getAllActionsTemplates(new AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MPolicyActionsTemplate>>() {

					@Override
					protected void success(
							final Collection<MPolicyActionsTemplate> policyActionTemplates) {
						getView().loadData(policyActionTemplates);
					}
				});
	}
}

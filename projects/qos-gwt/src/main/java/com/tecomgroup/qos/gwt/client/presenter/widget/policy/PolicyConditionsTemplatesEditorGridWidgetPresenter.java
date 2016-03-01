/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;

/**
 * @author kunilov.p
 *
 */
public class PolicyConditionsTemplatesEditorGridWidgetPresenter
		extends
			AbstractPolicyComponentTemplatesEditorGridWidgetPresenter<MPolicyConditionsTemplate, PolicyConditionsTemplatesEditorGridWidgetPresenter.MyView, PolicyConditionsTemplateInformationWidgetPresenter> {

	public static interface MyView
			extends
				AbstractPolicyComponentTemplatesEditorGridWidgetPresenter.MyView<MPolicyConditionsTemplate, PolicyConditionsTemplatesEditorGridWidgetPresenter> {
	}

	@Inject
	public PolicyConditionsTemplatesEditorGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PolicyConditionsTemplateInformationWidgetPresenter conditionsTemplateInformationWidgetPresenter,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService) {
		super(eventBus, view, conditionsTemplateInformationWidgetPresenter,
				policyComponentTemplateService);
		getView().setUiHandlers(this);
	}

	@Override
	protected void beforeOpenTemplateEditor(
			final MPolicyConditionsTemplate template) {
		super.beforeOpenTemplateEditor(template);
		policyComponentTemplateInformationWidgetPresenter.refreshConditions();
	}

	@Override
	protected void loadPolicyComponentTemplates() {
		policyComponentTemplateService
				.getAllConditionsTemplates(new AutoNotifyingAsyncCallback<Collection<MPolicyConditionsTemplate>>() {

					@Override
					protected void success(
							final Collection<MPolicyConditionsTemplate> policyConditionsTemplates) {
						getView().loadData(policyConditionsTemplates);
					}
				});
	}
}

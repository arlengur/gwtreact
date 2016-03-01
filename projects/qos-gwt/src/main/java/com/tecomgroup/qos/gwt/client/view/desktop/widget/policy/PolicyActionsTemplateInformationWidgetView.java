/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplateInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;

/**
 * @author ivlev.e
 *
 */
public class PolicyActionsTemplateInformationWidgetView
		extends
			AbstractPolicyComponentTemplateInformationWidgetView<MPolicyActionsTemplate, PolicyActionsTemplateInformationWidgetPresenter>
		implements
			PolicyActionsTemplateInformationWidgetPresenter.MyView {

	@Inject
	public PolicyActionsTemplateInformationWidgetView(final EventBus eventBus,
			final DialogFactory dialogFactory) {
		super(eventBus, dialogFactory);
	}
}
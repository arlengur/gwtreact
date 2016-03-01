/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplatesEditorWidgetPresenter;

/**
 * @author kunilov.p
 * 
 */
public class PolicyActionsTemplatesEditorWidgetView
		extends
			AbstractPolicyComponentTemplatesEditorWidgetView<PolicyActionsTemplatesEditorWidgetPresenter>
		implements
			PolicyActionsTemplatesEditorWidgetPresenter.MyView {

	@Inject
	public PolicyActionsTemplatesEditorWidgetView(final EventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected String getDialogTitle() {
		return messages.notificationsTemplateEditor();
	}
}

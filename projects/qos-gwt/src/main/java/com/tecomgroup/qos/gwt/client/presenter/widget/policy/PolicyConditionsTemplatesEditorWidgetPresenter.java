/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;

/**
 * @author kunilov.p
 * 
 */
public class PolicyConditionsTemplatesEditorWidgetPresenter
		extends
			PresenterWidget<PolicyConditionsTemplatesEditorWidgetPresenter.MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				PopupView,
				HasUiHandlers<PolicyConditionsTemplatesEditorWidgetPresenter> {

	}

	private final PolicyConditionsTemplatesEditorGridWidgetPresenter policyConditionsTemplatesEditorGridWidgetPresenter;

	@Inject
	public PolicyConditionsTemplatesEditorWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PolicyConditionsTemplatesEditorGridWidgetPresenter policyConditionsTemplatesEditorGridWidgetPresenter) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		this.policyConditionsTemplatesEditorGridWidgetPresenter = policyConditionsTemplatesEditorGridWidgetPresenter;
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(0, policyConditionsTemplatesEditorGridWidgetPresenter);
	}
}

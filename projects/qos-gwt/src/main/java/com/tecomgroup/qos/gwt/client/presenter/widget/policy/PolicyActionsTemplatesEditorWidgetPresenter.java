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
 * @author kshnyakin.m
 * 
 */
public class PolicyActionsTemplatesEditorWidgetPresenter
		extends
			PresenterWidget<PolicyActionsTemplatesEditorWidgetPresenter.MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				PopupView,
				HasUiHandlers<PolicyActionsTemplatesEditorWidgetPresenter> {

	}

	private final PolicyActionsTemplatesEditorGridWidgetPresenter policyActionsTemplatesEditorGridWidgetPresenter;

	@Inject
	public PolicyActionsTemplatesEditorWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PolicyActionsTemplatesEditorGridWidgetPresenter policyActionsTemplatesEditorGridWidgetPresenter) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		this.policyActionsTemplatesEditorGridWidgetPresenter = policyActionsTemplatesEditorGridWidgetPresenter;
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(0, policyActionsTemplatesEditorGridWidgetPresenter);
	}
}

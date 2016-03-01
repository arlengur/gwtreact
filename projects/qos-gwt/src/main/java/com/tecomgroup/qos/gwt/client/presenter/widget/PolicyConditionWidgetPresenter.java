/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.gwt.client.presenter.widget.PolicyConditionWidgetPresenter.MyView;

/**
 * @author abondin
 * 
 */
public class PolicyConditionWidgetPresenter extends PresenterWidget<MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				View,
				HasUiHandlers<PolicyConditionWidgetPresenter> {
		boolean hasErrors();

		void refreshCondition();

		void saveCondition();

		void setEnabled(boolean enabled);

		void setParameterType(ParameterType type);
	}

	private ConditionLevel conditionLevel;

	/**
	 * @param eventBus
	 * @param view
	 */
	@Inject
	public PolicyConditionWidgetPresenter(final EventBus eventBus,
			final MyView view) {
		super(eventBus, view);
		view.setUiHandlers(this);
	}

	/**
	 * @return the conditionLevel
	 */
	public ConditionLevel getConditionLevel() {
		return conditionLevel;
	}

	public boolean hasErrors() {
		return getView().hasErrors();
	}

	public void reset() {
		setConditionLevel(null);
		getView().refreshCondition();
	}

	public void saveCondition() {
		getView().saveCondition();
	}

	/**
	 * @param conditionLevel
	 *            the conditionLevel to set
	 */
	public void setConditionLevel(final ConditionLevel conditionLevel) {
		this.conditionLevel = conditionLevel;
	}

	public void setEnabled(final boolean enabled) {
		getView().setEnabled(enabled);
	}

	public void setParameterType(final ParameterType type) {
		getView().setParameterType(type);
	}

	public void setUp(final ConditionLevel conditionLevel) {
		setConditionLevel(conditionLevel);
		getView().refreshCondition();
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicyConditionLevels;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplateInformationWidgetPresenter.MyView;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * @author ivlev.e
 * 
 */
public class PolicyConditionsTemplateInformationWidgetPresenter
		extends
			AbstractPolicyComponentTemplateInformationWidgetPresenter<MPolicyConditionsTemplate, MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				AbstractPolicyComponentTemplateInformationWidgetPresenter.MyView<MPolicyConditionsTemplate, PolicyConditionsTemplateInformationWidgetPresenter> {

		@Override
		String getName();

		ParameterType getParameterType();

		@Override
		void reset();

		void setParameterType(ParameterType type);

		@Override
		void showTemplateUpdateModeSelectionDialog();

		void showTypeChangedSaveDialog();
	}

	private final PolicyConditionsTemplateWidgetPresenter policyConditionsTemplateWidgetPresenter;

	@Inject
	public PolicyConditionsTemplateInformationWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final QoSMessages messages,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final PolicyConditionsTemplateWidgetPresenter policyConditionsTemplateWidgetPresenter) {
		super(eventBus, view, messages, policyComponentTemplateService);
		this.policyConditionsTemplateWidgetPresenter = policyConditionsTemplateWidgetPresenter;
		getView().setUiHandlers(this);
	}

	@Override
	protected MPolicyConditionsTemplate createTemplate() {
		final MPolicyConditionsTemplate template = new MPolicyConditionsTemplate();
		fillTemplate(template,
				policyConditionsTemplateWidgetPresenter
						.getPolicyConditionLevels());

		return template;
	}

	@Override
	protected void fillTemplate(final MPolicyConditionsTemplate template) {
		super.fillTemplate(template);
		template.setParameterType(getView().getParameterType());
	}

	private void fillTemplate(final MPolicyConditionsTemplate template,
			final MPolicyConditionLevels policyConditionLevels) {
		fillTemplate(template);
		template.setConditionLevels(policyConditionLevels);
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(1, policyConditionsTemplateWidgetPresenter);
	}

	public void onParameterTypeChange(final ParameterType type) {
		policyConditionsTemplateWidgetPresenter.setParameterType(type);
	}

	public void refreshConditions() {
		policyConditionsTemplateWidgetPresenter.refreshConditions();
	}

	@Override
	protected void reset() {
		super.reset();
		policyConditionsTemplateWidgetPresenter.reset();
	}

	@Override
	protected String saveOrUpdateFailureHandler(final Throwable caught) {
		return caught.getMessage();
	}

	@Override
	public void saveOrUpdateTemplate(final boolean reapplyToPolicies) {
		fillTemplate(editableEntity,
				policyConditionsTemplateWidgetPresenter
						.getPolicyConditionLevels());
		super.saveOrUpdateTemplate(reapplyToPolicies);
	}

	@Override
	public void setCreateMode() {
		super.setCreateMode();
		policyConditionsTemplateWidgetPresenter
				.setParameterType(ParameterType.LEVEL);
	}

	@Override
	public void setUpdateMode(final MPolicyConditionsTemplate editableEntity) {
		super.setUpdateMode(editableEntity);
		getView().setParameterType(editableEntity.getParameterType());
		policyConditionsTemplateWidgetPresenter.setParameterType(editableEntity
				.getParameterType());
		policyConditionsTemplateWidgetPresenter
				.setPolicyCondition(editableEntity.getConditionLevels());
	}

	@Override
	protected void updateTemplate() {
		final MPolicyConditionLevels policyConditionLevels = policyConditionsTemplateWidgetPresenter
				.getPolicyConditionLevels();

		final boolean conditionsEqual = PolicyUtils
				.arePolicyConditionLevelsEqual(
						editableEntity.getConditionLevels(),
						policyConditionLevels);
		final boolean typeEqual = getView().getParameterType() == editableEntity
				.getParameterType();

		if (!typeEqual) {
			getView().showTypeChangedSaveDialog();
		} else if (!conditionsEqual) {
			getView().showTemplateUpdateModeSelectionDialog();
		} else {
			saveOrUpdateTemplate(false);
		}
	}

	@Override
	protected boolean validateForm() {
		return super.validateForm()
				& policyConditionsTemplateWidgetPresenter.validate();
	}
}

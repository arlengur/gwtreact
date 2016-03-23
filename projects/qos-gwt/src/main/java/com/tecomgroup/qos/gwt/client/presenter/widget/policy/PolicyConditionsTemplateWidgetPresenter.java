/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicyCondition;
import com.tecomgroup.qos.domain.pm.MPolicyConditionLevels;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.PolicyConditionWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * @author ivlev.e
 * 
 */
public class PolicyConditionsTemplateWidgetPresenter
		extends
			PresenterWidget<PolicyConditionsTemplateWidgetPresenter.MyView>
		implements
			UiHandlers {

	public interface MyView
			extends
				View,
				HasUiHandlers<PolicyConditionsTemplateWidgetPresenter> {

		void enableConditionControls(boolean enabled);

		MPolicyConditionsTemplate getConditionsTemplate();

		ThresholdType getThresholdType();

		void refreshConditions();

		void refreshThresholdType();

		void reset();

		void selectConditionsTemplate(
				MPolicyConditionsTemplate selectedConditionsTemplate);

		void setConditionsTemplates(
				Collection<MPolicyConditionsTemplate> templates);

		void setParameterType(ParameterType newParameterType,
				ParameterType previousParameterType);

		void setThresholdType(ThresholdType thresholdType);

		void toggleTemplateControls(boolean value);

		boolean validate();
	}

	private final QoSMessages messages;

	private final Map<PerceivedSeverity, PolicyConditionWidgetPresenter> conditionPresenters = new TreeMap<PerceivedSeverity, PolicyConditionWidgetPresenter>(
			MAlert.SEVERITY_DESC_COMPARATOR);

	private ParameterType previousParameterType;

	private final PolicyComponentTemplateServiceAsync policyComponentTemplateService;

	private boolean templateControlsEnabled;

	@Inject
	public PolicyConditionsTemplateWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final QoSMessages messages,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final PolicyConditionWidgetPresenter criticalConditonPresenter,
			final PolicyConditionWidgetPresenter warningConditonPresenter) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		conditionPresenters.put(PerceivedSeverity.CRITICAL,
				criticalConditonPresenter);
		conditionPresenters.put(PerceivedSeverity.WARNING,
				warningConditonPresenter);
		this.messages = messages;
		this.policyComponentTemplateService = policyComponentTemplateService;
		view.setUiHandlers(this);
	}

	public void actionEnableConditionWidget(final boolean enabled) {
		for (final PolicyConditionWidgetPresenter condition : conditionPresenters
				.values()) {
			condition.setEnabled(enabled);
		}
	}

	@Override
	public Widget asWidget() {
		return getView().asWidget();
	}

	public void enableConditionControls(final boolean enabled) {
		getView().enableConditionControls(enabled);
	}

	private Map<PerceivedSeverity, ConditionLevel> getConditionLevels() {
		final Map<PerceivedSeverity, ConditionLevel> conditions = new HashMap<PerceivedSeverity, ConditionLevel>();

		for (final Map.Entry<PerceivedSeverity, PolicyConditionWidgetPresenter> conditionEntry : conditionPresenters
				.entrySet()) {
			conditions.put(conditionEntry.getKey(), conditionEntry.getValue()
					.getConditionLevel());
		}
		return conditions;
	}

	public MPolicyConditionsTemplate getConditionsTemplate() {
		return getView().getConditionsTemplate();
	}

	public ParameterType getParameterType() {
		return previousParameterType;
	}

	public MContinuousThresholdFallCondition getPolicyCondition(
			final ParameterIdentifier parameterIdentifier) {
		MContinuousThresholdFallCondition policyCondition = null;

		final Map<PerceivedSeverity, ConditionLevel> conditionLevels = getConditionLevels();

		if (!conditionLevels.isEmpty()) {

			policyCondition = new MContinuousThresholdFallCondition();
			policyCondition.setCriticalLevel(conditionLevels
					.get(PerceivedSeverity.CRITICAL));
			policyCondition.setWarningLevel(conditionLevels
					.get(PerceivedSeverity.WARNING));
			policyCondition.setParameterIdentifier(parameterIdentifier);
			policyCondition.setThresholdType(getView().getThresholdType());
		}
		return policyCondition;
	}

	public MPolicyConditionLevels getPolicyConditionLevels() {
		MPolicyConditionLevels policyConditionLevels = null;

		final Map<PerceivedSeverity, ConditionLevel> conditionLevels = getConditionLevels();

		if (!conditionLevels.isEmpty()) {

			policyConditionLevels = new MPolicyConditionLevels();
			policyConditionLevels.setCriticalLevel(conditionLevels
					.get(PerceivedSeverity.CRITICAL));
			policyConditionLevels.setWarningLevel(conditionLevels
					.get(PerceivedSeverity.WARNING));
			policyConditionLevels
					.setThresholdType(getView().getThresholdType());
		}
		return policyConditionLevels;
	}

	/**
	 * 
	 * @param severity
	 * @return true if condition for given severity is not null
	 */
	public boolean isConditionEnabled(final PerceivedSeverity severity) {
		return conditionPresenters.containsKey(severity)
				&& conditionPresenters.get(severity).getConditionLevel() != null;
	}

	public void loadConditionsTemplates(final ParameterType parameterType,
			final MPolicyConditionsTemplate selectedTemplate) {
		if (templateControlsEnabled) {

			final ParameterType currentParameterType = (parameterType == null && selectedTemplate != null)
					? selectedTemplate.getParameterType()
					: parameterType;

			if (currentParameterType != null) {
				policyComponentTemplateService
						.getConditionsTemplates(
								currentParameterType,
								new AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MPolicyConditionsTemplate>>() {
									@Override
									protected void success(
											final Collection<MPolicyConditionsTemplate> templates) {
										getView().setConditionsTemplates(
												templates);
										if (selectedTemplate != null) {
											getView().selectConditionsTemplate(
													selectedTemplate);
										}
									}
								});
			}
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		for (final Map.Entry<PerceivedSeverity, PolicyConditionWidgetPresenter> entry : conditionPresenters
				.entrySet()) {
			setInSlot(entry.getKey(), entry.getValue());
		}
	}

	public void refreshConditions() {
		getView().refreshConditions();
	}

	public void refreshThresholdType() {
		getView().refreshThresholdType();
	}

	public void reset() {
		resetConditions();
		getView().reset();
		enableConditionControls(true);
	}

	public void resetConditions() {
		for (final PolicyConditionWidgetPresenter conditionWidgetPresenter : conditionPresenters
				.values()) {
			conditionWidgetPresenter.reset();
		}
	}

	public void setParameterType(final ParameterType type) {
		for (final PolicyConditionWidgetPresenter presenter : conditionPresenters
				.values()) {
			presenter.setParameterType(type);
		}
		getView().setParameterType(type, previousParameterType);
		previousParameterType = type;
	}

	public void setPolicyCondition(final MPolicyCondition policyCondition) {
		if (policyCondition instanceof MPolicyConditionLevels) {
			final MPolicyConditionLevels policyConditionLevels = (MPolicyConditionLevels) policyCondition;

			final PolicyConditionWidgetPresenter criticalConditionPresenter = conditionPresenters
					.get(PerceivedSeverity.CRITICAL);

			if (policyConditionLevels.getCriticalLevel() != null) {
				criticalConditionPresenter.setUp(policyConditionLevels
						.getCriticalLevel().copy());
			}

			final PolicyConditionWidgetPresenter warningConditionPresenter = conditionPresenters
					.get(PerceivedSeverity.WARNING);
			if (policyConditionLevels.getWarningLevel() != null) {
				warningConditionPresenter.setUp(policyConditionLevels
						.getWarningLevel().copy());
			}

			getView()
					.setThresholdType(policyConditionLevels.getThresholdType());
		}
	}

	public void setThresholdType(final ThresholdType thresholdType) {
		getView().setThresholdType(thresholdType);
	}

	public void toggleTemplateControls(final boolean value) {
		templateControlsEnabled = value;
		getView().toggleTemplateControls(value);
	}

	public boolean validate() {
		return getView().validate() && validateConditions();
	}

	public boolean validateConditions() {
		boolean conditionsAreEmpty = true;
		boolean hasErrors = false;

		for (final PolicyConditionWidgetPresenter conditionWidgetPresenter : conditionPresenters
				.values()) {
			conditionWidgetPresenter.saveCondition();

			final boolean currentConditionErrors = conditionWidgetPresenter
					.hasErrors();
			hasErrors |= currentConditionErrors;
			if (conditionWidgetPresenter.getConditionLevel() != null
					|| currentConditionErrors) {
				conditionsAreEmpty = false;
			}
		}

		if (conditionsAreEmpty) {
			AppUtils.showErrorMessage(messages.levelsAreNotDefined());
		} else if (!hasErrors) {
			try {
				PolicyUtils.validateConditionLevels(getPolicyConditionLevels(),
						messages);
				conditionsAreEmpty = false;
			} catch (final Exception ex) {
				conditionsAreEmpty = true;
				AppUtils.showErrorMessage(ex.getMessage());
			}
		}

		return !conditionsAreEmpty && !hasErrors;
	}
}

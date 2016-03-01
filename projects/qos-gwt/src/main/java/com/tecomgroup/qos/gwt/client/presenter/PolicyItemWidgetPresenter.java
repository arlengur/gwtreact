/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.CrudOperations;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicyCondition;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.exception.DeletedContactInformationException;
import com.tecomgroup.qos.exception.DeletedSourceException;
import com.tecomgroup.qos.exception.DisabledSourceException;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractEntityEditorDialogPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplateGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplateWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.shared.JSEvaluator;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.PolicyConfigurationServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.PolicyUtils;
import com.tecomgroup.qos.util.SimpleUtils.SimpleHandler;

/**
 * @author ivlev.e
 * 
 */
public class PolicyItemWidgetPresenter
		extends
			AbstractEntityEditorDialogPresenter<MPolicy, PolicyItemWidgetPresenter.MyView> {

	public static interface MyView
			extends
				AbstractEntityEditorDialogPresenter.MyView<MPolicy, PolicyItemWidgetPresenter> {
		void refreshPolicy();

		void savePolicy();

		void setAgents(List<MAgent> agents);

		void setAlertTypes(List<MAlertType> types);
	}

	private static Logger LOGGER = Logger
			.getLogger(PolicyItemWidgetPresenter.class.getName());

	public static int POLICY_ACTIONS_TEMPLATE_GRID_SLOT = 1;
	public static int POLICY_CONDITIONS_TEMPLATE_GRID_SLOT = 0;

	private final AlertServiceAsync alertService;

	private final PolicyConfigurationServiceAsync policyConfigService;

	private final TaskRetrieverAsync taskRetriever;

	private final QoSMessages messages;

	private final List<MAlertType> alertTypes = new ArrayList<MAlertType>();

	private final AgentServiceAsync agentService;

	private final PolicyActionsTemplateGridWidgetPresenter policyActionsTemplateGridWidgetPresenter;

	private final PolicyConditionsTemplateWidgetPresenter policyConditionsTemplateGridWidgetPresenter;

	private MAgentTask policyTask;

	private SimpleHandler refreshHandler;

	@Inject
	public PolicyItemWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final AgentServiceAsync agentService,
			final AlertServiceAsync alertService,
			final TaskRetrieverAsync taskRetriever,
			final PolicyConfigurationServiceAsync policyConfigurationService,
			final PolicyActionsTemplateGridWidgetPresenter policyActionsTemplateGridWidgetPresenter,
			final PolicyConditionsTemplateWidgetPresenter policyConditionsTemplateGridWidgetPresenter,
			final QoSMessages messages) {
		super(eventBus, view);
		this.agentService = agentService;
		policyConfigService = policyConfigurationService;
		this.alertService = alertService;
		this.taskRetriever = taskRetriever;
		this.messages = messages;
		this.policyActionsTemplateGridWidgetPresenter = policyActionsTemplateGridWidgetPresenter;
		this.policyConditionsTemplateGridWidgetPresenter = policyConditionsTemplateGridWidgetPresenter;
		policyConditionsTemplateGridWidgetPresenter
				.toggleTemplateControls(true);
		view.setUiHandlers(this);
	}

	public void actionSavePolicy(final MAgentTask task) {
		this.policyTask = task;
		boolean hasErrors = !policyConditionsTemplateGridWidgetPresenter
				.validateConditions();

		if (hasErrors) {
			LOGGER.log(Level.SEVERE, messages.conditionsNotSet());
			AppUtils.showErrorMessage(messages.conditionsNotSet());
		} else {
			getView().savePolicy();
			if (editableEntity.getId() == null) {
				editableEntity.setSource(Source.getTaskSource(task.getKey()));
			}
			PolicyUtils
					.setDefaultPolicyActionNames(editableEntity.getActions());
			try {
				PolicyUtils.validateAndInitPolicyCondition(editableEntity,
						task.getResultConfiguration(),
						JSEvaluator.getInstance(), messages);
			} catch (final Exception e) {
				LOGGER.log(Level.SEVERE, "Cannot validate policy", e);
				AppUtils.showErrorMessage(e.getMessage());
				hasErrors = true;
			}
			if (!hasErrors) {
				policyConfigService.saveOrUpdatePolicy(editableEntity,
						new AutoNotifyingAsyncCallback<MPolicy>() {
							@Override
							protected void failure(final Throwable caught) {
								final String errorMessage;
								final String policySourceKey = editableEntity
										.getSource().getKey();
								if (caught instanceof DisabledSourceException) {
									if (getCurrentMode() == CrudOperations.CREATE) {
										errorMessage = messages
												.unableToCreatePolicyWithDisabledSource(policySourceKey);
									} else {
										errorMessage = messages
												.unableToUpdatePolicyWithDisabledSource(policySourceKey);
									}
								} else if (caught instanceof DeletedSourceException) {
									if (getCurrentMode() == CrudOperations.CREATE) {
										errorMessage = messages
												.unableToCreatePolicyWithDeletedSource(policySourceKey);
									} else {
										errorMessage = messages
												.unableToUpdatePolicyWithDeletedSource(policySourceKey);
									}
								} else if (caught instanceof DeletedContactInformationException) {
									errorMessage = messages
											.deletedRecipientsError();

									final DeletedContactInformationException exception = (DeletedContactInformationException) caught;
									policyActionsTemplateGridWidgetPresenter
											.clearObsoleteContacts(exception
													.getDeletedKeys());
									policyActionsTemplateGridWidgetPresenter
											.loadUsers();

								} else {
									errorMessage = messages
											.unableToSavePolicy()
											+ ": "
											+ caught.getMessage();
								}
								LOGGER.log(Level.SEVERE, errorMessage, caught);
								AppUtils.showErrorMessage(errorMessage);
							}

							@Override
							protected void success(final MPolicy result) {
								final String message;
								if (getCurrentMode() == CrudOperations.CREATE) {
									message = messages
											.policyCreatedSuccessfully();
								} else {
									message = messages
											.policyEditedSuccessfully();
								}
								AppUtils.showInfoMessage(message);
								if (refreshHandler != null) {
									refreshHandler.handle();
								}
								getView().hide();
							}
						});
			}
		}
	}

	public void enableConditionsControls(final boolean enabled) {
		policyConditionsTemplateGridWidgetPresenter
				.enableConditionControls(enabled);
	}

	/**
	 * @return the alertTypes
	 */
	public List<MAlertType> getAlertTypes() {
		return alertTypes;
	}

	public MPolicy getPolicy() {
		return editableEntity;
	}

	public List<MPolicyActionWithContacts> getPolicyActionsFromGrid() {
		return policyActionsTemplateGridWidgetPresenter.getPolicyActions();
	}

	public MAgentTask getPolicyTask() {
		return policyTask;
	}

	public void loadAlertTypes() {
		alertService
				.getAllTypes(new AutoNotifyingAsyncCallback<Map<String, MAlertType>>(
						messages.alertTypesLoadingFail(), true) {
					@Override
					protected void success(final Map<String, MAlertType> result) {
						alertTypes.clear();
						alertTypes.addAll(result.values());
						getView().setAlertTypes(alertTypes);
					}
				});
	}

	public void loadPolicy() {
		if (editableEntity == null) {
			editableEntity = new MPolicy();
			editableEntity.setActions(new ArrayList<MPolicyAction>());
			policyTask = null;
			policyConditionsTemplateGridWidgetPresenter.resetConditions();
			getView().refreshPolicy();
		} else {
			taskRetriever.getTaskByKey(
					editableEntity.getSource().getKey(),
					new AutoNotifyingAsyncCallback<MAgentTask>(messages
							.taskLoadingFail(), true) {
						@Override
						protected void success(final MAgentTask result) {
							policyTask = result;
							getView().refreshPolicy();
						}
					});
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(POLICY_CONDITIONS_TEMPLATE_GRID_SLOT,
				policyConditionsTemplateGridWidgetPresenter);
		setInSlot(POLICY_ACTIONS_TEMPLATE_GRID_SLOT,
				policyActionsTemplateGridWidgetPresenter);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		policyActionsTemplateGridWidgetPresenter.reset();
		policyConditionsTemplateGridWidgetPresenter.reset();
		loadAlertTypes();
		agentService.getAllAgents(new AutoNotifyingAsyncCallback<List<MAgent>>(
				messages.agentsLoadingFail(), true) {
			@Override
			protected void success(final List<MAgent> result) {
				getView().setAgents(result);
				loadPolicy();
			}
		});
	}

	public void refreshConditions() {
		policyConditionsTemplateGridWidgetPresenter.refreshConditions();
	}

	public void refreshThresholdType() {
		policyConditionsTemplateGridWidgetPresenter.refreshThresholdType();
	}

	public void resetConditions() {
		policyConditionsTemplateGridWidgetPresenter.reset();
	}

	public void savePolicy(final String policyName,
			final ParameterIdentifier parameterIdentifier,
			final MPolicySendAlert sendAlertAction) {

		editableEntity.setDisplayName(policyName);

		final List<MPolicyAction> actions = new ArrayList<MPolicyAction>();
		actions.add(sendAlertAction);
		actions.addAll(getPolicyActionsFromGrid());
		editableEntity.setActions(actions);
		editableEntity
				.setConditionsTemplate(policyConditionsTemplateGridWidgetPresenter
						.getConditionsTemplate());
		editableEntity.setCondition(policyConditionsTemplateGridWidgetPresenter
				.getPolicyCondition(parameterIdentifier));
	}

	public void setParameterType(final ParameterType parameterType) {
		policyConditionsTemplateGridWidgetPresenter
				.setParameterType(parameterType);
		policyConditionsTemplateGridWidgetPresenter.loadConditionsTemplates(
				parameterType, editableEntity.getConditionsTemplate());
	}

	public void setPolicyActionsToGrid(
			final List<MPolicyActionWithContacts> actions) {
		policyActionsTemplateGridWidgetPresenter.setPolicyActions(actions);
	}

	public void setRefreshHandler(final SimpleHandler handler) {
		this.refreshHandler = handler;
	}

	public void setThresholdType(final ThresholdType thresholdType) {
		policyConditionsTemplateGridWidgetPresenter
				.setThresholdType(thresholdType);
	}

	public void setUpConditions(final MPolicyCondition policyCondition) {
		policyConditionsTemplateGridWidgetPresenter
				.setPolicyCondition(policyCondition);
	}

	public boolean validateNotificationActions() {
		return policyActionsTemplateGridWidgetPresenter.validate(true);
	}

	public boolean validatePolicyConditions() {
		return policyConditionsTemplateGridWidgetPresenter.validate();
	}
}

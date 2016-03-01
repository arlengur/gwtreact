/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.gwt.client.event.policy.AfterRemovePolicyActionsTemplatesEvent;
import com.tecomgroup.qos.gwt.client.event.policy.AfterRemovePolicyActionsTemplatesEvent.AfterRemovePolicyActionsTemplatesEventHandler;
import com.tecomgroup.qos.gwt.client.event.policy.AfterUpdatePolicyComponentTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.policy.AfterUpdatePolicyComponentTemplateEvent.AfterUpdatePolicyComponentTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.service.PolicyConfigurationServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 */
public abstract class AbstractPoliciesGridWidgetPresenter
		extends
			AbstractRemoteDataGridWidgetPresenter<PolicyWrapper, AbstractPoliciesGridWidgetPresenter.MyView>
		implements
			AfterRemovePolicyActionsTemplatesEventHandler,
			AfterUpdatePolicyComponentTemplateEventHandler {

	public interface MyView
			extends
				AbstractRemoteDataGridWidgetPresenter.MyView<PolicyWrapper, AbstractPoliciesGridWidgetPresenter> {
		void resetPolicyToolbar();
	}

	protected final PolicyConfigurationServiceAsync policyConfigService;

	protected final TaskRetrieverAsync taskRetriever;

	protected final QoSMessages messages;

	private final PlaceManager placeManager;

	private final PolicyActionsTemplatesEditorWidgetPresenter policyActionsTemplatesEditorWidgetPresenter;

	private final PolicyConditionsTemplatesEditorWidgetPresenter policyConditionsTemplatesEditorWidgetPresenter;

	private final PolicyComponentTemplateServiceAsync policyComponentTemplateService;

	private final PolicyItemWidgetPresenter policyItemWidgetPresenter;

	private String searchText;

	@Inject
	public AbstractPoliciesGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PlaceManager placeManager,
			final PolicyConfigurationServiceAsync policyConfigService,
			final TaskRetrieverAsync taskRetriever,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final QoSMessages messages,
			final PolicyActionsTemplatesEditorWidgetPresenter policyActionsTemplatesEditorWidgetPresenter,
			final PolicyConditionsTemplatesEditorWidgetPresenter policyConditionsTemplatesEditorWidgetPresenter,
			final PolicyItemWidgetPresenter policyItemWidgetPresenter) {
		super(eventBus, view);
		this.policyConfigService = policyConfigService;
		this.taskRetriever = taskRetriever;
		this.messages = messages;
		this.placeManager = placeManager;
		this.policyComponentTemplateService = policyComponentTemplateService;
		this.policyActionsTemplatesEditorWidgetPresenter = policyActionsTemplatesEditorWidgetPresenter;
		this.policyConditionsTemplatesEditorWidgetPresenter = policyConditionsTemplatesEditorWidgetPresenter;
		this.policyItemWidgetPresenter = policyItemWidgetPresenter;
		getView().setUiHandlers(this);
		getEventBus().addHandler(AfterRemovePolicyActionsTemplatesEvent.TYPE,
				this);
		getEventBus().addHandler(AfterUpdatePolicyComponentTemplateEvent.TYPE,
				this);
	}

	public void actionOpenPolicyActionsTemplatesEditor() {
		this.addToPopupSlot(policyActionsTemplatesEditorWidgetPresenter, true);
	}

	public void actionOpenPolicyConditionsTemplatesEditor() {
		this.addToPopupSlot(policyConditionsTemplatesEditorWidgetPresenter,
				true);
	}

	public void addPolicy(final PolicyWrapper item) {
		getView().addItem(item);
	}

	@SuppressWarnings("unchecked")
	public <X> X cast() {
		return (X) this;
	}

	@Override
	protected Criterion createFilteringCriterion() {
		return null;
	}

	@Override
	protected Criterion createLoadingCriterion() {
		return null;
	}

	protected String getSearchText() {
		return searchText;
	}

	/**
	 * @param criterion
	 * @param callback
	 */
	public void getTotalCount(final Criterion criterion,
			final AsyncCallback<Long> callback) {
		policyConfigService.getPoliciesCount(criterion, getSearchText(),
				callback);
	}

	@Override
	@Inject
	public void initialize() {
		policyItemWidgetPresenter
				.setRefreshHandler(new SimpleUtils.SimpleHandler() {
					@Override
					public void handle() {
						reload(false);
					}
				});
	}

	protected AsyncCallback<List<MPolicy>> loadPoliciesCallback(
			final AsyncCallback<List<PolicyWrapper>> callback) {
		return new AutoNotifyingAsyncCallback<List<MPolicy>>(
				messages.policiesLoadingFail(), true) {

			@Override
			protected void success(final List<MPolicy> policies) {
				if (policies == null || policies.isEmpty()) {
					callback.onSuccess(new ArrayList<PolicyWrapper>());
				} else {
					final Set<String> taskKeys = new HashSet<String>();
					final List<MPolicy> taskPolicies = new ArrayList<MPolicy>();
					for (final MPolicy policy : policies) {
						if (Source.Type.TASK.equals(policy.getSource()
								.getType())) {
							taskKeys.add(policy.getSource().getKey());
							taskPolicies.add(policy);
						}
					}
					taskRetriever.getTasksByKeys(new HashSet<String>(taskKeys),
							new AutoNotifyingAsyncCallback<List<MAgentTask>>(
									messages.tasksForPoliciesLoadingFail(),
									true) {

								@Override
								protected void success(
										final List<MAgentTask> tasks) {
									final List<PolicyWrapper> wrappers = new ArrayList<PolicyWrapper>();
									final Map<String, MAgentTask> taskByKey = SimpleUtils
											.getMap(tasks);

									final Set<String> incorrectPolicyNames = new HashSet<String>();
									for (final MPolicy policy : taskPolicies) {
										final String policyName = policy
												.getDisplayName()
												+ "("
												+ policy.getSource() + ")";
										final MAgentTask task = taskByKey
												.get(policy.getSource()
														.getKey());
										try {
											wrappers.add(new PolicyWrapper(
													policy, task, messages));
										} catch (final Exception e) {
											LOGGER.log(Level.SEVERE,
													"Incorrect policy "
															+ policyName, e);
											incorrectPolicyNames
													.add(policyName);
										}
									}
									if (!incorrectPolicyNames.isEmpty()) {
										AppUtils.showErrorMessage("Policies with following names "
												+ incorrectPolicyNames
														.toString()
												+ " are incorrect. Please contact the Administrator");
									}
									callback.onSuccess(wrappers);
								}
							});
				}
			}
		};
	}

	@Override
	public void onAfterRemovePolicyActionsTemplates(
			final AfterRemovePolicyActionsTemplatesEvent event) {
		reload(false);
	}

	@Override
	public void onAfterUpdatePolicyComponentTemplate(
			final AfterUpdatePolicyComponentTemplateEvent event) {
		reload(false);
	}

	@Override
	protected void onReveal() {
		searchText = null;
		getView().resetPolicyToolbar();
		reload(true);
	}

	public void openPolicyEditorDialog(final String policyKey) {
		policyConfigService.getPolicy(
				policyKey,
				new AutoNotifyingAsyncCallback<MPolicy>(messages
						.policyLoadingFail(), true) {
					@Override
					protected void success(final MPolicy policy) {
						if (policy == null) {
							final String message = messages
									.policyNotFound(policyKey);
							LOGGER.severe(message);
							AppUtils.showErrorMessage(message);
						} else {
							policyItemWidgetPresenter.setUpdateMode(policy);
							addToPopupSlot(policyItemWidgetPresenter, true);
						}
					}
				});
	}

	public void removePolicy(final PolicyWrapper item) {
		getView().removeItem(item);
	}

	public void removeTemplate(final MPolicyActionsTemplate template,
			final AsyncCallback<Void> callback) {
		policyComponentTemplateService.removeTemplate(template.getName(),
				template.getClass().getName(), callback);
	}

	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}

	public void updatePolicy(final PolicyWrapper item) {
		getView().updateItem(item);
	}
}

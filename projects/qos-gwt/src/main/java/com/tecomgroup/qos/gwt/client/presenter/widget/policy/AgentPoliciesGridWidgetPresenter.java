package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyTreeGridRow;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.presenter.widget.GridPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.service.PolicyConfigurationServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author smyshlyaev.s
 */

public class AgentPoliciesGridWidgetPresenter
		extends
			PresenterWidget<AgentPoliciesGridWidgetPresenter.MyView>
		implements
			UiHandlers,
			GridPresenter {

	public interface MyView
			extends
				View,
				HasUiHandlers<AgentPoliciesGridWidgetPresenter> {

		void initialize();

		void load();

		void removeItems(Set<String> keySet);

		void resetPolicyToolbar();

		void updateGrid(Map<String, List<PolicyWrapper>> arg);
	}
	private final PolicyConfigurationServiceAsync policyConfigService;
	private final TaskRetrieverAsync taskRetriever;

	private final QoSMessages messages;
	private Source source;

	private final PolicyActionsTemplatesEditorWidgetPresenter policyActionsTemplatesEditorWidgetPresenter;
	private final PolicyConditionsTemplatesEditorWidgetPresenter policyConditionsTemplatesEditorWidgetPresenter;

	private String searchText;

	@Inject
	public AgentPoliciesGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final PolicyConfigurationServiceAsync policyConfigService,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final TaskRetrieverAsync taskRetriever,
			final QoSMessages messages,
			final PolicyActionsTemplatesEditorWidgetPresenter policyActionsTemplatesEditorWidgetPresenter,
			final PolicyConditionsTemplatesEditorWidgetPresenter policyConditionsTemplatesEditorWidgetPresenter) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		this.policyActionsTemplatesEditorWidgetPresenter = policyActionsTemplatesEditorWidgetPresenter;
		this.policyConditionsTemplatesEditorWidgetPresenter = policyConditionsTemplatesEditorWidgetPresenter;
		this.policyConfigService = policyConfigService;
		this.taskRetriever = taskRetriever;
		this.messages = messages;
	}

	public void actionLoadPolicies(final FilterPagingLoadConfig config,
			final AsyncCallback<PagingLoadResult<PolicyTreeGridRow>> callback) {

		policyConfigService.getAgentPolicies(source.getKey(), getSearchText(),
				Order.desc("source.key"), config.getOffset(),
				config.getLimit(),
				new AutoNotifyingAsyncCallback<List<MPolicy>>() {

					@Override
					public void success(final List<MPolicy> policies) {
						policyConfigService.getAgentPoliciesCount(
								source.getKey(), getSearchText(),
								new AutoNotifyingAsyncCallback<Long>() {

									@Override
									protected void success(final Long count) {

										final Map<String, List<MPolicy>> groupedPolicies = SimpleUtils
												.groupBy(
														new SimpleUtils.Function<MPolicy, String>() {
															@Override
															public String apply(
																	final MPolicy policy) {
																return policy
																		.getSource()
																		.getKey();
															}
														}, policies);

										taskRetriever.getTasksByKeys(
												new HashSet<String>(
														groupedPolicies
																.keySet()),
												new AutoNotifyingAsyncCallback<List<MAgentTask>>() {

													@Override
													public void success(
															final List<MAgentTask> tasks) {
														final Map<String, List<PolicyWrapper>> policyWrappers = new TreeMap<String, List<PolicyWrapper>>();
														for (final MAgentTask task : tasks) {
															final String taskKey = task
																	.getKey();
															final List<MPolicy> policiesForTask = groupedPolicies
																	.get(taskKey);
															if (SimpleUtils
																	.isNotNullAndNotEmpty(policiesForTask)) {
																final List<PolicyWrapper> wrappers = new ArrayList<PolicyWrapper>();
																policyWrappers
																		.put(taskKey,
																				wrappers);
																for (final MPolicy policy : policiesForTask) {
																	wrappers.add(new PolicyWrapper(
																			policy,
																			task,
																			messages));
																}
															}
														}

														getView().updateGrid(
																policyWrappers);
														final PagingLoadResultBean<PolicyTreeGridRow> pagingLoadResult = new PagingLoadResultBean<PolicyTreeGridRow>(
																new ArrayList<PolicyTreeGridRow>(),
																count.intValue(),
																config.getOffset());
														callback.onSuccess(pagingLoadResult);
													}
												});
									}
								});
					}
				});
	}

	public void actionOpenActionsTemplatesEditor() {
		this.addToPopupSlot(policyActionsTemplatesEditorWidgetPresenter, true);
	}

	public void actionOpenConditionsTemplatesEditor() {
		this.addToPopupSlot(policyConditionsTemplatesEditorWidgetPresenter,
				true);
	}

	public void getAgentPoliciesCount(final AsyncCallback<Long> callback) {
		policyConfigService.getAgentPoliciesCount(source.getKey(),
				getSearchText(), callback);
	}

	private String getSearchText() {
		return searchText;
	}

	public Source getSource() {
		return source;
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().initialize();
	}

	@Override
	protected void onReveal() {
		searchText = null;
		getView().resetPolicyToolbar();
		reload(true);
	}

	@Override
	public void reload(final boolean force) {
		getView().load();
	}

	public void setAgentName(final String agentName) {
		source = Source.getAgentSource(agentName);
	}

	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}
}
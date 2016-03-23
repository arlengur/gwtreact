/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.AgentWrapper;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.SystemComponentStatisticServiceAsync;

/**
 * @author sviyazov.a
 * 
 */
public class AgentsGridWidgetPresenter
		extends
			AbstractLocalDataGridWidgetPresenter<AgentWrapper, AgentsGridWidgetPresenter.MyView> {

	public interface MyView
			extends
				AbstractLocalDataGridWidgetPresenter.MyView<AgentWrapper, AgentsGridWidgetPresenter> {
	}

	private final SystemComponentStatisticServiceAsync systemComponentStatisticService;

	private final QoSMessages messages;

	private final AgentServiceAsync agentService;

	private AgentTasksGridWidgetPresenter agentTasksGridWidgetPresenter;

	@Inject
	public AgentsGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final SystemComponentStatisticServiceAsync systemComponentStatisticService,
			final AgentServiceAsync agentService, final QoSMessages messages) {
		super(eventBus, view);
		this.systemComponentStatisticService = systemComponentStatisticService;
		this.agentService = agentService;
		this.messages = messages;
		getView().setUiHandlers(this);
	}

	public void actionDeleteAgents(final Set<String> agentKeysToDelete) {
		agentService.deleteAgents(
				agentKeysToDelete,
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages
						.agentsDeletionFail(), true) {

					@Override
					protected void success(final Void result) {
						getView().removeItems(agentKeysToDelete);
						AppUtils.showInfoMessage(messages
								.agentsDeletedSuccessfully());
					}
				});
	}

	public void actionLoadAgents(
			final AutoNotifyingAsyncLogoutOnFailureCallback<List<AgentWrapper>> callback) {
		systemComponentStatisticService
				.getAgentsStatistic(new AutoNotifyingAsyncLogoutOnFailureCallback<Map<String, AgentStatistic>>(
						messages.agentsLoadingFail(), true) {

					@Override
					protected void success(
							final Map<String, AgentStatistic> components) {
						final List<AgentWrapper> agents = new ArrayList<AgentWrapper>();
						for (final Entry<String, AgentStatistic> component : components
								.entrySet()) {
							final AgentStatistic componentStatistic = component
									.getValue();
							String description = null;
							if (componentStatistic.getComponent() instanceof MAgent) {
								final MAgent agent = (MAgent) componentStatistic
										.getComponent();
								description = agent.getDescription();
							}
							agents.add(new AgentWrapper(component.getKey(),
							componentStatistic.getComponent()
							.getDisplayName(), description,
							componentStatistic.getRegistrationTime(),
							componentStatistic.getLastResultTime(),
							getStateDisplayValue(componentStatistic.getState())));
						}
						callback.onSuccess(agents);
					}
				});
	}

	public void actionSelectAgent(final String agent) {
		agentTasksGridWidgetPresenter.loadTasks(agent);
	}

	public void clearAgentTasks() {
		agentTasksGridWidgetPresenter.clearTasks();
	}

	@Override
	public void reload(final boolean force) {
		actionLoadAgents(new AutoNotifyingAsyncLogoutOnFailureCallback<List<AgentWrapper>>(
				messages.agentsLoadingFail(), true) {
			@Override
			protected void success(final List<AgentWrapper> result) {
				getView().loadData(result);
			}
		});
	}

	public void setAgentTaskGridWidgetPresenter(
			final AgentTasksGridWidgetPresenter agentTaskGridWidgetPresenter) {
		this.agentTasksGridWidgetPresenter = agentTaskGridWidgetPresenter;
	}


	private String getStateDisplayValue(MAgent.AgentRegistrationState state)
	{
		if(state==null)
		{
			return "";
		}
		switch (state) {
			case NO_STATE:return "";
			case ACCEPTED:return messages.acceptedState();
			case SUCCESS:return messages.successfulState();
			case IN_PROGRESS:return messages.inProgressState();
			case PARTIALLY:return messages.partiallyState();
			case FAILED:return messages.failedState();
			default: return "";
		}

	}
}

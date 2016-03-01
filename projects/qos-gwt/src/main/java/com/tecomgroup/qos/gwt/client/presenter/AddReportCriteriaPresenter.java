/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.event.report.AddReportCriteriaEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentDialogPresenter;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;

/**
 * @author ivlev.e
 * 
 */
public class AddReportCriteriaPresenter extends AgentDialogPresenter {

	public static interface MyView extends AgentDialogView {
		void setTasks(List<MAgentTask> tasks);
	}

	private final TaskRetrieverAsync taskRetriever;

	/**
	 * @param eventBus
	 * @param view
	 * @param messages
	 * @param agentService
	 */
	@Inject
	public AddReportCriteriaPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final AgentServiceAsync agentService,
			final TaskRetrieverAsync taskRetriever) {
		super(eventBus, view, messages, agentService);
		this.taskRetriever = taskRetriever;
		getView().setUiHandlers(this);
	}

	public void addTasksByAgent(final String agentKey,
			final List<MAgentTask> tasks) {
		AppUtils.getEventBus().fireEvent(
				new AddReportCriteriaEvent(agentKey, tasks));
	}

	@Override
	public void agentSelected(final MAgent agent) {
		final AsyncCallback<List<MAgentTask>> callback = new AutoNotifyingAsyncCallback<List<MAgentTask>>(
				messages.tasksLoadingFail(), true) {

			@Override
			protected void success(final List<MAgentTask> result) {
				getView().<MyView> cast().setTasks(result);
			}
		};
		loadAgentTasks(agent.getKey(), callback);
	}

	private void loadAgentTasks(final String agentName,
			final AsyncCallback<List<MAgentTask>> callback) {
		taskRetriever.getAgentTasks(agentName, null, null, 0,
				Integer.MAX_VALUE, true, false, callback);
	}
}

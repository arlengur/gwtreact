/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.agent;

import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.TaskServiceAsync;

/**
 * @author sviyazov.a
 * 
 */
public class AgentTasksGridWidgetPresenter
		extends
			AbstractLocalDataGridWidgetPresenter<MAgentTask, AgentTasksGridWidgetPresenter.MyView> {

	public interface MyView
			extends
				AbstractLocalDataGridWidgetPresenter.MyView<MAgentTask, AgentTasksGridWidgetPresenter> {

		void clearStore();
	}

	private final TaskServiceAsync taskService;

	private final QoSMessages messages;

	private String currentAgent;

	@Inject
	public AgentTasksGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final TaskServiceAsync taskService,
			final QoSMessages messages) {
		super(eventBus, view);
		this.taskService = taskService;
		this.messages = messages;
		getView().setUiHandlers(this);
	}

	public void actionDeleteTasks(final Set<String> taskKeysToDelete) {
		taskService.deleteTasks(
				taskKeysToDelete,
				new AutoNotifyingAsyncCallback<Void>(messages
						.tasksDeletionFail(), true) {

					@Override
					protected void success(final Void result) {
						getView().removeItems(taskKeysToDelete);
						AppUtils.showInfoMessage(messages
								.tasksDeletedSuccessfully());
					}
				});
	}

	public void actionLoadTasks(
			final AutoNotifyingAsyncCallback<List<MAgentTask>> callback) {
		taskService.getAgentTasks(currentAgent, null, null, 0,
				Integer.MAX_VALUE, false, false, callback);
	}

	public void clearTasks() {
		currentAgent = null;
		getView().clearStore();
	}

	public boolean isAgentSelected() {
		return currentAgent != null;
	}

	public void loadTasks(final String agent) {
		currentAgent = agent;
		reload(true);
	}

	@Override
	public void reload(final boolean force) {
		actionLoadTasks(new AutoNotifyingAsyncCallback<List<MAgentTask>>(
				messages.tasksLoadingFail(), true) {
			@Override
			protected void success(final List<MAgentTask> result) {
				getView().loadData(result);
			}
		});
	}
}

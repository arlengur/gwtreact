/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.gwt.client.model.results.ResultRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataTreeGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.chart.AddBitrateToDashboarddWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.ResultRetrieverAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;

/**
 * @author ivlev.e
 * 
 */
public class AgentResultsGridWidgetPresenter
		extends
			AbstractLocalDataTreeGridWidgetPresenter<ResultRow, AgentResultsGridWidgetPresenter.MyView> {

    public interface MyView
			extends
				AbstractLocalDataTreeGridWidgetPresenter.MyView<ResultRow, AgentResultsGridWidgetPresenter> {

		void clear();
		void load();
		void updateGrid(List<MAgentTask> tasks,
				List<Map<String, Object>> results);
	}

	private static final long POLLING_INTERVAL = TimeConstants.MILLISECONDS_PER_MINUTE;

	private final ResultRetrieverAsync resultService;

	private Source source;

	private final TaskRetrieverAsync taskRetriever;

	private Timer agentResultsPolling;

	private List<MAgentTask> tasks;

    private final AddBitrateToDashboarddWidgetPresenter addBitrateToDashboardWidgetPresenter;

	@Inject
	public AgentResultsGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final ResultRetrieverAsync resultService,
			final TaskRetrieverAsync taskRetriever,
            final AddBitrateToDashboarddWidgetPresenter addBitrateToDashboardWidgetPresenter) {
		super(eventBus, view);
		this.resultService = resultService;
		this.taskRetriever = taskRetriever;
        this.addBitrateToDashboardWidgetPresenter = addBitrateToDashboardWidgetPresenter;
		getView().setUiHandlers(this);
	}

    public void displayAddAnalyticsToDashboardDialog(String key, String taskName) {
        addBitrateToDashboardWidgetPresenter.setTaskKey(key);
        addBitrateToDashboardWidgetPresenter.setTitle(taskName);
        addToPopupSlot(addBitrateToDashboardWidgetPresenter, false);
    }

	public void actionLoadResults(final List<MAgentTask> tasks,
			final AsyncCallback<List<Map<String, Object>>> callback) {

		final Map<String, Collection<?>> taskParameters = new HashMap<String, Collection<?>>();
		for (final MAgentTask task : tasks) {
			taskParameters.put(task.getKey(), null);
		}

		resultService.getLastResults(taskParameters, 0l, 1l, OrderType.DESC,
				callback);
	}

	public void actionLoadTasks(final AsyncCallback<List<MAgentTask>> callback) {
		taskRetriever.getAgentTasks(source.getKey(), null, null, 0,
				Integer.MAX_VALUE, true, new AsyncCallback<List<MAgentTask>>() {

					@Override
					public void onFailure(final Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(final List<MAgentTask> loadedTasks) {
						tasks = loadedTasks;
						callback.onSuccess(loadedTasks);
					}
				});
	}

	public void actionStartResultPolling(final List<MAgentTask> tasks) {
		agentResultsPolling = new Timer() {

			@Override
			public void run() {
				AgentResultsGridWidgetPresenter.this.onResultPolling(tasks);
			}
		};
		agentResultsPolling.scheduleRepeating((int) POLLING_INTERVAL);
	}

	@Override
	public void onHide() {
		stopResultPolling();
		tasks = null;
	}

	public void onResultPolling(final List<MAgentTask> tasks) {
		actionLoadResults(tasks,
				new AutoNotifyingAsyncLogoutOnFailureCallback<List<Map<String, Object>>>() {

					@Override
					protected void success(
							final List<Map<String, Object>> result) {
						getView().updateGrid(tasks, result);
					}
				});
	}

	@Override
	public void reload(final boolean force) {
		if (tasks == null || force) {
			getView().clear();
			// load tasks, current results and start results polling
			getView().load();
		} else {
			// load current results
			onResultPolling(tasks);
			// start results polling
			actionStartResultPolling(tasks);
		}
	}

	public void setAgentName(final String agentName) {
		source = Source.getAgentSource(agentName);
	}

	public void stopResultPolling() {
		if (agentResultsPolling != null) {
			agentResultsPolling.cancel();
		}
	}
}

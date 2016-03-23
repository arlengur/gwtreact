/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DisabledParameterLabelProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DisabledTaskLabelProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ParameterModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.ParameterProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.TaskProperties;
import com.tecomgroup.qos.service.TaskRetrieverAsync;

/**
 * @author abondin
 * 
 */
public class ParameterSelectorWidget {
	protected final TaskProperties taskProps = GWT.create(TaskProperties.class);

	protected final ParameterProperties parameterProperties = GWT
			.create(ParameterProperties.class);

	private final TaskRetrieverAsync taskRetriever;

	protected final CustomComboBox<MAgentTask> taskControl;

	protected final CustomComboBox<MResultParameterConfiguration> paramControl;

	public static Logger LOGGER = Logger
			.getLogger(ParameterSelectorWidget.class.getName());

	private final QoSMessages messages;

	private boolean onlyActive = true;

	@Inject
	public ParameterSelectorWidget(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final TaskRetrieverAsync taskRetriever, final QoSMessages messages) {
		this.messages = messages;
		this.taskRetriever = taskRetriever;

		final ListStore<MAgentTask> taskStore = new ListStore<MAgentTask>(
				taskProps.modelKey());
		taskStore.addSortInfo(new StoreSortInfo<MAgentTask>(taskProps
				.displayName(), SortDir.ASC));
		final DisabledTaskLabelProvider taskLabelProvider = new DisabledTaskLabelProvider(
				messages);
		taskControl = new CustomComboBox<MAgentTask>(taskStore,
				taskLabelProvider, appearanceFactoryProvider.get()
						.triggerFieldAppearance());
		taskControl.setTypeAhead(true);
		taskControl.setEditable(true);
		taskControl.setTriggerAction(TriggerAction.ALL);
		taskControl.setForceSelection(true);
		taskControl.setUpdateValueOnSelection(false);

		final ListStore<MResultParameterConfiguration> paramStore = new ListStore<MResultParameterConfiguration>(
				new ParameterModelKeyProvider());
		paramStore
				.addSortInfo(new StoreSortInfo<MResultParameterConfiguration>(
						parameterProperties.displayFormat(), SortDir.ASC));
		final DisabledParameterLabelProvider parameterLabelProvider = new DisabledParameterLabelProvider(
				messages);
		paramControl = new CustomComboBox<MResultParameterConfiguration>(
				paramStore, parameterLabelProvider, appearanceFactoryProvider
						.get().triggerFieldAppearance());
		paramControl.setTypeAhead(true);
		paramControl.setEditable(true);
		paramControl.setTriggerAction(TriggerAction.ALL);
		paramControl.setForceSelection(true);

		paramControl.setUpdateValueOnSelection(true);
		paramControl.setValidateOnBlur(false);

		initListeners();
	}
	public void disableControls() {
		taskControl.setEnabled(false);
		paramControl.setEnabled(false);
	}

	public void enableControls() {
		taskControl.setEnabled(true);
		paramControl.setEnabled(true);
	}

	/**
	 * @return the paramControl
	 */
	public ComboBox<MResultParameterConfiguration> getParamControl() {
		return paramControl;
	}

	/**
	 * @return the taskControl
	 */
	public ComboBox<MAgentTask> getTaskControl() {
		return taskControl;
	}

	protected void initListeners() {

		taskControl.addSelectionHandler(new SelectionHandler<MAgentTask>() {

			@Override
			public void onSelection(final SelectionEvent<MAgentTask> event) {
				updateTask(event.getSelectedItem());
			}
		});
		taskControl.addValueChangeHandler(new ValueChangeHandler<MAgentTask>() {
			@Override
			public void onValueChange(final ValueChangeEvent<MAgentTask> event) {
				updateTask(event.getValue());
			}
		});
	}

	public void reset() {
		taskControl.reset();
		paramControl.reset();
		taskControl.getStore().clear();
		paramControl.getStore().clear();
		updateEmptyText(true);
		disableControls();
	}

	public void selectAgent(final MAgent agent) {
		taskControl.getStore().clear();
		taskControl.reset();
		paramControl.getStore().clear();
		paramControl.reset();
		disableControls();
		if (agent != null) {
			taskRetriever.getAgentTasks(agent.getKey(), null, null, 0,
					Integer.MAX_VALUE, true, onlyActive,
					new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>() {
						@Override
						protected void failure(final Throwable caught) {
							LOGGER.log(Level.SEVERE, "Cannot select agent",
									caught);
						}
						@Override
						protected void success(final List<MAgentTask> result) {
							taskControl.reset();
							taskControl.getStore().addAll(result);
							final boolean hasResults = result != null
									&& !result.isEmpty();
							taskControl.setEnabled(hasResults);
							updateEmptyText(hasResults);
						}
					});
		} else {
			taskControl.reset();
			paramControl.reset();
		}
	}

	/**
	 * Flag to show only active (not disabled) tasks and parameters.
	 * 
	 * @param onlyActive
	 *            the onlyActive to set
	 */
	public void setOnlyActive(final boolean onlyActive) {
		this.onlyActive = onlyActive;
	}

	private void updateEmptyText(final boolean hasResults) {
		if (hasResults) {
			taskControl.setEmptyText(messages.emptyTaskText());
			paramControl.setEmptyText(messages.emptyParameterText());
		} else {
			taskControl.setEmptyText(messages.noTasksText());
			paramControl.setEmptyText("");
		}

	}

	protected void updateTask(final MAgentTask task) {
		if (((taskControl.getValue() == null) && (task != null))
				|| ((taskControl.getValue() != null) && !taskControl.getValue()
						.equals(task))) {
			paramControl.setEnabled(false);
			paramControl.getStore().clear();
			paramControl.reset();
			final boolean enabled = task.getResultConfiguration() != null
					&& task.getResultConfiguration()
							.getParameterConfigurations() != null
					&& !task.getResultConfiguration()
							.getParameterConfigurations().isEmpty();
			if (enabled) {
				paramControl.getStore().addAll(
						task.getResultConfiguration()
								.getParameterConfigurations(onlyActive));
				paramControl.setEnabled(true);
				paramControl.setEmptyText(messages.emptyParameterText());
			} else {
				paramControl.setEmptyText(messages.noParametersText());
			}
		}
	}

}

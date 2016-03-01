/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AddReportCriteriaPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DisabledTaskValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.TaskProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractAgentDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectorWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomCheckBoxSelectionModel;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;

/**
 * @author ivlev.e
 * 
 */
public class AddReportCriteriaView extends AbstractAgentDialogView
		implements
			AddReportCriteriaPresenter.MyView {

	protected class UIFieldHolder {

		@UiField(provided = true)
		public Grid<MAgentTask> taskGrid;

		@UiField(provided = true)
		public TextButton doneButton;

	}

	interface ViewUiBinder extends UiBinder<VBoxLayoutContainer, UIFieldHolder> {
	}

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	protected QoSMessages messages;

	private final VBoxLayoutContainer leftContainer;

	private final TaskProperties taskProps = GWT.create(TaskProperties.class);

	private final UIFieldHolder uiFieldHolder = new UIFieldHolder();

	private final int TASK_GRID_MIN_WIDTH = 100;

	/**
	 * @param eventBus
	 * @param appearanceFactoryProvider
	 * @param agentSelectorWidget
	 * @param agentGisWidget
	 */
	@Inject
	public AddReportCriteriaView(final EventBus eventBus,
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final AgentSelectorWidget agentSelectorWidget,
			final AgentGisWidget agentGisWidget) {
		super(eventBus, appearanceFactoryProvider, agentSelectorWidget,
				agentGisWidget);
		this.messages = messages;
		initializeGrid(appearanceFactoryProvider.get(), messages);

		uiFieldHolder.doneButton = new TextButton(new TextButtonCell(
				appearanceFactoryProvider.get()
						.<String> buttonCellHugeAppearance()));
		uiFieldHolder.doneButton.setText(messages.actionDone());

		leftContainer = UI_BINDER.createAndBindUi(uiFieldHolder);
		setLeftContent(leftContainer);
		initializeListeners();
	}

	@Override
	public Widget asWidget() {
		return getDialog();
	}

	private ColumnModel<MAgentTask> createColumnModel(
			final AppearanceFactory af, final QoSMessages messages,
			final CheckBoxSelectionModel<MAgentTask> sm) {
		final List<ColumnConfig<MAgentTask, ?>> list = new ArrayList<ColumnConfig<MAgentTask, ?>>();
		final ColumnConfig<MAgentTask, String> taskDisplayName = new ColumnConfig<MAgentTask, String>(
				new DisabledTaskValueProvider(messages, taskProps.displayName()
						.getPath()), 50, messages.task());
		list.add(sm.getColumn());
		list.add(taskDisplayName);

		return new ColumnModel<MAgentTask>(list);
	}

	@Override
	protected BorderLayoutContainer.BorderLayoutData getWestContainerLayoutData() {
		final BorderLayoutContainer.BorderLayoutData layoutData = super
				.getWestContainerLayoutData();

		layoutData.setCollapsible(true);
		layoutData.setCollapseMini(true);
		layoutData.setSplit(true);
		layoutData.setMinSize(TASK_GRID_MIN_WIDTH);

		return layoutData;
	}

	private void initializeGrid(final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		final IdentityValueProvider<MAgentTask> identity = new IdentityValueProvider<MAgentTask>();
		final CheckBoxSelectionModel<MAgentTask> selectionModel = new CustomCheckBoxSelectionModel<MAgentTask>(
				identity,
				appearanceFactory.<MAgentTask> checkBoxColumnAppearance());
		selectionModel.setSelectionMode(SelectionMode.SIMPLE);
		uiFieldHolder.taskGrid = new Grid<MAgentTask>(
				new ListStore<MAgentTask>(taskProps.modelKey()),
				createColumnModel(appearanceFactory, messages, selectionModel),
				new CustomGridView<MAgentTask>(
						appearanceFactory.gridStandardAppearance(),
						appearanceFactory.columnHeaderAppearance()));
		uiFieldHolder.taskGrid.setSelectionModel(selectionModel);
		uiFieldHolder.taskGrid
				.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		uiFieldHolder.taskGrid.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
		uiFieldHolder.taskGrid.getView().setAutoFill(true);
	}

	private void initializeListeners() {
		uiFieldHolder.doneButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(final SelectEvent event) {
				final MAgent currentAgent = getCurrentAgent();
				if (currentAgent != null) {
					getUiHandlers().<AddReportCriteriaPresenter> cast()
							.addTasksByAgent(
									currentAgent.getKey(),
									uiFieldHolder.taskGrid.getSelectionModel()
											.getSelectedItems());
				}
				hide();
			}
		});
	}

	@Override
	public void select(final MAgent agent) {
		super.select(agent);
		getUiHandlers().agentSelected(agent);
	}

	@Override
	public void setTasks(final List<MAgentTask> tasks) {
		uiFieldHolder.taskGrid.getStore().clear();
		uiFieldHolder.taskGrid.getStore().addAll(tasks);
	}
}

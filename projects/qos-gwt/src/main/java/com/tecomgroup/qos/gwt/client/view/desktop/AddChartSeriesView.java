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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AddChartSeriesPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ChartSeriesValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectorWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell.ButtonedGroupingCellHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ChartSelectionWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ParameterSelectorWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;

/**
 * @author abondin
 * 
 */
public class AddChartSeriesView
		extends
			SenchaPopupView<AddChartSeriesPresenter>
		implements
			AddChartSeriesPresenter.MyView,
			ClientConstants {

	interface ViewUiBinder extends UiBinder<Widget, AddChartSeriesView> {
	}

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	@UiField(provided = true)
	protected Dialog dialog;

	@UiField(provided = true)
	protected ContentPanel agentSelectorPanel;
	@UiField(provided = true)
	protected ContentPanel agentGisPanel;

	@UiField(provided = true)
	protected FramedPanel menuPanel;

	private final AgentSelectorWidget agentSelectorWidget;
	private final AgentGisWidget agentGisWidget;

	private final ParameterSelectorWidget parameterSelectorWidget;

	@UiField(provided = true)
	protected BorderLayoutContainer container;

	@UiField(provided = true)
	protected ComboBox<MAgentTask> taskSelector;
	@UiField(provided = true)
	protected ComboBox<MResultParameterConfiguration> parameterSelector;

	@UiField(provided = true)
	protected ComboBox<String> chartSelector;
	@UiField(provided = true)
	protected TextField newChartField;
	@UiField(provided = true)
	protected Radio selectChartRadio;
	@UiField(provided = true)
	protected Radio newChartRadio;

	@UiField(provided = true)
	protected TextButton addSeriesButton;

	@UiField(provided = true)
	protected TextButton buildChartButton;

	@UiField(provided = true)
	protected Grid<MChartSeries> grid;

	private final ChartSelectionWidget chartSelectionWizard;

	/**
	 * @param parameterSelectorWidget
	 * @param appearanceFactoryProvider
	 */
	@Inject
	public AddChartSeriesView(final EventBus eventBus,
			final QoSMessages messages,
			final AgentSelectorWidget agentSelectorWidget,
			final AgentGisWidget agentGisWidget,
			final ChartSelectionWidget chartSelectionWizard,
			final ParameterSelectorWidget parameterSelectorWidget,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		super(eventBus);
		this.agentSelectorWidget = agentSelectorWidget;
		this.agentGisWidget = agentGisWidget;
		this.chartSelectionWizard = chartSelectionWizard;
		this.parameterSelectorWidget = parameterSelectorWidget;
		parameterSelectorWidget.setOnlyActive(false);
		initialize(appearanceFactoryProvider, messages);
		initializeListeners();
		UI_BINDER.createAndBindUi(this);
	}
	@Override
	public Dialog asWidget() {
		return dialog;
	}

	private ColumnModel<MChartSeries> createNewChartSeriesColumnModel(
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		final List<ColumnConfig<MChartSeries, ?>> list = new ArrayList<ColumnConfig<MChartSeries, ?>>();
		final ColumnConfig<MChartSeries, String> info = new ColumnConfig<MChartSeries, String>(
				new ChartSeriesValueProvider(messages));
		info.setCell(new ButtonedGroupingCell<String>(appearanceFactory
				.<String> buttonedGroupingCellAppearance(),
				new ButtonedGroupingCellHandler() {
					@Override
					public void onRemovedButtonPressed(
							final List<String> modelKeys) {
						getUiHandlers().actionRemoveChartSeries(modelKeys);
					}
				}));
		list.add(info);
		return new ColumnModel<MChartSeries>(list);
	}
	private void initialize(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		container = new BorderLayoutContainer(appearanceFactoryProvider.get()
				.borderLayoutAppearance());
		menuPanel = new FramedPanel(appearanceFactoryProvider.get()
				.framedPanelAppearance());
		menuPanel.setHeaderVisible(false);
		menuPanel.setBorders(false);
		menuPanel.setBodyBorder(false);

		agentSelectorPanel = agentSelectorWidget.asWidget();

		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.addStyleName(appearanceFactoryProvider.get().resources()
				.css().addChartSeriesSelectAgentPanel());
		simplePanel.add(agentGisWidget.asWidget());

		agentGisPanel = new ContentPanel();
		agentGisPanel.add(simplePanel);
		agentGisPanel.setBorders(false);
		agentGisPanel.setBodyBorder(false);
		agentGisPanel.setHeaderVisible(false);

		taskSelector = parameterSelectorWidget.getTaskControl();
		parameterSelector = parameterSelectorWidget.getParamControl();

		chartSelector = chartSelectionWizard.getSelectChartCombo();
		newChartRadio = chartSelectionWizard.getNewChartRadio();
		selectChartRadio = chartSelectionWizard.getSelectChartRadio();
		newChartField = chartSelectionWizard.getNewChartField();

		addSeriesButton = new TextButton(new TextButtonCell(
				appearanceFactoryProvider.get()
						.<String> buttonCellHugeAppearance()));
		addSeriesButton.setText(messages.addSeries());
		addSeriesButton.setIcon(appearanceFactoryProvider.get().resources()
				.addButton());

		buildChartButton = new TextButton(new TextButtonCell(
				appearanceFactoryProvider.get()
						.<String> buttonCellHugeAppearance()));
		buildChartButton.setText(messages.buildChart());

		dialog = new Dialog(appearanceFactoryProvider.get().dialogAppearance());
		dialog.setPredefinedButtons();

		initializeGrid(appearanceFactoryProvider, messages);
	}

	/**
	 * @param appearanceFactoryProvider
	 * @param messages
	 */
	private void initializeGrid(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		grid = new Grid<MChartSeries>(new ListStore<MChartSeries>(
				new ModelKeyProvider<MChartSeries>() {

					@Override
					public String getKey(final MChartSeries item) {
						return item.getUniqueKey();
					}
				}), createNewChartSeriesColumnModel(
				appearanceFactoryProvider.get(), messages),
				new GridView<MChartSeries>(appearanceFactoryProvider.get()
						.gridAppearance()));
		grid.setHideHeaders(true);
		grid.addStyleName("qosGridStyle");
		grid.getView().setAutoFill(true);
		grid.setWidth(DEFAULT_FIELD_WIDTH);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	/**
	 * 
	 */
	private void initializeListeners() {
		addSeriesButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(final SelectEvent event) {
				final String chartName = chartSelectionWizard.getChart();
				// please do not use ComboBox.getCurrentValue() here because
				// it will return wrong value when control contains objects
				// with the same display names
				final MAgentTask task = taskSelector.getValue();
				final MResultParameterConfiguration parameter = parameterSelector
						.getValue();

				getUiHandlers()
						.actionAddChartSeries(task, parameter, chartName);
			}
		});
		buildChartButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(final SelectEvent event) {
				getUiHandlers().actionCommit();
				asWidget().hide();
				getUiHandlers().actionBuildCharts();
			}
		});
	}

	@Override
	public void loadAgents(final List<MAgent> agents) {
		agentSelectorWidget.loadAllAgents(agents);
		agentGisWidget.updateAgents(agents);

		MAgent agent = agentSelectorWidget.getSelectedAgent();

		if (agent != null) {
			agentSelectorWidget.agentSelected(agent);
		} else {
			parameterSelectorWidget.reset();
		}
	}

	@Override
	public void loadCharts() {
		chartSelectionWizard.loadCharts(ChartResultUtils
				.getAllChartNames(getUiHandlers().getAllSeries()));
	}

	@Override
	public void postInitialize() {
		chartSelectionWizard.addListener(getUiHandlers());
		agentSelectorWidget.addListener(getUiHandlers());
		agentSelectorWidget.addListener(agentGisWidget);
		agentGisWidget.addListener(getUiHandlers());
		agentGisWidget.addListener(agentSelectorWidget);
	}

	@Override
	public void select(final MAgent agent, final MAgentTask task,
			final MResultParameterConfiguration parameter, final String chart) {
		agentSelectorWidget.agentSelected(agent);
		parameterSelectorWidget.selectAgent(agent);
		// TODO
	}

	@Override
	public void updateSeries(final boolean updateChartWizard) {
		final String chartName = chartSelectionWizard.getChart();
		grid.getStore().clear();
		if (chartName != null) {
			final List<MChartSeries> series = ChartResultUtils
					.findSeriesByChartName(getUiHandlers().getAllSeries(),
							chartName);
			if (series != null) {
				grid.getStore().addAll(series);
			}
		}
		if (updateChartWizard) {
			chartSelectionWizard.addChart(chartName);
		}
	}

	@Override
	public String validate() {
		final String status = chartSelectionWizard.validate();
		return status;
	}
}

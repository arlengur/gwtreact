/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.validation.ValidationException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterHeaderContextMenuHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.TableResultPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DynamicModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ResultParameterValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.StorageKeyValueProviderFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.DoubleAsBooleanDataPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.DoubleDataPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeIntervalWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar.UpdateButtonHandler;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class DefaultTableResultsView
		extends
			ViewWithUiHandlers<TableResultPresenter>
		implements
			TableResultPresenter.TableResultsView {

	interface ViewUiBinder extends UiBinder<Widget, DefaultTableResultsView> {
	}

	public static Logger LOGGER = Logger
			.getLogger(DefaultTableResultsView.class.getName());

	public static final String RESULT_TIME = "resultTime";

	private final Widget widget;

	private final QoSMessages messages;

	private ListStore<Map<String, Object>> store;

	protected Grid<Map<String, Object>> grid;

	protected GridFilters<Map<String, Object>> gridFilters;

	@UiField(provided = true)
	protected BorderLayoutContainer borderLayoutContainer;

	@UiField(provided = true)
	protected FramedPanel timeFramedPanel;

	private BorderLayoutData centerData;

	@UiField(provided = true)
	protected CssFloatLayoutContainer toolbarContainer;

	protected TextButton exportButton;

	@UiField
	protected Label chartName;

	@UiField
	protected Label resultsTableTimeWidgetHeader;

	protected final HorizontalTimeToolbar timeToolbar;

	private final DateTimeIntervalWidget dateTimeIntervalWidget;

	private final NumberFormat numberFormatter = NumberFormat
			.getFormat(SimpleUtils.NUMBER_FORMAT);

	private final AppearanceFactory appearanceFactory;

	private final LocalizedFilterFactory filterFactory;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private ProgressBarMessageBox resultsExportProgressDialog;

	private String agentTimeZone;

	@Inject
	public DefaultTableResultsView(final QoSMessages messages,
			final HorizontalTimeToolbar timeToolbar,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final LocalizedFilterFactory filterFactory) {
		this.timeToolbar = timeToolbar;
		this.timeToolbar.setup(Type.DAY, Type.WEEK, Type.MONTH);
		dateTimeIntervalWidget = timeToolbar.getDateTimeIntervalWidget();

		appearanceFactory = appearanceFactoryProvider.get();
		this.messages = messages;
		this.filterFactory = filterFactory;
		initialize();
		initializeListeners();

		widget = UI_BINDER.createAndBindUi(this);
		chartName.addStyleName(appearanceFactory.resources().css()
				.chartNameLabel());
		resultsTableTimeWidgetHeader.addStyleName(appearanceFactory.resources()
				.css().resultsTableTimeWidgetHeader());
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public void closeResultsExportProgressDialog() {
		if (resultsExportProgressDialog != null) {
			resultsExportProgressDialog.hide();
		}
	}

	private ColumnConfig<Map<String, Object>, String> createDateColumn() {
		final ColumnConfig<Map<String, Object>, String> dateColumn = new ColumnConfig<Map<String, Object>, String>(
				new ResultParameterValueProvider<String>(
						SimpleUtils.DATE_PARAMETER_NAME), 50,
				messages.monitoringTimeAxis());
		return dateColumn;
	}

	@Override
	public void createDynamicGrid(
			final Map<MAgentTask, List<MResultParameterConfiguration>> columnInfo) {
		gridFilters = new GridFilters<Map<String, Object>>();
		initializeGrid(columnInfo);
		initializeFilters();
	}

	private Widget createExportControls() {
		final CssFloatLayoutContainer exportContainer = new CssFloatLayoutContainer();

		exportContainer.getElement().setMargins(new Margins(9, 5, 9, 0));
		exportButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> defaultCellAppearance()));
		exportButton.setText(messages.export());
		exportButton.setIcon(appearanceFactory.resources()
				.exportToExcelButton());
		exportButton.setTitle(messages.exportToExcel());
		exportButton.setWidth(160);

		final Menu exportMenu = new Menu(appearanceFactory.menuAppearance());
		exportMenu.setWidth(160);
		final MenuItem tableExport = new MenuItem(
				appearanceFactory.menuItemAppearance(),
				appearanceFactory.itemAppearance());
		tableExport.setText(messages.tableData());
		tableExport.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(final SelectionEvent<Item> event) {
				getUiHandlers().actionExportResults(false);
			}
		});
		final MenuItem rawExport = new MenuItem(
				appearanceFactory.menuItemAppearance(),
				appearanceFactory.itemAppearance());
		rawExport.setText(messages.rawData());
		rawExport.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(final SelectionEvent<Item> event) {
				getUiHandlers().actionExportResults(true);
			}
		});
		exportMenu.add(tableExport);
		exportMenu.add(rawExport);
		exportButton.setMenu(exportMenu);

		exportContainer.add(exportButton);
		return exportContainer;
	}

	private ColumnConfig<Map<String, Object>, Double> createNumberAsBooleanValueColumn(
			final String taskStorageKey, final String displayName) {
		final ValueProvider<Map<String, Object>, Boolean> booleanValueProvider = StorageKeyValueProviderFactory
				.getDoubleAsBooleanValueProvider(taskStorageKey);
		final ValueProvider<Map<String, Object>, Double> doubleValueProvider = StorageKeyValueProviderFactory
				.getDoubleValueProvider(taskStorageKey);
		final ColumnConfig<Map<String, Object>, Double> column = new ColumnConfig<Map<String, Object>, Double>(
				doubleValueProvider, 50, displayName);
		final Filter<Map<String, Object>, Boolean> filter = filterFactory
				.<Map<String, Object>> createBooleanFilter(booleanValueProvider);
		column.setCell(new DoubleAsBooleanDataPropertyEditor.DoubleAsBooleanPropertyCell(
				messages, true));
		gridFilters.addFilter(filter);
		return column;
	}

	private ColumnConfig<Map<String, Object>, Double> createNumericValueColumn(
			final String taskStorageKey, final String displayName) {
		final ValueProvider<Map<String, Object>, Double> valueProvider = StorageKeyValueProviderFactory
				.getDoubleValueProvider(taskStorageKey);
		final ColumnConfig<Map<String, Object>, Double> column = new ColumnConfig<Map<String, Object>, Double>(
				valueProvider, 100, displayName);
		final NumericFilter<Map<String, Object>, Double> numericFilter = filterFactory
				.<Map<String, Object>, Double> createNumericFilter(
						valueProvider, new DoublePropertyEditor());
		gridFilters.addFilter(numericFilter);
		column.setCell(new DoubleDataPropertyEditor.DoublePropertyCell(
				numberFormatter, messages, true));
		return column;
	}

	private ColumnConfig<Map<String, Object>, ?> createParameterValueColumn(
			final MResultParameterConfiguration parameter, final MAgentTask task) {
		final ColumnConfig<Map<String, Object>, ?> column;
		final String storageKey = parameter.getParameterIdentifier()
				.createTaskStorageKey(task.getKey());
		final String columnLabel = LabelUtils.createSeriesLabel(task
				.getModule().getAgent(), task, parameter);
		switch (parameter.getType()) {
			case BOOL :
				column = createNumberAsBooleanValueColumn(storageKey,
						columnLabel);
				break;
			default :
				column = createNumericValueColumn(storageKey, columnLabel);
				break;
		}

		column.setSortable(false);
		return column;
	}

	private ColumnModel<Map<String, Object>> createTableColumnModel(
			final Map<MAgentTask, List<MResultParameterConfiguration>> columnInfo) {
		final List<ColumnConfig<Map<String, Object>, ?>> list = new ArrayList<ColumnConfig<Map<String, Object>, ?>>();

		final ColumnConfig<Map<String, Object>, String> dateColumn = createDateColumn();
		list.add(dateColumn);
		for (final Entry<MAgentTask, List<MResultParameterConfiguration>> entry : columnInfo
				.entrySet()) {
			for (final MResultParameterConfiguration parameter : entry
					.getValue()) {
				final ColumnConfig<Map<String, Object>, ?> column = createParameterValueColumn(
						parameter, entry.getKey());
				list.add(column);
			}
		}

		return new ColumnModel<Map<String, Object>>(list);
	}

	@Override
	public Date getEndDate() {
		return dateTimeIntervalWidget.getEndDate();
	}

	@Override
	public List<String> getGridColumnsTitles() {
		final List<String> gridTitles = new ArrayList<String>();

		final ColumnModel<Map<String, Object>> columnModel = grid
				.getColumnModel();
		for (int index = 0; index < columnModel.getColumnCount(); index++) {
			gridTitles.add(columnModel.getColumn(index).getHeader().asString());
		}

		return gridTitles;
	}

	@Override
	public String getSelectedTimeZoneLabel() {
		return dateTimeIntervalWidget.getTimeZoneLabel();
	}

	@Override
	public Date getStartDate() {
		return dateTimeIntervalWidget.getStartDate();
	}

	@Override
	public String getTimeZone() {
		return dateTimeIntervalWidget.getTimeZone();
	}

	@Override
	public int getTimeZoneOffset() {
		return dateTimeIntervalWidget.getTimeZoneOffset();
	}

	@Override
	public TimeZoneType getTimeZoneType() {
		return dateTimeIntervalWidget.getTimeZoneType();
	}

	@Override
	public void initDateParameter(final String timeZone,
								  final TimeZoneType timeZoneType,
								  final Type timeIntervalType,
								  final Long startDate,
								  final Long endDate) {
		if (timeZoneType.equals(TimeZoneType.AGENT)) {
			dateTimeIntervalWidget.enableAgentTimeZone(timeZone);
		}
		dateTimeIntervalWidget.setTimeZone(timeZoneType, timeZone);
		dateTimeIntervalWidget.setTimeIntervalType(timeIntervalType);
		if (Type.CUSTOM.equals(timeIntervalType)) {
			dateTimeIntervalWidget.setTimeInterval(new Date(startDate), new Date(endDate));
		}
		agentTimeZone = timeZone;
	}

	private void initialize() {
		borderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());

		toolbarContainer = new CssFloatLayoutContainer();

		centerData = new BorderLayoutData();
		centerData.setMargins(new Margins(0, 9, 0, 9));

		timeFramedPanel = new FramedPanel(
				appearanceFactory.lightestFramedPanelAppearance());
		timeFramedPanel.setHeaderVisible(false);
		timeFramedPanel.setBorders(false);
		timeFramedPanel.setBodyBorder(false);
		timeFramedPanel.setAllowTextSelection(false);

		toolbarContainer.add(timeToolbar);

		final Widget exportControls = createExportControls();
		toolbarContainer.add(exportControls);

		exportControls.getElement().<XElement> cast().getStyle()
				.setFloat(Style.Float.RIGHT);

		store = new ListStore<Map<String, Object>>(new DynamicModelKeyProvider(
				SimpleUtils.DATE_PARAMETER_NAME));
	}

	private void initializeFilters() {
		gridFilters.initPlugin(grid);
		gridFilters.setLocal(true);
		gridFilters.setUpdateBuffer(TimeConstants.FILTER_UPDATE_DELAY);
		grid.addHeaderContextMenuHandler(new LocalizedFilterHeaderContextMenuHandler(
				messages));
	}

	private void initializeGrid(
			final Map<MAgentTask, List<MResultParameterConfiguration>> columnInfo) {
		grid = new Grid<Map<String, Object>>(store,
				createTableColumnModel(columnInfo),
				new CustomGridView<Map<String, Object>>(
						appearanceFactory.gridStandardAppearance(),
						appearanceFactory.columnHeaderAppearance()));
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.getView().setAutoFill(true);
		grid.getView().setColumnLines(true);
		grid.getView().setStripeRows(true);
		borderLayoutContainer.setCenterWidget(grid, centerData);
		borderLayoutContainer.forceLayout();
	}

	private void initializeListeners() {
		timeToolbar.setUpdateButtonHandler(new UpdateButtonHandler() {

			@Override
			public void onUpdateButtonPressed(final SelectEvent event) {
				try {
					dateTimeIntervalWidget.validate();
					getUiHandlers().doUpdateAction();
				} catch (final ValidationException e) {
					AppUtils.showErrorMessage(messages
							.invalidDateTimeInterval() + "\n" + e.getMessage());
				}
			}
		});
	}

	@Override
	public void openResultsExportProgressDialog(final boolean rawData) {
		final String title = rawData
				? messages.exportRawDataDialogTitle()
				: messages.exportAggregatedDataDialogTitle();

		resultsExportProgressDialog = new ProgressBarMessageBox(title,
				messages.exportResultsLingeringTask()) {

			@Override
			protected void onButtonPressed(final TextButton button) {
				if (button == getButtonById(PredefinedButton.CANCEL.name())) {
					getUiHandlers().cancelResultsExport();
					hide();
				}
			}
		};
		resultsExportProgressDialog.setProgressText(messages.initializing());
		resultsExportProgressDialog.show();
	}

	@Override
	public void setChartName(final String chartName) {
		this.chartName.setText(chartName);
	}

	@Override
	public void setData(final List<Map<String, Object>> data) {
		for (Map<String, Object> m:data){
			if (m.containsKey(RESULT_TIME)){
				m.put(RESULT_TIME, DateTimeWidget.formatDateTimeWithSeconds((Date) m.get(RESULT_TIME), -getTimeZoneOffset()));
			}
		}
		grid.getStore().clear();
		grid.getStore().addAll(data);
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot.equals(messages.chart())) {
			final BorderLayoutData layoutData = new BorderLayoutData(350);
			layoutData.setMargins(new Margins(9, 9, 9, 9));
			borderLayoutContainer.setSouthWidget(content, layoutData);
		}
	}

	@Override
	public void updateResultsExportProgress(final byte percent,
			final String progressText) {
		if (resultsExportProgressDialog != null) {
			resultsExportProgressDialog.updateProgress(percent, progressText);
		}
	}

	@Override
	public void updateResultsExportProgressText(final String progressText) {
		if (resultsExportProgressDialog != null) {
			resultsExportProgressDialog.getProgressBar().updateText(
					progressText);
		}
	}
}

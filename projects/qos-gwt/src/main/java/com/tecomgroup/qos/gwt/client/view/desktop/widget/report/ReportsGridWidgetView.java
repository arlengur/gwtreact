/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.gwt.client.event.report.DownloadReportEvent;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.ReportsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DurationValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.SystemComponentValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.filter.MAlertMapper;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSeverityPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertReportProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridViewWithTemplates;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ImageAnchor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AlertSeverityToolbar;

/**
 * @author ivlev.e
 *
 */
public class ReportsGridWidgetView
		extends
		AbstractRemoteDataGridViewWithTemplates<MAlertReport, ReportsGridWidgetPresenter>
		implements
		ReportsGridWidgetPresenter.MyView {

	private final AlertSeverityToolbar<MAlertReport> severityToolbar;

	private final AlertSeverityPropertyEditor severityPropertyEditor;

	private final AlertReportProperties alertReportProperties;

	private PushButton exportButton;

	private int totalElements;

	private Image addToDashboardButton;

	private ColumnConfig<MAlertReport, Date> startDateTimeColumn;

	private ColumnConfig<MAlertReport, Date> endDateTimeColumn;

	private static Logger LOGGER = Logger.getLogger(ReportsGridWidgetView.class
			.getName());

	private boolean initialized;

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public ReportsGridWidgetView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory,
			final QoSMessages messages) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		severityPropertyEditor = new AlertSeverityPropertyEditor(messages);
		alertReportProperties = GWT.create(AlertReportProperties.class);
		severityToolbar = new AlertSeverityToolbar<MAlertReport>(toolbar,
				appearanceFactory, severityPropertyEditor, this);
		initialized = false;
	}

	@Override
	protected boolean addButtonsToToolbar() {
		final CssFloatData layoutData = new CssFloatData();
		severityToolbar.addSeverityFilterButtons();
		exportButton = new PushButton(AbstractImagePrototype.create(
				appearanceFactory.resources().exportToExcelButtonUp())
				.createImage(), AbstractImagePrototype.create(
				appearanceFactory.resources().exportToExcelButtonDown())
				.createImage());
		toolbar.add(exportButton, layoutData);
		exportButton.getElement().<XElement> cast().getStyle()
				.setFloat(Float.RIGHT);
		exportButton.getElement().<XElement> cast()
				.setMargins(new Margins(8, 5, 5, 7));

		addToDashboardButton = createAddWidgetToDashboardButton();
		setToolbarStandardMargins(addToDashboardButton.getElement()
				.<XElement> cast());
		toolbar.insert(addToDashboardButton, 0);

		toolbar.insert(createSeparator(), 1);

		// disable export button if we have nothing to export
		setEnabledExportButton(isExportAllowed());
		return true;
	}

	@Override
	protected void applyDefaultConfiguration() {
		applyOrder(Order.desc(alertReportProperties.startDateTime().getPath()),
				false);

		applyCriterionToFilters(loadConfig, getUiHandlers()
				.getFilteringCriterion());
		severityToolbar.updateSeverityCheckboxes();

		hideColumns(new String[]{alertReportProperties.originator().getPath(),
				alertReportProperties.settings().getPath(),
				alertReportProperties.perceivedSeverity().getPath()}, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X cast() {
		return (X) this;
	}

	private Image createAddWidgetToDashboardButton() {
		final Image button = createToolBarButton(appearanceFactory.resources()
						.createWidgetIcon(), messages.addWidgetToDashboardMessage(),
				null);
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().displayAddAnalyticsToDashboardDialog();
			}
		});

		super.setToolbarStandardMargins(button.getElement().<XElement> cast());
		return button;
	}

	private ColumnConfig<MAlertReport, MAlertReport> createChartActionColumn() {
		final ColumnConfig<MAlertReport, MAlertReport> column = createIconColumn("chart");

		column.setCell(new IconedActionCell<MAlertReport>(appearanceFactory
				.<MAlertReport>iconedActionCellAppearance(appearanceFactory
						.resources().showChartForReport(), messages.chart()),
				messages.chart(), new ActionCell.Delegate<MAlertReport>() {

			@Override
			public void execute(final MAlertReport alertReport) {
				getUiHandlers().openChart(alertReport);
			}
		}));

		return column;
	}

	@Override
	protected List<Filter<MAlertReport, ?>> createFilters() {
		final List<Filter<MAlertReport, ?>> filters = new ArrayList<Filter<MAlertReport, ?>>();

		filters.add(filterFactory
				.<MAlertReport> createStringFilter(alertReportProperties
						.alertTypeDisplayName()));
		filters.add(filterFactory
				.<MAlertReport> createStringFilter(alertReportProperties
						.originator()));
		filters.add(createSeverityFilter());

		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridAlertsAppearance();
	}

	private ColumnConfig<MAlertReport, MAlertReport> createIconColumn(
			final String path) {
		final ColumnConfig<MAlertReport, MAlertReport> column = new ColumnConfig<MAlertReport, MAlertReport>(
				createReportEmptyValueProvider(path));
		column.setSortable(false);
		column.setResizable(false);
		column.setFixed(true);
		column.setMenuDisabled(true);
		column.setWidth(30);

		return column;
	}

	protected ValueProvider<MAlertReport, MAlertReport> createReportEmptyValueProvider(
			final String path) {
		return new ValueProvider<MAlertReport, MAlertReport>() {

			@Override
			public String getPath() {
				return path;
			}

			@Override
			public MAlertReport getValue(final MAlertReport alertReport) {
				return alertReport;
			}

			@Override
			public void setValue(final MAlertReport alertReport,
								 final MAlertReport value) {
				// do nothing
			}
		};
	}

	private ListFilter<MAlertReport, PerceivedSeverity> createSeverityFilter() {
		final ListFilter<MAlertReport, PerceivedSeverity> severityFilter = filterFactory
				.<MAlertReport, PerceivedSeverity> createEnumListFilter(
						alertReportProperties.perceivedSeverity(),
						severityPropertyEditor);
		severityToolbar.setSeverityFilter(severityFilter);
		return severityFilter;
	}

	@Override
	protected ListStore<MAlertReport> createStore() {
		return new ListStore<MAlertReport>(alertReportProperties.key());
	}

	private ColumnConfig<MAlertReport, MAlertReport> createTableActionColumn() {
		final ColumnConfig<MAlertReport, MAlertReport> column = createIconColumn("table");
		column.setCell(new AbstractCell<MAlertReport>() {
			@Override
			public void render(final Context context,
							   final MAlertReport alertReport, final SafeHtmlBuilder sb) {
				final PlaceRequest request = getUiHandlers()
						.createResultRequest(alertReport);
				if (request != null) {
					final ImageAnchor anchor = new ImageAnchor(AppUtils
							.createHref(request), appearanceFactory.resources()
							.showTableForReport(), appearanceFactory
							.imageAnchorAppearance(messages.results()));
					sb.append(SafeHtmlUtils.fromTrustedString(anchor
							.getElement().getString()));
				}

			}
		});
		return column;
	}

	@Override
	public AlertReportProperties getAlertReportProperties() {
		return alertReportProperties;
	}

	@Override
	protected List<ColumnConfig<MAlertReport, ?>> getGridColumns() {
		final ColumnConfig<MAlertReport, String> alertTypeDisplayNameColumn = new ColumnConfig<MAlertReport, String>(
				alertReportProperties.alertTypeDisplayName(), 65,
				messages.alert());

		final ColumnConfig<MAlertReport, String> systemComponentColumn = new ColumnConfig<MAlertReport, String>(
				new SystemComponentValueProvider.AlertReportSystemComponentValueProvider(
						alertReportProperties.systemComponent().getPath()), 45,
				messages.probe());

		final ColumnConfig<MAlertReport, String> sourceColumn = new ColumnConfig<MAlertReport, String>(
				alertReportProperties.source(), 45, messages.source());

		final ColumnConfig<MAlertReport, String> originatorColumn = new ColumnConfig<MAlertReport, String>(
				alertReportProperties.originator(), 45, messages.originator());

		startDateTimeColumn = new ColumnConfig<MAlertReport, Date>(
				alertReportProperties.startDateTime(), 45,
				messages.startDateTime());
		startDateTimeColumn
				.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));

		endDateTimeColumn = new ColumnConfig<MAlertReport, Date>(
				alertReportProperties.endDateTime(), 45, messages.endDateTime());
		endDateTimeColumn.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));

		final ColumnConfig<MAlertReport, String> durationColumn = new ColumnConfig<MAlertReport, String>(
				new DurationValueProvider.AlertReportDurationValueProvider(
						messages), 30, messages.duration());

		final ColumnConfig<MAlertReport, PerceivedSeverity> severityColumn = new ColumnConfig<MAlertReport, PerceivedSeverity>(
				alertReportProperties.perceivedSeverity(), 45,
				messages.perceivedSeverity());

		final ColumnConfig<MAlertReport, String> settingsColumn = new ColumnConfig<MAlertReport, String>(
				alertReportProperties.settings(), 45, messages.settings());

		final ColumnConfig<MAlertReport, Double> detectionValue = new ColumnConfig<MAlertReport, Double>(
				alertReportProperties.detectionValue(), 20, messages.detectionTimeValue());

		final ColumnConfig<MAlertReport, String> thresholdValue = new ColumnConfig<MAlertReport, String>(
				alertReportProperties.thresholdValue(), 20, messages.thresholdValue());

		final List<ColumnConfig<MAlertReport, ?>> columns = new ArrayList<ColumnConfig<MAlertReport, ?>>();
		columns.add(systemComponentColumn);
		columns.add(sourceColumn);
		columns.add(alertTypeDisplayNameColumn);
		columns.add(originatorColumn);
		columns.add(startDateTimeColumn);
		columns.add(endDateTimeColumn);
		columns.add(durationColumn);
		columns.add(thresholdValue);
		columns.add(detectionValue);
		columns.add(severityColumn);
		columns.add(settingsColumn);
		columns.add(createTableActionColumn());
		columns.add(createChartActionColumn());

		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE,
				ClientConstants.QOS_GRID_ALERTS_STYLE};
	}

	@Override
	protected String getHighlightedColumnPath() {
		return ORDER_ALIAS_PREFIX
				+ alertReportProperties.perceivedSeverity().getPath();
	}

	@Override
	public Set<PerceivedSeverity> getSelectedSeverites() {
		return severityToolbar.getCheckedValues();
	}

	@Override
	protected boolean hasToolbalTemplateButtons() {
		return true;
	}

	@Override
	@Inject
	public void initialize() {
		super.initialize();
		initializeListeners();
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		final ReportsGridViewConfig viewConfig = new ReportsGridViewConfig(
				((AlertsGridAppearance) gridAppearance).getResources(),
				appearanceFactory, alertReportProperties);
		grid.getView().setViewConfig(viewConfig);
	}

	private void initializeListeners() {
		exportButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				AppUtils.getEventBus().fireEvent(new DownloadReportEvent());
			}
		});
	}

	@Override
	protected RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlertReport>> initializeLoaderProxy() {
		return new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlertReport>>() {
			@Override
			public void load(final FilterPagingLoadConfig loadConfig,
							 final AsyncCallback<PagingLoadResult<MAlertReport>> callback) {

				final Set<String> sourceKeys = getUiHandlers().getSourceKeys();
				final TimeInterval timeInterval = getUiHandlers()
						.getTimeInterval();
				if (sourceKeys.isEmpty()) {
					AppUtils.showErrorMessage(messages
							.alertReportCriteriaCollectionIsEmpty());
				} else if (!timeInterval.isValid()) {
					AppUtils.showErrorMessage(messages
							.invalidDateTimeInterval());
				} else if (initialized) {
					getUiHandlers().setFilteringCriterion(
							convertFiltersToCriterion(loadConfig,
									MAlertMapper.getInstance()));
					final Criterion criterion = getUiHandlers()
							.getConfigurableCriterion();

					getUiHandlers().getAlertReportCount(
							sourceKeys,
							timeInterval,
							criterion,
							new AutoNotifyingAsyncCallback<Long>(
									"Cannot get alert report count", true) {
								@Override
								protected void success(final Long result) {
									totalElements = result.intValue();
									setEnabledExportButton(isExportAllowed());
									getUiHandlers()
											.actionLoadAlertReports(
													sourceKeys,
													timeInterval,
													criterion,
													getCurrentOrder(),
													loadConfig.getOffset(),
													loadConfig.getLimit(),
													new AutoNotifyingAsyncCallback<List<MAlertReport>>(
															"Cannot load alert reports",
															true) {
														@Override
														protected void success(
																final List<MAlertReport> result) {
															final PagingLoadResultBean<MAlertReport> pagingLoadResult = new PagingLoadResultBean<MAlertReport>(
																	result,
																	totalElements,
																	loadConfig
																			.getOffset());
															updateTimeZone(timeInterval
																	.getTimeZone());
															callback.onSuccess(pagingLoadResult);
														}
													});
								}
							});
				}
			}
		};
	}

	public void initLoader() {
		initialized = true;
	}

	private boolean isExportAllowed() {
		return totalElements > 0;
	}

	@Override
	protected boolean isHighlightedColumnSortable() {
		return true;
	}

	@Override
	public void loadTemplate(final MUserReportsTemplate template) {
		hideColumns(template.getHiddenColumns(), false);
		applyOrder(template.getOrder(), false);
		clearFilters(false);

		if (template.getCriterion() != null) {
			applyCriterionToFilters(loadConfig, template.getCriterion());
			severityToolbar.updateSeverityCheckboxes();
		}
		loadFirstPage();
	}

	/**
	 * Set enabled for exportButton
	 */
	@Override
	public void setEnabledExportButton(final boolean enabled) {
		exportButton.setEnabled(enabled);
		exportButton.setTitle(enabled ? messages.exportToExcel() : messages
				.inactive());
	}

	@Override
	protected void setToolbarStandardMargins(final XElement element) {
		element.setMargins(new Margins(10, 7, 0, 7));
	}

	/**
	 * Updates time zone of start and end date columns to show the data in
	 * selected time zone.
	 *
	 * @param timeZone
	 */
	private void updateTimeZone(final String timeZone) {
		final TimeZone timeZoneObject = DateUtils.getClientTimeZones().get(
				timeZone);

		if (timeZoneObject != null) {
			startDateTimeColumn.setCell(new DateCell(
					DateUtils.DATE_TIME_FORMATTER, timeZoneObject));
			endDateTimeColumn.setCell(new DateCell(
					DateUtils.DATE_TIME_FORMATTER, timeZoneObject));
		} else {
			LOGGER.log(
					Level.WARNING,
					"Time zone ("
							+ timeZone
							+ ") is not found. Data will be shown in default time zone.");
		}
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.*;
import java.util.logging.Logger;

import javax.validation.ValidationException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.tecomgroup.qos.ChartType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MUserResultTemplate;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesRemovedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.ResultsAnalyticsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartSettings;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.ChartToolbar.AddChartToDashboardDialogListener;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ChartSeriesValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.ChartSeriesProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell.ButtonedGroupingCellHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeIntervalWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.GroupingGrid;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar.UpdateButtonHandler;

/**
 * @author ivlev.e
 *
 */
public class ResultsAnalyticsView
        extends
        ViewWithUiHandlers<ResultsAnalyticsPresenter>
        implements
        ResultsAnalyticsPresenter.MyView,
        ValueChangeHandler<TimeInterval> {

    interface ViewUiBinder extends UiBinder<Widget, ResultsAnalyticsView> {
    }

    public static Logger LOGGER = Logger.getLogger(ResultsAnalyticsView.class
            .getName());

    protected static final String MAIN_CHART_CONTAINER_ID = "mainChartContainerId";

    protected static final String DIV_CONTAINER_PREFIX = "qosChart";

    protected static final int CHART_HEIGHT = 500;

    public static void showDefaultThresholdColorsMessage() {
        AppUtils.showInfoMessage(AppUtils.getMessages().thresholdsAreNotSet());
    }

    public static void showIncorrectTimeIntervalMessage(
            final double milliseconds) {
        AppUtils.showInfoMessage(AppUtils.getMessages().minTimeIntervalLoaded(
                String.valueOf(milliseconds
                        / TimeConstants.MILLISECONDS_PER_MINUTE)));
    }

    private final ChartSeriesProperties props = GWT
            .create(ChartSeriesProperties.class);

    private QoSMessages messages;

    private final Widget widget;

    @UiField(provided = true)
    protected TextButton addSeriesButton;

    private ToggleButton synchronizeButton;

    @UiField
    protected VerticalPanel seriesTableHeader;

    @UiField
    protected Label loadedTemplateLabel;

    @UiField(provided = true)
    protected Grid<MChartSeries> grid;

    protected GroupingView<MChartSeries> groupView;

    @UiField(provided = true)
    protected FramedPanel centerFramePanel;

    @UiField(provided = true)
    protected FramedPanel westPanel;

    @UiField
    protected CssFloatLayoutContainer templateBar;

    @UiField(provided = true)
    protected BorderLayoutContainer borderLayoutContainer;

    @UiField(provided = true)
    protected FramedPanel toolbarFramePanel;

    @UiField
    protected VerticalLayoutContainer chartContainer;

    @UiField
    protected VerticalLayoutContainer chartVerticalContainer;

    @UiField(provided = true)
    protected HorizontalTimeToolbar timeToolbar;

    private DateTimeIntervalWidget dateTimeIntervalWidget;

    protected int lastChartIndex = 0;

    protected final Map<String, ChartToolbar> toolbarByChartName;

    private final static ViewUiBinder UI_BINDER = GWT
            .create(ViewUiBinder.class);

    protected final AppearanceFactory appearanceFactory;

    protected final DialogFactory dialogFactory;

    private Image loadTemplateButton;

    private Image saveTemplateButton;

    private Image clearSeriesButton;

    private boolean timeIntervalChanged = false;

    private TimeInterval zoomInterval;

    private boolean chartSynchronizationInProgress;

    private final ClickHandler synchronizeButtonHandler = new ClickHandler() {

        @Override
        public void onClick(final ClickEvent event) {
            if (((ToggleButton) event.getSource()).getValue()) {
                synchronizeZoom(
                        ChartResultUtils.getAllChartNames(getUiHandlers()
                                .getAllSeries()),
                        TimeInterval.get(getTimeInterval()));
            } else {
                zoomInterval = null;
            }
            updateSynchronizeButtonState();
        }
    };

    @Inject
    public ResultsAnalyticsView(final HorizontalTimeToolbar timeToolbar,
                                final AppearanceFactoryProvider appearanceFactoryProvider,
                                final DialogFactory dialogFactory) {
        this.messages = AppUtils.getMessages();
        this.appearanceFactory = appearanceFactoryProvider.get();
        this.dialogFactory = dialogFactory;
        this.timeToolbar = timeToolbar;
        this.timeToolbar.setup(Type.DAY, Type.WEEK, Type.MONTH);
        chartSynchronizationInProgress = false;
        dateTimeIntervalWidget = timeToolbar.getDateTimeIntervalWidget();
        dateTimeIntervalWidget.setTimeZone(TimeZoneType.LOCAL, null);
        dateTimeIntervalWidget.addValueChangeHandler(this);

        borderLayoutContainer = new BorderLayoutContainer(
                appearanceFactoryProvider.get().borderLayoutAppearance());
        toolbarByChartName = new HashMap<String, ChartToolbar>();
        addSeriesButton = new TextButton(new TextButtonCell(
                appearanceFactoryProvider.get()
                        .<String> buttonCellHugeAppearance()));
        synchronizeButton = new ToggleButton(AbstractImagePrototype.create(
                appearanceFactory.resources().synchronizeButtonToggleUp())
                .createImage(), AbstractImagePrototype.create(
                appearanceFactory.resources().synchronizeButtonToggleDown())
                .createImage(), synchronizeButtonHandler);
        westPanel = new FramedPanel(appearanceFactoryProvider.get()
                .framedPanelAppearance());
        toolbarFramePanel = new FramedPanel(appearanceFactoryProvider.get()
                .lightFramedPanelAppearance());
        centerFramePanel = new FramedPanel(appearanceFactoryProvider.get()
                .lightFramedPanelAppearance());

        final ColumnModel<MChartSeries> colModel = createNewChartSeriesColumnModel();
        grid = new GroupingGrid<MChartSeries>(new ListStore<MChartSeries>(
                new ModelKeyProvider<MChartSeries>() {

                    @Override
                    public String getKey(final MChartSeries item) {
                        return item.getUniqueKey();
                    }
                }), colModel, groupView) {
        };

        widget = UI_BINDER.createAndBindUi(this);

        loadTemplateButton = AbstractImagePrototype.create(
                appearanceFactory.resources().loadTemplateButton())
                .createImage();
        saveTemplateButton = AbstractImagePrototype.create(
                appearanceFactory.resources().saveTemplateButton())
                .createImage();
        clearSeriesButton = AbstractImagePrototype.create(
                appearanceFactory.resources().clearSeriesButton())
                .createImage();

        saveTemplateButton.getElement().getStyle().setMarginRight(10, Unit.PX);
        loadTemplateButton.getElement().getStyle().setMarginRight(10, Unit.PX);

        templateBar.setSize("210px", "27px");
        templateBar.getElement().getStyle().setPadding(7, Unit.PX);
        templateBar.add(loadTemplateButton);
        templateBar.add(saveTemplateButton);
        templateBar.add(clearSeriesButton);

        clearSeriesButton.getElement().getStyle().setFloat(Float.RIGHT);
        configure();
    }

    @Override
    public void addSeries(final MChartSeries series) {
        grid.getStore().add(series);
    }

    @UiHandler("addSeriesButton")
    protected void addSeriesAction(final SelectEvent e) {
        getUiHandlers().openAddSeriesDialog();
    }

    @Override
    public void addToolbar(final ChartToolbar toolbar) {
        toolbarByChartName.put(toolbar.getChartName(), toolbar);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void buildCharts() {
        try {
            dateTimeIntervalWidget.validate();
            if (timeIntervalChanged) {
                if (zoomInterval != null) {
                    zoomInterval = getTimeInterval();
                }
                getUiHandlers().clearPageStateRelatedToTemplate();
                timeIntervalChanged = false;
                resetAddWidgetButtons();
            }
            final Map<String, List<MChartSeries>> map = ChartResultUtils
                    .groupByChartName(getUiHandlers().getAllSeries());
            int position = 0;
            for (final String chartName : map.keySet()) {
                createChartInPosition(chartName, map.get(chartName), position);
                position++;
            }
            centerFramePanel.forceLayout();
        } catch (final ValidationException e) {
            AppUtils.showErrorMessage(messages.invalidDateTimeInterval() + "\n"
                    + e.getMessage());
        }
    }

    public  void activateAgentTimeZone(final Collection<MChartSeries> allChartSeries){
        List<String> timeZones = ChartResultUtils.getTimeZones(allChartSeries);
        if (timeZones.size() == 1) {
            dateTimeIntervalWidget.enableAgentTimeZone(timeZones.get(0));
        } else {
            dateTimeIntervalWidget.disableAgentTimeZone();
        }
    }

    @Override
    public DialogFactory getDialogFactory() {
        return dialogFactory;
    }

    private void checkMinTimeInterval() {
        if ((getEndDate().getTime() - getStartDate().getTime()) < TimeConstants.MIN_TIME_INTERVAL_IN_CHART) {
            showIncorrectTimeIntervalMessage(TimeConstants.MIN_TIME_INTERVAL_IN_CHART);
            dateTimeIntervalWidget.setTimeInterval(getStartDate(), new Date(
                    getStartDate().getTime()
                            + TimeConstants.MIN_TIME_INTERVAL_IN_CHART));
        }
    }

    protected void clearCharts(final List<MChartSeries> series) {
        final Set<String> chartNames = ChartResultUtils
                .getAllChartNames(series);
        for (final String chart : chartNames) {
            ChartResultUtils.removeChart(chart);
        }
        chartVerticalContainer.clear();
        toolbarByChartName.clear();
    }

    @Override
    public void clearSeries(final List<MChartSeries> series) {
        clearCharts(series);
        grid.getStore().clear();
    }

    private void configure() {
        addSeriesButton.setText(messages.addSeries());
        addSeriesButton.setIcon(appearanceFactory.resources().addButton());
        StyleUtils.configureNoHeaders(westPanel);
        StyleUtils.configureNoHeaders(toolbarFramePanel);
        StyleUtils.configureNoHeaders(centerFramePanel);

        grid.addStyleName(ClientConstants.QOS_GRID_STYLE);
        grid.setHideHeaders(true);
        grid.getView().setAutoFill(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.setWidth(ClientConstants.CHART_LEFT_PANEL_WIDTH);

        seriesTableHeader.addStyleName(appearanceFactory.resources().css()
                .gridPanelHeader());
        seriesTableHeader.getElement().getStyle()
                .setWidth(ClientConstants.CHART_LEFT_PANEL_WIDTH, Unit.PX);
        loadedTemplateLabel.addStyleName(appearanceFactory.resources().css()
                .templateLabel());
        loadedTemplateLabel.getElement().getStyle()
                .setWidth(ClientConstants.CHART_LEFT_PANEL_WIDTH - 10, Unit.PX);
        templateBar.addStyleName(appearanceFactory.resources().css()
                .templateBar());
        templateBar.getElement().getStyle()
                .setWidth(ClientConstants.CHART_LEFT_PANEL_WIDTH, Unit.PX);

        configureSynchronizationControl();

        configureTemplateButton(loadTemplateButton,
                messages.templateLoadingHeader(), new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent event) {
                        getUiHandlers().openLoadTemplateDialog();
                    }
                });

        configureTemplateButton(saveTemplateButton,
                messages.tempalteSavingHeader(), new ClickHandler() {

                    @Override
                    public void onClick(final ClickEvent event) {
                        getUiHandlers().openSaveTemplateDialog();
                    }
                });

        configureTemplateButton(clearSeriesButton, messages.clearCharts(),
                new ClickHandler() {
                    @Override
                    public void onClick(final ClickEvent event) {
                        if (!getUiHandlers().getAllSeries().isEmpty()) {
                            dialogFactory.createConfirmationDialog(
                                    new ConfirmationHandler() {

                                        @Override
                                        public void onCancel() {

                                        }

                                        @Override
                                        public void onConfirm(
                                                final String comment) {
                                            getUiHandlers().clearState();
                                        }
                                    }, messages.clearCharts(),
                                    messages.clearChartsConfirmation(),
                                    CommentMode.DISABLED).show();
                        }

                    }
                });

        dateTimeIntervalWidget.disableCustomTimeInterval();
        timeToolbar.setUpdateButtonHandler(new UpdateButtonHandler() {

            @Override
            public void onUpdateButtonPressed(final SelectEvent event) {
                if (synchronizeButton.getValue()) {
                    zoomInterval = getTimeInterval();
                } else {
                    zoomInterval = null;
                }
                buildCharts();
                getUiHandlers().updateWidgetIcons();
            }
        });
    }

    private void configureSynchronizationControl() {
        final CssFloatLayoutContainer toolbarContainer = timeToolbar
                .getContainer();

        final Margins margins = new Margins(9, 5, 9, 5);
        toolbarContainer.add(StyleUtils.createSeparator(margins));

        synchronizeButton.setStyleName(appearanceFactory.resources().css()
                .synchronizeChartsButton());
        synchronizeButton.getElement().<XElement> cast().setMargins(margins);

        updateSynchronizeButtonState();
        toolbarContainer.add(synchronizeButton);
    }

    private void configureTemplateButton(final Image button,
                                         final String title, final ClickHandler handler) {
        button.addStyleName(appearanceFactory.resources().css().cursorPointer());
        button.setTitle(title);
        button.addClickHandler(handler);
    }

    private void createChart(final String chartName, final ChartType chartType,
                             final List<MChartSeries> seriesList, final ChartToolbar toolbar) {
        final ChartSettings settings = new ChartSettings.Builder()
                .chartName(chartName).chartType(chartType).series(seriesList)
                .startDate(getStartDate()).endDate(getEndDate())
                .timeZoneOffset(dateTimeIntervalWidget.getTimeZoneOffset())
                .divElementId(toolbar.getDivElementId())
                .lineType(toolbar.getLineType())
                .autoscaling(toolbar.isAutoscalingEnabled())
                .thresholds(toolbar.isThresholdsEnabled())
                .captions(toolbar.isCaptionsEnabled())
                .mouseTracking(toolbar.isMouseTrackingEnabled()).build();
        ChartResultUtils.createChart(settings, CHART_HEIGHT, messages.time());
    }

    private void createChartInPosition(final String chartName,
                                       final List<MChartSeries> seriesList, final int position) {
        final ChartType chartType = ChartResultUtils
                .resolveChartType(seriesList);

        ChartToolbar toolbar = getChartToolbar(chartName, chartType);
        final VerticalLayoutData layoutData = new VerticalLayoutData(1, -1);
        layoutData.setMargins(new Margins(0, 15, 10, 3));
        if (!toolbar.isRendered()) {
            chartVerticalContainer.insert(toolbar.asWidget(), position,
                    layoutData);
            toolbarByChartName.put(chartName, toolbar);
        } else if (!toolbar.getType().equals(chartType)) {
            chartVerticalContainer.remove(toolbar.asWidget());
            toolbarByChartName.remove(chartName);
            toolbar = getChartToolbar(chartName, chartType);
            toolbarByChartName.put(chartName, toolbar);
            chartVerticalContainer.insert(toolbar.asWidget(), position,
                    layoutData);
        }
        checkMinTimeInterval();
        createChart(chartName, chartType, seriesList, toolbar);
    }

    private ChartToolbar createChartToolbar(final String chartName,
                                            final ChartType chartType, final String divElementId) {
        return new ChartToolbar(chartName, chartType, messages, divElementId,
                this, appearanceFactory, dialogFactory,
                new AddChartToDashboardDialogListener() {

                    @Override
                    public void onAddChartToDashboard(
                            final DashboardChartWidget widget) {
                        getUiHandlers().openAddChartWidgetToDashboardDialog(
                                widget);
                    }
                }

        );
    }

    private ColumnModel<MChartSeries> createNewChartSeriesColumnModel() {
        final List<ColumnConfig<MChartSeries, ?>> list = new ArrayList<ColumnConfig<MChartSeries, ?>>();

        final ColumnConfig<MChartSeries, String> chart = new ColumnConfig<MChartSeries, String>(
                props.chartName());
        final ColumnConfig<MChartSeries, String> info = new ColumnConfig<MChartSeries, String>(
                new ChartSeriesValueProvider(messages));
        final ButtonedGroupingCell<String> seriesCell = new ButtonedGroupingCell<String>(
                appearanceFactory.<String> buttonedGroupingCellAppearance(),
                new ButtonedGroupingCellHandler() {
                    @Override
                    public void onRemovedButtonPressed(
                            final List<String> seriesKeys) {
                        AppUtils.getEventBus().fireEvent(
                                new ChartSeriesRemovedEvent(seriesKeys));
                    }
                });
        info.setCell(seriesCell);

        groupView = new ButtonedGroupingView<MChartSeries>(
                appearanceFactory.gridAppearance(),
                appearanceFactory.buttonedGroupingViewAppearance());
        groupView.setShowGroupedColumn(false);
        groupView.setForceFit(true);
        groupView.groupBy(chart);

        list.add(chart);
        list.add(info);

        return new ColumnModel<MChartSeries>(list);
    }

    @Override
    public MChartSeries findSeriesByKey(final String seriesUniqueKey) {
        return grid.getStore().findModelWithKey(seriesUniqueKey);
    }

    @Override
    public ChartToolbar getChartToolbar(final String chartName) {
        return toolbarByChartName.get(chartName);
    }

    @Override
    public ChartToolbar getChartToolbar(final String chartName,
                                        final ChartType type) {
        ChartToolbar toolbar = toolbarByChartName.get(chartName);
        if (toolbar == null) {
            final String newId = DIV_CONTAINER_PREFIX + lastChartIndex++;
            toolbar = createChartToolbar(chartName, type, newId);
        }
        return toolbar;
    }

    @Override
    public Collection<ChartToolbar> getChartToolbars() {
        return toolbarByChartName.values();
    }

    private Date getEndDate() {
        return dateTimeIntervalWidget.getEndDate();
    }

    @Override
    public ResultsAnalyticsPresenter getPresenter() {
        return getUiHandlers();
    }

    private Date getStartDate() {
        return dateTimeIntervalWidget.getStartDate();
    }

    @Override
    public String getTemplateLabel() {
        return loadedTemplateLabel.getText();
    }

    @Override
    public TimeInterval getTimeInterval() {
        return dateTimeIntervalWidget.getTimeInterval();
    }

    @Override
    public String getTimeZone() {
        return dateTimeIntervalWidget.getTimeZone();
    }

    @Override
    public String getTimeZone(String agentTimeZone) {
        return dateTimeIntervalWidget.getTimeZone();
    }

    @Override
    public TimeZoneType getTimeZoneType() {
        return dateTimeIntervalWidget.getTimeZoneType();
    }

    @Override
    public TimeInterval getZoomInterval() {
        return zoomInterval;
    }

    @Override
    public boolean isChartsSynchronizationEnabled() {
        return zoomInterval != null;
    }

    @Override
    public void loadTemplate(final MUserResultTemplate template) {
        final TimeInterval timeInterval = template.getTimeInterval();
        if (timeInterval.getTimeZoneType() != null && timeInterval.getTimeZone() != null) {
            dateTimeIntervalWidget.setTimeZone(timeInterval.getTimeZoneType(),
                    timeInterval.getTimeZone());
        }
        dateTimeIntervalWidget.setTimeIntervalType(timeInterval.getType());
        if (Type.CUSTOM.equals(timeInterval.getType())) {
            dateTimeIntervalWidget.setTimeInterval(
                    timeInterval.getStartDateTime(),
                    timeInterval.getEndDateTime());
        }
        if (template.isChartsSynchronizationEnabled()) {
            zoomInterval = getTimeInterval();
        } else {
            zoomInterval = null;
        }
        synchronizeButton.setValue(zoomInterval != null);
        buildCharts();
        appearanceFactory.buttonCellLightAppearance().onToggle(
                synchronizeButton.getElement().<XElement> cast(),
                synchronizeButton.getValue());
        updateSynchronizeButtonState();
    }

    @UiHandler("borderLayoutContainer")
    protected void onCollapseWestPanel(final CollapseItemEvent<ContentPanel> e) {
        new Timer() {

            @Override
            public void run() {
                for (final String chartName : toolbarByChartName.keySet()) {
                    ChartResultUtils.setSize(chartName,
                            toolbarByChartName.get(chartName).asWidget()
                                    .getOffsetWidth(), CHART_HEIGHT);
                }
            }
        }.schedule(100);
    }

    @UiHandler("borderLayoutContainer")
    protected void onExpandWestPanel(final ExpandItemEvent<ContentPanel> e) {
        new Timer() {

            @Override
            public void run() {
                for (final String chartName : toolbarByChartName.keySet()) {
                    ChartResultUtils.setSize(chartName,
                            toolbarByChartName.get(chartName).asWidget()
                                    .getOffsetWidth(), CHART_HEIGHT);
                }
            }
        }.schedule(100);
    }

    @Override
    public void onValueChange(final ValueChangeEvent<TimeInterval> event) {
        timeIntervalChanged = true;
    }

    @Override
    public void refreshGridView() {
        grid.getView().refresh(true);
    }

    @Override
    public void removeSeries(final MChartSeries series) {
        final boolean needRemoveChart = ChartResultUtils.removeSeries(
                series.getChartName(), series.getKey());
        grid.getStore().remove(series);
        if (needRemoveChart) {
            removeToolbar(series.getChartName());
        }
    }

    protected void removeToolbar(final String chartName) {
        final ChartToolbar toolbar = toolbarByChartName.get(chartName);
        if (toolbar != null) {
            chartVerticalContainer.remove(toolbar.asWidget());
            toolbarByChartName.remove(chartName);
        }
    }

    @Override
    public boolean renameChart(final String oldName, final String newName) {
        boolean result = false;
        final Map<String, List<MChartSeries>> map = ChartResultUtils
                .groupByChartName(getUiHandlers().getAllSeries());
        if ((map.get(newName) != null)) {
            AppUtils.showErrorMessage(messages.chartRenameFail(newName));
        } else {
            final List<MChartSeries> series = map.get(oldName);
            for (final MChartSeries mChartSeries : series) {
                mChartSeries.setChartName(newName);
            }
            final ChartToolbar toolbar = toolbarByChartName.get(oldName);
            toolbarByChartName.remove(oldName);
            toolbarByChartName.put(newName, toolbar);
            ChartResultUtils.renameChart(oldName, newName);
            getUiHandlers().resetSeries();
            result = true;
        }
        return result;
    }

    private void resetAddWidgetButtons() {
        for (final ChartToolbar toolbar : toolbarByChartName.values()) {
            toolbar.setWidgetIconState(false);
        }
    }

    @Override
    public void resetStore() {
        grid.getStore().clear();
        grid.getStore().addAll(getUiHandlers().getAllSeries());
    }

    @Override
    public void setTemplateLabel(final String templateName) {
        loadedTemplateLabel.setText(templateName);
        westPanel.forceLayout();
    }

    @Override
    public void setTimeInterval(final TimeInterval interval) {
        dateTimeIntervalWidget.setTimeIntervalType(interval.getType());
        if (interval.isValid()) {
            dateTimeIntervalWidget.setTimeInterval(interval.getStartDateTime(),
                    interval.getEndDateTime());
        }

        if (interval.getTimeZoneType() == TimeZoneType.CUSTOM
                && interval.getTimeZone() != null) {
            dateTimeIntervalWidget.setTimeZone(interval.getTimeZoneType(),
                    interval.getTimeZone());
        }

        if (interval.getTimeZoneType() == TimeZoneType.LOCAL
                || interval.getTimeZoneType() == TimeZoneType.AGENT) {
            dateTimeIntervalWidget
                    .setTimeZone(interval.getTimeZoneType(), null);

        }
    }

    @Override
    public void setTimeIntervalType(final Type intervalType) {
        dateTimeIntervalWidget.setTimeIntervalType(intervalType);
    }

    @Override
    public void synchronizeZoom(final Collection<String> charts,
                                final TimeInterval zoomInterval) {
        if (!chartSynchronizationInProgress) {
            chartSynchronizationInProgress = true;
            for (final String chart : charts) {
                ChartResultUtils.zoomChart(chart, zoomInterval
                                .getStartDateTime().getTime(), zoomInterval
                                .getEndDateTime().getTime(),
                        zoomInterval.getTimeZone(), zoomInterval
                                .getTimeZoneType().toString());
            }
            this.zoomInterval = zoomInterval;
            chartSynchronizationInProgress = false;
        }
    }

    private void updateSynchronizeButtonState() {
        synchronizeButton.setTitle(synchronizeButton.getValue() ? messages
                .chartsSynchronizationEnabled() : messages
                .chartsSynchronizationDisabled());
    }
}

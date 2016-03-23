/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.ChartType;
import com.tecomgroup.qos.Statefull;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.dashboard.DashboardChartWidget.ChartSeriesData;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent.BeforeLogoutEventHandler;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent.GridGroupRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent.LoadTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent.SaveTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.BuildChartsEvent;
import com.tecomgroup.qos.gwt.client.event.chart.BuildChartsEvent.BuildChartsEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartAutoscalingOptionChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartAutoscalingOptionChangedEvent.ChartAutoscalingOptionChangedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartBuiltEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartBuiltEvent.ChartBuiltEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartCaptionsOptionChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartCaptionsOptionChangedEvent.ChartCaptionsOptionChangedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesAddedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesAddedEvent.ChartSeriesAddedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesRemovedEvent.ChartSeriesRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartThresholdsOptionChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartThresholdsOptionChangedEvent.ChartThresholdsOptionChangedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartTypeChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartTypeChangedEvent.ChartTypeChangedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.ChartZoomChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartZoomChangedEvent.ChartZoomChangedEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartEvent;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartEvent.NavigateToChartEventHandler;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartWithConfirmEvent;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartWithConfirmEvent.NavigateToChartWithConfirmEventHandler;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent.DashboardWidgetAddedEventHandler;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetRemovedEvent.DashboardWidgetRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.chart.AddChartToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.ChartsGatekeeper;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.ChartToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.shared.InappropriateParameterTypeException;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.service.UserServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 *
 */
public class ResultsAnalyticsPresenter
        extends
        Presenter<ResultsAnalyticsPresenter.MyView, ResultsAnalyticsPresenter.MyProxy>
        implements
        UiHandlers,
        NavigateToChartEventHandler,
        NavigateToChartWithConfirmEventHandler,
        ChartSeriesRemovedEventHandler,
        ChartSeriesAddedEventHandler,
        BuildChartsEventHandler,
        LoadTemplateEventHandler,
        SaveTemplateEventHandler,
        GridGroupRemovedEventHandler<MChartSeries>,
        BeforeLogoutEventHandler,
        ChartZoomChangedEventHandler,
        ChartTypeChangedEventHandler,
        ChartCaptionsOptionChangedEventHandler,
        ChartThresholdsOptionChangedEventHandler,
        ChartAutoscalingOptionChangedEventHandler,
        ChartBuiltEventHandler,
        DashboardWidgetAddedEventHandler,
        DashboardWidgetRemovedEventHandler,
        Statefull {

    private final TaskRetrieverAsync taskRetriever;

    @ProxyCodeSplit
    @NameToken(QoSNameTokens.chartResults)
    @UseGatekeeper(ChartsGatekeeper.class)
    public static interface MyProxy
            extends
            ProxyPlace<ResultsAnalyticsPresenter> {
    }

    public interface MyView
            extends
            View,
            HasUiHandlers<ResultsAnalyticsPresenter> {

        void addSeries(MChartSeries series);

        void addToolbar(ChartToolbar toolbar);

        void buildCharts();

        void clearSeries(List<MChartSeries> series);

        MChartSeries findSeriesByKey(String seriesKey);

        ChartToolbar getChartToolbar(String chartName);

        ChartToolbar getChartToolbar(String chartName, ChartType type);

        Collection<ChartToolbar> getChartToolbars();

        ResultsAnalyticsPresenter getPresenter();

        String getTemplateLabel();

        TimeInterval getTimeInterval();

        String getTimeZone();

        String getTimeZone(String agetntTimeZone);

        TimeZoneType getTimeZoneType();

        TimeInterval getZoomInterval();

        DialogFactory getDialogFactory();

        boolean isChartsSynchronizationEnabled();

        void loadTemplate(MUserResultTemplate template);

        void refreshGridView();

        void removeSeries(MChartSeries series);

        boolean renameChart(String chartName, String newName);

        void resetStore();

        void setTemplateLabel(final String templateName);

        /**
         * @param interval
         */
        void setTimeInterval(TimeInterval interval);

        void setTimeIntervalType(TimeInterval.Type intervalType);

        void synchronizeZoom(final Collection<String> charts,
                             TimeInterval zoomInterval);

        void activateAgentTimeZone(final Collection<MChartSeries> allChartSeries);
    }

    private static final Logger LOGGER = Logger
            .getLogger(ResultsAnalyticsPresenter.class.getName());

    private final QoSMessages messages;

    private final List<MChartSeries> allSeries = new ArrayList<MChartSeries>();

    private final UserServiceAsync userService;

    private final AgentServiceAsync agentService;

    private final TemplateType TEMPLATE_TYPE = BaseTemplateType.RESULT;

    private final AddChartSeriesPresenter addChartPresenter;

    private final LoadTemplatePresenterWidget loadTemplatePresenter;

    private final SaveTemplatePresenterWidget saveTemplatePresenter;

    private HandlerRegistration loadTemplateHandlerRegistration;

    private HandlerRegistration saveTemplateHandlerRegistration;

    private HandlerRegistration gridGroupRemovedHandler;

    private String selectedTemplateName;

    private final AddChartToDashboardWidgetPresenter addWidgetToDashboard;
	@Inject
	public ResultsAnalyticsPresenter(final EventBus eventBus,
			final MyView view, final MyProxy proxy,
			final AddChartSeriesPresenter addChartPresenter,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final AddChartToDashboardWidgetPresenter addWidgetToDashboard,
			final UserServiceAsync userService,
			final TaskRetrieverAsync taskRetriever, final QoSMessages messages,
            final AgentServiceAsync agentService) {
		super(eventBus, view, proxy);
		this.messages = messages;
		this.addChartPresenter = addChartPresenter;
		this.loadTemplatePresenter = loadTemplatePresenter;
		this.saveTemplatePresenter = saveTemplatePresenter;
		this.userService = userService;
        this.agentService = agentService;
		this.addWidgetToDashboard = addWidgetToDashboard;
        this.taskRetriever = taskRetriever;
		getView().setUiHandlers(this);
		getEventBus().addHandler(ChartSeriesRemovedEvent.TYPE, this);
		getEventBus().addHandler(ChartSeriesAddedEvent.TYPE, this);
		getEventBus().addHandler(NavigateToChartEvent.TYPE, this);
        getEventBus().addHandler(NavigateToChartWithConfirmEvent.TYPE, this);
		getEventBus().addHandler(BuildChartsEvent.TYPE, this);
		getEventBus().addHandler(BeforeLogoutEvent.TYPE, this);
		getEventBus().addHandler(ChartZoomChangedEvent.TYPE, this);
		getEventBus().addHandler(ChartTypeChangedEvent.TYPE, this);
		getEventBus().addHandler(ChartAutoscalingOptionChangedEvent.TYPE, this);
		getEventBus().addHandler(ChartCaptionsOptionChangedEvent.TYPE, this);
		getEventBus().addHandler(ChartThresholdsOptionChangedEvent.TYPE, this);
		getEventBus().addHandler(ChartBuiltEvent.TYPE, this);
		getEventBus().addHandler(DashboardWidgetAddedEvent.TYPE, this);
		getEventBus().addHandler(DashboardWidgetRemovedEvent.TYPE, this);
	}

	private void buildChart(final Collection<MChartSeries> series,
			final String chartName, final TimeInterval interval,
			final boolean isAddedToDashboard,
			final boolean isThresholdsEnabled,
			final boolean isAutoscalingEnabled,
			final String lineType) {
        revealInParent();
        clearState();
        allSeries.addAll(series);
        final ChartToolbar toolbar = getChartToolbar(chartName,
                ChartResultUtils.resolveChartType(series));
        toolbar.setThresholdsValue(isThresholdsEnabled);
        toolbar.setAutoscalingValue(isAutoscalingEnabled);
        toolbar.setWidgetIconState(isAddedToDashboard);
        if (lineType != null) {
            toolbar.setLineType(lineType);
        }
        getView().setTimeInterval(interval);
        getView().addToolbar(toolbar);
        getView().resetStore();
        getView().buildCharts();
    }

    public void clearPageStateRelatedToTemplate() {
        resetSelectedTemplate();
        setTemplateLabel("");
    }

    @Override
    public void clearState() {
        getView().clearSeries(allSeries);
        allSeries.clear();
        getView().activateAgentTimeZone(allSeries);
        clearPageStateRelatedToTemplate();
    }
    /**
     * @return the allSeries
     */
    public List<MChartSeries> getAllSeries() {
        return allSeries;
    }

    public List<ChartSeriesData> getChartSeriesDataByChartName(
            final String chartName) {
        final List<ChartSeriesData> result = new ArrayList<ChartSeriesData>();
        for (final MChartSeries chartSeries : allSeries) {
            if (chartSeries.getChartName().equals(chartName)) {
                result.add(ChartSeriesData.fromMChartSeries(chartSeries));
            }
        }
        return result;
    }

    /**
     * Finds or creates and configures {@link ChartToolbar} according to the
     * provided {@link ChartType} and chartName.
     *
     * @param chartName
     * @param type
     * @return
     */
    public ChartToolbar getChartToolbar(final String chartName,
                                        final ChartType type) {
        return getView().getChartToolbar(chartName, type);
    }

    private TimeInterval getTimeInterval() {
        return getView().getTimeInterval();
    }

    @Override
    public void loadState() {
        loadTemplate(selectedTemplateName);
    }

    @Override
    public void loadTemplate(final LoadTemplateEvent event) {
        if (event.getTemplate() instanceof MUserResultTemplate) {
            final MUserResultTemplate template = (MUserResultTemplate) event.getTemplate();

            final Set<MChartSeries> chartSerieses = template.getSeries();
            agentService.getProbeKeysUserCanManage(new AsyncCallback<List<String>>() {
                @Override
                public void onFailure(Throwable caught) {
                    LOGGER.log(
                            Level.WARNING,
                            "Unable to filter chart template by agent",
                            caught);
                    AppUtils.showInfoMessage(messages.templateLoadingFail());
                }

                @Override
                public void onSuccess(List<String> managableAgentKeys) {
                    Set<MChartSeries> result = new HashSet<MChartSeries>();
                    for (final MChartSeries chart : chartSerieses) {
                        if (managableAgentKeys.contains(chart.getTask().getModule().getAgent().getKey())) {
                            result.add(chart);
                        }
                    }
                    if(result.size() == 0) {
                        LOGGER.log(
                                Level.WARNING,
                                "Template has no charts to display, check user permissions :" + template.getName());
                        AppUtils.showInfoMessage(messages.templateLoadingFail());
                    } else {
                        template.setSeries(result);
                        loadTemplate(template);
                    }
                }
            });
        }
    }

    private void loadTemplate(final MUserResultTemplate template) {
        getView().clearSeries(allSeries);
        allSeries.clear();
        allSeries.addAll(template.getSeries());
        getView().activateAgentTimeZone(allSeries);
        getView().loadTemplate(template);
        updateWidgetIcons();
        AppUtils.showInfoMessage(messages.templateLoadingSuccess());
        resetSeries();
        setCurrentTemplate(template);
    }

    private void loadTemplate(final String templateName) {
        if (SimpleUtils.isNotNullAndNotEmpty(templateName)) {
            userService.getTemplate(TEMPLATE_TYPE, AppUtils.getCurrentUser()
                            .getUser().getId(), templateName,
                    new AutoNotifyingAsyncLogoutOnFailureCallback<MUserAbstractTemplate>() {

                        @Override
                        protected void success(
                                final MUserAbstractTemplate template) {
                            loadTemplate(new LoadTemplateEvent(template));
                        }
                    });
        }
    }

    @Override
    public void onBeforeLogout(final BeforeLogoutEvent event) {
        clearState();
    }

    @Override
    protected void onBind() {
        super.onBind();
        ChartResultUtils.initGeneralChartParameters(messages);
    }

    @Override
    public void onBuildCharts(final BuildChartsEvent event) {
        getView().buildCharts();
        updateWidgetIcons();
    }

    @Override
    public void onChartAutoscalingOptionChangedEvent(
            final ChartAutoscalingOptionChangedEvent event) {
        onChartOptionStateChangedEvent();
    }

    @Override
    public void onChartBuilt(final ChartBuiltEvent event) {
        if (getView().isChartsSynchronizationEnabled()) {
            getView().synchronizeZoom(
                    Arrays.asList(new String[]{event.getChartName()}),
                    getView().getZoomInterval());
        }
    }

    @Override
    public void onChartCaptionsOptionChangedEvent(
            final ChartCaptionsOptionChangedEvent event) {
        onChartOptionStateChangedEvent();
    }

    private void onChartOptionStateChangedEvent() {
        if (this.isVisible()) {
            // don't clear template label
            resetSelectedTemplate();
        }
    }

    @Override
    public void onChartSeriesAddedEvent(final ChartSeriesAddedEvent event) {
        final MChartSeries chartSeries = event.getChartSeries();
        allSeries.add(chartSeries);
        getView().activateAgentTimeZone(allSeries);
        getView().addSeries(chartSeries);

        final String chartName = chartSeries.getChartName();
        final ChartToolbar toolbar = getView().getChartToolbar(chartName);
        if (toolbar != null) {
            toolbar.setWidgetIconState(false);
        }

        clearPageStateRelatedToTemplate();
    }

    @Override
    public void onChartSeriesRemovedEvent(final ChartSeriesRemovedEvent event) {
        final List<String> seriesKeys = event.getSeriesKeys();

        if (SimpleUtils.isNotNullAndNotEmpty(seriesKeys)) {
            final Set<String> allChartNames = ChartResultUtils
                    .getAllChartNames(getAllSeries());
            for (final String key : seriesKeys) {
                final MChartSeries chartSeries = ChartResultUtils
                        .findSeriesByKey(allSeries, key);
                if (chartSeries != null) {
                    allSeries.remove(chartSeries);
                    getView().removeSeries(chartSeries);
                    if (!allChartNames.contains(chartSeries.getChartName())) {
                        getView().refreshGridView();
                    }
                    final String chartName = chartSeries.getChartName();
                    final ChartToolbar toolbar = getView().getChartToolbar(
                            chartName);
                    if (toolbar != null) {
                        toolbar.setWidgetIconState(false);
                    }
                }
            }
            getView().activateAgentTimeZone(allSeries);
            updateWidgetIcons();
            clearPageStateRelatedToTemplate();
        }
    }

    private void onChartSynchronizationStatusChanged(final String chart,
                                                     final Date startDate, final Date endDate) {
        if (getView().isChartsSynchronizationEnabled()) {
            final Set<String> chartsToSynchronize = ChartResultUtils
                    .getAllChartNames(allSeries);
            chartsToSynchronize.remove(chart);
            final TimeInterval timeInterval = getTimeInterval();
            getView().synchronizeZoom(
                    chartsToSynchronize,
                    TimeInterval.get(timeInterval.getType(), startDate,
                            endDate, timeInterval.getTimeZoneType(),
                            timeInterval.getTimeZone(),
                            timeInterval.getClientTimeZone()));
        }
    }

    @Override
    public void onChartThresholdsOptionChangedEvent(
            final ChartThresholdsOptionChangedEvent event) {
        onChartOptionStateChangedEvent();
    }

    @Override
    public void onChartTypeChangedEvent(final ChartTypeChangedEvent event) {
        onChartOptionStateChangedEvent();
    }

    @Override
    public void onChartZoomChanged(final ChartZoomChangedEvent event) {
        onChartOptionStateChangedEvent();
        onChartSynchronizationStatusChanged(event.getChartName(),
                event.getStartDate(), event.getEndDate());
    }

    @Override
    public void onDashboardWidgetAdded(final DashboardWidgetAddedEvent event) {
        final DashboardWidget widget = event.getWidget();
        if (widget instanceof DashboardChartWidget) {
            final DashboardChartWidget chartWidget = (DashboardChartWidget) widget;
            final String chartName = chartWidget.getChartName();
            final ChartToolbar toolbar = getView().getChartToolbar(chartName);
            toolbar.setWidgetIconState(true);
        }
    }

    @Override
    public void onDashboardWidgetRemoved(final DashboardWidgetRemovedEvent event) {
        updateWidgetIcons();
    }

    @Override
    public void onGridGroupRemovedEvent(
            final GridGroupRemovedEvent<MChartSeries> event) {
        for (final MChartSeries s : event.getItems()) {
            allSeries.remove(s);
            getView().removeSeries(s);
        }
        getView().activateAgentTimeZone(allSeries);
        clearPageStateRelatedToTemplate();
    }

    @Override
    protected void onHide() {
        super.onHide();
        gridGroupRemovedHandler.removeHandler();
        // getView().onHide();
        loadTemplateHandlerRegistration.removeHandler();
        saveTemplateHandlerRegistration.removeHandler();
    }

	@Override
	public void onNavigateToChart(final NavigateToChartEvent event) {
		buildChart(event.getSeries(), event.getChartName(),
				event.getTimeInterval(), event.isAddedToDashboard(),
				event.isThresholdsEnabled(), event.isAutoscalingEnabled(),
				event.getLineType());
    }

    @Override
    public void onNavigateToChartWithConfirm(final NavigateToChartWithConfirmEvent event) {
        if (!getAllSeries().isEmpty()) {
            getView().getDialogFactory().createConfirmationDialog(
                    new ConfirmationDialog.ConfirmationHandler() {

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onConfirm(
                                final String comment) {
                            buildChart(event.getSeries(), event.getChartName(),
                                    event.getTimeInterval(), event.isAddedToDashboard(),
                                    event.isThresholdsEnabled(), event.isAutoscalingEnabled(),
                                    event.getLineType());
                        }
                    }, messages.clearCharts(),
                    messages.clearChartsConfirmation(),
                    ConfirmationDialog.CommentMode.DISABLED).show();
        } else {
            buildChart(event.getSeries(), event.getChartName(),
                    event.getTimeInterval(), event.isAddedToDashboard(),
                    event.isThresholdsEnabled(), event.isAutoscalingEnabled(),
                    event.getLineType());
        }
    }

	@Override
	protected void onReset() {
		super.onReset();
		/* Issues/5731: Remove the template automatically update when you click the tab Analytics.
		loadState();
		*/
	}

    public void openAddChartWidgetToDashboardDialog(
            final DashboardChartWidget widget) {
        addWidgetToDashboard.setDashboardChartWidget(widget);
        this.addToPopupSlot(addWidgetToDashboard, false);
    }

    public void openAddSeriesDialog() {
        addChartPresenter.setAllSeries(new ArrayList<MChartSeries>(allSeries));
        this.addToPopupSlot(addChartPresenter, false);
    }

    public void openLoadTemplateDialog() {
        loadTemplatePresenter.setTemplate(getView().getTemplateLabel());
        loadTemplatePresenter.setTemplateType(TEMPLATE_TYPE);

        addToPopupSlot(loadTemplatePresenter, false);
    }

    public void openSaveTemplateDialog() {
        saveTemplatePresenter.setTemplate(getView().getTemplateLabel());
        saveTemplatePresenter.setTemplateType(TEMPLATE_TYPE);
        addToPopupSlot(saveTemplatePresenter, false);
    }

    @Override
    public void prepareFromRequest(final PlaceRequest request) {
        super.prepareFromRequest(request);
        final String templateName = request.getParameter(
                RequestParams.template, null);
        // if templateName is null, then not overwrite selectedTemplateName
        // because overwriting selectedTemplateName with null
        // disappear load/save template widget dialog substitution
        if (SimpleUtils.isNotNullAndNotEmpty(templateName)) {
            loadTemplate(templateName);
        }

        final String tasksListString = request.getParameter(
                RequestParams.tasks, null);
        final String startTimestampString = request.getParameter(
                RequestParams.startDate, null);
        final String endTimestampString = request.getParameter(
                RequestParams.endDate, null);

        if (tasksListString != null && startTimestampString != null && endTimestampString != null) {
            final String[] taskIdStrings = tasksListString.split(",");
            final Map<Long,Collection> paramsMap=new HashMap();
            final Date start = new Date(Long.parseLong(startTimestampString));
            final Date end = new Date(Long.parseLong(endTimestampString));
            final List<Long> ids = new ArrayList<Long>();
            for (String idString : taskIdStrings) {
                final String parameterNames = request.getParameter(idString,null);
                if(parameterNames!=null && !parameterNames.trim().isEmpty()) {
                    paramsMap.put(Long.parseLong(idString),new HashSet(Arrays.asList(parameterNames.split(","))));
                }
                if(idString!=null && !idString.trim().isEmpty()) {
                    ids.add(Long.parseLong(idString));
                }
            }

            taskRetriever.getTasksByIds(ids, new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>() {

                @Override
                protected void success(List<MAgentTask> result) {

                    // set interval
                    MUserResultTemplate template = new MUserResultTemplate();
                    template.setSeries(new HashSet());
                    template.setTimeInterval(TimeInterval.get(start, end));
                    loadTemplate(template);

                    int freeChartIndex = 1;
                    final Map<String, String> chartNamesByType = new HashMap<String, String>();
                    getView().clearSeries(allSeries);
                    allSeries.clear();

                    for (MAgentTask task : result) {
                        final List<MResultParameterConfiguration> params = task.getResultConfiguration()
                                .getParameterConfigurations(true);
                        for (MResultParameterConfiguration param : params) {
                            String parameterName=param.getParameterIdentifier().getName();
                            if(paramsMap.get(task.getId()).contains(parameterName)) {
                                String chartType = param.getType() + "-" + param.getUnits();
                                String chartName = chartNamesByType.get(chartType);

                                if (chartName == null) {
                                    chartName = messages.chart() + freeChartIndex++;
                                    chartNamesByType.put(chartType, chartName);
                                }

                                MChartSeries series = new MChartSeries(task, param, chartName);
                                allSeries.add(series);
                                getView().activateAgentTimeZone(allSeries);
                                getView().addSeries(series);
                            }
                        }
                    }
                }
            });
        }
    }

    public void resetSelectedTemplate() {
        selectedTemplateName = null;
        AppUtils.removeParametersFromHistoryUrl(Arrays
                .asList(RequestParams.template));
    }

    public void resetSeries() {
        getView().resetStore();
        addChartPresenter.addSeries(null);
    }

    private void resetWidgetIcons() {
        final Collection<ChartToolbar> toolbars = getView().getChartToolbars();
        for (final ChartToolbar chartToolbar : toolbars) {
            chartToolbar.setWidgetIconState(false);
        }
    }

    @Override
    protected void revealInParent() {
        gridGroupRemovedHandler = getEventBus().addHandler(
                GridGroupRemovedEvent.TYPE, this);
        RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
                this);
        // allSeries.clear();
        // resetSeries();
        loadTemplateHandlerRegistration = getEventBus().addHandler(
                LoadTemplateEvent.TYPE, this);
        saveTemplateHandlerRegistration = getEventBus().addHandler(
                SaveTemplateEvent.TYPE, this);
    }

    public void revealTableView(final String chartName,
                                final String[] seriesIds, final Double[] extremes,
                                final String timezone) {
        PlaceRequest.Builder placeRequestBuilder = new PlaceRequest.Builder()
                .nameToken(QoSNameTokens.tableResults);
        MChartSeries series = null;
        for (int i = 0; i < seriesIds.length; i++) {
            series = getView().findSeriesByKey(
                    MChartSeries.getUniqueKey(chartName, seriesIds[i]));
            placeRequestBuilder = placeRequestBuilder.with(
                    RequestParams.taskKey + i, series.getTask().getKey());
            placeRequestBuilder = placeRequestBuilder.with(
                    RequestParams.parameterIdentifier + i, series
                            .getParameter().getParameterIdentifier()
                            .createParameterStorageKey());
        }
        placeRequestBuilder
                .with(RequestParams.startDate, Long.toString(extremes[0].longValue()))
                .with(RequestParams.endDate, Long.toString(extremes[1].longValue()))
                .with(RequestParams.timeZone, getView().getTimeZone())
                .with(RequestParams.timeZoneType, getView().getTimeZoneType().toString())
                .with(RequestParams.timeIntervalType, getView().getTimeInterval().getType().toString());

        AppUtils.getPlaceManager().revealPlace(placeRequestBuilder.build());
    }

    @Override
    public void saveState() {
        // TODO Implement
    }

    @Override
    public void saveTemplate(final SaveTemplateEvent event) {
        if (event.getTemplate() instanceof MUserResultTemplate) {
            final MUserResultTemplate template = (MUserResultTemplate) event
                    .getTemplate();
            template.setSeries(new HashSet<MChartSeries>(getAllSeries()));
            if (template.getSeries().size() == 0) {
                AppUtils.showInfoMessage(messages.emptySeries());
            }
            template.setTimeInterval(getTimeInterval());
            template.setChartsSynchronizationEnabled(getView()
                    .isChartsSynchronizationEnabled());
            if (template.isValid()) {
                setCurrentTemplate(template);
            } else {
                saveTemplatePresenter.setTemplate(template.getName());
            }
        }
    }

    private void setCurrentTemplate(final MUserAbstractTemplate template) {
        selectedTemplateName = template.getName();
        setTemplateLabel(selectedTemplateName);
        saveTemplatePresenter.setTemplate(selectedTemplateName);
        loadTemplatePresenter.setTemplate(selectedTemplateName);
    }

    private void setTemplateLabel(final String templateName) {
        getView().setTemplateLabel(templateName);
    }

    public void updateWidgetIcons() {
        resetWidgetIcons();
        userService.getDashboard(
                AppUtils.getCurrentUser().getUser().getLogin(),
                new AutoNotifyingAsyncLogoutOnFailureCallback<MDashboard>(messages
                        .loadDashbordFail(), true) {
                    @Override
                    protected void success(final MDashboard dashboard) {
                        if (dashboard != null) {
                            final Map<String, DashboardChartWidget> chartWidgets = dashboard
                                    .getChartWidgets();
                            final TimeInterval.Type intervalType = getView()
                                    .getTimeInterval().getType();
                            for (final DashboardChartWidget chartWidget : chartWidgets
                                    .values()) {
                                final String chartName = chartWidget
                                        .getChartName();
                                final ChartToolbar toolbar = getView()
                                        .getChartToolbar(chartName);
                                if (toolbar != null
                                        && intervalType == chartWidget
                                        .getIntervalType()) {
                                    final Set<ChartSeriesData> seriesForChartName = new HashSet<ChartSeriesData>(
                                            getChartSeriesDataByChartName(chartName));
                                    final Set<ChartSeriesData> seriesForWidget = new HashSet<ChartSeriesData>(
                                            chartWidget.getSeriesData());
                                    if (seriesForChartName
                                            .equals(seriesForWidget)) {
                                        toolbar.setWidgetIconState(true);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public void updateWidgetIconsRelatedToChart(final String chartName) {
        final ChartToolbar toolbar = getView().getChartToolbar(chartName);
        if (toolbar != null) {
            userService.getDashboard(AppUtils.getCurrentUser().getUser()
                    .getLogin(), new AutoNotifyingAsyncLogoutOnFailureCallback<MDashboard>(
                    messages.loadDashbordFail(), true) {
                @Override
                protected void success(final MDashboard dashboard) {
                    if (dashboard != null) {
                        boolean widgetExists = false;

                        final Map<String, DashboardChartWidget> chartWidgets = dashboard
                                .getChartWidgetsByChartName(chartName);
                        final TimeInterval.Type intervalType = getView()
                                .getTimeInterval().getType();
                        for (final DashboardChartWidget chartWidget : chartWidgets
                                .values()) {
                            if (intervalType == chartWidget.getIntervalType()) {
                                final Set<ChartSeriesData> seriesForChartName = new HashSet<ChartSeriesData>(
                                        getChartSeriesDataByChartName(chartName));
                                final Set<ChartSeriesData> seriesForWidget = new HashSet<ChartSeriesData>(
                                        chartWidget.getSeriesData());
                                if (seriesForChartName.equals(seriesForWidget)) {
                                    widgetExists = true;
                                }
                            }
                        }

                        toolbar.setWidgetIconState(widgetExists);
                    }
                }
            });

        }
    }

    public boolean validate(final MChartSeries series) {
        boolean result = true;
        if (ChartResultUtils.findSeriesByKey(this.allSeries,
                series.getUniqueKey()) != null) {
            AppUtils.showInfoWithConfirmMessage(messages
                    .seriesExistsConstraint());
            return false;
        }
        try {
            if (!validateParameterTypeAndUnits(series.getChartName(),
                    series.getParameter())) {
                AppUtils.showInfoMessage(messages
                        .equalsMeasureUnitsOneChartConstraint());
                result = false;
            }
        } catch (final InappropriateParameterTypeException e) {
            AppUtils.showErrorMessage("Validation error", e);
            result = false;
        }
        return result;
    }

    public boolean validateParameterTypeAndUnits(final String chartName,
                                                 final MResultParameterConfiguration parameter)
            throws InappropriateParameterTypeException {
        boolean result = true;
        final List<MChartSeries> list = ChartResultUtils.findSeriesByChartName(
                allSeries, chartName);

        if (list.isEmpty()) {
            result = true;
        } else if (ParameterType.LEVEL.equals(parameter.getType())
                || ParameterType.COUNTER.equals(parameter.getType())) {
            final MChartSeries series = list.get(0);

            // for null units assign empty string
            final String units1 = (series.getParameter().getUnits() != null)
                    ? series.getParameter().getUnits()
                    : "";
            final String units2 = (parameter.getUnits() != null) ? parameter
                    .getUnits() : "";

            if (!series.getParameter().getType().equals(parameter.getType())
                    || !units1.equals(units2)) {
                result = false;
            }
        } else if (ParameterType.BOOL.equals(parameter.getType())
                || ParameterType.PERCENTAGE.equals(parameter.getType())) {
            final MChartSeries series = list.get(0);
            if (!series.getParameter().getType().equals(parameter.getType())) {
                result = false;
            }
        } else if (ParameterType.PROPERTY.equals(parameter.getType())) {
            throw new InappropriateParameterTypeException(
                    messages.unsupportedParamTypeConstraint(ParameterType.PROPERTY
                            .toString()));
        }
        return result;
    }
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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
import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.ExportResultsWrapper.I18nResultLabels;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.gwt.client.DataExporter;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.QoSServlets;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.chart.ChartZoomChangedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartZoomChangedEvent.ChartZoomChangedEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartSettings;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.ChartsGatekeeper;
import com.tecomgroup.qos.gwt.client.utils.*;
import com.tecomgroup.qos.gwt.client.view.desktop.ChartToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeWidget;
import com.tecomgroup.qos.service.ResultRetrieverAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.TaskStatus;

/**
 * @author ivlev.e
 * 
 */
public class TableResultPresenter
		extends
			Presenter<TableResultPresenter.TableResultsView, TableResultPresenter.TableResultProxy>
		implements
			UiHandlers,
			ChartZoomChangedEventHandler,
			DataExporter<ExportResultsWrapper> {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.tableResults)
	@UseGatekeeper(ChartsGatekeeper.class)
	public static interface TableResultProxy
			extends
				ProxyPlace<TableResultPresenter> {
	}

	public static interface TableResultsView
			extends
				View,
				HasUiHandlers<TableResultPresenter> {
		void clear();

		void closeResultsExportProgressDialog();

		void createDynamicGrid(
				Map<MAgentTask, List<MResultParameterConfiguration>> columnInfo);

		Date getEndDate();

		List<String> getGridColumnsTitles();

		String getSelectedTimeZoneLabel();

		Date getStartDate();

		String getTimeZone();

		int getTimeZoneOffset();

		TimeZoneType getTimeZoneType();

		void initDateParameter(String timeZone, TimeZoneType timeZoneType, Type timeIntervalType, Long startDate, Long endDate);

		void openResultsExportProgressDialog(boolean rawData);

		void setChartName(String chartName);

		void setData(List<Map<String, Object>> data);

		void updateResultsExportProgress(byte percentages, String progressText);

		void updateResultsExportProgressText(String progressText);
	}

	private static Logger LOGGER = Logger.getLogger(TableResultPresenter.class
			.getName());

	private final static String TASK_CHECK_REQUEST_INTERVAL_PROPERTY = "client.results.export.check.request.pause.in.sec";

	private final static String CHART_CONTAINER_ID = "tableResultChartContainer";

	private final static String CHART_NAME_POSTFIX = "_tableResults_postfix";

	public static PlaceRequest createChartRequest(final String taskKey,
			final String parameterIdentifier, final Date startDateTime,
			final Date endDateTime, final String timeZone,
			final TimeZoneType timeZoneType, final String chartName) {
		return createRequest(QoSNameTokens.chartResults, taskKey,
				parameterIdentifier, startDateTime, endDateTime, timeZone,
				timeZoneType, chartName);
	}

	private static PlaceRequest createRequest(final String nameToken,
			final String taskKey, final String parameterIdentifier,
			final Date startDateTime, final Date endDateTime,
			final String timeZone, final TimeZoneType timeZoneType,
			final String chartName) {
		return new PlaceRequest.Builder()
				.nameToken(nameToken)
				.with(RequestParams.taskKey + 0, taskKey)
				.with(RequestParams.parameterIdentifier + 0,
						parameterIdentifier)
				.with(RequestParams.startDate,
						Long.toString(startDateTime.getTime()))
				.with(RequestParams.endDate,
						Long.toString(endDateTime.getTime()))
				.with(RequestParams.timeZone, timeZone)
				.with(RequestParams.timeZoneType,
						timeZoneType == null ? null : timeZoneType.toString())
				.with(RequestParams.chartName, chartName).build();

	}

	/**
	 * Creates new {@link PlaceRequest} to go to Result page.
	 * 
	 * @param taskKey
	 * @param parameterIdentifier
	 *            String representing {@link ParameterIdentifier} created by
	 *            {@link ParameterIdentifier#createParameterStorageKey()}
	 * @param startDateTime
	 *            The start date to show resuls.
	 * @param endDateTime
	 *            The end date to show results.
	 * @param timeZone
	 *            Can be null.
	 * @param timeZoneType
	 * @param chartName
	 * @return {@link PlaceRequest}
	 */
	public static PlaceRequest createResultRequest(final String taskKey,
			final String parameterIdentifier, final Date startDateTime,
			final Date endDateTime, final String timeZone,
			final TimeZoneType timeZoneType, final String chartName) {
		return createRequest(QoSNameTokens.tableResults, taskKey,
				parameterIdentifier, startDateTime, endDateTime, timeZone,
				timeZoneType, chartName);
	}

	private long exportRunningTaskId;

	private boolean resultExportUserCanceled;

	public final int exportTaskStatusCheckInterval;

	private final ResultRetrieverAsync resultService;

	private final TaskRetrieverAsync taskRetriever;

	private String timeZone;

	private TimeZoneType timeZoneType;

	private Type timeIntervalType;

	private Map<String, Collection<?>> taskParameters;

	private List<String> taskKeys;

	private List<String> parameterIdentifiers;

	private String chartName;

	/**
	 * Contains raw up-to-the-seconds value
	 */
	private Long startDate;

	/**
	 * Contains raw up-to-the-seconds value
	 */
	private Long endDate;

	private Date zoomedStartDate;

	private Date zoomedEndDate;

	private final ResultsAnalyticsPresenter resultsAnalyticsPresenter;

	private final ChartWidgetPresenter chartWidgetPresenter;

	private Map<MAgentTask, List<MResultParameterConfiguration>> columnInfo;

	private I18nResultLabels resultsLabels;

	private final QoSMessages messages;

	@Inject
	public TableResultPresenter(final EventBus eventBus,
			final TableResultsView view, final TableResultProxy proxy,
			final ResultRetrieverAsync resultService,
			final TaskRetrieverAsync taskRetriever,
			final ResultsAnalyticsPresenter resultsAnalyticsPresenter,
			final ChartWidgetPresenter chartWidgetPresenter,
			final QoSMessages messages) {
		super(eventBus, view, proxy);
		this.resultService = resultService;
		this.taskRetriever = taskRetriever;
		this.resultsAnalyticsPresenter = resultsAnalyticsPresenter;
		this.chartWidgetPresenter = chartWidgetPresenter;
		this.messages = messages;
		view.setUiHandlers(this);
		getEventBus().addHandler(ChartZoomChangedEvent.TYPE, this);

		exportTaskStatusCheckInterval = Integer.valueOf((String) AppUtils
				.getClientProperties()
				.get(TASK_CHECK_REQUEST_INTERVAL_PROPERTY))
				* TimeConstants.MILLISECONDS_PER_SECOND;
	}

	public void actionExportResults(final boolean rawData) {
		export(getDataWrapper(rawData));
	}

	private void awaitingExportTaskCompletion() {
		final Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(RequestParams.exportResultsTaskParameterName,
				Long.toString(exportRunningTaskId));

		final RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.GET, AppUtils.buildUrl(
						QoSServlets.downloadResultServlet + "/"
								+ QoSServlets.downloadResultServletStatusUrl,
						requestParameters));

		requestBuilder.setCallback(new RequestCallback() {

			@Override
			public void onError(final Request request, final Throwable throwable) {
				showExceptionErrorMessage(throwable);

				getView().closeResultsExportProgressDialog();
			}

			@Override
			public void onResponseReceived(final Request request,
					final Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					final String responseText = response.getText();
					if (responseText.startsWith(TaskStatus.RUNNING.name())) {
						final String percentageString = responseText
								.substring(TaskStatus.RUNNING.name().length() + 1);
						final Byte percent = Byte.parseByte(percentageString);
						getView().updateResultsExportProgress(percent,
								messages.resultsRetrieval(percent));
						final Timer timer = new Timer() {
							@Override
							public void run() {
								if (!resultExportUserCanceled) {
									awaitingExportTaskCompletion();
								}
							}
						};
						timer.schedule(exportTaskStatusCheckInterval);
					} else if (TaskStatus.COMPLETED.name().equals(responseText)
							&& !resultExportUserCanceled) {
						getView()
								.updateResultsExportProgress(
										SimpleUtils.MAX_PERCENTAGE_VALUE,
										messages.resultsRetrieval(SimpleUtils.MAX_PERCENTAGE_VALUE));
						// sleep for several seconds to see the last updated
						// progress status
						final Timer timer = new Timer() {
							@Override
							public void run() {
								downloadExportedResults();
							}
						};
						timer.schedule(TimeConstants.MILLISECONDS_PER_SECOND);
					} else if (TaskStatus.CANCELLED.name().equals(responseText)) {
						getView().closeResultsExportProgressDialog();
					} else if (responseText.startsWith(TaskStatus.ERROR.name())) {
						final String receivedErrorMessage = responseText
								.substring(TaskStatus.ERROR.name().length() + 1);

						final String errorMessage = messages
								.exportingResultsFail(exportRunningTaskId)
								+ ": " + receivedErrorMessage;
						LOGGER.severe(errorMessage);
						AppUtils.showErrorMessage(errorMessage);

						getView().closeResultsExportProgressDialog();
					}
				} else {
					showStatusCodeErrorMessage(response.getStatusCode());

					getView().closeResultsExportProgressDialog();
				}
			}
		});
		try {
			requestBuilder.send();
		} catch (final RequestException requestException) {
			showExceptionErrorMessage(requestException);
		}
	}

	public void cancelResultsExport() {
		final Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put(RequestParams.exportResultsTaskParameterName,
				Long.toString(exportRunningTaskId));
		final RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.DELETE, AppUtils.buildUrl(
						QoSServlets.downloadResultServlet, requestParameters));
		// RequestBuilder won't work without callback
		requestBuilder.setCallback(new RequestCallback() {
			@Override
			public void onError(final Request request, final Throwable exception) {
				// do nothing, task should be canceled anyway
			}

			@Override
			public void onResponseReceived(final Request request,
					final Response response) {
				// do nothing
			}
		});

		resultExportUserCanceled = true;
		try {
			requestBuilder.send();
		} catch (final RequestException requestException) {
			showExceptionErrorMessage(requestException);
		}
	}

	private List<MChartSeries> convertColumnInfoToChartSeries(
			final Map<MAgentTask, List<MResultParameterConfiguration>> columnInfo) {
		final List<MChartSeries> seriesList = new ArrayList<MChartSeries>();
		for (final Entry<MAgentTask, List<MResultParameterConfiguration>> entry : columnInfo
				.entrySet()) {
			for (final MResultParameterConfiguration parameter : entry
					.getValue()) {
				seriesList.add(new MChartSeries(entry.getKey(), parameter,
						chartName));
			}
		}
		return seriesList;
	}

	private void createExportResultsTask(final String jsonParameters,
			final boolean rawData) {
		final RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.POST, AppUtils.buildUrl(
						QoSServlets.downloadResultServlet,
						new HashMap<String, String>()));
		requestBuilder.setHeader("Content-Type", "application/json");
		requestBuilder.setRequestData(jsonParameters);
		requestBuilder.setCallback(new RequestCallback() {

			@Override
			public void onError(final Request request, final Throwable throwable) {
				showExceptionErrorMessage(throwable);
			}

			@Override
			public void onResponseReceived(final Request request,
					final Response response) {
				final int statusCode = response.getStatusCode();
				if (statusCode == Response.SC_OK) {
					resultExportUserCanceled = false;

					exportRunningTaskId = Long.parseLong(response.getText());
					getView().openResultsExportProgressDialog(rawData);
					awaitingExportTaskCompletion();
				} else {
					showStatusCodeErrorMessage(statusCode);
					getView().closeResultsExportProgressDialog();
				}
			}
		});
		try {
			requestBuilder.send();
		} catch (final RequestException requestException) {
			showExceptionErrorMessage(requestException);
		}
	}

	public void doUpdateAction() {
		ChartResultUtils.setTimezoneOffset(-getView().getTimeZoneOffset());
				ChartResultUtils
				.zoomChart(getUniqueChartName(chartName), getView()
						.getStartDate().getTime(), getView().getEndDate()
						.getTime(), getView().getTimeZone(), getView()
				.getTimeZoneType().toString());
	}

	private void downloadExportedResults() {
		getView().updateResultsExportProgressText(
				messages.resultsFileDownloading());

		// sleep for several seconds to see the last updated
		// progress status
		final Timer timer = new Timer() {
			@Override
			public void run() {
				final Map<String, String> requestParameters = new HashMap<String, String>();
				requestParameters.put(
						RequestParams.exportResultsTaskParameterName,
						Long.toString(exportRunningTaskId));

				Window.Location.replace(AppUtils.buildUrl(
						QoSServlets.downloadResultServlet + "/"
								+ QoSServlets.downloadResultServletResultUrl,
						requestParameters));

				getView().closeResultsExportProgressDialog();
			}
		};
		timer.schedule(TimeConstants.MILLISECONDS_PER_SECOND);
	}

	@Override
	public void export(final ExportResultsWrapper exportResultsWrapper) {
		resultService.serializeBean(exportResultsWrapper,
				new AutoNotifyingAsyncLogoutOnFailureCallback<String>(
						"Unable to serialize ExportResultsWrapper", false) {

					@Override
					protected void success(final String jsonPayload) {
						createExportResultsTask(jsonPayload,
								exportResultsWrapper.rawData);
					}
				});
	}

	@Override
	public Collection<String> getAgentDisplayNames() {
		final Collection<String> agentDisplayNames = new LinkedHashSet<String>();
		for (final MAgentTask task : columnInfo.keySet()) {
			agentDisplayNames.add(SimpleUtils.findSystemComponent(task)
					.getDisplayName());
		}
		return agentDisplayNames;
	}

	public ExportResultsWrapper getDataWrapper(final boolean rawData) {
		boolean booleanResults = false;
		if (columnInfo.values().iterator().next().get(0).getType()
				.equals(ParameterType.BOOL)) {
			booleanResults = true;
		}
		resultsLabels.parameterDisplayNames = getView().getGridColumnsTitles();

		return new ExportResultsWrapper(taskKeys, parameterIdentifiers,
				getStartDate(), getEndDate(), getView().getTimeZone(),
				DateUtils.getCurrentTimeZoneAsString(), getView()
						.getTimeZoneType(), resultsLabels,
				getAgentDisplayNames(), getView().getSelectedTimeZoneLabel(),
				DateTimeWidget.getCurrentLocaleDateTimeFormat().getPattern(), DateUtils.getLocale(),
				booleanResults, rawData);
	}

	/**
	 * Returns refined up-to-the-minutes value
	 */
	private Date getEndDate() {
		return zoomedEndDate == null ? getView().getEndDate() : zoomedEndDate;
	}

	/**
	 * Returns refined up-to-the-minutes value
	 */
	private Date getStartDate() {
		return zoomedStartDate == null
				? getView().getStartDate()
				: zoomedStartDate;
	}

	private String getUniqueChartName(final String userChartName) {
		return userChartName + CHART_NAME_POSTFIX;
	}

	private void loadTableResults(
			final Map<String, Collection<?>> taskParameters,
			final Date startDate, final Date endDate,
			final AsyncCallback<List<Map<String, Object>>> callback) {
		resultService.getResults(taskParameters, null, TimeInterval.get(startDate, endDate), 0l, Long.MAX_VALUE,
				OrderType.ASC, callback);
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(messages.chart(), chartWidgetPresenter);
		resultsLabels = new I18nResultLabels(messages.results(),
				messages.probe(), messages.startDateTime(),
				messages.endDateTime(), messages.timezone(), null,
				messages.noData(), messages.actionYes(), messages.actionNo(),
				messages.startOfData(), messages.endOfData());
	}

	@Override
	public void onChartZoomChanged(final ChartZoomChangedEvent event) {
		if (this.isVisible()) {
			zoomedStartDate = event.getStartDate();
			zoomedEndDate = event.getEndDate();
			loadTableResults(
					taskParameters,
					zoomedStartDate,
					zoomedEndDate,
					new AutoNotifyingAsyncLogoutOnFailureCallback<List<Map<String, Object>>>() {
						@Override
						protected void success(
								final List<Map<String, Object>> data) {
							updateResultGrid(data);
						}
					});
		}
	}

	private void onCreatePage(final List<Map<String, Object>> data) {
		updateResultGrid(data);
		final List<MChartSeries> seriesList = convertColumnInfoToChartSeries(columnInfo);
		final ChartType chartType = ChartResultUtils
				.resolveChartType(seriesList);
		final ChartToolbar toolbar = resultsAnalyticsPresenter.getChartToolbar(
				chartName, chartType);
		// note that usage of up-to-the-seconds startDate and
		// endDate variables is prohibited here, use
		// up-to-the-minutes getStartDate() and getEndDate()
		// instead.
		final ChartSettings settings = new ChartSettings.Builder()
				.chartName(getUniqueChartName(chartName))
				.chartType(chartType)
				.series(seriesList)
				.startDate(getStartDate())
				.endDate(getEndDate())
				.timeZoneOffset(getView().getTimeZoneOffset())
				.divElementId(CHART_CONTAINER_ID)
				.autoscaling(toolbar.isAutoscalingEnabled())
				.thresholds(toolbar.isThresholdsEnabled())
				.captions(toolbar.isCaptionsEnabled())
				.lineType(toolbar.getLineType())
				.mouseTracking(toolbar.isMouseTrackingEnabled()).build();
		chartWidgetPresenter.createChart(settings);
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
		taskKeys = RequestParams.getParametersSortByIndex(request,
				RequestParams.taskKey);
		parameterIdentifiers = RequestParams.getParametersSortByIndex(request,
				RequestParams.parameterIdentifier);
		taskParameters = RequestParams.getRelatedParameters(taskKeys,
				parameterIdentifiers);
		startDate = Long.parseLong(request.getParameter(
				RequestParams.startDate, null));
		endDate = Long.parseLong(request.getParameter(RequestParams.endDate,
				null));
		chartName = request.getParameter(RequestParams.chartName, null);
		timeZone = request.getParameter(RequestParams.timeZone, null);
		timeZoneType = TimeZoneType.valueOf(request.getParameter(
				RequestParams.timeZoneType, null));
		timeIntervalType = Type.valueOf(request.getParameter(RequestParams.timeIntervalType, Type.CUSTOM.toString()));
		zoomedStartDate = null;
		zoomedEndDate = null;
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);

		if (chartName != null) {
			ChartResultUtils.removeChart(getUniqueChartName(chartName));
		}
		getView().clear();
		getView().setChartName(chartName);
		Timer timer = new Timer() {
			@Override
			public void run() {
				getView().initDateParameter(timeZone, timeZoneType, timeIntervalType, startDate, endDate);
				taskRetriever.getTasksByKeys(
						taskKeys,
						false,
						new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>(messages.tasksLoadingFail(), true) {

							@Override
							public void success(final List<MAgentTask> tasks) {
								columnInfo = new LinkedHashMap<MAgentTask, List<MResultParameterConfiguration>>();
								final Map<String, MAgentTask> taskMap = SimpleUtils.getMap(tasks);
								for (final Map.Entry<String, Collection<?>> taskParametersEntry : taskParameters.entrySet()) {
									final MAgentTask task = taskMap.get(taskParametersEntry.getKey());
									final List<MResultParameterConfiguration> parameters = task.getResultConfiguration()
											                                                   .findParameterConfigurations(taskParametersEntry.getValue());
									if (!parameters.isEmpty()) {
										columnInfo.put(task, parameters);
									}
								}
								getView().createDynamicGrid(columnInfo);
								// note that usage of up-to-the-seconds startDate and endDate variables is prohibited here, use
								// up-to-the-minutes getStartDate() and getEndDate() instead.
								loadTableResults(
										taskParameters,
										getStartDate(),
										getEndDate(),
										new AutoNotifyingAsyncLogoutOnFailureCallback<List<Map<String, Object>>>() {
											@Override
											protected void success(
													final List<Map<String, Object>> data) {
												onCreatePage(data);
											}
										});
							}
						});
			}
		};
		timer.schedule(1000);
	}

	private void showExceptionErrorMessage(final Throwable throwable) {
		final String errorMessage = messages
				.exportingResultsFail(exportRunningTaskId)
				+ ": "
				+ throwable.getMessage();
		LOGGER.severe(errorMessage);
		AppUtils.showErrorMessage(errorMessage);
	}

	private void showStatusCodeErrorMessage(final int statusCode) {
		final String errorMessage = messages
				.exportingResultsFail(exportRunningTaskId)
				+ ", status code is " + statusCode;
		LOGGER.severe(errorMessage);
		AppUtils.showErrorMessage(errorMessage);
	}

	private void updateResultGrid(final List<Map<String, Object>> data) {
		getView().setData(data);
	}
}

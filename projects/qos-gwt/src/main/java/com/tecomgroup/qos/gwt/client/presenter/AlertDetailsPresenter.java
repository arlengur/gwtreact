/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.ChartType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.HasPostActionCallback.PostActionCallback;
import com.tecomgroup.qos.gwt.client.event.NavigateToResultDetailsEvent;
import com.tecomgroup.qos.gwt.client.event.NavigateToSourceEvent;
import com.tecomgroup.qos.gwt.client.event.alert.AcknowledgeAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.alert.ClearAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.alert.CommentAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.alert.UnacknowledgeAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AlertDetailsPresenter.AlertDetailsView;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter.Property;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.*;
import com.tecomgroup.qos.gwt.client.utils.*;
import com.tecomgroup.qos.gwt.shared.JSEvaluator;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.PolicyUtils;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author novohatskiy.r
 * 
 */
public abstract class AlertDetailsPresenter<V extends AlertDetailsView, P extends ProxyPlace<? extends AlertDetailsPresenter<?, ?>>>
		extends
			Presenter<V, P> implements UiHandlers {

	/**
	 * 
	 * @author abondin
	 * 
	 */
	public static interface AlertDetailsView
			extends
				View,
				HasUiHandlers<AlertDetailsPresenter<?, ?>> {
		static String COMMENTS_GRID = "commentsGrid";
		static String PROPERTIES_GRID = "propertiesGrid";
		void displayAlert(MAlert alert);
	}

	/**
	 * 
	 * @author abondin
	 * 
	 */
	protected static final class AlertKeyPropertiesContainer {
		protected final String alertTypeNameRequestParameter;
		protected final String sourceKeyRequestParameter;
		protected final String originatorKeyRequestParameter;
		protected final String settingsRequestParameter;
		/**
		 * @param alertTypeNameRequestParameter
		 * @param sourceKeyRequestParameter
		 * @param originatorKeyRequestParameter
		 * @param settingsRequestParameter
		 */
		public AlertKeyPropertiesContainer(
				final String alertTypeNameRequestParameter,
				final String sourceKeyRequestParameter,
				final String originatorKeyRequestParameter,
				final String settingsRequestParameter) {
			super();
			this.alertTypeNameRequestParameter = alertTypeNameRequestParameter;
			this.sourceKeyRequestParameter = sourceKeyRequestParameter;
			this.originatorKeyRequestParameter = originatorKeyRequestParameter;
			this.settingsRequestParameter = settingsRequestParameter;
		}

	}

	private static Logger LOGGER = Logger.getLogger(AlertDetailsPresenter.class
			.getName());

	public final static String CHART_CONTAINER_ID = "alertDetailsChartContainer";

	/**
	 * Creates {@link PlaceRequest} to go to AlertDetails page.
	 */
	public static PlaceRequest createAlertDetailsRequest(final MAlert alert) {
		return new PlaceRequest.Builder()
				.nameToken(QoSNameTokens.alertDetails)
				.with(RequestParams.alertTypeName,
						alert.getAlertType().getName())
				.with(RequestParams.originatorKey,
						alert.getOriginator().getKey())
				.with(RequestParams.sourceKey, alert.getSource().getKey())
				.with(RequestParams.settings, alert.getSettings()).build();
	}

	private final Map<String, PresenterWidget<?>> tabs;

	protected MAlert alert;

	private final SingleAlertHistoryGridWidgetPresenter singleAlertHistoryGridWidgetPresenter;

	private final AlertCommentsGridWidgetPresenter alertCommentsGridWidgetPresenter;

	private final PropertyGridWidgetPresenter propertiesGridWidgetPresenter;

	private final ChartWidgetPresenter chartWidgetPresenter;

	private static final NumberFormat numberFormat = NumberFormat
			.getFormat(SimpleUtils.NUMBER_FORMAT);

	private final AlertServiceAsync alertService;

	protected final QoSMessages messages;

	private final PlaceManager placeManager;

	private AlertKeyPropertiesContainer alertPropertiesContainer;

	private final Long resultTimeShift;

	private final static String CHART_NAME_POSTFIX = "_alertDetails_postfix";

	/**
	 * Indicates if alert data have never been loaded to the presenter.
	 */
	protected boolean initialAlertLoad = true;

	private final TaskRetrieverAsync taskRetriever;

	@Inject
	public AlertDetailsPresenter(
			final EventBus eventBus,
			final V view,
			final P proxy,
			final SingleAlertHistoryGridWidgetPresenter singleAlertHistoryGridWidgetPresenter,
			final ChartWidgetPresenter chartWidgetPresenter,
			final AlertCommentsGridWidgetPresenter alertCommentsGridWidgetPresenter,
			final PropertyGridWidgetPresenter propertiesGridWidgetPresenter,
			final AlertServiceAsync alertService,
			final TaskRetrieverAsync taskRetriever,
			@Named("clientProperties") final Map<String, Object> clientProperties) {
		super(eventBus, view, proxy);
		this.taskRetriever = taskRetriever;
		this.alert = null;
		this.chartWidgetPresenter = chartWidgetPresenter;
		this.singleAlertHistoryGridWidgetPresenter = singleAlertHistoryGridWidgetPresenter;
		this.alertCommentsGridWidgetPresenter = alertCommentsGridWidgetPresenter;
		this.propertiesGridWidgetPresenter = propertiesGridWidgetPresenter;
		this.alertService = alertService;
		this.messages = AppUtils.getMessages();
		this.placeManager = AppUtils.getPlaceManager();

		this.tabs = new LinkedHashMap<String, PresenterWidget<?>>();
		this.tabs.put(messages.chart(), chartWidgetPresenter);
		this.tabs.put(messages.alertHistory(),
				singleAlertHistoryGridWidgetPresenter);
		view.setUiHandlers(this);

		resultTimeShift = Long
				.parseLong(((String) clientProperties
						.get(AbstractAlertsGridWidgetPresenter.RESULT_TIME_SHIFT_IN_SEC_FOR_ALERT))
						.trim())
				* TimeConstants.MILLISECONDS_PER_SECOND;
	}

	public void actionAcknowledgeAlert(final String comment) {
		getEventBus().fireEvent(
				new AcknowledgeAlertsEvent(Arrays.asList(new MAlert[]{alert}),
						comment, new PostActionCallback() {
							@Override
							public void actionPerformed(
									final Object executionResult) {
								reload();
							}
						}));
	}

	public void actionClearAlert(final String comment) {
		getEventBus().fireEvent(
				new ClearAlertsEvent(Arrays.asList(new MAlert[]{alert}),
						comment, new PostActionCallback() {
							@Override
							public void actionPerformed(
									final Object executionResult) {
								reload();
							}
						}));
	}

	public void actionNavigateToChart() {
		final ParameterIdentifier parameterIdentifier = ConfigurationUtil
				.getAssociatedParameterIdentifier(alert);
		if (parameterIdentifier != null) {
			navigateToChart(parameterIdentifier);
		} else {
			AppUtils.showInfoWithConfirmMessage(messages
					.noResultsAssociatedWithAlert());
		}

	}

	public void actionNavigateToResultDetails() {
		final ParameterIdentifier parameterIdentifier = ConfigurationUtil
				.getAssociatedParameterIdentifier(alert);
		if (parameterIdentifier != null
				&& alert.getSource() instanceof MAgentTask) {
			getEventBus().fireEvent(
					new NavigateToResultDetailsEvent(alert.getSource(),
							parameterIdentifier, getAlertStartTime(),
							getAlertEndTime(), null, TimeZoneType.LOCAL, alert
									.getAlertType().getDisplayName(), null));
		} else {
			AppUtils.showInfoWithConfirmMessage(messages
					.noResultsAssociatedWithAlert());
		}
	}

	public void actionNavigateToSource() {
		getEventBus().fireEvent(
				new NavigateToSourceEvent(alert.getSource(), null));
	}

	public void actionSelectGridPresenter(final String tabKey) {
		final PresenterWidget<?> presenter = tabs.get(tabKey);
		if (presenter instanceof SingleAlertHistoryGridWidgetPresenter) {
			final SingleAlertHistoryGridWidgetPresenter singleAlertHistoryGridWidgetPresenter = (SingleAlertHistoryGridWidgetPresenter) presenter;
			singleAlertHistoryGridWidgetPresenter.loadHistory(alert);
		}
	}

	public void actionUnAcknowledgeAlert(final String comment) {
		getEventBus().fireEvent(
				new UnacknowledgeAlertsEvent(
						Arrays.asList(new MAlert[]{alert}), comment,
						new PostActionCallback() {
							@Override
							public void actionPerformed(
									final Object executionResult) {
								reload();
							}
						}));
	}

	private List<Property> collectProperties(final MAlert alert) {
		final List<Property> properties = new ArrayList<Property>();

		properties.add(new Property(messages.perceivedSeverity(), alert
				.getPerceivedSeverity()));
		properties.add(new Property(messages.lastUpdateDateTime(),
				DateUtils.DATE_TIME_FORMATTER.format(alert
						.getLastUpdateDateTime())));
		properties.add(new Property(messages.severityChangeDateTime(),
				DateUtils.DATE_TIME_FORMATTER.format(alert
						.getSeverityChangeDateTime())));
		properties.add(new Property(messages.creationDateTime(),
				DateUtils.DATE_TIME_FORMATTER.format(alert
						.getCreationDateTime())));
		properties.add(new Property(messages.probableCause(), alert
				.getAlertType().getProbableCause()));

		final Property thresholdProperty = createThresholdProperty(alert);
		if (thresholdProperty != null) {
			properties.add(thresholdProperty);
			if (alert.getDetectionValue() != null) {
				properties.add(new Property(messages.detectionTimeValue(),
						numberFormat.format(alert.getDetectionValue())));
			} else {
				LOGGER.log(Level.SEVERE, "Alert doesn't have detection value.");
			}
		}

		properties
				.add(new Property(messages.acknowledged(), alert
						.isAcknowledged() ? messages.actionYes() : messages
						.actionNo()));
		properties.add(new Property(messages.count(), alert.getAlertCount()));
		String durationValue = null;

		durationValue = DateUtils.formatDuration(alert.getDuration(), messages);

		properties.add(new Property(messages.duration(), durationValue));
		properties.add(new Property(messages.source(), alert.getSource()
				.getDisplayName()));
		properties.add(new Property(messages.originator(), alert
				.getOriginator().getDisplayName()));
		properties.add(new Property(messages.active(), MAlert.isActive(alert
				.getStatus()) ? messages.actionYes() : messages.actionNo()));
		String alertSettings = alert.getSettings();
		if (alertSettings == null || alertSettings.isEmpty()) {
			alertSettings = messages.actionNo();
		}
		properties.add(new Property(messages.settings(), alertSettings));
		properties.add(new Property(messages.description(), alert
				.getAlertType().getDescription()));

		return properties;
	}

	public void commentAlert(final String comment) {
		getEventBus().fireEvent(
				new CommentAlertsEvent(Arrays.asList(new MAlert[]{alert}),
						comment, new PostActionCallback() {
							@Override
							public void actionPerformed(
									final Object executionResult) {
								alertCommentsGridWidgetPresenter.reload(false);
								singleAlertHistoryGridWidgetPresenter
										.reload(false);
							}
						}));
	}

	private Property createThresholdProperty(final MAlert alert) {
		Property thresholdProperty = null;
		try {
			final Double thresholdValue = PolicyUtils.getAlertThresholdValue(
					alert, JSEvaluator.getInstance(), messages);
			if (thresholdValue != null) {
				thresholdProperty = new Property(messages.thresholdValue(),
						numberFormat.format(thresholdValue));
			}
		} catch (final Exception ex) {
			LOGGER.log(Level.WARNING, "Unable to get alert's threshold value",
					ex);
		}
		return thresholdProperty;
	}

	private void displayChart() {
		final MAgentTask task = (MAgentTask) alert.getSource();
		final ParameterIdentifier parameterIdentifier = ConfigurationUtil
				.stringToParameterIdentifier(
						alert.getSettings(),
						true,
						ConfigurationUtil.PROPERTY_PARAMETER_SEPARATOR_FOR_ALERT_SETTINGS);
		final MResultParameterConfiguration parameter = task
				.getResultConfiguration().findParameterConfiguration(
						parameterIdentifier);
		final String chartName = alert.getAlertType().getDisplayName();
		final List<MChartSeries> seriesList = Arrays.asList(new MChartSeries(
				task, parameter, chartName));
		final ChartType chartType = ChartResultUtils
				.resolveChartType(seriesList);

		final ChartSettings settings = new ChartSettings.Builder()
				.chartName(getUniqueChartName(chartName))
				.chartType(chartType)
				.series(seriesList)
				.startDate(getAlertStartTime())
				.endDate(getAlertEndTime())
				.timeZoneOffset(DateUtils.getCurrentTimeZoneOffset())
				.divElementId(CHART_CONTAINER_ID)
				.lineType(ChartResultUtils.getDefaultLineType(chartType))
				.zoom(false).build();
		chartWidgetPresenter.createChart(settings);
	}

	private Date getAlertEndTime() {
		return new Date(alert.getSeverityChangeDateTime().getTime()
				+ resultTimeShift);
	}

	private Date getAlertStartTime() {
		return new Date(alert.getSeverityChangeDateTime().getTime()
				- resultTimeShift);
	}

	private String getUniqueChartName(final String userChartName) {
		return userChartName + CHART_NAME_POSTFIX;
	}

	private void loadAlert(final String alertTypeName, final String sourceKey,
			final String originatorKey, final String settings) {

		alertService.getAlert(alertTypeName, sourceKey, originatorKey,
				settings, new AutoNotifyingAsyncLogoutOnFailureCallback<MAlert>(
						"Alert cannot be loaded", true) {

					@Override
					protected void success(final MAlert resultAlert) {
						onAlertLoaded(resultAlert);
						if (initialAlertLoad) {
							displayChart();
						}
						if (initialAlertLoad) {
							initialAlertLoad = false;
						}
					}
				});
	}

	private void navigateToChart(final ParameterIdentifier parameterIdentifier) {
		taskRetriever.getTaskByKey(alert.getSource().getKey(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<MAgentTask>() {

					@Override
					protected void success(final MAgentTask result) {
						final String chartName = alert.getAlertType()
								.getDisplayName();

						final MResultParameterConfiguration parameterConfiguration = result
								.getResultConfiguration()
								.findParameterConfiguration(parameterIdentifier);
						final List<MChartSeries> series = Arrays
								.asList(new MChartSeries(result,
										parameterConfiguration, chartName));

						final PlaceRequest request = (new PlaceRequest.Builder())
								.nameToken(QoSNameTokens.chartResults).build();
						AppUtils.getPlaceManager().revealPlace(request);
						new Timer() {
							@Override
							public void run() {
								final TimeInterval interval = TimeInterval.get(
										TimeInterval.Type.CUSTOM,
										getAlertStartTime(), getAlertEndTime(),
										TimeZoneType.LOCAL,
										DateUtils.getCurrentTimeZoneAsString(),
										DateUtils.getCurrentTimeZoneAsString());
								AppUtils.getEventBus().fireEvent(
										new NavigateToChartEvent(chartName,
												series, interval, false, true,
												false, null));
							}
						}.schedule(TimeConstants.MILLISECONDS_PER_SECOND);
					}
				});
	}

	protected void onAlertLoaded(final MAlert alert) {
		this.alert = alert;
		getView().displayAlert(alert);
		alertCommentsGridWidgetPresenter.loadHistory(alert);
		singleAlertHistoryGridWidgetPresenter.loadHistory(alert);
		propertiesGridWidgetPresenter.setProperties(collectProperties(alert));

	}

	@Override
	protected void onBind() {
		alertPropertiesContainer = null;
		super.onBind();
		for (final Map.Entry<String, PresenterWidget<?>> entry : tabs
				.entrySet()) {
			setInSlot(entry.getKey(), entry.getValue());
		}
		setInSlot(AlertDetailsView.COMMENTS_GRID,
				alertCommentsGridWidgetPresenter);
		setInSlot(AlertDetailsView.PROPERTIES_GRID,
				propertiesGridWidgetPresenter);
	}

	@Override
	protected void onHide() {
		super.onHide();
		if (alert != null) {
			ChartResultUtils.removeChart(getUniqueChartName(alert
					.getAlertType().getDisplayName()));
		}
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
		final String alertTypeNameRequestParameter = request.getParameter(
				RequestParams.alertTypeName, null);
		final String originatorKeyRequestParameter = request.getParameter(
				RequestParams.originatorKey, null);
		final String sourceKeyRequestParameter = request.getParameter(
				RequestParams.sourceKey, null);
		final String settingsRequestParameter = request.getParameter(
				RequestParams.settings, null);
		if (alertTypeNameRequestParameter != null
				&& originatorKeyRequestParameter != null
				&& sourceKeyRequestParameter != null) {
			alertPropertiesContainer = new AlertKeyPropertiesContainer(
					alertTypeNameRequestParameter, sourceKeyRequestParameter,
					originatorKeyRequestParameter, settingsRequestParameter);
		} else {
			alertPropertiesContainer = null;
			AppUtils.showErrorMessage(messages.cannotLoadAlertDetails());
			placeManager.revealPlace(new PlaceRequest.Builder().nameToken(
					QoSNameTokens.alerts).build());
		}
	}

	private void reload() {
		loadAlert(alert.getAlertType().getName(), alert.getSource().getKey(),
				alert.getOriginator().getKey(), alert.getSettings());
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
		if (alertPropertiesContainer != null) {
			initialAlertLoad = true;
			loadAlert(alertPropertiesContainer.alertTypeNameRequestParameter,
					alertPropertiesContainer.sourceKeyRequestParameter,
					alertPropertiesContainer.originatorKeyRequestParameter,
					alertPropertiesContainer.settingsRequestParameter);
		}
	}
}
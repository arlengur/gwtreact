/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.report;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.gwt.client.ClientDateConverter;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.chart.NavigateToChartWithConfirmEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.ReportsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenterWithTemplates;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AbstractAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertReportProperties;
import com.tecomgroup.qos.service.AlertReportRetrieverAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.service.UserServiceAsync;
import com.tecomgroup.qos.util.ConfigurationUtil;

/**
 * @author ivlev.e
 * 
 */
public class ReportsGridWidgetPresenter
		extends
			AbstractRemoteDataGridWidgetPresenterWithTemplates<MAlertReport, ReportsGridWidgetPresenter.MyView> {

	public interface MyView
			extends
				AbstractRemoteDataGridWidgetPresenterWithTemplates.SharedDataGridWidgetViewWithTemplates<MAlertReport, ReportsGridWidgetPresenter> {

		<X> X cast();

		AlertReportProperties getAlertReportProperties();

		Set<PerceivedSeverity> getSelectedSeverites();

		void loadTemplate(MUserReportsTemplate template);

		void onBind();

		void setEnabledExportButton(boolean enabled);

		void initLoader();
	}

	protected final QoSMessages messages;

	private final AlertReportProperties alertReportProperties;

	private final AlertReportRetrieverAsync alertReportRetriever;

	private final TaskRetrieverAsync taskRetriever;

	private final AddAnalyticsToDashboardWidgetPresenter addAnalyticsToDashboardDialog;

	/**
	 * @see {@link ReportsGridWidgetPresenter#setReportsParameters(Set, TimeInterval)}
	 * @see {@link ReportsPresenter#setReportsParameters(TimeInterval)

	 */
	private Set<String> sourceKeys = Collections.<String> emptySet();

	/**
	 * @see {@link ReportsGridWidgetPresenter#setReportsParameters(Set, TimeInterval)}
	 * @see {@link ReportsPresenter#setReportsParameters(TimeInterval)

	 */
	private TimeInterval timeInterval;

	private final Long resultTimeShift;

	private static final TemplateType TEMPLATE_TYPE = BaseTemplateType.REPORT;

	/**
	 * @param eventBus
	 * @param view
	 */
	@Inject
	public ReportsGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final AlertReportRetrieverAsync alertReportRetriever,
			final QoSMessages messages,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final UserServiceAsync userService,
			final TaskRetrieverAsync taskRetriever,
			final AddAnalyticsToDashboardWidgetPresenter addAnalyticsToDashboardWidgetPresenter) {
		super(eventBus, view, loadTemplatePresenter, saveTemplatePresenter,
				userService);
		this.alertReportRetriever = alertReportRetriever;
		this.alertReportProperties = view.getAlertReportProperties();
		this.messages = messages;
		this.taskRetriever = taskRetriever;
		addAnalyticsToDashboardDialog = addAnalyticsToDashboardWidgetPresenter;

		resultTimeShift = Long
				.parseLong(((String) AppUtils
						.getClientProperties()
						.get(AbstractAlertsGridWidgetPresenter.RESULT_TIME_SHIFT_IN_SEC_FOR_ALERT))
						.trim())
				* TimeConstants.MILLISECONDS_PER_SECOND;

		getView().setUiHandlers(this);
	}

	public void actionLoadAlertReports(final Set<String> sourceKeys,
			final TimeInterval timeInterval, final Criterion criterion,
			final Order order, final int startPosition, final int size,
			final AsyncCallback<List<MAlertReport>> callback) {
		alertReportRetriever.getAlertReports(sourceKeys, timeInterval,
				criterion, order, startPosition, size, callback);
	}

	@SuppressWarnings("unchecked")
	public <X> X cast() {
		return (X) this;
	}

	@Override
	protected Criterion createFilteringCriterion() {
		final CriterionQuery query = CriterionQueryFactory.getQuery();
		Criterion criterion = query.eq(alertReportProperties
				.perceivedSeverity().getPath(), PerceivedSeverity.CRITICAL);
		criterion = query.or(criterion, query.eq(alertReportProperties
				.perceivedSeverity().getPath(), PerceivedSeverity.WARNING));
		criterion = query.or(criterion, query.eq(alertReportProperties
				.perceivedSeverity().getPath(), PerceivedSeverity.MAJOR));

		return criterion;
	}

	@Override
	protected Criterion createLoadingCriterion() {
		return null;
	}

	/**
	 * Creates new {@link PlaceRequest} for {@link MAlertReport} to go to Result
	 * page.
	 * 
	 * @param alertReport
	 *            The provided {@link MAlertReport}
	 * @return {@link PlaceRequest} or null if {@link MAlertReport} is not
	 *         related to results.
	 */
	public PlaceRequest createResultRequest(final MAlertReport alertReport) {
		return AbstractAlertsGridWidgetPresenter.createResultRequest(
				alertReport.getAlert(), getConvertedStartDateTime(alertReport),
				getConvertedEndDateTime(alertReport),
				timeInterval.getTimeZone(), timeInterval.getTimeZoneType());
	}

	public void displayAddAnalyticsToDashboardDialog() {
		addToPopupSlot(addAnalyticsToDashboardDialog, false);
	}

	public void getAlertReportCount(final Set<String> sourceKeys,
			final TimeInterval timeInterval, final Criterion criterion,
			final AsyncCallback<Long> callback) {
		alertReportRetriever.getAlertReportCount(sourceKeys, timeInterval,
				criterion, callback);
	}

	private Date getConvertedEndDateTime(final MAlertReport alertReport) {
		Date time = null;
		if (alertReport.getEndDateTime() != null) {
			time = new Date(alertReport.getEndDateTime().getTime()
					+ resultTimeShift);
		} else {
			time = new Date(System.currentTimeMillis() + resultTimeShift);
		}
		return ClientDateConverter.convertClientDateTimeToTimeZone(time,
				timeInterval.getTimeZone());
	}

	private Date getConvertedStartDateTime(final MAlertReport alertReport) {
		final Date time = new Date(alertReport.getStartDateTime().getTime()
				- resultTimeShift);
		return ClientDateConverter.convertClientDateTimeToTimeZone(time,
				timeInterval.getTimeZone());
	}

	public Set<PerceivedSeverity> getSelectedSeverities() {
		return getView().getSelectedSeverites();
	}

	public Set<String> getSourceKeys() {
		return sourceKeys;
	}

	@Override
	protected TemplateType getTemplateType() {
		return TEMPLATE_TYPE;
	}

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public void loadReports() {
		loadFirstPage();
	}
	public void initLoader() {
		getView().initLoader();
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal(false);
	}

	public void openChart(final MAlertReport alertReport) {
		final ParameterIdentifier parameterIdentifier = ConfigurationUtil
				.getAssociatedParameterIdentifier(alertReport.getAlert());
		if (parameterIdentifier != null
				&& alertReport.getAlert().getSource() instanceof MAgentTask) {
			openChartResults(alertReport, parameterIdentifier);

		} else {
			AppUtils.showInfoWithConfirmMessage(messages
					.noResultsAssociatedWithAlertReport());
		}
	}

	private void openChartResults(final MAlertReport alertReport,
			final ParameterIdentifier parameterIdentifier) {
		taskRetriever.getTaskByKey(alertReport.getAlert().getSource().getKey(),
				new AutoNotifyingAsyncLogoutOnFailureCallback<MAgentTask>() {

					@Override
					protected void success(final MAgentTask result) {
						final PlaceRequest request = (new PlaceRequest.Builder())
								.nameToken(QoSNameTokens.chartResults).build();
						AppUtils.getPlaceManager().revealPlace(request);
						new Timer() {
							@Override
							public void run() {
								final String chartName = alertReport.getAlert()
										.getAlertType().getDisplayName();
								final MResultParameterConfiguration parameterConfiguration = result
										.getResultConfiguration()
										.findParameterConfiguration(
												parameterIdentifier);
								final List<MChartSeries> series = Arrays
										.asList(new MChartSeries(result,
												parameterConfiguration,
												chartName));
								final TimeInterval interval = TimeInterval.get(
										Type.CUSTOM,
										getConvertedStartDateTime(alertReport),
										getConvertedEndDateTime(alertReport),
										timeInterval.getTimeZoneType(),
										timeInterval.getTimeZone(),
										timeInterval.getClientTimeZone());
								AppUtils.getEventBus().fireEvent(
										new NavigateToChartWithConfirmEvent(chartName,
												series, interval, false, true,
												false, null));

							}
						}.schedule(TimeConstants.MILLISECONDS_PER_SECOND);
					}
				});
	}

	/**
	 * @see {@link ReportsPresenter#setReportsParameters(TimeInterval)}
	 * 
	 * @param sourceKeys
	 * @param timeInterval
	 */
	public void setReportsParameters(final Set<String> sourceKeys,
			final TimeInterval timeInterval) {
		this.sourceKeys = sourceKeys;
		this.timeInterval = timeInterval;
	}
}

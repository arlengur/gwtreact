/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserAlertsTemplate;
import com.tecomgroup.qos.gwt.client.event.HasPostActionCallback.PostActionCallback;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.NavigateToAlertDetailsEvent;
import com.tecomgroup.qos.gwt.client.event.NavigateToSourceEvent;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.alert.AcknowledgeAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.alert.ClearAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.alert.CommentAlertsEvent;
import com.tecomgroup.qos.gwt.client.event.alert.UnacknowledgeAlertsEvent;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.TableResultPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenterWithTemplates;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertProperties;
import com.tecomgroup.qos.service.AlertServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;
import com.tecomgroup.qos.util.ConfigurationUtil;

/**
 * @author abondin
 *
 */
public abstract class AbstractAlertsGridWidgetPresenter
		extends
			AbstractRemoteDataGridWidgetPresenterWithTemplates<MAlert, AbstractAlertsGridWidgetPresenter.MyView> {

	public interface MyView
			extends
				AbstractRemoteDataGridWidgetPresenterWithTemplates.SharedDataGridWidgetViewWithTemplates<MAlert, AbstractAlertsGridWidgetPresenter> {

		AlertProperties getAlertProperties();

		void loadTemplate(MUserAlertsTemplate template);

		void onBind();
	}

	public static final String RESULT_TIME_SHIFT_IN_SEC_FOR_ALERT = "client.result.time.shift.in.sec.for.alert";

	private static final TemplateType TEMPLATE_TYPE = BaseTemplateType.ALERT;

	public static PlaceRequest createChartRequest(final MAlert alert,
			final Date startDateTime, final Date endDateTime,
			final String timeZone, final TimeZoneType timeZoneType) {
		PlaceRequest result = null;

		final ParameterIdentifier parameterIdentifier = ConfigurationUtil
				.getAssociatedParameterIdentifier(alert);
		if (parameterIdentifier != null) {
			result = TableResultPresenter
					.createChartRequest(
							alert.getSource().getKey(),
							ConfigurationUtil
							.parameterIdentifierToString(
											parameterIdentifier,
											true,
									ConfigurationUtil.PROPERTY_PARAMETER_SEPARATOR_FOR_ALERT_SETTINGS),

									startDateTime, endDateTime, timeZone, timeZoneType,
									alert.getAlertType().getDisplayName());
		}

		return result;
	}

	/**
	 * Creates new {@link PlaceRequest} for {@link MAlert} to go to Result page.
	 *
	 * @param alert
	 *            The provided {@link MAlert}
	 * @param startDateTime
	 *            The start date to show resuls.
	 * @param endDateTime
	 *            The end date to show results.
	 * @return {@link PlaceRequest} or null if {@link MAlert} is not related to
	 *         results.
	 */
	public static PlaceRequest createResultRequest(final MAlert alert,
			final Date startDateTime, final Date endDateTime,
			final String timeZone, final TimeZoneType timeZoneType) {
		PlaceRequest result = null;

		final ParameterIdentifier parameterIdentifier = ConfigurationUtil
				.getAssociatedParameterIdentifier(alert);
		if (parameterIdentifier != null) {
			result = TableResultPresenter.createResultRequest(alert.getSource()
					.getKey(), parameterIdentifier.createParameterStorageKey(),
					startDateTime, endDateTime, timeZone, timeZoneType, alert
					.getAlertType().getDisplayName());
		}

		return result;
	}

	protected final AlertServiceAsync alertService;

	protected AlertProperties alertProperties;

	private final AddAlertsToDashboardWidgetPresenter addAlertsToDashboardWidget;

	@Inject
	public AbstractAlertsGridWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final AlertServiceAsync alertService,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final AddAlertsToDashboardWidgetPresenter addAlertsToDashboardWidget,
			final UserServiceAsync userService) {
		super(eventBus, view, loadTemplatePresenter, saveTemplatePresenter,
				userService);
		this.alertService = alertService;
		this.addAlertsToDashboardWidget = addAlertsToDashboardWidget;

		alertProperties = view.getAlertProperties();
		getView().setUiHandlers(this);
	}

	public void actionAcknowledgeAlerts(final Collection<MAlert> alerts,
			final String comment) {
		getEventBus().fireEvent(
				new AcknowledgeAlertsEvent(alerts, comment,
						new PostActionCallback() {
							@Override
							public void actionPerformed(final Object result) {
								getView().reload(false);
							}
						}));
	}

	public void actionClearAlerts(final Collection<MAlert> alerts,
			final String comment) {
		getEventBus().fireEvent(
				new ClearAlertsEvent(alerts, comment, new PostActionCallback() {
					@Override
					public void actionPerformed(final Object result) {
						getView().reload(false);
					}
				}));
	}

	public void actionNavigateToDetails(final MAlert alert) {
		getEventBus().fireEvent(new NavigateToAlertDetailsEvent(alert, null));
	}

	public void actionNavigateToSource(final MSource source) {
		getEventBus().fireEvent(new NavigateToSourceEvent(source, null));
	}

	public void actionUnAcknowledgeAlerts(final Collection<MAlert> alerts,
			final String comment) {
		getEventBus().fireEvent(
				new UnacknowledgeAlertsEvent(alerts, comment,
						new PostActionCallback() {
							@Override
							public void actionPerformed(final Object result) {
								getView().reload(false);
							}
						}));
	}

	/**
	 * @param alert
	 */
	public void addAlert(final MAlert alert) {
		getView().addItem(alert);
	}

	@SuppressWarnings("unchecked")
	public <X> X cast() {
		return (X) this;
	}

	/**
	 * @param alerts
	 * @param comment
	 */
	public void commentAlerts(final List<MAlert> alerts, final String comment) {
		getEventBus().fireEvent(new CommentAlertsEvent(alerts, comment, null));
		// TODO Update history table?
	}

	@Override
	protected Criterion createFilteringCriterion() {
		final CriterionQuery query = CriterionQueryFactory.getQuery();
		Criterion criterion = query.eq(alertProperties.perceivedSeverity()
				.getPath(), PerceivedSeverity.CRITICAL);
		criterion = query.or(criterion, query.eq(alertProperties
				.perceivedSeverity().getPath(), PerceivedSeverity.WARNING));
		criterion = query.or(criterion, query.eq(alertProperties
				.perceivedSeverity().getPath(), PerceivedSeverity.MAJOR));
		criterion = query.and(criterion,
				query.eq(alertProperties.status().getPath(), Status.ACTIVE));

		return criterion;
	}

	@Override
	protected Criterion createLoadingCriterion() {
		return null;
	}

	public void displayAddAlertsToDashboardDialog() {
		addToPopupSlot(addAlertsToDashboardWidget, false);
	}

	@Override
	protected TemplateType getTemplateType() {
		return TEMPLATE_TYPE;
	}

	/**
	 *
	 * @param criterion
	 * @param callback
	 */
	public void getTotalAlertsCount(final Criterion criterion,
			final AsyncCallback<Long> callback) {
		alertService.getAlertsCount(criterion, callback);
	}

	@Override
	public void loadTemplate(final LoadTemplateEvent event) {
		if (event.getTemplate() instanceof MUserAlertsTemplate) {
			super.loadTemplate(event);
			final MUserAlertsTemplate template = (MUserAlertsTemplate) event
					.getTemplate();
			getView().loadTemplate(template);
		}
	}

	/**
	 * @param alert
	 */
	public void removeAlert(final MAlert alert) {
		getView().removeItem(alert);
	}

	@Override
	public void saveTemplate(final SaveTemplateEvent event) {
		if (event.getTemplate() instanceof MUserAlertsTemplate) {
			super.saveTemplate(event);
			final MUserAlertsTemplate template = (MUserAlertsTemplate) event
					.getTemplate();
			template.setCriterion(getFilteringCriterion());
			template.setOrder(getCurrentOrder());
			template.setHiddenColumns(getHiddenColumns());
		}
	}
	/**
	 * @param alert
	 */
	public void updateAlert(final MAlert alert) {
		getView().updateItem(alert);
	}
}

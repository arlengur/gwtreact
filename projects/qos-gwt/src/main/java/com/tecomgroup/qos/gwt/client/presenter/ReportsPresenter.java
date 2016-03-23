/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
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
import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.AlertReportWrapper.I18nReportLabels;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.gwt.client.DataExporter;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.QoSServlets;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent.GridGroupRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent.LoadTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent.SaveTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.event.report.AddReportCriteriaEvent;
import com.tecomgroup.qos.gwt.client.event.report.AddReportCriteriaEvent.AddReportCriteriaEventHandler;
import com.tecomgroup.qos.gwt.client.event.report.DownloadReportEvent;
import com.tecomgroup.qos.gwt.client.event.report.DownloadReportEvent.DownloadReportEventHandler;
import com.tecomgroup.qos.gwt.client.event.report.RemoveReportCriteriaEvent;
import com.tecomgroup.qos.gwt.client.event.report.RemoveReportCriteriaEvent.RemoveReportCriteriaEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.ReportsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.ReportsGatekeeper;
import com.tecomgroup.qos.gwt.client.utils.*;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.AlertReportRetrieverAsync;
import com.tecomgroup.qos.service.TaskRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;

import javax.validation.ValidationException;

/**
 * @author ivlev.e
 * 
 */
public class ReportsPresenter
		extends
			Presenter<ReportsPresenter.MyView, ReportsPresenter.MyProxy>
		implements
			UiHandlers,
			AddReportCriteriaEventHandler,
			RemoveReportCriteriaEventHandler,
			GridGroupRemovedEventHandler<MAgentTask>,
			LoadTemplateEventHandler,
			SaveTemplateEventHandler,
			DownloadReportEventHandler,
			DataExporter<AlertReportWrapper> {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.reports)
	@UseGatekeeper(ReportsGatekeeper.class)
	public static interface MyProxy extends ProxyPlace<ReportsPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<ReportsPresenter> {

		void addCriteria(String agentKey, List<MAgentTask> tasks);

		TimeInterval getSelectedTimeInterval();

		String getSelectedTimeZoneLabel();

		Collection<MAgentTask> getTasksByKeys(List<String> modelKeys);

		boolean isReportChanged();

		void loadTemplate(MUserReportsTemplate template);

		void refreshGridView();

		void removeTask(MAgentTask task);

		void setReportTasks(List<MAgentTask> tasks);

		void showConfirmationDialogToExportChangedReport();

		void showErrorDialog(String errorMessage);

        void validateTimeInterval() throws ValidationException;

		void activateAgentTimeZone();
    }

	private Map<String, Set<MAgentTask>> allCriteria = new LinkedHashMap<String, Set<MAgentTask>>();

	public static Logger LOGGER = Logger.getLogger(ReportsPresenter.class
			.getName());

	private final AddReportCriteriaPresenter addReportCriteriaPresenter;

	private HandlerRegistration gridGroupRemovedHandler;

	private final ReportsGridWidgetPresenter gridPresenter;

	private AlertReportWrapper.I18nReportLabels reportLabels;

	private final AgentServiceAsync agentService;

	private final TaskRetrieverAsync taskRetriever;

	private final AlertReportRetrieverAsync alertReportService;

	private final QoSMessages messages;
	/**
	 * Stores parameters for currently displayed report
	 */
	private AlertReportWrapper currentAlertReportWrapper;

	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 */
	@Inject
	public ReportsPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy,
			final AddReportCriteriaPresenter addReportCriteriaPresenter,
			final ReportsGridWidgetPresenter gridPresenter,
			final AgentServiceAsync agentService,
			final TaskRetrieverAsync taskRetriever,
			final AlertReportRetrieverAsync alertReportService,
			final QoSMessages messages) {
		super(eventBus, view, proxy);
		this.addReportCriteriaPresenter = addReportCriteriaPresenter;
		this.gridPresenter = gridPresenter;
		this.agentService = agentService;
		this.taskRetriever = taskRetriever;
		this.alertReportService = alertReportService;
		this.messages = messages;

		getView().setUiHandlers(this);
		getEventBus().addHandler(AddReportCriteriaEvent.TYPE, this);
		getEventBus().addHandler(RemoveReportCriteriaEvent.TYPE, this);
		getEventBus().addHandler(DownloadReportEvent.TYPE, this);
		getEventBus().addHandler(LoadTemplateEvent.TYPE, this);
		getEventBus().addHandler(SaveTemplateEvent.TYPE, this);
	}

	public void actionLoadAllAgents() {
		agentService.getAllAgentKeys(new AutoNotifyingAsyncLogoutOnFailureCallback<List<String>>(messages.agentsLoadingFail(), true) {
			@Override
			protected void success(final List<String> agentKeys) {
				taskRetriever.getAgentTasks(agentKeys,
											0,
											Integer.MAX_VALUE,
											false,
											new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>(messages.tasksLoadingFail(), true) {
												@Override
												protected void success(final List<MAgentTask> tasks) {
													allCriteria = SimpleUtils.mapTasksByAgentKey(tasks);
													getView().setReportTasks(tasks);
													setReportsParameters(getView().getSelectedTimeInterval());
													getView().activateAgentTimeZone();
												}
											});
			}
		});
	}

	public void actionOpenAddReportCriterionDialog() {
		this.addToPopupSlot(addReportCriteriaPresenter, false);
	}

	/**
	 * Returns new template (without name, userId) containing full criterion
	 * {@link AbstractRemoteDataGridWidgetPresenter#getConfigurableCriterion()}.
	 */
	public MUserReportsTemplate createTemplateToExportData() {
		final MUserReportsTemplate template = new MUserReportsTemplate();
		template.setCriterion(gridPresenter.getConfigurableCriterion());
		template.setOrder(gridPresenter.getCurrentOrder());
		template.setHiddenColumns(gridPresenter.getHiddenColumns());
		template.setSourceKeys(getSourceKeys());
		template.setTimeInterval(getView().getSelectedTimeInterval());
		return template;
	}

	/**
	 * Returns new template (without name and userId) containing only filtering
	 * criterion
	 * {@link AbstractRemoteDataGridWidgetPresenter#getFilteringCriterion()}.
	 * 
	 * @return
	 */
	public MUserReportsTemplate createTemplateToLoadDataIntoView() {
		final MUserReportsTemplate template = new MUserReportsTemplate();
		template.setCriterion(gridPresenter.getFilteringCriterion());
		template.setOrder(gridPresenter.getCurrentOrder());
		template.setHiddenColumns(gridPresenter.getHiddenColumns());
		template.setSourceKeys(getSourceKeys());
		template.setTimeInterval(getView().getSelectedTimeInterval());
		return template;
	}

	@Override
	public void export(final AlertReportWrapper alertReportWrapper) {
		gridPresenter.getView().setEnabledExportButton(false);
		alertReportService.serializeBean(alertReportWrapper,
				new AutoNotifyingAsyncLogoutOnFailureCallback<String>(
						"Cannot serialize AlertReportWrapper", false) {

					@Override
					protected void success(final String jsonPayload) {
						final RequestBuilder requestBuilder = new RequestBuilder(
								RequestBuilder.POST, AppUtils.buildUrl(
										QoSServlets.exportAlertReport,
										new HashMap<String, String>()));
						requestBuilder.setHeader("Content-Type",
								"application/json");
						requestBuilder.setRequestData(jsonPayload);
						requestBuilder.setCallback(new RequestCallback() {

							@Override
							public void onError(final Request request,
									final Throwable exception) {
								AppUtils.showErrorMessage(messages
										.downloadReportFail());
								gridPresenter.getView().setEnabledExportButton(
										true);
							}

							@Override
							public void onResponseReceived(
									final Request request,
									final Response response) {
								Window.Location.replace(AppUtils.buildUrl(
										QoSServlets.exportAlertReport,
										new HashMap<String, String>()));
								gridPresenter.getView().setEnabledExportButton(
										true);
							}
						});
						try {
							requestBuilder.send();
						} catch (final RequestException requestExeption) {
							AppUtils.showErrorMessage(messages
									.downloadReportFail());
							gridPresenter.getView()
									.setEnabledExportButton(true);
						}
					}
				});
	}

	/**
	 * Downloads report despite the fact that report was changed
	 */
	public void forceDownloadReport() {

		// these parameters are applied immediately after user change it,
		// so we take their latest state
		currentAlertReportWrapper.template.setCriterion(gridPresenter
				.getConfigurableCriterion());
		currentAlertReportWrapper.template.setOrder(gridPresenter
				.getCurrentOrder());
		currentAlertReportWrapper.template.setHiddenColumns(gridPresenter
				.getHiddenColumns());

		export(currentAlertReportWrapper);
	}

	@Override
	public Collection<String> getAgentDisplayNames() {
		final Collection<String> agentsNames = new LinkedHashSet<String>();
		for (final Map.Entry<String, Set<MAgentTask>> criteriaEntry : allCriteria
				.entrySet()) {
			if (criteriaEntry.getValue().iterator().hasNext()) {
				final String name = SimpleUtils.findSystemComponent(
						criteriaEntry.getValue().iterator().next())
						.getDisplayName();
				agentsNames.add(name);
			}
		}
		return agentsNames;
	}

	/**
	 * @return the allCriteria
	 */
	public Map<String, Set<MAgentTask>> getAllCriteria() {
		return allCriteria;
	}

	private Set<String> getSourceKeys() {
		final Set<String> sourceKeys = new HashSet<String>();
		for (final Map.Entry<String, Set<MAgentTask>> criteriaEntry : allCriteria
				.entrySet()) {
			for (final MAgentTask task : criteriaEntry.getValue()) {
				sourceKeys.add(task.getKey());
			}
		}
		return sourceKeys;
	}

	public AlertReportWrapper getDataWrapper() {
		// show only selected severities in the report or
		// all severities if nothing is selected
		final Set<PerceivedSeverity> selectedSeverities = gridPresenter
				.getSelectedSeverities();
		if (!selectedSeverities.isEmpty()) {
			reportLabels.severities = LabelUtils.getSeverityLabels(
					selectedSeverities, messages);
		} else {
			reportLabels.severities = LabelUtils.getAllSeverityLabels(messages);
		}

		return new AlertReportWrapper(createTemplateToExportData(),
				reportLabels, getAgentDisplayNames(), getView()
						.getSelectedTimeZoneLabel(),
				DateUtils.DATE_TIME_FORMAT, DateUtils.getLocale());
	}

	public void loadReports() {
		gridPresenter.initLoader();
		gridPresenter.loadReports();
		currentAlertReportWrapper = getDataWrapper();
	}

	@Override
	public void loadTemplate(final LoadTemplateEvent event) {
		if (event.getTemplate() instanceof MUserReportsTemplate) {
			final MUserReportsTemplate template = (MUserReportsTemplate) event
					.getTemplate();
			taskRetriever.getTasksByKeys(template.getSourceKeys(), false,
					new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>() {

						@Override
						protected void success(final List<MAgentTask> tasks) {
							agentService.getProbeKeysUserCanManage(
									new AsyncCallback<List<String>>() {

										@Override
										public void onFailure(Throwable caught) {
											LOGGER.log(
													Level.WARNING,
													"Unable to filter report template by agent",
													caught);
											AppUtils.showInfoMessage(messages.templateLoadingFail());
										}

										@Override
										public void onSuccess(List<String> managableAgentKeys) {
											List<MAgentTask> result = new LinkedList<MAgentTask>();
											for (final MAgentTask task : tasks) {
												if (managableAgentKeys.contains(task.getModule().getAgent().getKey())) {
													result.add(task);
												}
											}
											if(result.size() == 0) {
												AppUtils.showInfoMessage(messages.templateLoadingFail());
											}

											loadTemplate(template, result);
										}
									});
							}

					});
		}
	}

    private void loadTemplate(final MUserReportsTemplate template, final List<MAgentTask> tasks) {
        allCriteria.clear();

        if (tasks != null) {
            for (final MAgentTask task : tasks) {
                final String agentKey = SimpleUtils
                        .findSystemComponent(task).getKey();
                Set<MAgentTask> agentTasks = allCriteria
                        .get(agentKey);
                if (agentTasks == null) {
                    agentTasks = new LinkedHashSet<MAgentTask>();
                    allCriteria.put(agentKey, agentTasks);
                }
                agentTasks.add(task);
            }
        }
        getView().setReportTasks(tasks);
		getView().activateAgentTimeZone();
        getView().loadTemplate(template);
        gridPresenter.setReportsParameters(
                template.getSourceKeys(),
                template.getTimeInterval());
        gridPresenter.getView().loadTemplate(template);
    }

	/**
	 * Receive criteria for report after tasks selection dialog closed
	 */
	@Override
	public void onAddReportCriteriaEvent(final AddReportCriteriaEvent event) {
		final String agentKey = event.getAgentKey();
		Set<MAgentTask> concreteAgentTasks = allCriteria.get(agentKey);
		if (concreteAgentTasks == null) {
			concreteAgentTasks = new LinkedHashSet<MAgentTask>();
		} else {
			concreteAgentTasks.clear();
		}
		concreteAgentTasks.addAll(event.getTasks());
		allCriteria.put(agentKey, concreteAgentTasks);
		getView().addCriteria(agentKey, event.getTasks());
		getView().activateAgentTimeZone();
		setReportsParameters(getView().getSelectedTimeInterval());
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(gridPresenter.getClass().getName(), gridPresenter);

		reportLabels = new I18nReportLabels(messages.report(),
				messages.probe(), messages.perceivedSeverity(),
				messages.startDateTime(), messages.endDateTime(),
				messages.timezone(), messages.comments(),
                LabelUtils.getAllSeverityLabels(messages),
				gridPresenter.getColumnNames());
	}

	@Override
	public void onDownloadReportEvent(final DownloadReportEvent event) {

		final String errorMessage = validateReportParameters();

		if (errorMessage == null) {
			if (getView().isReportChanged()) {
				getView().showConfirmationDialogToExportChangedReport();
			} else {
				final AlertReportWrapper alertReportWrapper = getDataWrapper();
				export(alertReportWrapper);
			}
		} else {
			getView().showErrorDialog(errorMessage);
		}
	}

	@Override
	public void onGridGroupRemovedEvent(
			final GridGroupRemovedEvent<MAgentTask> event) {
		removeAgentTasksCriteria(event.getItems());
	}

	@Override
	protected void onHide() {
		super.onHide();
		gridGroupRemovedHandler.removeHandler();
	}

	@Override
	public void onRemoveReportCriteriaEvent(
			final RemoveReportCriteriaEvent event) {
		final List<String> modelKeys = event.getModelKeys();
		final Collection<MAgentTask> tasks = getView()
				.getTasksByKeys(modelKeys);
		if (tasks.isEmpty()) {
			return;
		}
		removeAgentTasksCriteria(tasks);
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);
		final String templateName = request.getParameter(
				RequestParams.template, null);
		// if templateName is null, then not overwrite selectedTemplateName
		// because overwriting selectedTemplateName with null
		// disappear load/save template widget dialog substitution
		if (templateName != null) {
			gridPresenter.setSelectedTemplateName(templateName);
		}

        final String tasksListString = request.getParameter(
                RequestParams.tasks, null);
        final String startTimestampString = request.getParameter(
                RequestParams.startDate, null);
        final String endTimestampString = request.getParameter(
                RequestParams.endDate, null);

        if (tasksListString != null && startTimestampString != null && endTimestampString != null) {
            final String[] taskIdStrings = tasksListString.split(",");
            final Date start = new Date(Long.parseLong(startTimestampString));
            final Date end = new Date(Long.parseLong(endTimestampString));

            final List<Long> ids = new ArrayList<Long>();
            for (String idString : taskIdStrings) {
                ids.add(Long.parseLong(idString));
            }

            taskRetriever.getTasksByIds(ids, new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>() {

                @Override
                protected void success(List<MAgentTask> tasks) {

                    final MUserReportsTemplate template = new MUserReportsTemplate();
                    final Set<String> taskKeys = new HashSet<String>();

                    for (MAgentTask task : tasks) {
                        taskKeys.add(task.getKey());
                    }

                    template.setSourceKeys(taskKeys);
                    template.setTimeInterval(TimeInterval.get(start, end));
                    template.setOrder(Order.desc("startDateTime"));
                    loadTemplate(template, tasks);
                }
            });
        }
	}

	/**
	 * Removes tasks from agent's task set and removes the set if it's empty.
	 */
	private void removeAgentTasksCriteria(final Collection<MAgentTask> tasksToRemove) {
		String agentKey = null;
		for (final MAgentTask taskToRemove : tasksToRemove) {
			agentKey = taskToRemove.getModule().getAgent().getKey();
			allCriteria.get(agentKey).remove(taskToRemove);
			getView().removeTask(taskToRemove);
		}

		if (allCriteria.get(agentKey).isEmpty()) {
			allCriteria.remove(agentKey);
			getView().refreshGridView();
		}
		getView().activateAgentTimeZone();
		setReportsParameters(getView().getSelectedTimeInterval());
	}

	@Override
	protected void revealInParent() {
		gridGroupRemovedHandler = getEventBus().addHandler(
				GridGroupRemovedEvent.TYPE, this);
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}

	@Override
	public void saveTemplate(final SaveTemplateEvent event) {
		if (event.getTemplate() instanceof MUserReportsTemplate) {
			final MUserReportsTemplate template = (MUserReportsTemplate) event
					.getTemplate();

			template.setCriterion(gridPresenter.getConfigurableCriterion());
			template.setOrder(gridPresenter.getCurrentOrder());
			template.setHiddenColumns(gridPresenter.getHiddenColumns());
			template.setSourceKeys(getSourceKeys());
			template.setTimeInterval(getView().getSelectedTimeInterval());
		}
	}

	public void setReportsParameters(final TimeInterval timeInterval) {
		gridPresenter.setReportsParameters(getSourceKeys(), timeInterval);
	}

	/**
	 * Validates report parameters.
	 * 
	 * @return error message, if some report parameters are incorrect, otherwise
	 *         null.
	 */
	public String validateReportParameters() {
		String validationMessage = null;

		if (getAllCriteria().isEmpty()) {
			validationMessage = messages.alertReportCriteriaCollectionIsEmpty();
		} else if (!getView().getSelectedTimeInterval().isValid()) {
            try {
                getView().validateTimeInterval();
            } catch (final ValidationException e) {
                validationMessage = messages.invalidDateTimeInterval() + "\n"
                        + e.getMessage();
            }
		}
		return validationMessage;
	}
}

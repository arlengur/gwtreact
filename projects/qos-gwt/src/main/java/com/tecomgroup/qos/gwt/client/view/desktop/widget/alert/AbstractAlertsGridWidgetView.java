/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.LongPropertyEditor;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MUserAlertsTemplate;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AbstractAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DurationValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.SystemComponentValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertProbableCausePropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSeverityCell;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSeverityPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSpecificReasonPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertStausPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.BooleanDataPropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridViewWithTemplates;

/**
 * @author abondin
 * 
 */
public abstract class AbstractAlertsGridWidgetView
		extends
			AbstractRemoteDataGridViewWithTemplates<MAlert, AbstractAlertsGridWidgetPresenter> {

	protected AlertProperties alertProperties;

	private final AlertSeverityPropertyEditor severityPropertyEditor;

	private final AlertSeverityToolbar<MAlert> severityToolbar;

	public static Logger LOGGER = Logger
			.getLogger(AbstractAlertsGridWidgetView.class.getName());

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public AbstractAlertsGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		severityPropertyEditor = new AlertSeverityPropertyEditor(messages);
		alertProperties = GWT.create(AlertProperties.class);
		severityToolbar = new AlertSeverityToolbar<MAlert>(toolbar,
				appearanceFactory, severityPropertyEditor, this);
	}

	@Override
	protected boolean addButtonsToToolbar() {
		final Widget ackAlert = createToolBarButton(appearanceFactory
				.resources().alertAck(), messages.acknowledge(),
				new SelectedItemsActionHandler<MAlert>() {
					@Override
					public void onAction(final List<MAlert> alerts,
							final String comment) {
						getUiHandlers()
								.actionAcknowledgeAlerts(alerts, comment);
					}

				});

		final Widget unackAlert = createToolBarButton(appearanceFactory
				.resources().alertUnAck(), messages.unAcknowledge(),
				new SelectedItemsActionHandler<MAlert>() {
					@Override
					public void onAction(final List<MAlert> alerts,
							final String comment) {
						getUiHandlers().actionUnAcknowledgeAlerts(alerts,
								comment);
					}
				});

		final Widget clearAlert = createToolBarButton(appearanceFactory
				.resources().alertClear(), messages.clear(),
				new SelectedItemsActionHandler<MAlert>() {
					@Override
					public void onAction(final List<MAlert> alerts,
							final String comment) {
						getUiHandlers().actionClearAlerts(alerts, comment);
					}
				});
		final Widget commentAlert = createToolBarButton(appearanceFactory
				.resources().alertComment(), messages.comment(), null,
				CommentMode.REQUIRED, new SelectedItemsActionHandler<MAlert>() {
					@Override
					public void onAction(final List<MAlert> alerts,
							final String comment) {
						getUiHandlers().commentAlerts(alerts, comment);
					}
				});

		final Image source = createToolBarButton(appearanceFactory.resources()
				.alertSource(), messages.source(), null);
		source.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final MAlert alert = grid.getSelectionModel().getSelectedItem();
				if (alert != null) {
					getUiHandlers().actionNavigateToSource(alert.getSource());
				}
			}
		});
		final Image details = createToolBarButton(appearanceFactory.resources()
				.alertDetails(), messages.details(), null);
		details.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final MAlert alert = grid.getSelectionModel().getSelectedItem();
				if (alert != null) {
					getUiHandlers().actionNavigateToDetails(alert);
				}
			}
		});
		final Image originator = createToolBarButton(appearanceFactory
				.resources().alertOriginator(), messages.originator(), null);
		originator.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AppUtils.showErrorMessage("Go To Originator(Policy) is not implemented");
			}
		});

		final Image update = createToolBarButton(appearanceFactory.resources()
				.updateButtonInvert(), messages.update(), null);
		update.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				reload(false);
			}
		});

		final Image clearFilters = createToolBarButton(appearanceFactory
				.resources().clearFilters(), messages.actionClearFilters(),
				null);
		clearFilters.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dialogFactory.createConfirmationDialog(
						new ConfirmationHandler() {
							@Override
							public void onCancel() {
								// Do nothing
							}
							@Override
							public void onConfirm(final String comment) {
								severityToolbar.clearAllSeverityCheckboxes();
								clearFilters(false);
								cleanupCurrentTemplate();
							}
						}, messages.actionClearFilters(),
						messages.clearFiltersConfirm(), CommentMode.DISABLED)
						.show();
			}
		});
		final Image addToDashboard = createToolBarButton(appearanceFactory
				.resources().createWidgetIcon(),
				messages.addWidgetToDashboardMessage(), null);
		addToDashboard.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().displayAddAlertsToDashboardDialog();
			}
		});

		setToolbarStandardMargins(ackAlert.getElement().<XElement> cast());
		setToolbarStandardMargins(unackAlert.getElement().<XElement> cast());
		setToolbarStandardMargins(clearAlert.getElement().<XElement> cast());
		setToolbarStandardMargins(commentAlert.getElement().<XElement> cast());
		setToolbarStandardMargins(source.getElement().<XElement> cast());
		setToolbarStandardMargins(details.getElement().<XElement> cast());
		setToolbarStandardMargins(originator.getElement().<XElement> cast());
		setToolbarStandardMargins(update.getElement().<XElement> cast());
		setToolbarStandardMargins(clearFilters.getElement().<XElement> cast());
		setToolbarStandardMargins(addToDashboard.getElement().<XElement> cast());

		final CssFloatData layoutData = new CssFloatData();
		toolbar.add(addToDashboard, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(ackAlert, layoutData);
		toolbar.add(unackAlert, layoutData);
		toolbar.add(clearAlert, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(commentAlert, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(source, layoutData);
		toolbar.add(details, layoutData);
		// TODO: uncomment when "Go To Originator(Policy)" will be implemented
		// toolbar.add(originator, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(update, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(clearFilters, layoutData);
		toolbar.add(createSeparator(), layoutData);
		severityToolbar.addSeverityFilterButtons();

		return true;
	}

	@Override
	protected void applyDefaultConfiguration() {
		final MUserAlertsTemplate defaultTemplate = new MUserAlertsTemplate();
		defaultTemplate.setCriterion(getUiHandlers().getFilteringCriterion());
		defaultTemplate.setOrder(Order.desc(alertProperties
				.lastUpdateDateTime().getPath()));
		defaultTemplate.setHiddenColumns(new String[]{
				alertProperties.settings().getPath(),
				alertProperties.perceivedSeverity().getPath(),
				alertProperties.probableCause().getPath(),
				alertProperties.originator().getPath(),
				alertProperties.alertCount().getPath()});
		loadTemplate(defaultTemplate, true);
	}

	@Override
	protected List<Filter<MAlert, ?>> createFilters() {
		final List<Filter<MAlert, ?>> filters = new ArrayList<Filter<MAlert, ?>>();

		final ListFilter<MAlert, Status> statusFilter = filterFactory
				.<MAlert, MAlertType.Status> createEnumListFilter(
						alertProperties.status(), new AlertStausPropertyEditor(
								messages));
		filters.add(statusFilter);

		filters.add(createSeverityFilter());

		filters.add(filterFactory
				.<MAlert, SpecificReason> createEnumListFilter(
						alertProperties.specificReason(),
						new AlertSpecificReasonPropertyEditor(messages)));
		filters.add(filterFactory.<MAlert, ProbableCause> createEnumListFilter(
				alertProperties.probableCause(),
				new AlertProbableCausePropertyEditor(messages)));

		filters.add(filterFactory.<MAlert> createStringFilter(alertProperties
				.displayName()));
		filters.add(filterFactory.<MAlert> createDateFilter(alertProperties
				.creationDateTime()));
		filters.add(filterFactory.<MAlert> createBooleanFilter(alertProperties
				.acknowledged()));
		filters.add(filterFactory.<MAlert> createDateFilter(alertProperties
				.lastUpdateDateTime()));
		filters.add(filterFactory.<MAlert> createStringFilter(alertProperties
				.source()));
		filters.add(filterFactory.<MAlert> createStringFilter(alertProperties
				.originator()));
		filters.add(filterFactory.<MAlert, Long> createNumericFilter(
				alertProperties.alertCount(), new LongPropertyEditor()));

		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridAlertsAppearance();
	}

	private ListFilter<MAlert, PerceivedSeverity> createSeverityFilter() {
		final ListFilter<MAlert, PerceivedSeverity> severityFilter = filterFactory
				.<MAlert, PerceivedSeverity> createEnumListFilter(
						alertProperties.perceivedSeverity(),
						severityPropertyEditor);
		severityToolbar.setSeverityFilter(severityFilter);
		return severityFilter;
	}

	@Override
	protected ListStore<MAlert> createStore() {
		return new ListStore<MAlert>(alertProperties.key());
	}

	public AlertProperties getAlertProperties() {
		return alertProperties;
	}

	@Override
	protected List<ColumnConfig<MAlert, ?>> getGridColumns() {
		final ColumnConfig<MAlert, Status> statusColumn = new ColumnConfig<MAlert, Status>(
				alertProperties.status(), 30, messages.active());
		statusColumn.setCell(new PropertyDisplayCell<Status>(
				new PropertyEditor<Status>() {
					@Override
					public Status parse(final CharSequence text)
							throws ParseException {
						if (text == null) {
							return null;
						} else if (text.equals(messages.actionYes())) {
							return Status.ACTIVE;
						} else {
							return Status.CLEARED;
						}
					}
					@Override
					public String render(final Status object) {
						if (object == null) {
							return null;
						} else if (MAlert.isActive(object)) {
							return messages.actionYes();
						} else {
							return messages.actionNo();
						}
					}
				}));
		final ColumnConfig<MAlert, String> displayNameColumn = new ColumnConfig<MAlert, String>(
				alertProperties.displayName(), 50, messages.displayName());

		displayNameColumn.setCell(new AlertDetailsHrefCell(getStore()));

		final ColumnConfig<MAlert, Boolean> acknowledgedColumn = new ColumnConfig<MAlert, Boolean>(
				alertProperties.acknowledged(), 40, messages.acknowledged());
		acknowledgedColumn
				.setCell(new BooleanDataPropertyEditor.BooleanPropertyCell(
						messages, false));

		final ColumnConfig<MAlert, ProbableCause> probableCauseColumn = new ColumnConfig<MAlert, ProbableCause>(
				alertProperties.probableCause(), 70, messages.probableCause());
		probableCauseColumn.setCell(new AlertProbableCausePropertyEditor.Cell(
				messages));

		final ColumnConfig<MAlert, Date> creationDateTimeColumn = new ColumnConfig<MAlert, Date>(
				alertProperties.creationDateTime(), 60,
				messages.creationDateTime());
		creationDateTimeColumn.setCell(new DateCell(
				DateUtils.DATE_TIME_FORMATTER));

		final ColumnConfig<MAlert, Date> lastUpdateDateTimeColumn = new ColumnConfig<MAlert, Date>(
				alertProperties.lastUpdateDateTime(), 60,
				messages.lastUpdateDateTime());
		lastUpdateDateTimeColumn.setCell(new DateCell(
				DateUtils.DATE_TIME_FORMATTER));

		final ColumnConfig<MAlert, String> systemComponentColumn = new ColumnConfig<MAlert, String>(
				new SystemComponentValueProvider.AlertSystemComponentValueProvider(
						alertProperties.systemComponent().getPath()), 50,
				messages.probe());

		final ColumnConfig<MAlert, String> sourceColumn = new ColumnConfig<MAlert, String>(
				alertProperties.source(), 50, messages.source());

		final ColumnConfig<MAlert, String> originatorColumn = new ColumnConfig<MAlert, String>(
				alertProperties.originator(), 50, messages.originator());

		final ColumnConfig<MAlert, PerceivedSeverity> perceivedSeverityColumn = new ColumnConfig<MAlert, PerceivedSeverity>(
				alertProperties.perceivedSeverity(), 70,
				messages.perceivedSeverity());
		perceivedSeverityColumn.setCell(new AlertSeverityCell(messages));

		final ColumnConfig<MAlert, String> settingsColumn = new ColumnConfig<MAlert, String>(
				alertProperties.settings(), 50, messages.settings());

		final ColumnConfig<MAlert, Long> countColumn = new ColumnConfig<MAlert, Long>(
				alertProperties.alertCount(), 50, messages.count());

		final ColumnConfig<MAlert, String> durationColumn = new ColumnConfig<MAlert, String>(
				new DurationValueProvider.AlertDurationValueProvider(messages),
				50, messages.duration());

		final List<ColumnConfig<MAlert, ?>> columns = new ArrayList<ColumnConfig<MAlert, ?>>();
		columns.add(statusColumn);
		columns.add(displayNameColumn);
		columns.add(acknowledgedColumn);
		columns.add(probableCauseColumn);
		columns.add(creationDateTimeColumn);
		columns.add(lastUpdateDateTimeColumn);
		columns.add(durationColumn);
		columns.add(countColumn);
		columns.add(systemComponentColumn);
		columns.add(sourceColumn);
		columns.add(originatorColumn);
		columns.add(settingsColumn);
		columns.add(perceivedSeverityColumn);
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
				+ alertProperties.perceivedSeverity().getPath();
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		final AlertsGridViewConfig viewConfig = new AlertsGridViewConfig(
				((AlertsGridAppearance) gridAppearance).getResources(),
				appearanceFactory, alertProperties);
		grid.getView().setViewConfig(viewConfig);
		grid.addStyleName(appearanceFactory.resources().css().textMainColor());
	}

	@Override
	protected abstract RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlert>> initializeLoaderProxy();

	@Override
	protected boolean isHighlightedColumnSortable() {
		return true;
	}

	public void loadTemplate(final MUserAlertsTemplate template) {
		loadTemplate(template, false);
	}

	private void loadTemplate(final MUserAlertsTemplate template,
			final boolean defaultTemplate) {
		hideColumns(template.getHiddenColumns(), false);
		applyOrder(template.getOrder(), false);
		clearFilters(false);
		if (template.getCriterion() != null) {
            filters.setAutoReload(false);
			applyCriterionToFilters(loadConfig, template.getCriterion());
			severityToolbar.updateSeverityCheckboxes();
            filters.setAutoReload(true);
		}

		grid.getView().refresh(true);
		if (!defaultTemplate) {
			AppUtils.showInfoMessage(messages.templateLoadingSuccess());
		}
	}
	
	public void cleanupCurrentTemplate() {
		getUiHandlers().setTemplateLabel(null);
		getUiHandlers().setSelectedTemplateName(null);
	}
}

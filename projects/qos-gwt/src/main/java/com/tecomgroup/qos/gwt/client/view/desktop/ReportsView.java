/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog.DialogMessages;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.gwt.client.event.report.RemoveReportCriteriaEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.ReportsPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DisabledTaskValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.TaskProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.*;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell.ButtonedGroupingCellHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar.UpdateButtonHandler;

/**
 * @author ivlev.e
 * 
 */
public class ReportsView extends ViewWithUiHandlers<ReportsPresenter>
		implements
			ReportsPresenter.MyView,
			ValueChangeHandler<TimeInterval> {

	interface ViewUiBinder extends UiBinder<Widget, ReportsView> {
	}

	private final AppearanceFactory appearanceFactory;

	private final DialogFactory dialogFactory;

	@UiField(provided = true)
	protected TextButton addReportCriterionButton;

	@UiField(provided = true)
	protected TextButton addAllCriterionButton;

	@UiField(provided = true)
	protected TextButton buildReportButton;

	@UiField(provided = true)
	protected BorderLayoutContainer borderLayoutContainer;

	@UiField(provided = true)
	protected Grid<MAgentTask> grid;

	@UiField(provided = true)
	protected FramedPanel centerFramePanel;

	@UiField(provided = true)
	protected HorizontalTimeToolbar timeToolbar;

	private final DateTimeIntervalWidget dateTimeIntervalWidget;

	@UiField(provided = true)
	protected FramedPanel westPanel;

	private final QoSMessages messages;

	private final Widget widget;

	private final TaskProperties taskProps = GWT.create(TaskProperties.class);

	private FastButtonedGrouingView<MAgentTask> groupView;

	private ConfirmationDialog addAllAgentsConfirmationDialog;

	private ConfirmationDialog exportChangedReportConfirmationDialog;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private boolean isReportChanged;

	@Inject
	public ReportsView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final HorizontalTimeToolbar timeToolbar,
			final DialogFactory dialogFactory) {
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.dialogFactory = dialogFactory;
		this.timeToolbar = timeToolbar;
		this.timeToolbar.setup(Type.DAY, Type.WEEK, Type.MONTH);
		dateTimeIntervalWidget = timeToolbar.getDateTimeIntervalWidget();
		dateTimeIntervalWidget.addValueChangeHandler(this);
		initialize();
		widget = UI_BINDER.createAndBindUi(this);
		configure();
	}

	@UiHandler("addAllCriterionButton")
	protected void addAllCriterionAction(final SelectEvent e) {
		addAllAgentsConfirmationDialog.show();
	}

	@Override
	public void addCriteria(final String agentKey, final List<MAgentTask> tasks) {
		final ListStore<MAgentTask> store = grid.getStore();
		for (final MAgentTask task : tasks) {
			final MAgentTask taskInStore = store.findModel(task);
			if (taskInStore != null) {
				// data could be changed, so we need to refresh
				store.remove(taskInStore);
			}
		}
		addRecords(tasks);
		isReportChanged = true;
	}

	@UiHandler("addReportCriterionButton")
	protected void addCriterionAction(final SelectEvent e) {
		getUiHandlers().actionOpenAddReportCriterionDialog();
	}

	/**
	 * Add records and fire single refresh event to update view
	 * 
	 * @param tasks
	 */
	private void addRecords(final List<MAgentTask> tasks) {
		grid.getStore().addAll(tasks);
		grid.getView().refresh(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("buildReportButton")
	protected void buildReportButton(final SelectEvent e) {
		final String errorMessage = getUiHandlers().validateReportParameters();

		if (errorMessage == null) {
			getUiHandlers().setReportsParameters(getSelectedTimeInterval());
			getUiHandlers().loadReports();
			isReportChanged = false;
			ReportsPresenter handler=getUiHandlers();
			Collection<String> agentNames=handler.getAgentDisplayNames();
		} else {
			showErrorDialog(errorMessage);
		}
	}

	private void configure() {
		addReportCriterionButton.setText(messages.selectAgents());
		addAllCriterionButton.setText(messages.addAllAgents());
		buildReportButton.setText(messages.buildReport());

		StyleUtils.configureNoHeaders(centerFramePanel);
		StyleUtils.configureNoHeaders(westPanel);

		grid.addStyleName(ClientConstants.QOS_GRID_STYLE);
		grid.setHideHeaders(true);
		grid.getView().setAutoFill(true);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);

		timeToolbar.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		timeToolbar.addStyleName(appearanceFactory.resources().css()
				.textMainColor());

		dateTimeIntervalWidget.disableCustomTimeInterval();
		timeToolbar.setUpdateButtonHandler(new UpdateButtonHandler() {

			@Override
			public void onUpdateButtonPressed(final SelectEvent event) {
				buildReportButton(event);
			}
		});

		addAllAgentsConfirmationDialog = dialogFactory
				.createConfirmationDialog(new ConfirmationHandler() {

					@Override
					public void onCancel() {
						// do nothing
					}

					@Override
					public void onConfirm(final String comment) {
						getUiHandlers().actionLoadAllAgents();
					}
				}, messages.message(), messages.addAllAgentsConfirm(),
						CommentMode.DISABLED);

		final DialogMessages exportConfirmDialogMessages = new BaseDialog.BaseDialogMessages(
				messages) {

			@Override
			public String cancel() {
				return messages.export();
			}

			@Override
			public String ok() {
				return messages.rebuild();
			}

		};

		exportChangedReportConfirmationDialog = dialogFactory
				.createConfirmationDialog(new ConfirmationHandler() {

					/**
					 * On export button pressed
					 */
					@Override
					public void onCancel() {
						getUiHandlers().forceDownloadReport();
					}

					/**
					 * On rebuild button pressed
					 */
					@Override
					public void onConfirm(final String comment) {
						buildReportButton(new SelectEvent());
					}
				}, messages.message(), messages.rebuildReportConfirm(),
						CommentMode.DISABLED, exportConfirmDialogMessages);

		if (AppUtils.isDemoMode()) {
			dateTimeIntervalWidget.applyDemoMode(TimeInterval.Type.DAY);
		}
	}

	private ColumnModel<MAgentTask> createColumnModel() {
		final List<ColumnConfig<MAgentTask, ?>> columns = new ArrayList<ColumnConfig<MAgentTask, ?>>();

		final ColumnConfig<MAgentTask, String> agentDisplayName = new ColumnConfig<MAgentTask, String>(
				taskProps.agentDisplayName());
		final ColumnConfig<MAgentTask, String> taskDisplayName = new ColumnConfig<MAgentTask, String>(
				new DisabledTaskValueProvider(messages, taskProps.displayName()
						.getPath()));
		final ButtonedGroupingCell<String> seriesCell = new ButtonedGroupingCell<String>(
				appearanceFactory.<String> buttonedGroupingCellAppearance(),
				new ButtonedGroupingCellHandler() {
					@Override
					public void onRemovedButtonPressed(
							final List<String> modelKeys) {
						AppUtils.getEventBus().fireEvent(
								new RemoveReportCriteriaEvent(modelKeys));
					}
				});
		taskDisplayName.setCell(seriesCell);

		groupView = new FastButtonedGrouingView<MAgentTask>(
				appearanceFactory.gridAppearance(),
				appearanceFactory.buttonedGroupingViewAppearance());
		groupView.setShowGroupedColumn(false);
		groupView.setForceFit(true);
		groupView.groupBy(agentDisplayName);

		columns.add(agentDisplayName);
		columns.add(taskDisplayName);
		return new ColumnModel<MAgentTask>(columns);
	}

	@Override
	public TimeInterval getSelectedTimeInterval() {
		return dateTimeIntervalWidget.getTimeInterval();
	}

	@Override
	public String getSelectedTimeZoneLabel() {
		return dateTimeIntervalWidget.getTimeZoneLabel();
	}

	@Override
	public Collection<MAgentTask> getTasksByKeys(final List<String> modelKeys) {
		final List<MAgentTask> result = new ArrayList<MAgentTask>();
		for (final String key : modelKeys) {
			final MAgentTask task = grid.getStore().findModelWithKey(key);
			if (task != null) {
				result.add(task);
			}
		}
		return result;
	}

	private void initialize() {
		borderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		centerFramePanel = new FramedPanel(
				appearanceFactory.framedPanelAppearance());
		westPanel = new FramedPanel(appearanceFactory.framedPanelAppearance());
		addReportCriterionButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()));
		addAllCriterionButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()));
		buildReportButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()));

		final ListStore<MAgentTask> listStore = new ListStore<MAgentTask>(
				taskProps.modelKey());
		grid = new GroupingGrid<MAgentTask>(listStore, createColumnModel(),
				groupView);
	}

	@Override
	public boolean isReportChanged() {
		return isReportChanged;
	}

	@Override
	public void loadTemplate(final MUserReportsTemplate template) {
		final TimeInterval timeInterval = template.getTimeInterval();
		final String timeZone = timeInterval.getTimeZone();

		dateTimeIntervalWidget.setTimeIntervalType(timeInterval.getType());

		if (timeInterval.getType() == Type.CUSTOM) {
			dateTimeIntervalWidget.setTimeInterval(timeInterval.getStartDateTime(), timeInterval.getEndDateTime());
		}
        if (timeInterval.getTimeZoneType() != null && timeZone != null) {
            dateTimeIntervalWidget.setTimeZone(timeInterval.getTimeZoneType(), timeZone);
        }
	}

	@Override
	public void onValueChange(final ValueChangeEvent<TimeInterval> event) {
		isReportChanged = true;
	}

	@Override
	public void refreshGridView() {
		grid.getView().refresh(true);
		isReportChanged = true;
	}

	@Override
	public void removeTask(final MAgentTask task) {
		grid.getStore().remove(task);
		isReportChanged = true;
	}

	/**
	 * Add grid widget into slot
	 */
	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		centerFramePanel.add(content);
	}

	@Override
	public void setReportTasks(final List<MAgentTask> tasks) {
		grid.getStore().clear();
		addRecords(tasks);
		isReportChanged = true;
	}

	@Override
	public void showConfirmationDialogToExportChangedReport() {
		exportChangedReportConfirmationDialog.show();
	}

	@Override
	public void showErrorDialog(final String errorMessage) {
		AppUtils.showErrorMessage(errorMessage);
	}

    @Override
    public void validateTimeInterval() {
        dateTimeIntervalWidget.validate();
    }

	@Override
	public void activateAgentTimeZone() {
		List<String> timeZones = new ArrayList<String>();
		for (MAgentTask task : grid.getStore().getAll()) {
			String timeZone = task.getModule().getAgent().getTimeZone();
			if (!timeZones.contains(timeZone)) {
				timeZones.add(timeZone);
			}
		}
		if (timeZones.size() == 1) {
			dateTimeIntervalWidget.enableAgentTimeZone(timeZones.get(0));
		} else {
			dateTimeIntervalWidget.disableAgentTimeZone();
		}
	}
}

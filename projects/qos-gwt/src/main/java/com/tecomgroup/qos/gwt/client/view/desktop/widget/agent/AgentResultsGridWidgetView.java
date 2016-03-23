/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import java.util.*;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages.DefaultFormattedResultMessages;
import com.tecomgroup.qos.gwt.client.model.results.ParameterRow;
import com.tecomgroup.qos.gwt.client.model.results.ResultRow;
import com.tecomgroup.qos.gwt.client.model.results.TaskRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentResultsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.CustomTreeGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.TreeGridRowModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataTreeGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold.ThresholdCell;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class AgentResultsGridWidgetView
		extends
			AbstractLocalDataTreeGridView<ResultRow, AgentResultsGridWidgetPresenter>
		implements
			AgentResultsGridWidgetPresenter.MyView {

	private TreeLoader<ResultRow> loader;

    private static String ADD_WIDGET_COLUMN_PATH = "add_widget";

	@Inject
	public AgentResultsGridWidgetView() {
		super();
	}

	@Override
	public void clear() {
		grid.getTreeStore().clear();
	}

	private ColumnConfig<ResultRow, ?> createDateColumn() {
		final ColumnConfig<ResultRow, Date> dateColumn = new ColumnConfig<ResultRow, Date>(
				new ValueProvider<ResultRow, Date>() {

					@Override
					public String getPath() {
						return TreeGridFields.DATE.toString();
					}

					@Override
					public Date getValue(final ResultRow row) {
						return row.getDate();
					}

					@Override
					public void setValue(final ResultRow row, final Date value) {
					}
				}, 70, messages.time());
		dateColumn.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));
		dateColumn.setSortable(false);
		dateColumn.setMenuDisabled(true);
		return dateColumn;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

    private boolean taskHasMultipleProgramBitrates(final MAgentTask task) {
        List<MResultParameterConfiguration> paramConfigs = task.getResultConfiguration().getParameterConfigurations();
        Set<String> bitrateParams = new HashSet<String>();

        for (MResultParameterConfiguration paramConfig : paramConfigs) {
            if ("totalBitrate".equalsIgnoreCase(paramConfig.getName())) {
                MProperty programId = paramConfig.getProperty("programId");
                bitrateParams.add(programId.getValue());
            }
        }

        return bitrateParams.size() > 1;
    }

	private void createGridData(final List<MAgentTask> tasks) {
		grid.getTreeStore().clear();
		for (final MAgentTask task : tasks) {
			final TaskRow taskRow = new TaskRow(task.getKey(),
					task.getDisplayName(), taskHasMultipleProgramBitrates(task));
			grid.getTreeStore().add(taskRow);
            final List<MResultParameterConfiguration> parameterConfigurations = task
                    .getResultConfiguration().getParameterConfigurations();
            Collections.sort(parameterConfigurations, new Comparator<MResultParameterConfiguration>() {
                @Override
                public int compare(final MResultParameterConfiguration o1, final MResultParameterConfiguration o2) {
                    return o1.getParsedDisplayFormat().compareTo(o2.getParsedDisplayFormat());
                }
            });
            for (final MResultParameterConfiguration parameter : parameterConfigurations) {
				final String taskStorageKey = parameter
						.getParameterIdentifier().createTaskStorageKey(
								task.getKey());
				final ParameterRow parameterRow = new ParameterRow(
						taskStorageKey, parameter.getParsedDisplayFormat(),
						parameter.getThreshold(), parameter.getType());
				grid.getTreeStore().add(taskRow, parameterRow);
			}
		}
	}

	private ColumnConfig<ResultRow, ?> createNameColumn() {
		final ColumnConfig<ResultRow, String> nameColumn = new ColumnConfig<ResultRow, String>(
				new ValueProvider<ResultRow, String>() {

					@Override
					public String getPath() {
						return TreeGridFields.NAME.toString();
					}

					@Override
					public String getValue(final ResultRow row) {
						return row.getName();
					}

					@Override
					public void setValue(final ResultRow row, final String value) {
					}
				}, 85, messages.parameter());
		return nameColumn;
	}

	@Override
	protected TreeStore<ResultRow> createStore() {
		return new TreeStore<ResultRow>(new TreeGridRowModelKeyProvider());
	}

	private ColumnConfig<ResultRow, ?> createThresholdColumn() {
		final ColumnConfig<ResultRow, MParameterThreshold> thresholdColumn = new ColumnConfig<ResultRow, MParameterThreshold>(
				new ValueProvider<ResultRow, MParameterThreshold>() {

					@Override
					public String getPath() {
						return "threshold";
					}

					@Override
					public MParameterThreshold getValue(final ResultRow row) {
						return row instanceof ParameterRow
								? ((ParameterRow) row).getThreshold()
								: null;
					}

					@Override
					public void setValue(final ResultRow row,
							final MParameterThreshold value) {
					}
				}, 200);
		thresholdColumn.setCell(new ThresholdCell(200, 16,
				new DefaultFormattedResultMessages(messages)));
		thresholdColumn.setFixed(true);
		thresholdColumn.setSortable(false);
		thresholdColumn.setMenuDisabled(true);
		return thresholdColumn;
	}

	private ColumnConfig<ResultRow, ?> createValueColumn() {
		final ColumnConfig<ResultRow, String> valueColumn = new ColumnConfig<ResultRow, String>(
				new ValueProvider<ResultRow, String>() {

					@Override
					public String getPath() {
						return TreeGridFields.VALUE.toString();
					}

					@Override
					public String getValue(final ResultRow row) {
						return row.getFormatedValue(new DefaultFormattedResultMessages(
								messages));
					}

					@Override
					public void setValue(final ResultRow row, final String value) {
					}
				}, 70, messages.value());
		valueColumn.setFixed(true);
		valueColumn.setSortable(false);
		valueColumn.setMenuDisabled(true);
		return valueColumn;
	}

    private ColumnConfig<ResultRow, ?> createAddWidgetColumn() {
        final ColumnConfig<ResultRow, Boolean> column = new ColumnConfig<ResultRow, Boolean>(
            new ValueProvider<ResultRow, Boolean>() {
                @Override
                public String getPath() {
                    return ADD_WIDGET_COLUMN_PATH;
                }

                @Override
                public Boolean getValue(final ResultRow row) {
                    return row instanceof TaskRow
                            ? ((TaskRow) row).isMultipleProgramBitrateTask()
                            : false;
                }

                @Override
                public void setValue(final ResultRow row, final Boolean value) {
                    // do nothing
                }
            }, 25);

        column.setCell(new AbstractCell<Boolean>() {

            @Override
            public void render(final Context context,
                               final Boolean value, final SafeHtmlBuilder sb) {

                if (value) {
                    final Image button = AbstractImagePrototype.create(appearanceFactory.resources()
                            .createWidgetIcon()).createImage();
                    button.setTitle(messages.addWidgetToDashboardMessage());
                    button.addStyleName(appearanceFactory.resources().css().cursorPointer());

                    sb.append(SafeHtmlUtils.fromTrustedString(button.toString()));
                }
            }
        });
        column.setFixed(true);
        column.setSortable(false);
        column.setMenuDisabled(true);
        return column;
    }

	@Override
	protected List<ColumnConfig<ResultRow, ?>> getGridColumns() {
		final List<ColumnConfig<ResultRow, ?>> columns = new ArrayList<ColumnConfig<ResultRow, ?>>();

        columns.add(createAddWidgetColumn());
		columns.add(createPaddingColumn());
		columns.add(createNameColumn());
		columns.add(createValueColumn());
		columns.add(createThresholdColumn());
		columns.add(createDateColumn());
		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE,
				appearanceFactory.resources().css().textMainColor()};
	}

	@Override
	protected int getTreeColumnIndex() {
		return 1;
	}

	@Override
	public void initialize() {
		super.initialize();
		grid.setHideHeaders(false);
		setupTreeGridView(new CustomTreeGridView<ResultRow>(
			appearanceFactory.columnHeaderAppearance(), 
			true));
		grid.getView().setViewConfig(new AgentResultsViewConfig());
        grid.addCellClickHandler(new CellClickEvent.CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                final int colIndex = event.getCellIndex();
                final ColumnConfig<?, ?> column = grid.getColumnModel()
                        .getColumn(colIndex);
				final TaskRow row = (TaskRow)grid.getStore().get(event.getRowIndex());
                if (column != null
                        && column.getPath().equals(ADD_WIDGET_COLUMN_PATH) && row.isMultipleProgramBitrateTask()) {
                    getUiHandlers().displayAddAnalyticsToDashboardDialog(row.getKey(), row.getName());
                }
            }
        });
		initializeLoader();
	}

	private void initializeLoader() {
		loader = new TreeLoader<ResultRow>(initializeLoaderProxy()) {

			@Override
			public boolean hasChildren(final ResultRow parent) {
				return parent instanceof TaskRow;
			}
		};
	}

	private RpcProxy<ResultRow, List<ResultRow>> initializeLoaderProxy() {
		return new RpcProxy<ResultRow, List<ResultRow>>() {

			@Override
			public void load(final ResultRow loadConfig,
					final AsyncCallback<List<ResultRow>> callback) {
				getUiHandlers().actionLoadTasks(
						new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgentTask>>(
								messages.tasksLoadingFail(), true) {

							@Override
							protected void success(final List<MAgentTask> tasks) {
								createGridData(tasks);
								getUiHandlers()
										.actionLoadResults(
												tasks,
												new AutoNotifyingAsyncLogoutOnFailureCallback<List<Map<String, Object>>>() {

													@Override
													protected void success(
															final List<Map<String, Object>> result) {
														updateGrid(tasks,
																result);

														getUiHandlers()
																.actionStartResultPolling(
																		tasks);

														Scheduler
																.get()
																.scheduleDeferred(
																		new ScheduledCommand() {

																			@Override
																			public void execute() {
																				grid.collapseAll();
																			}
																		});
													}
												});
							}
						});
			}
		};
	}

	@Override
	public void load() {
		loader.load();
	}

	/**
	 * Updates TreeGrid by replacement all rows
	 * 
	 * @param tasks
	 *            - results or null
	 */
	@Override
	public void updateGrid(final List<MAgentTask> tasks,
			final List<Map<String, Object>> allResults) {
		for (final Map<String, Object> taskResults : allResults) {

			for (final Map.Entry<String, Object> taskResultEntry : taskResults
					.entrySet()) {
				final String taskStorageKey = taskResultEntry.getKey();
				final ParameterRow parameterRow = (ParameterRow) grid
						.getTreeStore().findModelWithKey(taskStorageKey);
				if (parameterRow != null) {
					final Double value = (Double) taskResultEntry.getValue();
					final Date date = (Date) taskResults
							.get(SimpleUtils.DATE_PARAMETER_NAME);
					parameterRow.setValue(value);
					parameterRow.setDate(date);
				}
			}
		}
		grid.getView().refresh(false);
	}
}

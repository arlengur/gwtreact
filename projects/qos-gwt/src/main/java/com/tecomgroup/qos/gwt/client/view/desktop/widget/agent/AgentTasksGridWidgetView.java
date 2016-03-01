/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.LongPropertyEditor;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter.BooleanFilterMessages;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentTasksGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.SamplingRateValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.TaskPropertiesValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.TaskProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataGridView;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author sviyazov.a
 * 
 */
public class AgentTasksGridWidgetView
		extends
			AbstractLocalDataGridView<MAgentTask, AgentTasksGridWidgetPresenter>
		implements
			AgentTasksGridWidgetPresenter.MyView {

	private final TaskProperties taskProps = GWT.create(TaskProperties.class);

	private final SamplingRateValueProvider samplingRateValueProvider = new SamplingRateValueProvider(
			taskProps.samplingRate().getPath());

	private final TaskPropertiesValueProvider taskPropertiesValueProvider = new TaskPropertiesValueProvider(
			taskProps.properties().getPath());

	@Inject
	public AgentTasksGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		CONFIRMATION_DIALOG_WIDTH = 600;
	}

	@Override
	protected boolean addButtonsToToolbar() {
		final Margins margins = new Margins(10, 7, 7, 10);
		final CssFloatData layoutData = new CssFloatData();
		final Image delete = AbstractImagePrototype.create(
				appearanceFactory.resources().deleteButton()).createImage();
		delete.setTitle(messages.delete());

		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final List<MAgentTask> tasks = grid.getSelectionModel()
						.getSelectedItems();
				if (SimpleUtils.isNotNullAndNotEmpty(tasks)) {
					String title, message;
					if (tasks.size() > 1) {
						title = messages.tasksRemoval();
						message = messages
								.taskMultipleDeletionQuestion(SimpleUtils
										.toCommaSeparatedString(SimpleUtils
												.getKeys(tasks)))
								+ " " + messages.tasksDeletionLongDescription();
					} else {
						title = messages.taskRemoval() + ": "
								+ tasks.get(0).getKey();
						message = messages.taskSingleDeletionQuestion() + " "
								+ messages.tasksDeletionLongDescription();
					}
					dialogFactory.createConfirmationDialog(
							new ConfirmationDialog.ConfirmationHandler() {
								@Override
								public void onCancel() {
									// Do nothing
								}

								@Override
								public void onConfirm(final String comment) {
									getUiHandlers().actionDeleteTasks(
											SimpleUtils.getKeys(tasks));
								}
							}, title, message, CommentMode.DISABLED,
							CONFIRMATION_DIALOG_WIDTH).show();
				}
			}
		});
		delete.getElement().<XElement> cast().setMargins(margins);
		toolbar.add(delete, layoutData);

		final Image separator = createSeparator();
		separator.getElement().<XElement> cast().setMargins(margins);
		toolbar.add(separator, layoutData);

		final Image update = createToolBarButton(appearanceFactory.resources()
				.updateButtonInvert(), messages.update(), null);
		update.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (getUiHandlers().isAgentSelected()) {
					getUiHandlers().reload(true);
				}
			}
		});
		update.getElement().<XElement> cast().setMargins(margins);
		toolbar.add(update, layoutData);

		return true;
	}

	@Override
	public void clearStore() {
		getStore().clear();
	}

	private BooleanFilter<MAgentTask> createDisabledFilter() {
		final BooleanFilter<MAgentTask> filter = filterFactory
				.createBooleanFilter(taskProps.disabled());
		filter.setMessages(new BooleanFilterMessages() {

			@Override
			public String noText() {
				return messages.actionYes();
			}

			@Override
			public String yesText() {
				return messages.actionNo();
			}
		});
		return filter;
	}

	@Override
	protected List<Filter<MAgentTask, ?>> createFilters() {

		final List<Filter<MAgentTask, ?>> filters = new ArrayList<Filter<MAgentTask, ?>>();

		filters.add(filterFactory.<MAgentTask> createDateFilter(taskProps
				.dateCreation()));

		filters.add(filterFactory.<MAgentTask> createStringFilter(taskProps
				.key()));

		filters.add(filterFactory.<MAgentTask> createStringFilter(taskProps
				.displayName()));

		filters.add(filterFactory.<MAgentTask> createStringFilter(taskProps
				.moduleName()));

		filters.add(filterFactory.<MAgentTask, Long> createNumericFilter(
				taskProps.version(), new LongPropertyEditor()));

		filters.add(filterFactory.<MAgentTask, Long> createNumericFilter(
				samplingRateValueProvider, new LongPropertyEditor()));

		filters.add(filterFactory
				.<MAgentTask> createStringFilter(taskPropertiesValueProvider));

		filters.add(createDisabledFilter());

		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

	@Override
	protected ListStore<MAgentTask> createStore() {
		return new ListStore<MAgentTask>(taskProps.modelKey());
	}

	@Override
	protected List<ColumnConfig<MAgentTask, ?>> getGridColumns() {
		final ColumnConfig<MAgentTask, String> key = new ColumnConfig<MAgentTask, String>(
				taskProps.key(), 110, messages.key());
		final ColumnConfig<MAgentTask, String> displayName = new ColumnConfig<MAgentTask, String>(
				taskProps.displayName(), 150, messages.displayName());
		final ColumnConfig<MAgentTask, String> moduleName = new ColumnConfig<MAgentTask, String>(
				taskProps.moduleName(), 90, messages.moduleName());
		final ColumnConfig<MAgentTask, Long> version = new ColumnConfig<MAgentTask, Long>(
				taskProps.version(), 25, messages.version());
		final ColumnConfig<MAgentTask, Long> samplingRate = new ColumnConfig<MAgentTask, Long>(
				samplingRateValueProvider, 35, messages.samplingRate());
		final ColumnConfig<MAgentTask, Date> creationDate = new ColumnConfig<MAgentTask, Date>(
				taskProps.dateCreation(), 55, messages.taskCreationDate());
		creationDate.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));
		final ColumnConfig<MAgentTask, String> properties = new ColumnConfig<MAgentTask, String>(
				taskPropertiesValueProvider, 100, messages.properties());
		final ColumnConfig<MAgentTask, Boolean> isActive = new ColumnConfig<MAgentTask, Boolean>(
				taskProps.disabled(), 30, messages.taskActive());

		isActive.setCell(new PropertyDisplayCell<Boolean>(
				new PropertyEditor<Boolean>() {

					@Override
					public Boolean parse(final CharSequence text)
							throws ParseException {
						return text.equals(messages.actionYes()) ? false : true;
					}

					@Override
					public String render(final Boolean disabled) {
						return disabled ? messages.actionNo() : messages
								.actionYes();
					}
				}));

		samplingRate.setCell(new PropertyDisplayCell<Long>(
				new PropertyEditor<Long>() {

					@Override
					public Long parse(final CharSequence text)
							throws ParseException {
						Long value;
						try {
							value = Long.parseLong(text.toString());
						} catch (final NumberFormatException exception) {
							value = null;
						}
						return value;
					}

					@Override
					public String render(final Long value) {
						return value == null ? "" : value.toString();
					}
				}));

		final List<ColumnConfig<MAgentTask, ?>> columnList = new ArrayList<ColumnConfig<MAgentTask, ?>>();
		columnList.add(key);
		columnList.add(displayName);
		columnList.add(moduleName);
		columnList.add(version);
		columnList.add(samplingRate);
		columnList.add(creationDate);
		columnList.add(properties);
		columnList.add(isActive);

		return columnList;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE};
	}
}
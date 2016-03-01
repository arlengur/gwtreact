/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.AgentWrapper;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataGridView;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author sviyazov.a
 * 
 */
public class AgentsGridWidgetView
		extends
			AbstractLocalDataGridView<AgentWrapper, AgentsGridWidgetPresenter>
		implements
			AgentsGridWidgetPresenter.MyView {

	public interface AgentWrapperProperties
			extends
				PropertyAccess<AgentWrapper> {
		@Path("description")
		ValueProvider<AgentWrapper, String> description();
		@Path("displayName")
		ValueProvider<AgentWrapper, String> displayName();
		@Path("agent")
		ModelKeyProvider<AgentWrapper> key();
		@Path("lastResultTime")
		ValueProvider<AgentWrapper, Date> lastResultTime();
		@Path("registrationTime")
		ValueProvider<AgentWrapper, Date> registrationTime();
		@Path("state")
		ValueProvider<AgentWrapper, String> state();
	}

	private final AgentWrapperProperties agentProps = GWT
			.create(AgentWrapperProperties.class);
	private String selectedAgent;

	@Inject
	public AgentsGridWidgetView(final QoSMessages messages,
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
				final List<AgentWrapper> agentWrappers = grid
						.getSelectionModel().getSelectedItems();
				if (SimpleUtils.isNotNullAndNotEmpty(agentWrappers)) {
					String title, message;
					if (agentWrappers.size() > 1) {
						title = messages.agentsRemoval();
						final List<String> keys = new ArrayList<String>();
						for (final AgentWrapper wrapper : agentWrappers) {
							keys.add(wrapper.getAgent());
						}
						message = messages
								.agentsMultipleDeletionQuestion(SimpleUtils
										.toCommaSeparatedString(keys))
								+ " "
								+ messages.agentsDeletionLongDescription();
					} else {
						title = messages.agentRemoval() + ": "
								+ agentWrappers.get(0).getAgent();
						message = messages.agentsSingleDeletionQuestion() + " "
								+ messages.agentsDeletionLongDescription();
					}
					dialogFactory.createConfirmationDialog(
							new ConfirmationDialog.ConfirmationHandler() {
								@Override
								public void onCancel() {
									// Do nothing
								}

								@Override
								public void onConfirm(final String comment) {
									if (SimpleUtils
											.isNotNullAndNotEmpty(agentWrappers)) {
										final Set<String> agentKeys = new HashSet<String>();
										for (final AgentWrapper agentWrapper : agentWrappers) {
											agentKeys.add(agentWrapper
													.getAgent());
										}
										getUiHandlers().actionDeleteAgents(
												agentKeys);
									}
								}
							}, title, message, CommentMode.DISABLED,
							CONFIRMATION_DIALOG_WIDTH).show();
				}
			}
		});
		delete.addStyleName(appearanceFactory.resources().css().cursorPointer());
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
				getUiHandlers().reload(true);
			}
		});
		update.getElement().<XElement> cast().setMargins(margins);
		toolbar.add(update, layoutData);

		return true;
	}

	@Override
	protected List<Filter<AgentWrapper, ?>> createFilters() {
		final List<Filter<AgentWrapper, ?>> filters = new ArrayList<Filter<AgentWrapper, ?>>();

		filters.add(filterFactory.<AgentWrapper> createDateFilter(agentProps
				.registrationTime()));

		filters.add(filterFactory.<AgentWrapper> createDateFilter(agentProps
				.lastResultTime()));

		filters.add(filterFactory.<AgentWrapper> createStringFilter(agentProps
				.displayName()));

		filters.add(filterFactory.<AgentWrapper> createStringFilter(agentProps
				.state()));

		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

	@Override
	protected ListStore<AgentWrapper> createStore() {
		final ListStore<AgentWrapper> store = new ListStore<AgentWrapper>(
				agentProps.key());
		store.addStoreFilterHandler(new StoreFilterHandler<AgentWrapper>() {

			@Override
			public void onFilter(final StoreFilterEvent<AgentWrapper> event) {
				// There are actually 2 types of events being fired: one when
				// filters are cleared, and right after that - one when all new
				// filters (even if there is none) are applied. The only way to
				// check it is through filter set size.
				if (store.getFilters().size() > 0
						&& store.findModelWithKey(selectedAgent) == null) {
					selectedAgent = null;
					getUiHandlers().clearAgentTasks();
				}
			}
		});
		return store;
	}

	@Override
	protected List<ColumnConfig<AgentWrapper, ?>> getGridColumns() {
		final List<ColumnConfig<AgentWrapper, ?>> agentColumns = new ArrayList<ColumnConfig<AgentWrapper, ?>>();
		final ColumnConfig<AgentWrapper, String> displayNameColumn = new ColumnConfig<AgentWrapper, String>(
				agentProps.displayName(), 100, messages.name());
		final ColumnConfig<AgentWrapper, String> descriptionColumn = new ColumnConfig<AgentWrapper, String>(
				agentProps.description(), 100, messages.description());
		final ColumnConfig<AgentWrapper, Date> registrationTimeColumn = new ColumnConfig<AgentWrapper, Date>(
				agentProps.registrationTime(), 55,
				messages.systemComponentRegistrationTime());
		final ColumnConfig<AgentWrapper, Date> lastResultTimeColumn = new ColumnConfig<AgentWrapper, Date>(
				agentProps.lastResultTime(), 55,
				messages.systemComponentLastResultTime());
		final ColumnConfig<AgentWrapper, String> stateColumn = new ColumnConfig<AgentWrapper, String>(
				agentProps.state(), 100, messages.registrationState());

		registrationTimeColumn.setCell(new DateCell(
				DateUtils.DATE_TIME_FORMATTER));
		lastResultTimeColumn
				.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));

		displayNameColumn.setCell(new AgentDetailsHrefCell(getStore()));

		agentColumns.add(displayNameColumn);
		agentColumns.add(descriptionColumn);
		agentColumns.add(registrationTimeColumn);
		agentColumns.add(lastResultTimeColumn);
		agentColumns.add(stateColumn);


		return agentColumns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE};
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();

		grid.addRowClickHandler(new RowClickHandler() {

			@Override
			public void onRowClick(final RowClickEvent event) {
				final List<AgentWrapper> items = grid.getSelectionModel()
						.getSelectedItems();
				if (items.size() == 1) {
					final AgentWrapper agent = grid.getSelectionModel()
							.getSelectedItem();
					selectedAgent = agent.getAgent();
					getUiHandlers().actionSelectAgent(selectedAgent);
				} else {
					selectedAgent = null;
					getUiHandlers().clearAgentTasks();
				}
			}
		});
	}

	@Override
	protected void onAfterFirstAttach() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				grid.getView().layout();
			}
		});
	}

	@Override
	public void removeItems(final Set<String> keys) {
		super.removeItems(keys);

		if (store.findModelWithKey(selectedAgent) == null) {
			selectedAgent = null;
			getUiHandlers().clearAgentTasks();
		}
	}
}

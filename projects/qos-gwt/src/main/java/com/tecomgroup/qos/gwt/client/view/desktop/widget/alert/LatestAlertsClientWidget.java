/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.dashboard.LatestAlertsWidget;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AlertDetailsPresenter;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DurationValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.SystemComponentValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.AbstractWidgetTileContentElement;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author ivlev.e
 *
 */
public class LatestAlertsClientWidget
		extends
			AbstractWidgetTileContentElement<LatestAlertsWidget>
		implements
			QoSEventListener {

	private final VerticalLayoutContainer content;

	private final Grid<MAlert> grid;

	private final ListStore<MAlert> store;

	private final AlertProperties alertProperties = GWT
			.create(AlertProperties.class);

	private final QoSMessages messages;

	private final UserServiceAsync userService;

	private final QoSEventService eventService;

	private final static int REFRESH_DELAY_IN_SEC = 10;

	/**
	 * Initial column width (WORKAROUND). It is actual for small screen devices.
	 */
	private final static int DEFAULT_DEVICE_COLUMN_WIDTH = 70;

	private Timer loadingDataTask;

	private final static Logger LOGGER = Logger
			.getLogger(LatestAlertsClientWidget.class.getName());

	public LatestAlertsClientWidget(final UserServiceAsync userService,
			final QoSEventService eventService, final LatestAlertsWidget model,
			final QoSMessages messages,
			final AppearanceFactory appearanceFactory) {
		super(model);
		this.eventService = eventService;
		this.userService = userService;
		this.messages = messages;
		store = new ListStore<MAlert>(alertProperties.key());
		final AlertsGridAppearance gridAppearance = appearanceFactory
				.gridAlertsAppearance();
		grid = new Grid<MAlert>(store, createColumnModel(),
				new CustomGridView<MAlert>(gridAppearance,
						appearanceFactory.columnHeaderAppearance()));

		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.addStyleName(ClientConstants.QOS_GRID_ALERTS_STYLE);
		grid.addStyleName(ClientConstants.QOS_GRID_ALERTS_ALWAYS_SHINING);
		grid.getView().setEmptyText(messages.noAlertsFound());

		final LatestAlertsGridViewConfig viewConfig = new LatestAlertsGridViewConfig(
				gridAppearance.getResources(), appearanceFactory);

		grid.getView().setViewConfig(viewConfig);

		grid.getView().setStripeRows(false);
		grid.getView().setAutoFill(true);
		grid.getView().setForceFit(true);
		grid.getStore().setAutoCommit(true);
		grid.getView().setColumnLines(false);
		grid.addStyleName(appearanceFactory.resources().css().textMainColor());

		content = new VerticalLayoutContainer();
		content.add(grid, new VerticalLayoutData(1, 1));
		content.setScrollMode(ScrollMode.NONE);
		initializeListeners();
	}

	private void adjustColumnWidth() {
		final int conainerWidth = grid.getParent().getOffsetWidth();
		final double newColumnWidth = Math.floor((double) conainerWidth
				/ grid.getColumnModel().getColumnCount());
		for (final ColumnConfig<MAlert, ?> column : grid.getColumnModel()
				.getColumns()) {
			column.setWidth(Double.valueOf(newColumnWidth).intValue());
		}
	}

	private void configureColumn(final String columnHeader,
			final ColumnConfig<MAlert, String> column) {
		column.setHeader(columnHeader);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setFixed(true);
		if (!AppUtils.screenWidthIsEnough()) {
			column.setWidth(DEFAULT_DEVICE_COLUMN_WIDTH);
		}
	}

	private ColumnModel<MAlert> createColumnModel() {
		final List<ColumnConfig<MAlert, ?>> columns = new ArrayList<ColumnConfig<MAlert, ?>>();

		final ColumnConfig<MAlert, String> displayNameColumn = new ColumnConfig<MAlert, String>(
				alertProperties.displayName());
		final ColumnConfig<MAlert, String> systemComponentColumn = new ColumnConfig<MAlert, String>(
				new SystemComponentValueProvider.AlertSystemComponentValueProvider(
						alertProperties.systemComponent().getPath()));
		final ColumnConfig<MAlert, String> durationColumn = new ColumnConfig<MAlert, String>(
				new DurationValueProvider.AlertDurationValueProvider(messages));
		final ColumnConfig<MAlert, String> sourceColumn = new ColumnConfig<MAlert, String>(
				alertProperties.source());
		final ColumnConfig<MAlert, String>originatorColumn = new ColumnConfig<MAlert, String>(
				alertProperties.originator());

		configureColumn(messages.displayName(), displayNameColumn);
		configureColumn(messages.probe(), systemComponentColumn);
		configureColumn(messages.duration(), durationColumn);
		columns.add(displayNameColumn);
		columns.add(systemComponentColumn);
		columns.add(durationColumn);
		/**
		 * Add additional columns for wide widgets
		 */
		if(this.getModel().getColspan()>1) {
			configureColumn(messages.source(), sourceColumn);
			configureColumn(messages.originator(), originatorColumn);
			columns.add(sourceColumn);
			columns.add(originatorColumn);
		}

		return new ColumnModel<MAlert>(columns);
	}

	@Override
	public void dispose() {
		eventService.unsubscribe(
				LatestAlertsWidget.EVENT_SERVICE_DOMAIN_PREFIX, model.getKey(),
				this);
	}

	@Override
	public Widget getContentElement() {
		return content;
	}

	@Override
	public void initialize() {
		hideWidget(content, true);
		loadData();
		eventService.subscribe(LatestAlertsWidget.EVENT_SERVICE_DOMAIN_PREFIX,
				model.getKey(), this, model);
	}

	private void initializeListeners() {
		grid.addViewReadyHandler(new ViewReadyHandler() {

			@Override
			public void onViewReady(final ViewReadyEvent event) {
				grid.getView().getScroller().getStyle()
				.setOverflow(Overflow.HIDDEN);

			}
		});

		grid.addRowClickHandler(new RowClickHandler() {

			@Override
			public void onRowClick(final RowClickEvent event) {
				final MAlert alert = grid.getSelectionModel().getSelectedItem();
				if (alert != null) {
					final PlaceRequest request = AlertDetailsPresenter
							.createAlertDetailsRequest(alert);
					AppUtils.getPlaceManager().revealPlace(request);
				}
			}
		});
	}

	private void loadData() {
		LOGGER.finer("Loading data for the latest alerts widget "
				+ model.getKey());
		userService.loadWigetData(model, new AsyncCallback<List<MAlert>>() {

			@Override
			public void onFailure(final Throwable caught) {
				AppUtils.showInfoMessage("Cannot load data for "
						+ model.getKey());
			}

			@Override
			public void onSuccess(final List<MAlert> result) {
				store.clear();
				store.addAll(result);
				loadingDataTask = null;

				new Timer() {
					@Override
					public void run() {
						refresh();
						hideWidget(content, false);
					}
				}.schedule(TimeConstants.MILLISECONDS_PER_SECOND);
			}
		});
	}

	@Override
	public void onServerEvent(final AbstractEvent event) {
		if (loadingDataTask == null) {
			loadingDataTask = scheduleLoadingDataTask();
		}
	}

	@Override
	public void refresh() {
		adjustColumnWidth();
		grid.getView().refresh(true);
		content.forceLayout();
	}

	private Timer scheduleLoadingDataTask() {
		final Timer task = new Timer() {

			@Override
			public void run() {
				loadData();
			}
		};
		task.schedule(REFRESH_DELAY_IN_SEC
				* TimeConstants.MILLISECONDS_PER_SECOND);

		return task;
	}

}

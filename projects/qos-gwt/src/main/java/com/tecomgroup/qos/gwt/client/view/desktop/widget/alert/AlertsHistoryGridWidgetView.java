/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AlertsHistoryGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.AlertUpdateValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.SystemComponentValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.filter.MAlertUpdateMapper;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertUpdateTypePropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertHistoryProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridView;

/**
 * @author abondin
 * 
 */
public class AlertsHistoryGridWidgetView
		extends
			AbstractRemoteDataGridView<MAlertUpdate, AlertsHistoryGridWidgetPresenter>
		implements
			AlertsHistoryGridWidgetPresenter.MyView {

	protected AlertHistoryProperties alertHistoryProperties;

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public AlertsHistoryGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		alertHistoryProperties = GWT.create(AlertHistoryProperties.class);
		refreshAfterGridDataReload = true;
	}

	@Override
	protected boolean addButtonsToToolbar() {
		// No toolbar needed
		return false;
	}

	@Override
	protected void applyDefaultConfiguration() {
		applyOrder(Order.desc(alertHistoryProperties.dateTime().getPath()),
				false);
	}

	@Override
	protected List<Filter<MAlertUpdate, ?>> createFilters() {
		final List<Filter<MAlertUpdate, ?>> filters = new ArrayList<Filter<MAlertUpdate, ?>>();

		filters.add(filterFactory
				.<MAlertUpdate> createDateFilter(alertHistoryProperties
						.dateTime()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.alertTypeDisplayName()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.systemComponent()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.source()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.originator()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.field()));
		filters.add(filterFactory
				.<MAlertUpdate, UpdateType> createEnumListFilter(
						alertHistoryProperties.action(),
						new AlertUpdateTypePropertyEditor(messages)));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.previousValue()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.newValue()));
		filters.add(filterFactory
				.<MAlertUpdate> createStringFilter(alertHistoryProperties
						.comment()));
		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

	@Override
	protected ListStore<MAlertUpdate> createStore() {
		return new ListStore<MAlertUpdate>(alertHistoryProperties.key());
	}

	@Override
	public AlertHistoryProperties getAlertHistoryProperties() {
		return alertHistoryProperties;
	}

	/**
	 * @return
	 */
	@Override
	protected List<ColumnConfig<MAlertUpdate, ?>> getGridColumns() {
		final ColumnConfig<MAlertUpdate, Date> dateTime = new ColumnConfig<MAlertUpdate, Date>(
				alertHistoryProperties.dateTime(), 50, messages.time());
		dateTime.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));
		final ColumnConfig<MAlertUpdate, String> alertDisplayName = new ColumnConfig<MAlertUpdate, String>(
				alertHistoryProperties.alertTypeDisplayName(), 50,
				messages.alert());

		final ColumnConfig<MAlertUpdate, String> systemComponent = new ColumnConfig<MAlertUpdate, String>(
				new SystemComponentValueProvider.AlertUpdateSystemComponentValueProvider(
						alertHistoryProperties.systemComponent().getPath()),
				50, messages.probe());

		final ColumnConfig<MAlertUpdate, String> source = new ColumnConfig<MAlertUpdate, String>(
				alertHistoryProperties.source(), 50, messages.source());
		final ColumnConfig<MAlertUpdate, String> originator = new ColumnConfig<MAlertUpdate, String>(
				alertHistoryProperties.originator(), 50, messages.originator());

		final ColumnConfig<MAlertUpdate, UpdateType> action = new ColumnConfig<MAlertUpdate, UpdateType>(
				alertHistoryProperties.action(), 50, messages.action());
		action.setCell(new AlertUpdateTypePropertyEditor.Cell(messages));

		final ColumnConfig<MAlertUpdate, String> field = new ColumnConfig<MAlertUpdate, String>(
				alertHistoryProperties.field(), 50, messages.parameter());
		final ColumnConfig<MAlertUpdate, String> previousValue = new ColumnConfig<MAlertUpdate, String>(
				new AlertUpdateValueProvider.AlertUpdateOldValueProvider(
						alertHistoryProperties.previousValue().getPath()), 50,
				messages.previousValue());
		final ColumnConfig<MAlertUpdate, String> newValue = new ColumnConfig<MAlertUpdate, String>(
				new AlertUpdateValueProvider.AlertUpdateNewValueProvider(
						alertHistoryProperties.newValue().getPath()), 50,
				messages.newValue());
		final ColumnConfig<MAlertUpdate, String> comment = new ColumnConfig<MAlertUpdate, String>(
				alertHistoryProperties.comment(), 50, messages.comment());

		final List<ColumnConfig<MAlertUpdate, ?>> columns = new ArrayList<ColumnConfig<MAlertUpdate, ?>>();

		columns.add(dateTime);
		columns.add(alertDisplayName);
		columns.add(systemComponent);
		columns.add(source);
		columns.add(originator);
		columns.add(action);
		columns.add(field);
		columns.add(previousValue);
		columns.add(newValue);
		columns.add(comment);

		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE};
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		grid.getView().setStripeRows(true);
	}

	@Override
	protected RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlertUpdate>> initializeLoaderProxy() {
		return new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlertUpdate>>() {
			@Override
			public void load(final FilterPagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<MAlertUpdate>> callback) {
				getUiHandlers().setFilteringCriterion(
						convertFiltersToCriterion(loadConfig,
								MAlertUpdateMapper.getInstance()));
				final Criterion criterion = getUiHandlers()
						.getConfigurableCriterion();

				getUiHandlers().getTotalHistoryCount(
						criterion,
						new AutoNotifyingAsyncCallback<Long>(
								"Cannot get alerts history count", true) {
							@Override
							protected void success(final Long result) {
								final int totalElements = result.intValue();
								getUiHandlers()
										.actionLoadHistory(
												criterion,
												getCurrentOrder(),
												loadConfig.getOffset(),
												loadConfig.getLimit(),
												new AutoNotifyingAsyncCallback<List<MAlertUpdate>>(

												"Cannot load history", true) {
													@Override
													protected void success(
															final List<MAlertUpdate> result) {
														final PagingLoadResultBean<MAlertUpdate> pagingLoadResult = new PagingLoadResultBean<MAlertUpdate>(
																result,
																totalElements,
																loadConfig
																		.getOffset());
														callback.onSuccess(pagingLoadResult);
													}
												});
							}
						});
			}
		};
	}
}

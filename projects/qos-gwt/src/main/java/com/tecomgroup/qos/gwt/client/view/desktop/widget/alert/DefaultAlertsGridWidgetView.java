/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.DefaultAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.filter.MAlertMapper;

/**
 * @author ivlev.e
 * 
 */
public class DefaultAlertsGridWidgetView extends AbstractAlertsGridWidgetView
		implements
			DefaultAlertsGridWidgetPresenter.MyView {

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public DefaultAlertsGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
	}

	@Override
	protected List<Filter<MAlert, ?>> createFilters() {
		final List<Filter<MAlert, ?>> filters = super.createFilters();

		filters.add(filterFactory.<MAlert> createStringFilter(alertProperties
				.systemComponent()));

		return filters;
	}

	@Override
	protected boolean hasToolbalTemplateButtons() {
		return true;
	}

	@Override
	protected RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlert>> initializeLoaderProxy() {
		return new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<MAlert>>() {
			@Override
			public void load(final FilterPagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<MAlert>> callback) {
				getUiHandlers().setFilteringCriterion(
						convertFiltersToCriterion(loadConfig,
								MAlertMapper.getInstance()));
				final Criterion criterion = getUiHandlers()
						.getConfigurableCriterion();
				getUiHandlers().getTotalAlertsCount(
						criterion,
						new AutoNotifyingAsyncCallback<Long>(messages
								.alertsCountLoadingFail(), true) {
							@Override
							protected void success(final Long result) {
								final int totalElements = result.intValue();
								getUiHandlers()
										.<DefaultAlertsGridWidgetPresenter> cast()
										.actionLoadAlerts(
												criterion,
												getCurrentOrder(),
												loadConfig.getOffset(),
												loadConfig.getLimit(),
												new AutoNotifyingAsyncCallback<List<MAlert>>(
														messages.alertsLoadingFail(),
														true) {
													@Override
													protected void success(
															final List<MAlert> result) {
														final PagingLoadResultBean<MAlert> pagingLoadResult = new PagingLoadResultBean<MAlert>(
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

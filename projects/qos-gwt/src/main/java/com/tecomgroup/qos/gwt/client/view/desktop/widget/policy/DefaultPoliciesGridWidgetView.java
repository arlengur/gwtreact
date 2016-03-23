/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.DefaultPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;

/**
 * @author ivlev.e
 * 
 */
public class DefaultPoliciesGridWidgetView
		extends
			AbstractPoliciesGridWidgetView
		implements
			DefaultPoliciesGridWidgetPresenter.MyView {

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public DefaultPoliciesGridWidgetView(
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory,
			final PolicyToolbarWidgetPresenter<PolicyWrapper>
                    policyToolbarWidgetPresenter,
            final PolicyItemWidgetPresenter policyItemWidgetPresenter) {
		super(messages, appearanceFactoryProvider, dialogFactory,
				filterFactory, policyToolbarWidgetPresenter, policyItemWidgetPresenter);
	}

	@Override
	protected RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PolicyWrapper>> initializeLoaderProxy() {
		return new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PolicyWrapper>>() {
			@Override
			public void load(
					final FilterPagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<PolicyWrapper>> callback) {
				getUiHandlers().setFilteringCriterion(
						convertFiltersToCriterion(loadConfig, null));
				final Criterion criterion = getUiHandlers()
						.getConfigurableCriterion();
				getUiHandlers().getTotalCount(
						criterion,
						new AutoNotifyingAsyncLogoutOnFailureCallback<Long>(
								"Cannot get alerts count", true) {
							@Override
							protected void success(final Long result) {
								final int totalElements = result.intValue();
								getUiHandlers()
										.<DefaultPoliciesGridWidgetPresenter> cast()
										.actionLoadPolicies(
												criterion,
												getCurrentOrder(),
												loadConfig.getOffset(),
												loadConfig.getLimit(),
												new AutoNotifyingAsyncLogoutOnFailureCallback<List<PolicyWrapper>>(
														"Cannot load policies",
														true) {
													@Override
													protected void success(
															final List<PolicyWrapper> result) {
														final PagingLoadResultBean<PolicyWrapper> pagingLoadResult = new PagingLoadResultBean<PolicyWrapper>(
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

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin.desktop;

import com.google.gwt.inject.client.AsyncProvider;
import com.tecomgroup.qos.gwt.client.gin.QoSGinjector;
import com.tecomgroup.qos.gwt.client.presenter.AgentStatusPresenter;
import com.tecomgroup.qos.gwt.client.presenter.AlertsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.DashboardPresenter;
import com.tecomgroup.qos.gwt.client.presenter.Page404Presenter;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.PolicyListPresenter;
import com.tecomgroup.qos.gwt.client.presenter.ReportsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.ResultsAnalyticsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.TableResultPresenter;
import com.tecomgroup.qos.gwt.client.presenter.UserManagerPresenter;

/**
 * Only presenters for desktop components should be listed here. For binding of
 * presenters and views see {@link DesktopQoSGinModule}
 * 
 * @author ivlev.e
 */
public interface DesktopQoSGinjector extends QoSGinjector {

	AsyncProvider<AgentStatusPresenter> getAgentStatusPresenter();

	AsyncProvider<AlertsPresenter> getAlertsPresenter();

	AsyncProvider<DashboardPresenter> getDashboardPresenter();

	AsyncProvider<Page404Presenter> getPage404Presenter();

	AsyncProvider<PolicyItemWidgetPresenter> getPolicyItemPresenter();

	AsyncProvider<PolicyListPresenter> getPolicyListPresenter();

	AsyncProvider<ReportsPresenter> getReportsPresenter();

	AsyncProvider<ResultsAnalyticsPresenter> getResultsAnalyticsPresenter();

	AsyncProvider<TableResultPresenter> getTableResultPresenter();

	AsyncProvider<UserManagerPresenter> getUserManagerPresenter();
}

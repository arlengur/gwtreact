/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin.desktop;

import com.google.inject.Singleton;
import com.tecomgroup.qos.gwt.client.filter.DefaultLocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.gin.QoSGinModule;
import com.tecomgroup.qos.gwt.client.presenter.AddChartSeriesPresenter;
import com.tecomgroup.qos.gwt.client.presenter.AddReportCriteriaPresenter;
import com.tecomgroup.qos.gwt.client.presenter.AgentListPresenter;
import com.tecomgroup.qos.gwt.client.presenter.AgentStatusPresenter;
import com.tecomgroup.qos.gwt.client.presenter.AlertsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.ChangeUserPasswordWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.DashboardPresenter;
import com.tecomgroup.qos.gwt.client.presenter.GisPresenter;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.MainPagePresenter;
import com.tecomgroup.qos.gwt.client.presenter.Page404Presenter;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.PolicyListPresenter;
import com.tecomgroup.qos.gwt.client.presenter.ReportsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.ResultsAnalyticsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.SystemInformationPresenter;
import com.tecomgroup.qos.gwt.client.presenter.TableResultPresenter;
import com.tecomgroup.qos.gwt.client.presenter.TemplatesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.UserManagerPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddWidgetToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.DashboardPagerPresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.widget.PolicyConditionWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.UserSettingsWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentResultsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentTasksGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AddAlertsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AgentAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AlertCommentsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AlertsHistoryGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.DefaultAlertsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.SingleAlertHistoryGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.chart.AddBitrateToDashboarddWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.chart.AddChartToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.gis.AddMapToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.AgentPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.DefaultPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplateGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplateInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplatesEditorGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplatesEditorWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplateInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplateWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplatesEditorGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplatesEditorWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.AddAnalyticsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.GroupInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.GroupManagerWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserManagerGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.AddChartSeriesView;
import com.tecomgroup.qos.gwt.client.view.desktop.AddReportCriteriaView;
import com.tecomgroup.qos.gwt.client.view.desktop.AgentListView;
import com.tecomgroup.qos.gwt.client.view.desktop.AgentStatusView;
import com.tecomgroup.qos.gwt.client.view.desktop.AlertsView;
import com.tecomgroup.qos.gwt.client.view.desktop.ChangeUserPasswordWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.DashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.DefaultTableResultsView;
import com.tecomgroup.qos.gwt.client.view.desktop.GisView;
import com.tecomgroup.qos.gwt.client.view.desktop.LoadTemplateWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.MainPageView;
import com.tecomgroup.qos.gwt.client.view.desktop.Page404View;
import com.tecomgroup.qos.gwt.client.view.desktop.PolicyItemWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.PolicyListView;
import com.tecomgroup.qos.gwt.client.view.desktop.ReportsView;
import com.tecomgroup.qos.gwt.client.view.desktop.ResultsAnalyticsView;
import com.tecomgroup.qos.gwt.client.view.desktop.SaveTemplateWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.SystemInformationView;
import com.tecomgroup.qos.gwt.client.view.desktop.TemplatesGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.UserManagerView;
import com.tecomgroup.qos.gwt.client.view.desktop.UserSettingsWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AddWidgetToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DashboardPagerWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.PropertyGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.AgentResultsGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.AgentTasksGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.AgentsGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AddAlertsToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AgentAlertsGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AlertCommentsGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AlertsHistoryGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.ChartWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.DefaultAlertsGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.SingleAlertHistoryGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.chart.AddBitrateToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.chart.AddChartToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AddMapToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.AgentPoliciesGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.DefaultPoliciesGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyActionsTemplateGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyActionsTemplateInformationWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyActionsTemplatesEditorGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyActionsTemplatesEditorWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyConditionWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyConditionsTemplateInformationWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyConditionsTemplateWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyConditionsTemplatesEditorGridWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyConditionsTemplatesEditorWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.PolicyToolbarWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.report.AddAnalyticsToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.users.GroupInformationWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.users.GroupManagerWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.users.UserInformationWidgetView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.users.UserManagerGridWidgetView;

/**
 * @author ivlev.e
 * 
 */
public class DesktopQoSGinModule extends QoSGinModule {

	@Override
	protected void configure() {
		super.configure();

		bindPresenter(MainPagePresenter.class, MainPagePresenter.MyView.class,
				MainPageView.class, MainPagePresenter.MyProxy.class);

		bindPresenter(TableResultPresenter.class,
				TableResultPresenter.TableResultsView.class,
				DefaultTableResultsView.class,
				TableResultPresenter.TableResultProxy.class);

		bindPresenter(AlertsPresenter.class, AlertsPresenter.MyView.class,
				AlertsView.class, AlertsPresenter.MyProxy.class);

		bindPresenter(Page404Presenter.class, Page404Presenter.MyView.class,
				Page404View.class, Page404Presenter.MyProxy.class);

		bindPresenter(PolicyListPresenter.class,
				PolicyListPresenter.MyView.class, PolicyListView.class,
				PolicyListPresenter.MyProxy.class);

		bindPresenter(GisPresenter.class, GisPresenter.MyView.class,
				GisView.class, GisPresenter.MyProxy.class);

		bindPresenter(AgentListPresenter.class,
				AgentListPresenter.MyView.class, AgentListView.class,
				AgentListPresenter.MyProxy.class);

		bindPresenter(ReportsPresenter.class, ReportsPresenter.MyView.class,
				ReportsView.class, ReportsPresenter.MyProxy.class);

		bindPresenter(UserManagerPresenter.class,
				UserManagerPresenter.MyView.class, UserManagerView.class,
				UserManagerPresenter.MyProxy.class);

		bindPresenterWidget(DashboardPagerPresenterWidget.class,
				DashboardPagerPresenterWidget.MyView.class,
				DashboardPagerWidgetView.class);

		bindPresenterWidget(AddMapToDashboardWidgetPresenter.class,
				AddMapToDashboardWidgetPresenter.MyView.class,
				AddMapToDashboardView.class);

		bindPresenterWidget(AddChartToDashboardWidgetPresenter.class,
				AddChartToDashboardWidgetPresenter.MyView.class,
				AddChartToDashboardView.class);

		bindPresenterWidget(AddWidgetToDashboardWidgetPresenter.class,
				AddWidgetToDashboardWidgetPresenter.MyView.class,
				AddWidgetToDashboardView.class);

		bindPresenterWidget(AddAlertsToDashboardWidgetPresenter.class,
				AddAlertsToDashboardWidgetPresenter.MyView.class,
				AddAlertsToDashboardView.class);

		bindPresenterWidget(AddAnalyticsToDashboardWidgetPresenter.class,
				AddAnalyticsToDashboardWidgetPresenter.MyView.class,
				AddAnalyticsToDashboardView.class);

		bindPresenterWidget(LoadTemplatePresenterWidget.class,
				LoadTemplatePresenterWidget.MyView.class,
				LoadTemplateWidgetView.class);

		bindPresenterWidget(SaveTemplatePresenterWidget.class,
				SaveTemplatePresenterWidget.MyView.class,
				SaveTemplateWidgetView.class);

		bindPresenterWidget(SystemInformationPresenter.class,
				SystemInformationPresenter.MyView.class,
				SystemInformationView.class);

		bindPresenterWidget(DefaultAlertsGridWidgetPresenter.class,
				DefaultAlertsGridWidgetPresenter.MyView.class,
				DefaultAlertsGridWidgetView.class);

		bindPresenterWidget(AgentAlertsGridWidgetPresenter.class,
				AgentAlertsGridWidgetPresenter.MyView.class,
				AgentAlertsGridWidgetView.class);

		bindPresenterWidget(AgentResultsGridWidgetPresenter.class,
				AgentResultsGridWidgetPresenter.MyView.class,
				AgentResultsGridWidgetView.class);

		bindPresenterWidget(DefaultPoliciesGridWidgetPresenter.class,
				DefaultPoliciesGridWidgetPresenter.MyView.class,
				DefaultPoliciesGridWidgetView.class);

		bindPresenterWidget(AgentPoliciesGridWidgetPresenter.class,
				AgentPoliciesGridWidgetPresenter.MyView.class,
				AgentPoliciesGridWidgetView.class);

		bindPresenterWidget(AlertsHistoryGridWidgetPresenter.class,
				AlertsHistoryGridWidgetPresenter.MyView.class,
				AlertsHistoryGridWidgetView.class);

		bindPresenterWidget(SingleAlertHistoryGridWidgetPresenter.class,
				SingleAlertHistoryGridWidgetPresenter.MyView.class,
				SingleAlertHistoryGridWidgetView.class);

		bindPresenterWidget(AlertCommentsGridWidgetPresenter.class,
				AlertCommentsGridWidgetPresenter.MyView.class,
				AlertCommentsGridWidgetView.class);

		bindPresenterWidget(PolicyConditionWidgetPresenter.class,
				PolicyConditionWidgetPresenter.MyView.class,
				PolicyConditionWidgetView.class);

		bindPresenterWidget(PropertyGridWidgetPresenter.class,
				PropertyGridWidgetPresenter.MyView.class,
				PropertyGridWidgetView.class);

		bindPresenterWidget(TemplatesGridWidgetPresenter.class,
				TemplatesGridWidgetPresenter.MyView.class,
				TemplatesGridWidgetView.class);

		bindPresenterWidget(UserSettingsWidgetPresenter.class,
				UserSettingsWidgetPresenter.MyView.class,
				UserSettingsWidgetView.class);

		bindPresenterWidget(AddReportCriteriaPresenter.class,
				AddReportCriteriaPresenter.MyView.class,
				AddReportCriteriaView.class);

		bindPresenterWidget(UserManagerGridWidgetPresenter.class,
				UserManagerGridWidgetPresenter.MyView.class,
				UserManagerGridWidgetView.class);

		bindPresenterWidget(GroupManagerWidgetPresenter.class,
				GroupManagerWidgetPresenter.MyView.class,
				GroupManagerWidgetView.class);

		bindPresenterWidget(UserInformationWidgetPresenter.class,
				UserInformationWidgetPresenter.MyView.class,
				UserInformationWidgetView.class);

		bindPresenterWidget(GroupInformationWidgetPresenter.class,
				GroupInformationWidgetPresenter.MyView.class,
				GroupInformationWidgetView.class);

		bindPresenterWidget(PolicyItemWidgetPresenter.class,
				PolicyItemWidgetPresenter.MyView.class, PolicyItemWidgetView.class);

		bindPresenterWidget(PolicyActionsTemplateGridWidgetPresenter.class,
				PolicyActionsTemplateGridWidgetPresenter.MyView.class,
				PolicyActionsTemplateGridWidgetView.class);

		bindPresenterWidget(PolicyConditionsTemplateWidgetPresenter.class,
				PolicyConditionsTemplateWidgetPresenter.MyView.class,
				PolicyConditionsTemplateWidgetView.class);

		bindPresenterWidget(
				PolicyActionsTemplateInformationWidgetPresenter.class,
				PolicyActionsTemplateInformationWidgetPresenter.MyView.class,
				PolicyActionsTemplateInformationWidgetView.class);

		bindPresenterWidget(
				PolicyConditionsTemplateInformationWidgetPresenter.class,
				PolicyConditionsTemplateInformationWidgetPresenter.MyView.class,
				PolicyConditionsTemplateInformationWidgetView.class);

		bindPresenterWidget(
				PolicyActionsTemplatesEditorGridWidgetPresenter.class,
				PolicyActionsTemplatesEditorGridWidgetPresenter.MyView.class,
				PolicyActionsTemplatesEditorGridWidgetView.class);

		bindPresenterWidget(
				PolicyConditionsTemplatesEditorGridWidgetPresenter.class,
				PolicyConditionsTemplatesEditorGridWidgetPresenter.MyView.class,
				PolicyConditionsTemplatesEditorGridWidgetView.class);

		bindPresenterWidget(PolicyActionsTemplatesEditorWidgetPresenter.class,
				PolicyActionsTemplatesEditorWidgetPresenter.MyView.class,
				PolicyActionsTemplatesEditorWidgetView.class);

		bindPresenterWidget(
				PolicyConditionsTemplatesEditorWidgetPresenter.class,
				PolicyConditionsTemplatesEditorWidgetPresenter.MyView.class,
				PolicyConditionsTemplatesEditorWidgetView.class);

		bindPresenterWidget(ChangeUserPasswordWidgetPresenter.class,
				ChangeUserPasswordWidgetPresenter.MyView.class,
				ChangeUserPasswordWidgetView.class);

		bindPresenterWidget(AgentsGridWidgetPresenter.class,
				AgentsGridWidgetPresenter.MyView.class,
				AgentsGridWidgetView.class);

		bindPresenterWidget(AgentTasksGridWidgetPresenter.class,
				AgentTasksGridWidgetPresenter.MyView.class,
				AgentTasksGridWidgetView.class);

		bindPresenter(ResultsAnalyticsPresenter.class,
				ResultsAnalyticsPresenter.MyView.class,
				ResultsAnalyticsView.class,
				ResultsAnalyticsPresenter.MyProxy.class);

		bindPresenter(AgentStatusPresenter.class,
				AgentStatusPresenter.MyView.class, AgentStatusView.class,
				AgentStatusPresenter.MyProxy.class);

		bindPresenter(DashboardPresenter.class,
				DashboardPresenter.MyView.class, DashboardView.class,
				DashboardPresenter.MyProxy.class);

		bindPresenterWidget(PolicyToolbarWidgetPresenter.class,
				PolicyToolbarWidgetPresenter.MyView.class,
				PolicyToolbarWidgetView.class);

		bindPresenterWidget(AddChartSeriesPresenter.class,
				AddChartSeriesPresenter.MyView.class, AddChartSeriesView.class);

		bindPresenterWidget(ChartWidgetPresenter.class,
				ChartWidgetPresenter.MyView.class, ChartWidgetView.class);

        bindPresenterWidget(AddBitrateToDashboarddWidgetPresenter.class,
                AddBitrateToDashboarddWidgetPresenter.MyView.class,
                AddBitrateToDashboardView.class);

		bind(AppearanceFactory.class).toProvider(
				AppearanceFactoryProvider.class);
		bind(AppearanceFactoryProvider.class).in(Singleton.class);

		bind(LocalizedFilterFactory.class).to(
				DefaultLocalizedFilterFactory.class).in(Singleton.class);
	}
}

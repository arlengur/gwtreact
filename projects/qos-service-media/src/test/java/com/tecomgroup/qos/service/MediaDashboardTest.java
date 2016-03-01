/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.tecomgroup.qos.dashboard.DashboardAgentsWidget;
import com.tecomgroup.qos.dashboard.DashboardMapWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.LatestAlertsWidget;
import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.EventBroadcastDispatcher;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author kshnyakin.m
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/testServiceContext.xml"})
@ActiveProfiles(AbstractService.TEST_MEDIA_CONTEXT_PROFILE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MediaDashboardTest {

	@Autowired
	private AlertService alertService;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	protected TransactionTemplate readOnlyTransactionTemplate;

	@Autowired
	private InternalPolicyConfigurationService policyConfigurationService;

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private InternalUserService userService;

	@Autowired
	private MediaAgentService agentService;

	private DefaultTaskService taskService;

	@Autowired
	private WidgetDeleter mediaUserService;

	private MUser user;

	private MAgent agent;
	private MAgent otherAgent;
	private MAgentModule module;
	private MAgentModule otherModule;
	private MAgentTask task;
	private MAgentTask otherTask;

	private MPolicy policy;

	private void clearAgentRelatedWidgets(final DashboardAgentsWidget widget,
			final MAgent agentToDelete) {
		Assert.assertNull(userService.getDashboard(user.getLogin()));
		createDashboard(widget);
		final List<MDashboard> dashboards = userService.getAllDashboards();
		Assert.assertEquals(1, dashboards.size());
		final MDashboard dashboard = dashboards.iterator().next();
		Assert.assertEquals(1, dashboard.getWidgets().size());

		agentService.delete(agentToDelete);
	}

	private void clearTaskRelatedWidgets(final DashboardWidget widget,
			final MAgentTask taskToDelete) {
		Assert.assertNull(userService.getDashboard(user.getLogin()));
		createDashboard(widget);
		final List<MDashboard> dashboards = userService.getAllDashboards();
		Assert.assertEquals(1, dashboards.size());
		final MDashboard dashboard = dashboards.iterator().next();
		Assert.assertEquals(1, dashboard.getWidgets().size());

		taskService.delete(taskToDelete);
	}

	private void createDashboard(final DashboardWidget widget) {
		final MDashboard dashboard = new MDashboard();
		dashboard.setUsername(user.getLogin());
		dashboard.addWidget(widget);

		userService.updateDashboard(dashboard);
	}

	private void initModelObjects() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				agent = SharedModelConfiguration
						.createLightWeightAgent("test-agent");
				modelSpace.save(agent);

				otherAgent = SharedModelConfiguration
						.createLightWeightAgent("test-other-agent");
				modelSpace.save(otherAgent);

				module = MediaModelConfiguration.createMediaAgentModule(agent);
				modelSpace.save(module);

				otherModule = MediaModelConfiguration
						.createMediaAgentModule(otherAgent);
				modelSpace.save(otherModule);

				task = SharedModelConfiguration.createAgentTask(module);
				modelSpace.save(task);

				otherTask = SharedModelConfiguration
						.createAgentTask(otherModule);
				modelSpace.save(otherTask);

				policy = SharedModelConfiguration.createPolicy(
						"key" + UUID.randomUUID(), task, "policyActionName",
						new ParameterIdentifier(
								SharedModelConfiguration.SIGNAL_LEVEL, null));

				modelSpace.save(policy);

				user = userService.findUser("User");
			}
		});
		alertService.registerAlertType(SharedModelConfiguration
				.createAlertType(
						SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
						SharedModelConfiguration.IT09A_ALERT_TYPE_NAME));
	}

	private void sendAlerts() {
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				Source.getSource(task), Source.getSource(policy),
				"indicationSourceSettings1"), "User");
	}

	@Before
	public void setUp() throws Exception {
		initModelObjects();
		taskService = new DefaultTaskService();
		taskService.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		taskService.setTransactionTemplate(transactionTemplate);
		taskService.setPropertyUpdater(EasyMock
				.createMock(PropertyUpdater.class));
		taskService.setModelSpace(modelSpace);
		taskService.setPolicyConfigurationService(policyConfigurationService);
		taskService.setWidgetDeleter(mediaUserService);
		taskService
				.setStorageService(EasyMock.createMock(StorageService.class));
		taskService.setEventBroadcastDispatcher(EasyMock
				.createMock(EventBroadcastDispatcher.class));
		taskService.setAgentService(agentService);
	}

	@Test
	public void testClearAgentRelatedAlertsWidgets() {
		sendAlerts();

		final LatestAlertsWidget alertWidget = new LatestAlertsWidget();
		alertWidget.setAgentKeys(new HashSet<String>(Arrays.asList(
				agent.getKey(), otherAgent.getKey())));
		alertWidget.setSeverities(new HashSet<PerceivedSeverity>(Arrays
				.asList(PerceivedSeverity.MINOR)));

		clearAgentRelatedWidgets(alertWidget, agent);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());
		final Collection<DashboardWidget> widgets = dashboard.getWidgets()
				.values();
		Assert.assertEquals(1, widgets.size());
		final LatestAlertsWidget loadedWidget = ((LatestAlertsWidget) widgets
				.iterator().next());

		Assert.assertEquals(1, loadedWidget.getAgentKeys().size());
		Assert.assertTrue(loadedWidget.getAgentKeys().contains(
				otherAgent.getKey()));
	}

	@Test
	@Transactional
	public void testClearAgentRelatedMapWidgets() {
		final DashboardMapWidget mapWidget = new DashboardMapWidget();
		mapWidget.setAgentKeys(new HashSet<String>(Arrays.asList(
				agent.getKey(), otherAgent.getKey())));
		mapWidget.setZoom(1);

		clearAgentRelatedWidgets(mapWidget, agent);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());
		final Collection<DashboardWidget> widgets = dashboard.getWidgets()
				.values();
		Assert.assertEquals(1, widgets.size());
		final DashboardMapWidget loadedWidget = ((DashboardMapWidget) widgets
				.iterator().next());

		Assert.assertEquals(1, loadedWidget.getAgentKeys().size());
		Assert.assertTrue(loadedWidget.getAgentKeys().contains(
				otherAgent.getKey()));
	}

	@Test
	@Transactional
	public void testClearTaskRelatedChartWidgets() {
		final DashboardChartWidget chartWidget = new DashboardChartWidget();
		final List<DashboardChartWidget.ChartSeriesData> chartSeriesData = new ArrayList<DashboardChartWidget.ChartSeriesData>();
		final String chartName = "Chart1";

        final MChartSeries chartSeries1 = new MChartSeries(task, null, chartName);
        final MChartSeries chartSeries2 = new MChartSeries(otherTask, null, chartName);
        modelSpace.save(chartSeries1);
        modelSpace.save(chartSeries2);
		chartSeriesData.add(DashboardChartWidget.ChartSeriesData
				.fromMChartSeries(chartSeries1));
		chartSeriesData
				.add(DashboardChartWidget.ChartSeriesData
						.fromMChartSeries(chartSeries2));
		chartWidget.setSeriesData(chartSeriesData);

		clearTaskRelatedWidgets(chartWidget, otherTask);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());
		final Collection<DashboardWidget> widgets = dashboard.getWidgets()
				.values();
		Assert.assertEquals(1, widgets.size());
		final DashboardChartWidget loadedWidget = ((DashboardChartWidget) widgets
				.iterator().next());

		Assert.assertEquals(1, loadedWidget.getSeriesData().size());
		Assert.assertEquals(task.getId(), loadedWidget.getSeriesData()
				.iterator().next().getTaskId());
	}

	@Test
	public void testDeleteAgentRelatedAlertsWidgets() {
		sendAlerts();

		final LatestAlertsWidget alertWidget = new LatestAlertsWidget();
		alertWidget.setAgentKeys(new HashSet<String>(Arrays.asList(agent
                .getKey())));
		alertWidget.setSeverities(new HashSet<PerceivedSeverity>(Arrays
                .asList(PerceivedSeverity.MINOR)));

		clearAgentRelatedWidgets(alertWidget, agent);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());

		Assert.assertTrue(dashboard.getWidgets().isEmpty());
	}

	@Test
	@Transactional
	public void testDeleteAgentsRelatedMapWidgets() {
		final DashboardMapWidget mapWidget = new DashboardMapWidget();
		mapWidget.setAgentKeys(new HashSet<String>(
				Arrays.asList(agent.getKey())));
		mapWidget.setZoom(1);

		clearAgentRelatedWidgets(mapWidget, agent);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());

		Assert.assertTrue(dashboard.getWidgets().isEmpty());
	}

	@Test
	@Transactional
	public void testDeleteTaskRelatedChartWidgets() {
		final DashboardChartWidget chartWidget = new DashboardChartWidget();
		final List<DashboardChartWidget.ChartSeriesData> chartSeries = new ArrayList<DashboardChartWidget.ChartSeriesData>();
		final String chartName = "Chart1";
        final MChartSeries chartSeries1 = new MChartSeries(task, null, chartName);

        modelSpace.save(chartSeries1);
		chartSeries.add(DashboardChartWidget.ChartSeriesData
				.fromMChartSeries(chartSeries1));
		chartWidget.setSeriesData(chartSeries);

		clearTaskRelatedWidgets(chartWidget, task);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());
		Assert.assertTrue(dashboard.getWidgets().isEmpty());
	}

	@Test
	@Transactional
	public void testDeleteTaskRelatedLiveStreamWidgets() {
		final LiveStreamWidget liveWidget = new LiveStreamWidget();
		liveWidget.setTaskKey(task.getKey());
		liveWidget.setStreamKey("testStreamKey");
		clearTaskRelatedWidgets(liveWidget, task);

		final MDashboard dashboard = userService.getDashboard(user.getLogin());

		Assert.assertTrue(dashboard.getWidgets().isEmpty());
	}

}

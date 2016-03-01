/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.LatestAlertsWidget;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SharedModelConfiguration;
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
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * @author abondin
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DashboardTest {

	@Autowired
	private InternalUserService userService;

	@Autowired
	private AlertService alertService;

	private MUser user;

	private MAgent agent;
	private MAgentModule module;
	private MAgentTask task;

	@Autowired
	private ModelSpace modelSpace;

	private MPolicy policy;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Before
	public void before() throws Exception {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				agent = SharedModelConfiguration
						.createLightWeightAgent("test-agent");
				modelSpace.save(agent);

				module = SharedModelConfiguration.createAgentModule(agent);
				modelSpace.save(module);
				task = SharedModelConfiguration.createAgentTask(module);
				modelSpace.save(task);

				policy = SharedModelConfiguration.createPolicy(
						"key" + UUID.randomUUID(), task, "policyActionName",
						new ParameterIdentifier(
								SharedModelConfiguration.SIGNAL_LEVEL, null));

				modelSpace.save(policy);

				user = userService.findUser("User");

				alertService.registerAlertType(SharedModelConfiguration
						.createAlertType(
								SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
								SharedModelConfiguration.IT09A_ALERT_TYPE_NAME));;
			}
		});
	}

	private void sendAlerts() {
		alertService.activateAlert(SharedModelConfiguration.createIndication(
				SharedModelConfiguration.IT09A_ALERT_TYPE_NAME,
				Source.getSource(task), Source.getSource(policy),
				"indicationSourceSettings1"), "User");
	}

	@Test
	public void testCreateUserDashboardWithIncorrectUserName() {
		Assert.assertNull(userService.getDashboard(user.getLogin()));

		MDashboard dashboard = new MDashboard();
		dashboard.setUsername("NoSuchUser");
		try {
			userService.updateDashboard(dashboard);
		} catch (final ModelSpaceException e) {
			// Dashboard cannot be updated with incorrect username
		}

		dashboard = userService.getDashboard(user.getLogin());
		Assert.assertNull(dashboard);
	}

	@Test
	public void testCreateUserDashboardWithLatestAlertWidget() {
		sendAlerts();
		Assert.assertNull(userService.getDashboard(user.getLogin()));

		MDashboard dashboard = new MDashboard();
		dashboard.setUsername(user.getLogin());

		final LatestAlertsWidget widget = new LatestAlertsWidget();
		widget.setAgentKeys(new HashSet<String>(Arrays.asList(agent.getKey())));
		// Alert in database has severity MINOR. That means, that this widget
		// should show no alerts
		widget.setSeverities(new HashSet<PerceivedSeverity>(Arrays
				.asList(PerceivedSeverity.CRITICAL)));
		dashboard.addWidget(widget);
		userService.updateDashboard(dashboard);

		dashboard = userService.getDashboard(user.getLogin());

		final DashboardWidget loadedWidget = dashboard.getWidgets().values()
				.iterator().next();
		Assert.assertTrue(loadedWidget instanceof LatestAlertsWidget);

		final LatestAlertsWidget widget2 = new LatestAlertsWidget();
		widget.setAgentKeys(new HashSet<String>(Arrays.asList(agent.getKey())));
		// Alert in database has severity MINOR. That means, that new widget
		// will show one alert
		widget2.setSeverities(new HashSet<PerceivedSeverity>(Arrays
				.asList(PerceivedSeverity.MINOR)));
		dashboard.addWidget(widget2);
		userService.updateDashboard(dashboard);

		dashboard = userService.getDashboard(user.getLogin());
		final List<DashboardWidget> widgets = new ArrayList<DashboardWidget>(
				dashboard.getWidgets().values());
		Assert.assertEquals(2, widgets.size());
	}

	@Test
	public void testCreateUserDashboardWithoutWidgets() {
		Assert.assertNull(userService.getDashboard(user.getLogin()));

		MDashboard dashboard = new MDashboard();
		dashboard.setUsername(user.getLogin());
		userService.updateDashboard(dashboard);

		dashboard = userService.getDashboard(user.getLogin());
		Assert.assertEquals(user.getLogin(), dashboard.getUsername());
	}

	@Test
	public void testUniqueKeyInLatestAlertsWidget() {
		// Agents added in different order
		LatestAlertsWidget widget1 = new LatestAlertsWidget();
		widget1.setAgentKeys(new LinkedHashSet<String>(Arrays.asList("Key1",
				"Key2")));
		LatestAlertsWidget widget2 = new LatestAlertsWidget();
		widget2.setAgentKeys(new LinkedHashSet<String>(Arrays.asList("Key2",
				"Key1")));
		Assert.assertEquals(widget1.getKey(), widget2.getKey());

		// Severities added in different order
		widget1 = new LatestAlertsWidget();
		widget1.setSeverities(new LinkedHashSet<PerceivedSeverity>(Arrays
				.asList(PerceivedSeverity.CRITICAL, PerceivedSeverity.WARNING)));
		widget2 = new LatestAlertsWidget();
		widget2.setSeverities(new LinkedHashSet<PerceivedSeverity>(Arrays
				.asList(PerceivedSeverity.WARNING, PerceivedSeverity.CRITICAL)));
		Assert.assertEquals(widget1.getKey(), widget2.getKey());
	}

}

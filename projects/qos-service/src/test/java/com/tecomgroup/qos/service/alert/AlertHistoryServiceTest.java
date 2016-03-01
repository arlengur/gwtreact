/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.alert.AlertHistoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SharedModelConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author kunilov.p
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@Transactional
public class AlertHistoryServiceTest {

	@Autowired
	private AlertHistoryService alertHistoryService;

	@Autowired
	private ModelSpace modelSpace;

	private MAlert alert;

	private void createAlertUpdate(final String filed, final Object oldValue,
			final Object newValue, final String comment) {
        createAlertUpdate(filed, oldValue, newValue, comment, new Date());
	}

    private void createAlertUpdate(final String filed, final Object oldValue,
                                   final Object newValue, final String comment, final Date date) {
        final MAlertUpdate alertUpdate = new MAlertUpdate(alert,
                UpdateType.UPDATE, date, "test", filed, oldValue,
                newValue, comment);
        modelSpace.save(alertUpdate);
    }

	@Before
	public void setUp() {

		final MAgent agent = SharedModelConfiguration
				.createLightWeightAgent("resultTestAgentName");
		modelSpace.save(agent);
		final MAgentModule module = SharedModelConfiguration
				.createAgentModule(agent);
		modelSpace.save(module);
		final MAgentTask agentTask = SharedModelConfiguration
				.createAgentTask(module);
		modelSpace.save(agentTask);

		final MPolicy policy = SharedModelConfiguration
				.createPolicy(agentTask, "ACTIVATE");
		modelSpace.save(policy);

		final MAlertType alertType = new MAlertType();
		alertType.setName("alertTypeName");
		alertType.setDisplayName("Alert Type");
		alertType.setProbableCause(ProbableCause.ADAPTER_ERROR);
		alertType.setDescription("Alert Type Description");
		modelSpace.save(alertType);

		final Date dateTime = new Date();
		alert = new MAlert();
		alert.setAlertType(alertType);
		alert.setContext("indicationContext");
		alert.setLastUpdateDateTime(dateTime);
		alert.setCreationDateTime(dateTime);
		alert.setSeverityChangeDateTime(dateTime);
		alert.setExtraData("indicationExtraData");
		alert.setSettings("alertSettings");
		alert.setPerceivedSeverity(PerceivedSeverity.MINOR);
		alert.setSpecificReason(SpecificReason.NONE);
		alert.setSource(agentTask);
		alert.setOriginator(policy);
		alert.setAcknowledged(false);
		alert.setStatus(Status.ACTIVE);
		alert.setLastUpdateType(UpdateType.NEW);
		alert.setAlertCount(0L);
		alert.setCountSinceLastAck(0L);

		modelSpace.save(alert);
	}

	@Test
	public void testAddAlertUpdate() {
		final String field = "context";
		final String user = "test";
		final String oldValue = alert.getContext();
		final String newValue = "newAlertContext";
		alertHistoryService.addAlertUpdate(alert, UpdateType.UPDATE,
				new Date(), user, field, oldValue, newValue,
				"comment for update");

		final List<MAlertUpdate> alertUpdates = modelSpace
				.getAll(MAlertUpdate.class);
		assertEquals(1, alertUpdates.size());
		final MAlertUpdate alertUpdate = alertUpdates.iterator().next();
		assertEquals(alert, alertUpdate.getAlert());
		assertEquals(UpdateType.UPDATE, alertUpdate.getUpdateType());
		assertEquals(field, alertUpdate.getField());
		assertEquals(user, alertUpdate.getUser());
		assertEquals(oldValue, alertUpdate.getOldValue());
		assertEquals(newValue, alertUpdate.getNewValue());
	}

	@Test
	public void testGetAlertHistory() {
		createAlertUpdate("context", alert.getContext(), "newContext",
				"comment for update");
		createAlertUpdate("extraData", alert.getExtraData(), "newExtraData",
				"comment for update");
		createAlertUpdate("specificReason", alert.getSpecificReason(),
				"newSpecificReason", "comment for update");

		final List<MAlertUpdate> alertUpdates = alertHistoryService
				.getAlertHistory(
						alert,
						CriterionQueryFactory.getQuery().between("dateTime",
								new Date(System.currentTimeMillis() - 10000),
								new Date()), Order.desc("dateTime"), 1, 2);

		assertEquals(2, alertUpdates.size());
	}

	@Test
	public void testGetAlertHistoryUsingPages() {
		final MAlertUpdate alertUpdate = new MAlertUpdate(alert,
				UpdateType.UPDATE, new Date(), "test", "context",
				alert.getContext(), "newAlertContext", "comment for update");
		modelSpace.save(alertUpdate);
	}

    @Test
    public void testGetCommentsForReports() {
        final Date reportStart = new Date();
        final Date reportEnd = new Date(reportStart.getTime() + 10000);
        final MAlertReport report = new MAlertReport();
        report.setAlert(alert);
        report.setStartDateTime(reportStart);
        report.setEndDateTime(reportEnd);
        report.setPerceivedSeverity(PerceivedSeverity.CRITICAL);
        modelSpace.save(report);


        final String comment = "is related to report";
        createAlertUpdate("context", alert.getContext(), "newContext",
                comment, new Date(reportStart.getTime() + 1000));
        createAlertUpdate("specificReason", alert.getSpecificReason(),
                "newSpecificReason", "", new Date(reportStart.getTime() + 2000));
        createAlertUpdate("extraData", alert.getExtraData(), "newExtraData",
                "is not related to report", new Date(reportStart.getTime() + 20000));

        final List<MAlertReport> reports = new ArrayList<>();
        reports.add(report);

        final Map<Long, List<MAlertUpdate>> comments = alertHistoryService.getAlertReportComments(reports, reportStart, reportEnd);

        final List<MAlertUpdate> reportComments = comments.get(report.getId());
        assertNotNull(reportComments);
        assertEquals(1, reportComments.size());
        assertEquals(comment, reportComments.get(0).getComment());
    }

}

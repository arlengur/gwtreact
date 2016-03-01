/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MAlertType.*;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.AlertService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class AlertReportServiceTest {
	@Autowired
	private AlertReportService alertReportService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private ModelSpace modelSpace;

	private MAlert alert;

	private MAgent agent;

	private MAgentTask task;

	private MPolicy policy;

	private MAlertIndication indication;

	private void createOpenedAlertReport() {
		final MAlertReport alertReport = new MAlertReport();
		alertReport.setAlert(alert);
		alertReport.setEndDateTime(null);
		alertReport.setStartDateTime(new Date());
		alertReport.setPerceivedSeverity(PerceivedSeverity.CRITICAL);
		modelSpace.save(alertReport);
	}

	@Before
	public void setUp() {
		agent = SharedModelConfiguration
				.createLightWeightAgent("resultTestAgentName");
		modelSpace.save(agent);
		final MAgentModule module = SharedModelConfiguration
				.createAgentModule(agent);
		modelSpace.save(module);
		task = SharedModelConfiguration.createAgentTask(module);
		modelSpace.save(task);

		policy = SharedModelConfiguration.createPolicy(task, "ACTIVATE");
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
		alert.setSource(task);
		alert.setOriginator(policy);
		alert.setAcknowledged(false);
		alert.setStatus(Status.ACTIVE);
		alert.setLastUpdateType(UpdateType.NEW);
		alert.setAlertCount(0L);
		alert.setCountSinceLastAck(0L);
		modelSpace.save(alert);

		indication = SharedModelConfiguration.createIndication(
				alertType.getName(), Source.getTaskSource(task.getKey()),
				Source.getTaskSource(alert.getOriginator().getKey()),
				alert.getSettings());
	}

	private void testClearedAlertActivationWithSeverityChange(
			final PerceivedSeverity firstIndicationSeverity,
			final PerceivedSeverity secondIndicationSeverity) {
		long indicationTimestamp = System.currentTimeMillis();
		final long indicationTimestampStep = 2000;

		// open new report by alert activation
		indication.setPerceivedSeverity(firstIndicationSeverity);
		indication.setIndicationType(UpdateType.NEW);
		indication.setDateTime(new Date(indicationTimestamp));
		alertService.activateAlert(indication, "test");

		// close report by alert clearing
		indication.setIndicationType(UpdateType.AUTO_CLEARED);
		indicationTimestamp += indicationTimestampStep;
		indication.setDateTime(new Date(indicationTimestamp));
		alertService.clearAlert(indication, null, "test");

		// open new report with other severity by alert activation
		indication.setIndicationType(UpdateType.NEW);
		indication.setPerceivedSeverity(secondIndicationSeverity);
		indicationTimestamp += indicationTimestampStep;
		indication.setDateTime(new Date(indicationTimestamp));
		alertService.activateAlert(indication, "test");

		// close report by alert clearing
		indication.setIndicationType(UpdateType.AUTO_CLEARED);
		indicationTimestamp += indicationTimestampStep;
		indication.setDateTime(new Date(indicationTimestamp));
		alertService.clearAlert(indication, null, "test");

		// only two reports must have been created
		final List<MAlertReport> alertReports = modelSpace
				.getAll(MAlertReport.class);
		Assert.assertEquals(2, alertReports.size());
	}

	@Test
	public void testCloseAlertReport() {
		final Date startDateTime = new Date();
		alertReportService.openAlertReport(alert, startDateTime);

		final Date endDateTime = new Date(startDateTime.getTime()
				+ TimeConstants.MILLISECONDS_PER_MINUTE);
		alertReportService.closeAlertReport(alert, endDateTime);
		final List<MAlertReport> alertReports = modelSpace
				.getAll(MAlertReport.class);
		Assert.assertEquals(1, alertReports.size());
		final MAlertReport alertReport = alertReports.iterator().next();
		Assert.assertNotNull(alertReport.getEndDateTime());
		Assert.assertEquals(startDateTime, alertReport.getStartDateTime());
		Assert.assertEquals(endDateTime, alertReport.getEndDateTime());
	}

	@Test(expected = ServiceException.class)
	public void testCloseAlertReportWhenMoreThanOneOpenedAlertReportAlreadyExistsInDatabase() {
		// add first opened alert report in database
		createOpenedAlertReport();
		// add second opened alert report in database
		createOpenedAlertReport();

		// open alert report once again
		alertReportService.closeAlertReport(alert, new Date());
	}

	@Test
	public void testCloseAlreadyClosedAlertReport() {
		// open alert report
		final Date startDateTime = new Date();
		alertReportService.openAlertReport(alert, startDateTime);

		// close alert report
		final Date endDateTime = new Date(startDateTime.getTime()
				+ TimeConstants.MILLISECONDS_PER_SECOND);
		alertReportService.closeAlertReport(alert, endDateTime);

		// close alert report once again
		alertReportService.closeAlertReport(alert, endDateTime);

		final List<MAlertReport> alertReports = modelSpace
				.getAll(MAlertReport.class);
		Assert.assertEquals(1, alertReports.size());
		final MAlertReport alertReport = alertReports.iterator().next();
		Assert.assertEquals(endDateTime, alertReport.getEndDateTime());
		Assert.assertEquals(startDateTime, alertReport.getStartDateTime());
	}

	@Test
	public void testCriticalClearedAlertActivationWithSeverityChange() {
		testClearedAlertActivationWithSeverityChange(
				PerceivedSeverity.CRITICAL, PerceivedSeverity.WARNING);
	}

	@Test
	public void testGetAlertReports() {
		final Date startDateTime = new Date();
		alertReportService.openAlertReport(alert, startDateTime);
		alertReportService.closeAlertReport(alert,
				new Date(startDateTime.getTime()
						+ TimeConstants.MILLISECONDS_PER_SECOND));
		alertReportService.openAlertReport(alert,
				new Date(startDateTime.getTime() + 2
						* TimeConstants.MILLISECONDS_PER_SECOND));

		final Set<String> sourceKeys = new HashSet<>();
		sourceKeys.add(agent.getKey());
		sourceKeys.add(task.getKey());
		final List<MAlertReport> alertReports = alertReportService
				.getAlertReports(sourceKeys, TimeInterval.get(startDateTime,
						new Date(startDateTime.getTime() + 3
								* TimeConstants.MILLISECONDS_PER_SECOND)),
						null, Order.asc("startDateTime"), null, null);

		Assert.assertEquals(2, alertReports.size());
	}

	@Test
	public void testOpenAlertReport() {
		final Date startDateTime = new Date();
		alertReportService.openAlertReport(alert, startDateTime);

		final List<MAlertReport> alertReports = modelSpace
				.getAll(MAlertReport.class);
		Assert.assertEquals(1, alertReports.size());
		final MAlertReport alertReport = alertReports.iterator().next();
		Assert.assertNull(alertReport.getEndDateTime());
		Assert.assertEquals(startDateTime, alertReport.getStartDateTime());
	}

	@Test(expected = ServiceException.class)
	public void testOpenAlertReportWhenMoreThanOneOpenedAlertReportAlreadyExistsInDatabase() {
		// add first opened alert report in database
		createOpenedAlertReport();
		// add second opened alert report in database
		createOpenedAlertReport();

		// open alert report once again
		alertReportService.openAlertReport(alert, new Date());
	}

	@Test
	public void testOpenAlreadyOpenedAlertReportWithDifferentSeverity() {
		// open alert report
		final Date startDateTime = new Date();
		alertReportService.openAlertReport(alert, startDateTime);

		// open alert report once again
		alert.setPerceivedSeverity(PerceivedSeverity.MAJOR);
		alertReportService.openAlertReport(alert, startDateTime);

		final List<MAlertReport> alertReports = modelSpace
				.getAll(MAlertReport.class);
		Assert.assertEquals(1, alertReports.size());
		final MAlertReport alertReport = alertReports.iterator().next();
		Assert.assertNull(alertReport.getEndDateTime());
		Assert.assertEquals(startDateTime, alertReport.getStartDateTime());
	}

	@Test
	public void testOpenAlreadyOpenedAlertReportWithTheSameSeverity() {
		// open alert report
		final Date startDateTime = new Date();
		alertReportService.openAlertReport(alert, startDateTime);

		// open alert report once again
		alertReportService.openAlertReport(alert, startDateTime);

		final List<MAlertReport> alertReports = modelSpace
				.getAll(MAlertReport.class);
		Assert.assertEquals(1, alertReports.size());
		final MAlertReport alertReport = alertReports.iterator().next();
		Assert.assertNull(alertReport.getEndDateTime());
		Assert.assertEquals(startDateTime, alertReport.getStartDateTime());
	}

	@Test
	public void testWarningClearedAlertActivationWithSeverityChange() {
		testClearedAlertActivationWithSeverityChange(PerceivedSeverity.WARNING,
				PerceivedSeverity.CRITICAL);
	}
}

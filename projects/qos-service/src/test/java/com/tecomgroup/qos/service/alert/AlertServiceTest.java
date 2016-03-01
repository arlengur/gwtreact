/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.exception.AlertException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.EventBroadcastDispatcher;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.InternalSourceService;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.*;

/**
 * @author pkunilov
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"
})
@Transactional
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
public class AlertServiceTest {

	private PropagationAlertService alertService;

	private AlertHistoryService alertHistoryService;

	private InternalSourceService sourceService;

	private EventBroadcastDispatcher eventBroadcastDispatcher;

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TransactionTemplate readOnlyTransactionTemplate;

	@Autowired
	AuthorizeService authorizeService;

	private MAlertIndication indicationWithSource1;
	private MAlertIndication indicationWithSource2;
	private MAlertIndication indicationWithSource3;

	private MAlertIndication indicationWithTask1;
	private MAlertIndication indicationWithTask2;
	private MAlertIndication indicationWithTask3;

	private MAlertIndication indicationWithDetectionValue1;
	private MAlertIndication indicationWithDetectionValue2;

	private final String alertTypeName = SharedModelConfiguration.IT09A_ALERT_TYPE_NAME;

	private Source originator;
	private Source otherOriginator;

	private MAgent agent;
	private MAgentModule module;
	private MAgentTask task;

	private MPolicy policy;

	private MPolicy otherPolicy;

	private Long count;

	private Long countSinceLastAck;

	private void pause() {
		try {
			Thread.sleep(100);
		} catch (final InterruptedException e) {
			// ignore
		}
	}

	@Before
	public void setUp() {
		alertHistoryService = EasyMock
				.createStrictMock(AlertHistoryService.class);
		eventBroadcastDispatcher = EasyMock
				.createStrictMock(EventBroadcastDispatcher.class);
		sourceService = EasyMock.createMock(InternalSourceService.class);
		alertService = new PropagationAlertService();
		alertService.setAlertHistoryService(alertHistoryService);
		alertService.setSourceService(sourceService);
		alertService.setModelSpace(modelSpace);
		alertService.setTransactionTemplate(transactionTemplate);
		alertService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		alertService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		alertService.setAlertTypesFile(new File(
				"../qos-gwt-media/config/alertTypes.config"));
		alertService.setPropagationEnabled(false);
		alertService.setPostProcessAlerts(false);


		alertService.setAuthorizeService(authorizeService);
		alertService.init();

		agent = SharedModelConfiguration.createLightWeightAgent("testAgent");
		modelSpace.save(agent);
		module = SharedModelConfiguration.createAgentModule(agent);
		modelSpace.save(module);
		task = SharedModelConfiguration.createAgentTask(module, null);
		modelSpace.save(task);
		policy = SharedModelConfiguration.createPolicy(
				"key" + UUID.randomUUID(), task, "policyActionName",
				new ParameterIdentifier(SharedModelConfiguration.SIGNAL_LEVEL,
						null));
		originator = Source.getSource(policy);
		modelSpace.save(policy);
		otherPolicy = SharedModelConfiguration.createPolicy(
				"key" + UUID.randomUUID(), task, "otherPolicyActionName",
				new ParameterIdentifier(SharedModelConfiguration.SIGNAL_LEVEL,
						null));
		modelSpace.save(otherPolicy);
		otherOriginator = Source.getSource(otherPolicy);

		indicationWithSource1 = SharedModelConfiguration.createIndication(
				alertTypeName, Source.getTaskSource(task.getKey()), originator,
				"indicationSourceSettings1");
		indicationWithSource2 = SharedModelConfiguration.createIndication(
				alertTypeName, Source.getTaskSource(task.getKey()), originator,
				"indicationSourceSettings2");
		indicationWithSource3 = SharedModelConfiguration.createIndication(
				alertTypeName, Source.getTaskSource(task.getKey()), originator,
				"indicationSourceSettings3");

		indicationWithTask1 = SharedModelConfiguration.createIndication(
				alertTypeName, Source.getTaskSource(task.getKey()), originator,
				"indicationSettings1");
		indicationWithTask2 = SharedModelConfiguration.createIndication(
				alertTypeName, Source.getTaskSource(task.getKey()), originator,
				"indicationSettings2");
		indicationWithTask3 = SharedModelConfiguration.createIndication(
				alertTypeName, Source.getTaskSource(task.getKey()), originator,
				"indicationSettings3");

		indicationWithDetectionValue1 = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						"indicationDetectionValueSettings");
		indicationWithDetectionValue1
				.setDetectionValue(SharedModelConfiguration.ALERT_DETECTION_VALUE);

		indicationWithDetectionValue2 = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						"indicationDetectionValueSettings");
		indicationWithDetectionValue2
				.setDetectionValue(SharedModelConfiguration.ALERT_DETECTION_VALUE);

		count = 1L;
		countSinceLastAck = 0L;
	}

	@SuppressWarnings("unchecked")
	private void testAcknowledgeAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
				EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(eventBroadcastDispatcher);

		MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(Boolean.FALSE, alert.isAcknowledged());
		Assert.assertNull(alert.getAcknowledgmentDateTime());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertEquals(count, alert.getAlertCount());
		Assert.assertEquals(countSinceLastAck, alert.getCountSinceLastAck());

		alert = alertService.acknowledgeAlert(indication, null, "test");
		Assert.assertEquals(Boolean.TRUE, alert.isAcknowledged());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertNotNull(alert.getAcknowledgmentDateTime());
		Assert.assertEquals(countSinceLastAck, alert.getCountSinceLastAck());
		Assert.assertEquals(count, alert.getAlertCount());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testAcknowledgeAlertBySource() {
		testAcknowledgeAlert(indicationWithSource1);
	}

	@Test
	public void testAcknowledgeAlertByTask() {
		testAcknowledgeAlert(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testAcknowledgeUnrealAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(eventBroadcastDispatcher);

		alertService.acknowledgeAlert(indication, null, "test");

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test(expected = AlertException.class)
	public void testAcknowledgeUnrealAlertBySource() {
		testAcknowledgeUnrealAlert(indicationWithSource1);
	}

	@Test(expected = AlertException.class)
	public void testAcknowledgeUnrealAlertByTask() {
		testAcknowledgeUnrealAlert(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testActivateAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(indication.getSettings(), alert.getSettings());
		Assert.assertEquals(indication.getContext(), alert.getContext());
		Assert.assertEquals(indication.getExtraData(), alert.getExtraData());
		Assert.assertEquals(indication.getSource(),
				Source.getSource(alert.getSource()));
		Assert.assertEquals(alertTypeName, alert.getAlertType().getName());
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());
		Assert.assertEquals(indication.getPerceivedSeverity(),
				alert.getPerceivedSeverity());
		Assert.assertEquals(indication.getSpecificReason(),
				alert.getSpecificReason());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertEquals(count, alert.getAlertCount());
		Assert.assertEquals(countSinceLastAck, alert.getCountSinceLastAck());
		Assert.assertEquals(Boolean.FALSE, alert.isAcknowledged());
		Assert.assertNull(alert.getAcknowledgmentDateTime());
		Assert.assertNull(alert.getClearedDateTime());
		Assert.assertNotNull(alert.getCreationDateTime());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testActivateAlertBySource() {
		testActivateAlert(indicationWithSource1);
	}

	@Test
	public void testActivateAlertByTask() {
		testActivateAlert(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertsWithDifferentOriginators() {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(otherPolicy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		MAlert alert = alertService.activateAlert(indicationWithTask1, "test");
		indicationWithTask1.setOriginator(otherOriginator);
		alert = alertService.activateAlert(indicationWithTask1, "test");

		final List<MAlert> alerts = alertService.getAlertsByOriginator(
				originator, null, null, null);
		Assert.assertEquals(1, alerts.size());
		final List<MAlert> otherAlerts = alertService.getAlertsByOriginator(
				otherOriginator, null, null, null);
		Assert.assertEquals(1, otherAlerts.size());
		Assert.assertEquals(otherOriginator,
				Source.getSource(alert.getOriginator()));

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertWithDetectionValue() {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(
				indicationWithDetectionValue1, "test");

		Assert.assertNotNull(alert.getDetectionValue());
		Assert.assertEquals(indicationWithDetectionValue1.getDetectionValue(),
				alert.getDetectionValue(), 0.00001);

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertWithoutDetectionValue() {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));;
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indicationWithTask1,
				"test");

		Assert.assertNull(alert.getDetectionValue());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertWithTheSameDetectionValue() {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		MAlert alert = alertService.activateAlert(
				indicationWithDetectionValue1, "test");
		alert = alertService.activateAlert(indicationWithDetectionValue2,
				"test");

		Assert.assertEquals(UpdateType.REPEAT, alert.getLastUpdateType());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	private void testClearAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(Status.ACTIVE, alert.getStatus());
		Assert.assertNull(alert.getClearedDateTime());
		Assert.assertEquals(count, alert.getAlertCount());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());

		final MAlert clearedAlert = alertService.clearAlert(indication, null,
				"test");

		Assert.assertEquals(Status.CLEARED, clearedAlert.getStatus());
		Assert.assertNotNull(clearedAlert.getClearedDateTime());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertEquals(count, clearedAlert.getAlertCount());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testClearAlertBySource() {
		testClearAlert(indicationWithSource1);
	}

	@Test
	public void testClearAlertByTask() {
		testClearAlert(indicationWithSource1);
	}

	@Test
	public void testDurationCalculation() {

		final Date criticalIndicationDate = new Date(new Date().getTime()
				- TimeConstants.MILLISECONDS_PER_HOUR * 3);
		final Date clearIndicationDate = new Date(new Date().getTime()
				- TimeConstants.MILLISECONDS_PER_HOUR);

		final MAlertIndication criticalIndication = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings1");

		final MAlertIndication clearIndication = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings1");

		criticalIndication.setDateTime(criticalIndicationDate);
		clearIndication.setDateTime(clearIndicationDate);

		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		final MAlert alert = alertService.activateAlert(criticalIndication,
				"test");
		Assert.assertEquals(Status.ACTIVE, alert.getStatus());
		Assert.assertEquals(alert.getCreationDateTime(),
				alert.getSeverityChangeDateTime());

		alertService.clearAlert(clearIndication, null, "test");

		Assert.assertEquals(Status.CLEARED, alert.getStatus());
		Assert.assertEquals(clearIndicationDate.getTime()
				- criticalIndicationDate.getTime(), alert.getDuration()
				.longValue());

		alertService.activateAlert(criticalIndication, "test");

		final MAlertIndication warningIndication = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						PerceivedSeverity.WARNING,
						"indicationCriticalSettings1");

		final Date severityChangeDate = new Date(new Date().getTime()
				- TimeConstants.MILLISECONDS_PER_HOUR * 2);
		warningIndication.setDateTime(severityChangeDate);

		alertService.activateAlert(warningIndication, "test");

		alertService.clearAlert(clearIndication, null, "test");
		Assert.assertEquals(Status.CLEARED, alert.getStatus());
		Assert.assertEquals(
				clearIndicationDate.getTime() - severityChangeDate.getTime(),
				alert.getDuration().longValue());
	}

	@SuppressWarnings("unchecked")
	private void testGetAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");

		final MAlert gettedAlert = alertService.getAlert(alertTypeName,
				indication.getSource(), originator, indication.getSettings());
		Assert.assertEquals(alert, gettedAlert);

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testGetAlertBySource() {
		testGetAlert(indicationWithSource1);
	}

	@Test
	public void testGetAlertByTask() {
		testGetAlert(indicationWithTask1);
	}

	@Test
	public void testGetAlertsByOriginator() {
		testGetAlertsByOriginatorAndOriginatorType(indicationWithTask1,
				originator);
	}

	@SuppressWarnings("unchecked")
	private void testGetAlertsByOriginatorAndOriginatorType(
			final MAlertIndication indication, final Source originator) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");

		final List<MAlert> alerts = alertService.getAlertsByOriginator(
				originator, null, null, null);
		Assert.assertFalse(alerts.isEmpty());
		Assert.assertEquals(alert, alerts.iterator().next());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testGetAlertsBySource() {
		testGetAlertsBySourceAndSourceType(indicationWithTask1,
				indicationWithTask1.getSource());
	}

	@SuppressWarnings("unchecked")
	private void testGetAlertsBySourceAndSourceType(
			final MAlertIndication indication, final Source source) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");

		final List<MAlert> alerts = alertService.getAlertsBySource(Source.getSource(task.getParent().getParent()),
				null, null, null, false, null);
		Assert.assertFalse(alerts.isEmpty());
		Assert.assertEquals(alert, alerts.iterator().next());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testGetAlertsSummaryDurationByAgentKey() {

		final MAgent testAgent1 = SharedModelConfiguration
				.createLightWeightAgent("221");
		final MAgent testAgent2 = SharedModelConfiguration
				.createLightWeightAgent("221-1");
		final MAgent testAgent3 = SharedModelConfiguration
				.createLightWeightAgent("221-2");
		modelSpace.save(testAgent1);
		modelSpace.save(testAgent2);
		modelSpace.save(testAgent3);
		final MTestAgentModule testModule1 = SharedModelConfiguration
				.createAgentModule(testAgent1);
		testModule1.setKey(testModule1.getParent().getKey() + "."
				+ testModule1.getKey());
		final MTestAgentModule testModule2 = SharedModelConfiguration
				.createAgentModule(testAgent2);
		testModule2.setKey(testModule2.getParent().getKey() + "."
				+ testModule2.getKey());
		final MTestAgentModule testModule3 = SharedModelConfiguration
				.createAgentModule(testAgent3);
		testModule3.setKey(testModule3.getParent().getKey() + "."
				+ testModule3.getKey());
		modelSpace.save(testModule1);
		modelSpace.save(testModule2);
		modelSpace.save(testModule3);
		final MAgentTask complexTask1 = SharedModelConfiguration
				.createAgentTask(testModule1);
		final MAgentTask complexTask2 = SharedModelConfiguration
				.createAgentTask(testModule2);
		final MAgentTask complexTask3 = SharedModelConfiguration
				.createAgentTask(testModule3);
		modelSpace.save(complexTask1);
		modelSpace.save(complexTask2);
		modelSpace.save(complexTask3);

		final MAlertIndication indicationWithTaskCritical1 = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getSource(complexTask1), originator,
						PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings1");
		indicationWithTaskCritical1.setDateTime(new Date(new Date().getTime()
				- TimeConstants.MILLISECONDS_PER_HOUR));
		final MAlertIndication indicationWithTaskCritical2 = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getSource(complexTask2), originator,
						PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings2");
		indicationWithTaskCritical2.setDateTime(new Date(new Date().getTime()
				- 4 * TimeConstants.MILLISECONDS_PER_HOUR));
		final MAlertIndication indicationWithTaskCritical3 = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getSource(complexTask3), originator,
						PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings3");
		indicationWithTaskCritical3.setDateTime(new Date(new Date().getTime()
				- 2 * TimeConstants.MILLISECONDS_PER_HOUR));





		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(complexTask1);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(complexTask2);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(complexTask3);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertService.activateAlert(indicationWithTaskCritical1, "test");
		alertService.activateAlert(indicationWithTaskCritical2, "test");
		alertService.activateAlert(indicationWithTaskCritical3, "test");

		final Set<PerceivedSeverity> severities = new HashSet<PerceivedSeverity>(
				Arrays.<PerceivedSeverity> asList(PerceivedSeverity.CRITICAL));

		final Map<String, Long> result = alertService
				.getAlertsSummaryDurationByAgentKey(severities, Type.WEEK);
		Assert.assertEquals(3, result.size());
		Assert.assertNotNull(result.get(testAgent1.getDisplayName()));
		Assert.assertNotNull(result.get(testAgent2.getDisplayName()));
		Assert.assertNotNull(result.get(testAgent3.getDisplayName()));
	}

	@SuppressWarnings("unchecked")
	private void testGetAllAlertsByProperties(
			final MAlertIndication indication1,
			final MAlertIndication indication2,
			final MAlertIndication indication3,
			final Source.Type indicationSourceType) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(7);
		EasyMock.replay(eventBroadcastDispatcher);

		alertService.activateAlert(indication1, "test");
		alertService.activateAlert(indication2, "test");
		alertService.activateAlert(indication3, "test");

		alertService.clearAlert(indication1, null, "test");
		List<MAlert> alerts = alertService.getAlerts(null,
				Order.desc("creationDateTime"), null, null);
		Assert.assertEquals(3, alerts.size());
		final CriterionQuery query = CriterionQueryFactory.getQuery();
		alerts = alertService.getAlerts(
				query.not(query.eq("status", Status.CLEARED)),
				Order.desc("creationDateTime"), null, null);
		Assert.assertEquals(2, alerts.size());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testGetAllAlertsByPropertiesBySource() {
		testGetAllAlertsByProperties(indicationWithSource1,
				indicationWithSource2, indicationWithSource3,
				Source.Type.SERVER);
	}

	@Test
	public void testGetAllAlertsByPropertiesByTask() {
		testGetAllAlertsByProperties(indicationWithTask1, indicationWithTask2,
				indicationWithTask3, Source.Type.TASK);
	}

	private void testGetAllAlertsUsingPages(final MAlertIndication indication1,
			final MAlertIndication indication2,
			final MAlertIndication indication3) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertService.activateAlert(indication1, "test");
		alertService.activateAlert(indication2, "test");
		alertService.activateAlert(indication3, "test");

		final List<MAlert> alerts = alertService.getAlerts(null,
				Order.desc("creationDateTime"), 1, 2);
		Assert.assertEquals(2, alerts.size());

		EasyMock.verify(sourceService);
	}

	@Test
	public void testGetAllAlertsUsingPagesBySource() {
		testGetAllAlertsUsingPages(indicationWithSource1,
				indicationWithSource2, indicationWithSource3);
	}

	@Test
	public void testGetAllAlertsUsingPagesByTask() {
		testGetAllAlertsByProperties(indicationWithTask1, indicationWithTask2,
				indicationWithTask3, Source.Type.TASK);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testResetAlerts() {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indicationWithTask1,
				"test");

		List<MAlert> alerts = alertService.getAlertsByOriginator(
				indicationWithTask1.getOriginator(), null, null, null);
		Assert.assertFalse(alerts.isEmpty());
		Assert.assertEquals(alert, alerts.iterator().next());

		alertService.disable(alert);
		alerts = alertService.getAlertsByOriginator(
				indicationWithTask1.getOriginator(), null, null, null);
		Assert.assertTrue(alerts.isEmpty());

		alertService.resetDisabledAlerts(originator);
		alerts = alertService.getAlertsByOriginator(
				indicationWithTask1.getOriginator(), null, null, null);
		Assert.assertFalse(alerts.isEmpty());
		Assert.assertEquals(alert, alerts.iterator().next());
		Assert.assertNotNull(alert.getDuration());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	private void testUnAcknowledgeAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(Boolean.FALSE, alert.isAcknowledged());
		Assert.assertNull(alert.getAcknowledgmentDateTime());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertEquals(countSinceLastAck, alert.getCountSinceLastAck());
		Assert.assertEquals(count, alert.getAlertCount());

		alert = alertService.acknowledgeAlert(indication, null, "test");
		Assert.assertEquals(Boolean.TRUE, alert.isAcknowledged());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertNotNull(alert.getAcknowledgmentDateTime());
		Assert.assertEquals(countSinceLastAck, alert.getCountSinceLastAck());
		Assert.assertEquals(count, alert.getAlertCount());

		alert = alertService.unAcknowledgeAlert(indication, null, "test");
		Assert.assertEquals(Boolean.FALSE, alert.isAcknowledged());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertNotNull(alert.getAcknowledgmentDateTime());
		Assert.assertEquals(countSinceLastAck, alert.getCountSinceLastAck());
		Assert.assertEquals(count, alert.getAlertCount());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUnAcknowledgeAlertBySource() {
		testUnAcknowledgeAlert(indicationWithSource1);
	}

	@Test
	public void testUnAcknowledgeAlertByTask() {
		testUnAcknowledgeAlert(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUnAcknowledgeUnrealAlert(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(1);
		EasyMock.replay(eventBroadcastDispatcher);

		alertService.unAcknowledgeAlert(indication, null, "test");

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test(expected = AlertException.class)
	public void testUnAcknowledgeUnrealAlertBySource() {
		testUnAcknowledgeUnrealAlert(indicationWithSource1);
	}

	@Test(expected = AlertException.class)
	public void testUnAcknowledgeUnrealAlertByTask() {
		testUnAcknowledgeUnrealAlert(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdateAlertWithTheSameProperties(
			final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		MAlert alert = alertService.activateAlert(indication, "test");
		final Date dateCreation = alert.getCreationDateTime();
		final Long alertId = alert.getId();
		pause();
		alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(alertId, alert.getId());
		Assert.assertEquals(dateCreation, alert.getCreationDateTime());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastUpdateDateTime());
		Assert.assertEquals(UpdateType.REPEAT, alert.getLastUpdateType());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdateAlertWithTheSamePropertiesByTask() {
		testUpdateAlertWithTheSameProperties(indicationWithTask1);
	}

	@Test
	public void testUpdateAlertWithTheSamePropertiesSource() {
		testUpdateAlertWithTheSameProperties(indicationWithSource1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdateContext(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());

		final String newContext = "newContext";
		indication.setContext(newContext);
		final MAlert updatedAlert = alertService.activateAlert(indication,
				"test");
		Assert.assertEquals(newContext, updatedAlert.getContext());
		Assert.assertEquals(UpdateType.UPDATE, updatedAlert.getLastUpdateType());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdateContextBySource() {
		testUpdateContext(indicationWithSource1);
	}

	@Test
	public void testUpdateContextByTask() {
		testUpdateContext(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdateExtraData(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());

		final String newExtraData = "newExtraData";
		indication.setExtraData(newExtraData);
		final MAlert updatedAlert = alertService.activateAlert(indication,
				"test");
		Assert.assertEquals(newExtraData, updatedAlert.getExtraData());
		Assert.assertEquals(UpdateType.UPDATE, updatedAlert.getLastUpdateType());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdateExtraDataBySource() {
		testUpdateExtraData(indicationWithSource1);
	}

	@Test
	public void testUpdateExtraDataByTask() {
		testUpdateExtraData(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdatePerceivedSeverity(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());

		indication.setPerceivedSeverity(PerceivedSeverity.NOTICE);
		final MAlert updatedAlert = alertService.activateAlert(indication,
				"test");
		Assert.assertEquals(PerceivedSeverity.NOTICE,
				updatedAlert.getPerceivedSeverity());
		Assert.assertEquals(UpdateType.SEVERITY_DEGRADATION,
				updatedAlert.getLastUpdateType());
		Assert.assertEquals(Boolean.FALSE, updatedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck,
				updatedAlert.getCountSinceLastAck());
		Assert.assertNull(updatedAlert.getAcknowledgmentDateTime());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdatePerceivedSeverityBySource() {
		testUpdatePerceivedSeverity(indicationWithSource1);
	}

	@Test
	public void testUpdatePerceivedSeverityByTask() {
		testUpdatePerceivedSeverity(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdatePerceivedSeverityWithAckChange(
			final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(5);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());
		Assert.assertEquals(count, alert.getAlertCount());

		final MAlert ackedAlert = alertService.acknowledgeAlert(indication,
				null, "test");
		Assert.assertEquals(UpdateType.ACK, ackedAlert.getLastUpdateType());
		Assert.assertEquals(Boolean.TRUE, ackedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck,
				ackedAlert.getCountSinceLastAck());
		Assert.assertEquals(count, ackedAlert.getAlertCount());

		indication.setPerceivedSeverity(PerceivedSeverity.MAJOR);
		final MAlert updatedAlert = alertService.activateAlert(indication,
				"test");
		Assert.assertEquals(PerceivedSeverity.MAJOR,
				updatedAlert.getPerceivedSeverity());
		Assert.assertEquals(UpdateType.SEVERITY_UPGRADE,
				updatedAlert.getLastUpdateType());
		Assert.assertEquals(Boolean.FALSE, updatedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck,
				updatedAlert.getCountSinceLastAck());
		Assert.assertEquals(++count, updatedAlert.getAlertCount());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdatePerceivedSeverityWithAckChangeBySource() {
		testUpdatePerceivedSeverityWithAckChange(indicationWithSource1);
	}

	@Test
	public void testUpdatePerceivedSeverityWithAckChangeByTask() {
		testUpdatePerceivedSeverityWithAckChange(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdatePerceivedSeverityWithoutAckChange(
			final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(5);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());
		Assert.assertEquals(count, alert.getAlertCount());

		final MAlert ackedAlert = alertService.acknowledgeAlert(indication,
				null, "test");
		Assert.assertEquals(UpdateType.ACK, ackedAlert.getLastUpdateType());
		Assert.assertEquals(Boolean.TRUE, ackedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck,
				ackedAlert.getCountSinceLastAck());
		Assert.assertEquals(count, ackedAlert.getAlertCount());

		indication.setPerceivedSeverity(PerceivedSeverity.NOTICE);
		final MAlert updatedAlert = alertService.activateAlert(indication,
				"test");
		Assert.assertEquals(PerceivedSeverity.NOTICE,
				updatedAlert.getPerceivedSeverity());
		Assert.assertEquals(UpdateType.SEVERITY_DEGRADATION,
				updatedAlert.getLastUpdateType());
		Assert.assertEquals(Boolean.TRUE, updatedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck,
				updatedAlert.getCountSinceLastAck());
		Assert.assertEquals(count, updatedAlert.getAlertCount());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdatePerceivedSeverityWithoutAckChangeBySource() {
		testUpdatePerceivedSeverityWithoutAckChange(indicationWithSource1);
	}

	@Test
	public void testUpdatePerceivedSeverityWithoutAckChangeByTask() {
		testUpdatePerceivedSeverityWithoutAckChange(indicationWithTask1);
	}

	@SuppressWarnings("unchecked")
	private void testUpdateSpecificReason(final MAlertIndication indication) {
		sourceService.getDomainSource(EasyMock.isA(MAgentTask.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(task);
		sourceService.getDomainSource(EasyMock.isA(MPolicy.class.getClass()), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().andReturn(policy);
		EasyMock.replay(sourceService);

		alertHistoryService.addAlertUpdate(EasyMock.anyObject(MAlert.class),
				EasyMock.anyObject(UpdateType.class),
				EasyMock.anyObject(Date.class),
				EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class), EasyMock.anyObject(),
				EasyMock.anyObject(), EasyMock.anyObject(String.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.replay(alertHistoryService);

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		final MAlert alert = alertService.activateAlert(indication, "test");
		Assert.assertEquals(UpdateType.NEW, alert.getLastUpdateType());

		indication.setSpecificReason(SpecificReason.UNKNOWN);
		final MAlert updatedAlert = alertService.activateAlert(indication,
				"test");
		Assert.assertEquals(SpecificReason.UNKNOWN,
				updatedAlert.getSpecificReason());
		Assert.assertEquals(UpdateType.UPDATE, updatedAlert.getLastUpdateType());

		EasyMock.verify(sourceService);
		EasyMock.verify(alertHistoryService);
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@Test
	public void testUpdateSpecificReasonBySource() {
		testUpdateSpecificReason(indicationWithSource1);
	}

	@Test
	public void testUpdateSpecificReasonByTask() {
		testUpdateSpecificReason(indicationWithTask1);
	}
}

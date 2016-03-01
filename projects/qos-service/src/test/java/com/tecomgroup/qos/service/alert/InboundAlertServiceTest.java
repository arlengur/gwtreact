/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.EventBroadcastDispatcher;
import com.tecomgroup.qos.modelspace.jdbc.dao.JdbcAlertServiceDao;
import com.tecomgroup.qos.modelspace.jdbc.dao.recording.JdbcRecordingSchedulerServiceDao;
import com.tecomgroup.qos.modelspace.jdbc.dao.recording.RecordingSchedulerServiceDao;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.InternalEventBroadcaster;
import com.tecomgroup.qos.service.InternalSourceService;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author muvarov
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml",
		"classpath:/com/tecomgroup/qos/service/sharedServiceContext.xml"})
@Transactional
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
public class InboundAlertServiceTest {

	private InboundAlertServiceImpl alertService;
	private PropagationAlertService propagationAlertService;

	private AlertHistoryService alertHistoryService;

	private InternalSourceService sourceService;

	private EventBroadcastDispatcher eventBroadcastDispatcher;
	private InternalEventBroadcaster internalEventBroadcastDispatcher;

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private JdbcAlertServiceDao alertServiceDataProvider;

	@Autowired
	private RecordingSchedulerServiceDao recordingServiceDataProvider;

	@Autowired
	private AgentStatusMonitor statusMonitor;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TransactionTemplate readOnlyTransactionTemplate;

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
		internalEventBroadcastDispatcher = EasyMock
				.createStrictMock(InternalEventBroadcaster.class);

		sourceService = EasyMock.createMock(InternalSourceService.class);
		propagationAlertService = new PropagationAlertService();
		propagationAlertService.setAlertHistoryService(alertHistoryService);
		propagationAlertService.setSourceService(sourceService);
		propagationAlertService.setModelSpace(modelSpace);
		propagationAlertService.setTransactionTemplate(transactionTemplate);
		propagationAlertService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		propagationAlertService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		propagationAlertService.setInternalEventBroadcaster(internalEventBroadcastDispatcher);
		propagationAlertService.setAlertTypesFile(new File(
				"../qos-gwt-media/config/alertTypes.config"));
		propagationAlertService.setPropagationEnabled(false);
		propagationAlertService.setPostProcessAlerts(false);
		propagationAlertService.init();

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

		modelSpace.flush();
		count = 1L;
		countSinceLastAck = 0L;

		alertService = new InboundAlertServiceImpl();
		alertService.setAlertServiceDataProvider(alertServiceDataProvider);
		alertService.setRecordingServiceDataProvider(recordingServiceDataProvider);
		alertService.setModelSpace(modelSpace);
		alertService.setTransactionTemplate(transactionTemplate);
		alertService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		alertService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		alertService.setInternalEventBroadcaster(internalEventBroadcastDispatcher);
		alertService.setAlertTypesFile(new File(
				"../qos-gwt-media/config/alertTypes.config"));
		alertService.setStatusMonitor(statusMonitor);
		alertService.init();
	}

	@SuppressWarnings("unchecked")
	private void testActivateAlert(final MAlertIndication indication) {
		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(eventBroadcastDispatcher);
		List<MAgent> agents=modelSpace.getAll(MAgent.class);
		List<MAgentTask> tasks=modelSpace.getAll(MAgentTask.class);
		final AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(indication.getSettings(), alert.getSettings());
		Assert.assertEquals(indication.getContext(), alert.getContext());
		Assert.assertEquals(indication.getExtraData(), alert.getExtradata());
		Assert.assertEquals(indication.getSource().getKey(), alert.getSourceName());
		Assert.assertEquals(alertTypeName, alert.getAlertTypeName());
		Assert.assertEquals(UpdateType.NEW, alert.getLastupdatetype());
		Assert.assertEquals(indication.getPerceivedSeverity(),
				alert.getPerceivedseverity());
		Assert.assertEquals(indication.getSpecificReason(),
				alert.getSpecificreason());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastupdatedatetime());
		Assert.assertEquals(count.longValue(), alert.getAlert_count().longValue());
		Assert.assertEquals(countSinceLastAck.longValue(), alert.getCountsincelastack().longValue());
		Assert.assertEquals(Boolean.FALSE.booleanValue(), alert.isAcknowledged());
		Assert.assertNull(alert.getAcknowledgmentdatetime());
		Assert.assertNull(alert.getCleareddatetime());
		Assert.assertNotNull(alert.getCreationdatetime());
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

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);
		List<MAgent> agents=modelSpace.getAll(MAgent.class);
		List<MAgentTask> tasks=modelSpace.getAll(MAgentTask.class);
		AlertDTO alert = alertService.activateAlert(indicationWithTask1);
		indicationWithTask1.setOriginator(otherOriginator);
		alert = alertService.activateAlert(indicationWithTask1);

		final List<MAlert> alerts = propagationAlertService.getAlertsByOriginator(
				indicationWithTask1.getOriginator(), null, null, null);
		Assert.assertEquals(1, alerts.size());
		final List<MAlert> otherAlerts = propagationAlertService.getAlertsByOriginator(
				otherOriginator, null, null, null);
		Assert.assertEquals(1, otherAlerts.size());
		Assert.assertEquals(otherOriginator.getKey(),
				alert.getOriginatorName());

		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertWithDetectionValue() {
		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(
				indicationWithDetectionValue1);

		Assert.assertNotNull(alert.getDetectionvalue());
		Assert.assertEquals(indicationWithDetectionValue1.getDetectionValue(),
				alert.getDetectionvalue(), 0.00001);

		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertWithoutDetectionValue() {
		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(3);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(indicationWithTask1);

		Assert.assertNull(alert.getDetectionvalue());
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testActivateAlertWithTheSameDetectionValue() {
		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);

		AlertDTO alert = alertService.activateAlert(
				indicationWithDetectionValue1);
		alert = alertService.activateAlert(indicationWithDetectionValue2);

		Assert.assertEquals(UpdateType.REPEAT, alert.getLastupdatetype());
		EasyMock.verify(eventBroadcastDispatcher);
	}

	@SuppressWarnings("unchecked")
	private void testClearAlert(final MAlertIndication indication) {

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(4);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(MAlertType.Status.ACTIVE, alert.getStatus());
		Assert.assertNull(alert.getCleareddatetime());
		Assert.assertEquals(count.longValue(), alert.getAlert_count().longValue());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastupdatedatetime());

		final MAlert clearedAlert = propagationAlertService.clearAlert(indication, null,
				"test");

		Assert.assertEquals(MAlertType.Status.CLEARED, clearedAlert.getStatus());
		Assert.assertNotNull(clearedAlert.getClearedDateTime());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastupdatedatetime());
		Assert.assertEquals(count, clearedAlert.getAlertCount());

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
						MAlertType.PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings1");

		final MAlertIndication clearIndication = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						MAlertType.PerceivedSeverity.CRITICAL,
						"indicationCriticalSettings1");

		criticalIndication.setDateTime(criticalIndicationDate);
		clearIndication.setDateTime(clearIndicationDate);

		AlertDTO alert = alertService.activateAlert(criticalIndication);
		Assert.assertEquals(MAlertType.Status.ACTIVE, alert.getStatus());
		Assert.assertEquals(alert.getCreationdatetime(),
				alert.getSeveritychangedatetime());

		alert = alertService.clearAlert(clearIndication);

		Assert.assertEquals(MAlertType.Status.CLEARED, alert.getStatus());
		Assert.assertEquals(clearIndicationDate.getTime()
				- criticalIndicationDate.getTime(), alert.getDuration()
				.longValue());

		alert = alertService.activateAlert(criticalIndication);

		final MAlertIndication warningIndication = SharedModelConfiguration
				.createIndication(alertTypeName,
						Source.getTaskSource(task.getKey()), originator,
						MAlertType.PerceivedSeverity.WARNING,
						"indicationCriticalSettings1");

		final Date severityChangeDate = new Date(new Date().getTime()
				- TimeConstants.MILLISECONDS_PER_HOUR * 2);
		warningIndication.setDateTime(severityChangeDate);

		alert = alertService.activateAlert(warningIndication);

		alert = alertService.clearAlert(clearIndication);
		Assert.assertEquals(MAlertType.Status.CLEARED, alert.getStatus());
		Assert.assertEquals(
				clearIndicationDate.getTime() - severityChangeDate.getTime(),
				alert.getDuration().longValue());
	}


	@SuppressWarnings("unchecked")
	private void testUpdateAlertWithTheSameProperties(
			final MAlertIndication indication) {

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);

		AlertDTO alert = alertService.activateAlert(indication);
		final Date dateCreation = alert.getCreationdatetime();
		final Long alertId = alert.getIdLongValue();
		pause();
		alert = alertService.activateAlert(indication);
		Assert.assertEquals(alertId, alert.getIdLongValue());
		Assert.assertEquals(dateCreation, alert.getCreationdatetime());
		Assert.assertEquals(indication.getDateTime(),
				alert.getLastupdatedatetime());
		Assert.assertEquals(UpdateType.REPEAT, alert.getLastupdatetype());

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

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);

		AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(UpdateType.NEW, alert.getLastupdatetype());

		final String newContext = "newContext";
		indication.setContext(newContext);
		final AlertDTO updatedAlert = alertService.activateAlert(indication);
		Assert.assertEquals(newContext, updatedAlert.getContext());
		Assert.assertEquals(UpdateType.UPDATE, updatedAlert.getLastupdatetype());
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
		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(UpdateType.NEW, alert.getLastupdatetype());

		final String newExtraData = "newExtraData";
		indication.setExtraData(newExtraData);
		final AlertDTO updatedAlert = alertService.activateAlert(indication);
		Assert.assertEquals(newExtraData, updatedAlert.getExtradata());
		Assert.assertEquals(UpdateType.UPDATE, updatedAlert.getLastupdatetype());
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

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(UpdateType.NEW, alert.getLastupdatetype());

		indication.setPerceivedSeverity(MAlertType.PerceivedSeverity.NOTICE);
		final AlertDTO updatedAlert = alertService.activateAlert(indication);
		Assert.assertEquals(MAlertType.PerceivedSeverity.NOTICE,
				updatedAlert.getPerceivedseverity());
		Assert.assertEquals(UpdateType.SEVERITY_DEGRADATION,
				updatedAlert.getLastupdatetype());
		Assert.assertEquals(Boolean.FALSE.booleanValue(), updatedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck.longValue(),
				updatedAlert.getCountsincelastack().longValue());
		Assert.assertNull(updatedAlert.getAcknowledgmentdatetime());

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
	private void testUpdatePerceivedSeverityWithoutAckChange(
			final MAlertIndication indication) {

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(7);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(UpdateType.NEW, alert.getLastupdatetype());
		Assert.assertEquals(count.longValue(), alert.getAlert_count().longValue());

		final MAlert ackedAlert = propagationAlertService.acknowledgeAlert(indication,
				null, "test");
		modelSpace.flush();
		Assert.assertEquals(UpdateType.ACK, ackedAlert.getLastUpdateType());
		Assert.assertEquals(Boolean.TRUE, ackedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck,
				ackedAlert.getCountSinceLastAck());
		Assert.assertEquals(count, ackedAlert.getAlertCount());

		indication.setPerceivedSeverity(MAlertType.PerceivedSeverity.NOTICE);
		final AlertDTO updatedAlert = alertService.activateAlert(indication);
		Assert.assertEquals(MAlertType.PerceivedSeverity.NOTICE,
				updatedAlert.getPerceivedseverity());
		Assert.assertEquals(UpdateType.SEVERITY_DEGRADATION,
				updatedAlert.getLastupdatetype());
		Assert.assertEquals(Boolean.TRUE.booleanValue(), updatedAlert.isAcknowledged());
		Assert.assertEquals(countSinceLastAck.longValue(),
				updatedAlert.getCountsincelastack().longValue());
		Assert.assertEquals(count.longValue(), updatedAlert.getAlert_count().longValue());

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

		eventBroadcastDispatcher
				.broadcast(EasyMock.anyObject(Collection.class));
		EasyMock.expectLastCall().times(6);
		EasyMock.replay(eventBroadcastDispatcher);

		final AlertDTO alert = alertService.activateAlert(indication);
		Assert.assertEquals(UpdateType.NEW, alert.getLastupdatetype());

		indication.setSpecificReason(MAlertType.SpecificReason.UNKNOWN);
		final AlertDTO updatedAlert = alertService.activateAlert(indication);
		Assert.assertEquals(MAlertType.SpecificReason.UNKNOWN,
				updatedAlert.getSpecificreason());
		Assert.assertEquals(UpdateType.UPDATE, updatedAlert.getLastupdatetype());

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

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.util.*;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertReportEvent;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;

/**
 * A default implementation of {@link AlertReportService}
 * 
 * @author kunilov.p
 * 
 */
@Transactional(readOnly = true)
public class DefaultAlertReportService extends AbstractService
		implements
		AlertReportService,
			InitializingBean {

	private ObjectReader alertReportWrapperReader;

	private ObjectWriter alertReportWrapperWriter;

	@Autowired
	private AuthorizeService authorizeService;

	private final Logger LOGGER = Logger
			.getLogger(DefaultAlertReportService.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);
		alertReportWrapperReader = objectMapper
				.reader(AlertReportWrapper.class);
		alertReportWrapperWriter = objectMapper
				.writerWithDefaultPrettyPrinter();
	}

	@Override
	@Transactional(readOnly = false)
	public void closeAlertReport(final MAlert alert, final Date endDateTime) {
		final MAlertReport openedAlertReport = getOpenedAlertReport(alert);
		if (openedAlertReport == null) {
			LOGGER.warn("Unable to close alert report with endDateTime = "
					+ endDateTime + ": opened alert report not found for "
					+ alert
					+ ". Possible reason is more than one cleared indication.");
		} else {
			if (openedAlertReport.getStartDateTime().before(endDateTime)) {
				openedAlertReport.setEndDateTime(endDateTime);
				modelSpace.saveOrUpdate(openedAlertReport);

				notifyAlertReportOpenClose(AbstractEvent.EventType.DELETE,
						alert, endDateTime, openedAlertReport.getId());
				// FIXME: remove when bug http://rnd/issues/3241 will be fixed
				// completely
				LOGGER.debug("Close opened alert report: " + openedAlertReport);
			} else {
				// FIXME: remove when bug http://rnd/issues/3241 will be fixed
				// completely
				LOGGER.error("!!!IMPORTANT!!! Please contact your administrator! "
						+ "Unable to close alert report with endDateTime = "
						+ endDateTime
						+ ": incorrect report is retrieved to close: "
						+ openedAlertReport);
			}
		}
	}

	private void notifyAlertReportOpenClose(AbstractEvent.EventType type,
			MAlert alert, Date date, long reportID) {
		if(alert.getRelatedRecordingTaskKey() != null) {
			internalEventBroadcaster.broadcast(Arrays
					.asList(new AlertReportEvent(type,
							alert.getId(),
							new DateTime(date),
							alert.getSource().getParent().getParent().getKey(),
							alert.getRelatedRecordingTaskKey(),reportID)));
		}
	}

	@Override
	public AlertReportWrapper deserializeBean(final String beanPayload) {
		AlertReportWrapper bean = null;
		try {
			bean = alertReportWrapperReader
					.<AlertReportWrapper> readValue(beanPayload);
		} catch (final Exception ex) {
			throw new ServiceException(
					"Cannot deserialize alert report wrapper", ex);
		}
		return bean;
	}

	@Override
	public long getAlertReportCount(final Set<String> sourceKeys,
			final TimeInterval timeInterval, final Criterion filteringCriterion) {
		return modelSpace.count(
				MAlertReport.class,
				getAlertReportCriterion(sourceKeys, timeInterval,
						filteringCriterion));
	}

	private Criterion getAlertReportCriterion(final Set<String> sourceKeys,
			final TimeInterval timeInterval, final Criterion filteringCriterion) {

		Criterion sourceCriterion = null;
		if (SimpleUtils.isNotNullAndNotEmpty(sourceKeys)) {
			sourceCriterion = modelSpace.createCriterionQuery().in(
					"alert.source.key", sourceKeys);
		}

		Criterion probeCriterion = null;
		List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();
		if(SimpleUtils.isNotNullAndNotEmpty(agentKeys)) {
			probeCriterion = modelSpace.createCriterionQuery().in(
					"alert.source.parent.parent.key", agentKeys);
		}

		Criterion timeIntervalCriterion = null;
		if (timeInterval != null) {
			final Date endDateTime = TimeInterval.getEndDate(
					timeInterval.getEndDateTime(), timeInterval.getType());
			final Date startDateTime = TimeInterval.getStartDate(
					timeInterval.getStartDateTime(), endDateTime,
					timeInterval.getType());

			timeIntervalCriterion = Utils
					.createTimeIntervalIntersectionCriterion(startDateTime,
							"startDateTime", endDateTime, "endDateTime");
		}
		return SimpleUtils.mergeCriterions(sourceCriterion, probeCriterion,
				timeIntervalCriterion, filteringCriterion);
	}

	@Override
	public List<MAlertReport> getAlertReports(final Set<String> sourceKeys,
			final TimeInterval timeInterval,
			final Criterion filteringCriterion, final Order order,
			final Integer startPosition, final Integer size) {
		final List<MAlertReport> alertReports = modelSpace.find(
				MAlertReport.class,
				getAlertReportCriterion(sourceKeys, timeInterval,
						filteringCriterion), order, startPosition, size);

		return alertReports;
	}

	private MAlertReport getOpenedAlertReport(final MAlert alert) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("alert.id", alert.getId());
		criterion = query.and(criterion, query.isNull("endDateTime"));
		final List<MAlertReport> reports = modelSpace.find(MAlertReport.class,
				criterion);
		if (reports.size() > 1) {
			throw new ServiceException("More than one opened report found for "
					+ alert);
		}
		MAlertReport result = null;
		if (reports.size() > 0) {
			result = reports.iterator().next();
		}
		return result;
	}

	@Transactional(readOnly = false)
	@Override
	public void openAlertReport(final MAlert alert, final Date startDateTime) {
		final MAlertReport openedAlertReport = getOpenedAlertReport(alert);
		if (openedAlertReport != null) {
			final boolean severityChanged = !alert.getPerceivedSeverity()
					.equals(openedAlertReport.getPerceivedSeverity());
			// do nothing if severity wasn't changed
			if (severityChanged) {
				LOGGER.error("Unable to open alert report with startDateTime = "
						+ startDateTime
						+ ": opened alert report already exists: "
						+ openedAlertReport);
			}
		} else {
			final MAlertReport alertReport = new MAlertReport();
			alertReport.setAlert(alert);
			alertReport.setPerceivedSeverity(alert.getPerceivedSeverity());
			alertReport.setStartDateTime(startDateTime);
			//check if we try to open already closed alert BUG #6119
			if(alert.getClearedDateTime()!=null && alert.getClearedDateTime().after(startDateTime) && MAlertType.Status.CLEARED.equals(alert.getStatus()))
			{
				alertReport.setEndDateTime(alert.getClearedDateTime());
			}else{
				alertReport.setEndDateTime(null);
			}
			modelSpace.save(alertReport);

			notifyAlertReportOpenClose(AbstractEvent.EventType.CREATE, alert, startDateTime,alertReport.getId());
			// FIXME: remove when bug http://rnd/issues/3241 will be fixed
			// completely
			LOGGER.debug("Create opened alert report: " + alertReport);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void processAlertUpdateEvent(final MAlertUpdate alertUpdate) {
		final UpdateType updateType = alertUpdate.getUpdateType();

		// FIXME: remove when bug http://rnd/issues/3241 will be fixed
		// completely
		LOGGER.debug("Process alert update: " + alertUpdate);

		// close previous alert report
		if (updateType.isSeverityChanged() || updateType.isCleared()) {
			closeAlertReport(alertUpdate.getAlert(), alertUpdate.getDateTime());
		}
		// add new alert report
		if (updateType == UpdateType.NEW || updateType.isSeverityChanged()) {
			openAlertReport(alertUpdate.getAlert(), alertUpdate.getDateTime());
		}
	}

	@Override
	public String serializeBean(final AlertReportWrapper bean) {
		String result = null;
		try {
			result = alertReportWrapperWriter.writeValueAsString(bean);
		} catch (final Exception ex) {
			throw new ServiceException("Cannot serialize alert report wrapper",
					ex);
		}
		return result;
	}
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.exception.UnknownSourceException;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.AlertService;
import com.tecomgroup.qos.service.InternalSourceService;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import com.tecomgroup.qos.util.AuditLogger;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.ActivateAlertEvent;
import com.tecomgroup.qos.event.AlertEvent;
import com.tecomgroup.qos.exception.AlertException;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;

/**
 * 
 * @author abondin
 * 
 */
@Transactional(readOnly = true)
public abstract class DefaultAlertService extends AbstractService
		implements
		AlertService {

	private File alertTypesFile;

	@Autowired
	protected InternalSourceService sourceService;

	@Autowired
	protected AlertHistoryService alertHistoryService;

	@Autowired
	private AuthorizeService authorizeService;

	private final static Logger LOGGER = Logger
			.getLogger(DefaultAlertService.class);

	private final ObjectMapper jsonMapper = new ObjectMapper();

	@Override
	@Transactional(readOnly = false)
	public MAlert acknowledgeAlert(final MAlertIndication indication,
			final String comment, final String accountToken) {
		final MAlert alert = getAlert(indication);
		if (alert != null && alert.isDisabled()) {
			throw new AlertException("Alert was disabled");
		}
		if (alert == null) {
			throw new AlertException("Alert for indication "
					+ indication.toString() + " not found");
		}
		updateAlertPropertiesWhenAcknowledgeAlert(alert, indication,
				accountToken, comment);

		updateDateTime(alert, indication.getDateTime());

		modelSpace.update(alert);

		sendAlertEvent(new AlertEvent(EventType.UPDATE, alert.getId(), alert.getSource().getParent().getParent().getKey(),
				alert.getPerceivedSeverity(),alert.getStatus()));
		return alert;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void acknowledgeAlerts(final List<MAlertIndication> indications,
			final String comment) {
		try {
			final String accountToken = getCurrentAccount();
			for (final MAlertIndication indication : indications) {
				executeInTransaction(false, new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							final TransactionStatus status) {
						acknowledgeAlert(indication, comment, accountToken);
					}
				});
			}
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Acknowledge alerts  : {}"
					, Arrays.toString(indications.toArray(new MAlertIndication[0])));
		}catch (Exception e)
		{
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to acknowledge alerts  : {}, reason : {} "
					, Arrays.toString(indications.toArray(new MAlertIndication[0])), e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public MAlert activateAlert(final MAlertIndication indication,
			final String accountToken) {
		MAlert alert = getAlert(indication);
		boolean isOutdatedUpdate=false;
		Status oldStatus = null;
		UpdateType oldLastUpdate = null;
		final boolean newAlert = alert == null;
		if (newAlert) {
			alert = createNewAlert(indication);
			// We have to save alert to create a report
			modelSpace.save(alert);
			resetCount(alert, indication.getDateTime(), accountToken);
		} else {
			oldStatus = alert.getStatus();
			oldLastUpdate=alert.getLastUpdateType();
			updateAlert(alert, indication, accountToken);
			//check last update status, to prevent the case when,
			// alert OPEN processed after the same accident alert AUTO_CLEARED. BUG #6119
			if(alert.getClearedDateTime()!=null && alert.getClearedDateTime().after(indication.getDateTime()))
			{
				isOutdatedUpdate=true;
			}else{
				alert.setStatus(Status.ACTIVE);
			}
		}
		//don't update lastUpdateTime an lastUpdateType if we process outdated update.
		if(!isOutdatedUpdate){
			updateDateTime(alert, indication.getDateTime());
		}else{
			alert.setLastUpdateType(oldLastUpdate);
		}
		final AlertEvent.EventType eventType;
		if (newAlert) {
			eventType = EventType.CREATE;
		} else {
			if(!isOutdatedUpdate) {
				if (oldStatus.equals(Status.CLEARED)) {
					eventType = EventType.CREATE;
				} else {
					eventType = EventType.UPDATE;
				}
			}else{
				eventType = EventType.CREATE;
			}
		}
		modelSpace.update(alert);

		String agentKey = alert.getSource().getParent().getParent().getKey();
		sendAlertEvent(new AlertEvent(eventType,alert.getId(),agentKey,
				alert.getPerceivedSeverity(),alert.getStatus()));
		eventBroadcastDispatcher.broadcast(Arrays
				.asList(new ActivateAlertEvent(eventType, alert.getId(), alert
						.getPerceivedSeverity(),agentKey)));
		return alert;
	}

	@Override
	@Transactional(readOnly = false)
	public MAlert clearAlert(final MAlertIndication indication,
			final String comment, final String accountToken) {
		final MAlert alert = getAlert(indication);
		if (alert != null && !alert.isDisabled()) {
			resetAlertProperties(alert, indication.getDateTime(),
					indication.getIndicationType());

			alertHistoryService.addAlertUpdate(alert,
					alert.getLastUpdateType(), indication.getDateTime(),
					accountToken, null, null, null, comment);

			modelSpace.update(alert);

			sendAlertEvent(new AlertEvent(EventType.UPDATE, alert.getId(), alert.getSource().getParent().getParent().getKey(),
					alert.getPerceivedSeverity(),alert.getStatus()));
		}
		return alert;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void clearAlerts(final List<MAlertIndication> indications,
			final String comment) {
		try {
			final String accountToken = getCurrentAccount();
			for (final MAlertIndication indication : indications) {
				executeInTransaction(false, new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							final TransactionStatus status) {
						clearAlert(indication, comment, accountToken);
					}
				});
			}
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Clear alerts  : {}"
					, Arrays.toString(indications.toArray(new MAlertIndication[0])));
		}catch (Exception e)
		{
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to clear alerts  : {}, reason : {} "
					, Arrays.toString(indications.toArray(new MAlertIndication[0])), e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public MAlert commentAlert(final MAlertIndication indication,
			final String comment, final String accountToken) {
		final MAlert alert = getAlert(indication);
		if (alert != null && alert.isDisabled()) {
			throw new AlertException("Alert was disabled");
		}
		if (alert == null) {
			throw new AlertException("Alert for indication "
					+ indication.toString() + " not found");
		}
		alert.setLastUpdateType(UpdateType.COMMENT);

		alertHistoryService.addAlertUpdate(alert, alert.getLastUpdateType(),
				indication.getDateTime(), accountToken, "comment", null, null,
				comment);

		updateDateTime(alert, indication.getDateTime());

		modelSpace.update(alert);
		sendAlertEvent(new AlertEvent(EventType.UPDATE, alert.getId(), alert.getSource().getParent().getParent().getKey(),
				alert.getPerceivedSeverity(), alert.getStatus()));
		return alert;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void commentAlerts(final List<MAlertIndication> indications,
			final String comment) {
		try {
			final String accountToken = getCurrentAccount();
			for (final MAlertIndication indication : indications) {
				executeInTransaction(false, new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							final TransactionStatus status) {
						commentAlert(indication, comment, accountToken);

					}
				});
			}
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Comment alerts  : {}"
					, Arrays.toString(indications.toArray(new MAlertIndication[0])));
		}catch (Exception e)
		{
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to comment alerts  : {}, reason : {} "
					, Arrays.toString(indications.toArray(new MAlertIndication[0])), e.getMessage());
			throw e;
		}
	}

	private MAlert createNewAlert(final MAlertIndication indication) {
		final MAlert alert = new MAlert(indication);
		final MAlertType alertType = getAlertType(indication.getAlertType()
				.getName());
		if (alertType == null) {
			throw new AlertException("AlertType "
					+ indication.getAlertType().getName() + " not found");
		}
		alert.setAlertType(alertType);

		final MAgentTask source = validateAlertSource(indication.getSource());
		alert.setSource(source);
		final MPolicy originator = validateAlertOriginator(indication
				.getOriginator());
		alert.setOriginator(originator);

		alert.setCreationDateTime(indication.getDateTime());
		alert.setSeverityChangeDateTime(alert.getCreationDateTime());
		alert.setDetectionValue(indication.getDetectionValue());
		alert.setAcknowledged(false);
		alert.setStatus(Status.ACTIVE);
		alert.setLastUpdateType(UpdateType.NEW);
		alert.setAlertCount(1L);
		alert.setCountSinceLastAck(0L);

		return alert;
	}

	@Override
	@Transactional(readOnly = false)
	public void disable(final MAlert alert) {
		if (!alert.isDisabled()) {
			alert.setDisabled(true);
			try {
				final Date disabledDateTime = new Date();
				resetAlertProperties(alert, disabledDateTime,
						UpdateType.OPERATOR_DELETED);
				alertHistoryService.addAlertUpdate(alert,
						alert.getLastUpdateType(), disabledDateTime, "user",
						null, null, null, null);

				modelSpace.update(alert);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Disable alert: " + alert);
				}
				sendAlertEvent(new AlertEvent(EventType.DELETE, alert.getId(), alert.getSource().getParent().getParent().getKey(),
						alert.getPerceivedSeverity(),alert.getStatus()));
			} catch (final Exception ex) {
				throw new AlertException("Unable to disable alert: " + alert,
						ex);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void disableAlertsByOriginator(final Source originator) {
		final List<MAlert> alerts = getAlertsByOriginator(originator, null,
				null, null);
		for (final MAlert alert : alerts) {
			disable(alert);
		}
	}

	private MAlert getAlert(final MAlertIndication indication) {
		final MAlert alert = getAlert(indication.getAlertType(),
				indication.getSource(), indication.getOriginator(),
				indication.getSettings(), false, false);
		return alert;
	}

	private MAlert getAlert(final MAlertType alertType, final Source source,
			final Source originator, final String settings,
			final boolean onlyEnabled, final boolean postProcess) {
		return getAlert(alertType, source.getKey(), originator.getKey(),
				settings, onlyEnabled, postProcess);
	}

	private MAlert getAlert(final MAlertType alertType, final String sourceKey,
			final String originatorKey, final String settings,
			final boolean onlyEnabled, final boolean postProcess) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("source.key", sourceKey);
		criterion = query.and(criterion,
				query.eq("originator.key", originatorKey));
		if (onlyEnabled) {
			criterion = query.and(criterion, query.eq("disabled", false));
		}
		if (settings == null) {
			criterion = query.and(criterion, query.isNull("settings"));
		} else {
			criterion = query.and(criterion, query.eq("settings", settings));
		}
		criterion = query.and(
				criterion,
				query.and(criterion,
						query.eq("alertType.name", alertType.getName())));
		final MAlert alert = modelSpace.findUniqueEntity(MAlert.class,
				criterion);
		if (postProcess) {
			postProcessAlert(alert);
		}
		return alert;
	}

	@Override
	public MAlert getAlert(final Long alertId) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("id", alertId);
		final MAlert alert = modelSpace.findUniqueEntity(MAlert.class,
				criterion);
		return alert;
	}

	@Deprecated
	@Override
	public MAlert getAlert(final String alertTypeName, final Source source,
			final Source originator, final String settings) {
		return getAlert(alertTypeName, source.getKey(), originator.getKey(),
				settings);
	}

	@Override
	public MAlert getAlert(final String alertTypeName, final String sourceKey,
			final String originatorKey, final String settings) {
		final MAlertType alertType = getAlertType(alertTypeName);
		if (alertType == null) {
			throw new AlertException("AlertType " + alertTypeName
					+ " not found");
		}
		return getAlert(alertType, sourceKey, originatorKey, settings, true,
				true);
	}

	private Criterion buildProbeFilterCriterion() {
		List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();
		if(!agentKeys.isEmpty()) {
			return modelSpace.createCriterionQuery().in("source.parent.parent.key", agentKeys);
		}
		return null;
	}

	@Override
	public List<MAlert> getAlerts(final Criterion criterion, final Order order,
			final Integer startPosition, final Integer size) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion queryCriterion = query.eq("disabled", false);
		if (criterion != null) {
			queryCriterion = query.and(criterion, queryCriterion);
		}

		Criterion probeCriterion = buildProbeFilterCriterion();
		if(probeCriterion == null) {
			return new ArrayList<MAlert>();
		}

		final List<MAlert> alerts = modelSpace.find(MAlert.class,
				SimpleUtils.mergeCriterions(queryCriterion, probeCriterion), order, startPosition, size);
		postProcessAlerts(alerts);
		return alerts;
	}

	private List<MAlert> getAlertsByOriginator(final Source originator,
			final boolean disabled, final Order order,
			final Integer startPosition, final Integer size) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("disabled", disabled);
		criterion = query.and(criterion,
				query.eq("originator.key", originator.getKey()));

		final List<MAlert> alerts = modelSpace.find(MAlert.class, criterion,
				order, startPosition, size);
		postProcessAlerts(alerts);
		return alerts;
	}

	@Override
	public List<MAlert> getAlertsByOriginator(final Source originator,
			final Order order, final Integer startPosition, final Integer size) {
		return getAlertsByOriginator(originator, false, order, startPosition,
				size);
	}

	@Override
	public Long getAlertsCount(final Criterion criterion) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion queryCriterion = query.eq("disabled", false);
		if (criterion != null) {
			queryCriterion = query.and(criterion, queryCriterion);
		}

		Criterion probeCriterion = buildProbeFilterCriterion();
		if(probeCriterion == null) {
			return 0L;
		}

		return modelSpace.count(MAlert.class,
				SimpleUtils.mergeCriterions(queryCriterion, probeCriterion));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> getAlertsSummaryDurationByAgentKey(
			final Set<PerceivedSeverity> severities, final Type intervalType) {

		Criterion probeCriterion = buildProbeFilterCriterion();
		if(probeCriterion == null) {
			return new HashMap<>();
		}

		final CriterionQuery query = modelSpace.createCriterionQuery();
		final TimeInterval interval = TimeInterval.get(intervalType);
		Criterion criterion = Utils.createTimeIntervalIntersectionCriterion(
				interval.getStartDateTime(), "creationDateTime",
				interval.getEndDateTime(), "clearedDateTime");
		// if severities is empty or null then search for all severities
		if (SimpleUtils.isNotNullAndNotEmpty(severities)) {
			criterion = SimpleUtils.mergeCriterions(criterion,
					query.in("perceivedSeverity", severities));
		}

		criterion = SimpleUtils.mergeCriterions(criterion, probeCriterion);

		final List<Object[]> rawData = (List<Object[]>) modelSpace
				.findProperties(MAlert.class, criterion,
						Order.asc("source.parent.parent.key"), null, "status",
						"severityChangeDateTime", "clearedDateTime",
						"source.parent.parent.key",
						"source.parent.parent.displayName");
		return processRawStatistics(rawData, interval);
	}

	private MAlertType getAlertType(final String alertTypeName) {
		final MAlertType alertType = modelSpace.findUniqueEntity(
				MAlertType.class,
				modelSpace.createCriterionQuery().eq("name", alertTypeName));
		return alertType;
	}

	@Override
	public Set<String> getAllAlertTypeNames() {
		return new HashSet<String>(getAllTypes().keySet());
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, MAlertType> getAllTypes() {
		final Map<String, MAlertType> types = new HashMap<String, MAlertType>();
		for (final MAlertType type : modelSpace.getAll(MAlertType.class)) {
			types.put(type.getName(), type);
		}
		return types;
	}

	/**
	 * @return
	 */
	private String getCurrentAccount() {
		final Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		final String token = authentication == null ? null : authentication
				.getName();
		return token;
	}

	private void incrementCount(final MAlert alert, final Date dateTime,
			final String accountToken) {
		alertHistoryService.addAlertUpdate(alert, alert.getLastUpdateType(),
				dateTime, accountToken, "count", alert.getAlertCount(),
				alert.getAlertCount() + 1, null);
		alert.setAlertCount(alert.getAlertCount() + 1);
	}

	private void incrementCountSinceLastAck(final MAlert alert,
			final Date dateTime, final String accountToken) {
		alertHistoryService.addAlertUpdate(alert, alert.getLastUpdateType(),
				dateTime, accountToken, "countSinceLastAck",
				alert.getCountSinceLastAck(), alert.getCountSinceLastAck() + 1,
				null);
		alert.setCountSinceLastAck(alert.getCountSinceLastAck() + 1);
	}

	public void init() {
		if (alertTypesFile != null && alertTypesFile.exists()) {
			initialize();
		}
	}

	private void initialize() {
		try {
			final AlertTypesConfiguration configuration = jsonMapper.readValue(
					alertTypesFile, AlertTypesConfiguration.class);
			final Map<String, MAlertType> allTypes = executeInTransaction(true,
					new TransactionCallback<Map<String, MAlertType>>() {

						@Override
						public Map<String, MAlertType> doInTransaction(
								final TransactionStatus status) {
							return getAllTypes();
						}
					});
			for (final MAlertType type : configuration.getTypes()) {
				if (!allTypes.containsKey(type.getName())) {
					executeInTransaction(false,
							new TransactionCallbackWithoutResult() {
								@Override
								protected void doInTransactionWithoutResult(
										final TransactionStatus status) {
									registerAlertType(type);
								}
							});
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("Cannot load default alert types from "
					+ alertTypesFile, ex);
		}
	}

	/**
	 * @param alert
	 */
	protected abstract void postProcessAlert(MAlert alert);

	/**
	 * Post processes alerts before send to the client
	 * 
	 * @param alerts
	 */
	protected void postProcessAlerts(final List<MAlert> alerts) {
		for (final MAlert alert : alerts) {
			postProcessAlert(alert);
		}
	}

	private Map<String, Long> processRawStatistics(
			final List<Object[]> rawData, final TimeInterval timeInterval) {
		Map<String, Long> sortedResult = null;
		if (rawData.size() > 0) {
			final Map<String, Long> result = new HashMap<>();
			String currentAgentKey = (String) rawData.get(0)[3];
			String currentAgentDisplayName = (String) rawData.get(0)[4];

			long summaryByAgent = 0;
			for (final Object[] row : rawData) {
				if (row[3].equals(currentAgentKey)) {
					summaryByAgent += MAlert.getDurationOnInterval(
							(Status) row[0], (Date) row[1], (Date) row[2],
							timeInterval);
				} else {
					result.put(currentAgentDisplayName, summaryByAgent);
					currentAgentKey = (String) row[3];
					currentAgentDisplayName = (String) row[4];
					summaryByAgent = 0;
					summaryByAgent += MAlert.getDurationOnInterval(
							(Status) row[0], (Date) row[1], (Date) row[2],
							timeInterval);
				}
			}
			result.put(currentAgentDisplayName, summaryByAgent);

			sortedResult = Collections.unmodifiableMap(sortMapByValues(result));
		}
		return sortedResult;
	}

	@Override
	@Transactional(readOnly = false)
	public void registerAlertType(final MAlertType type) {
		try {
			final MAlertType existing = getAlertType(type.getName());
			if (existing == null) {
				modelSpace.save(type);
			} else {
				// TODO Update ?
			}
		} catch (final Exception ex) {
			throw new AlertException("Unable to register alert type: "
					+ type.getName(), ex);
		}
	}

	/**
	 * Reset alert properties:
	 * <ul>
	 * <li>Status</li>
	 * <li>ClearedDateTime</li>
	 * <li>LastUpdateType</li>
	 * <li>LastUpdateTime</li>
	 * <li>Acknowledged</li>
	 * <li>CountSinceLastAck</li>
	 * </ul>
	 * 
	 * @param alert
	 * @param timestamp
	 * @param updateType
	 */
	private void resetAlertProperties(final MAlert alert, final Date timestamp,
			final UpdateType updateType) {
		alert.setStatus(Status.CLEARED);
		alert.setClearedDateTime(timestamp);
		alert.setLastUpdateType(updateType);

		updateDateTime(alert, timestamp);

		// reset acknowledged state
		if (alert.isAcknowledged()) {
			alert.setAcknowledged(false);
			alert.setCountSinceLastAck(0L);
		}

	}

	private void resetCount(final MAlert alert, final Date dateTime,
			final String accountToken) {
		alertHistoryService
				.addAlertUpdate(alert, alert.getLastUpdateType(), dateTime,
						accountToken, "count", alert.getAlertCount(), 1, null);
		alert.setAlertCount(1L);
	}

	private void resetCountSinceLastAck(final MAlert alert,
			final Date dateTime, final String accountToken, final String comment) {
		resetCountSinceLastAck(alert, alert.getLastUpdateType(), dateTime,
				accountToken, comment);
	}

	private void resetCountSinceLastAck(final MAlert alert,
			final UpdateType updateType, final Date dateTime,
			final String accountToken, final String comment) {
		alertHistoryService.addAlertUpdate(alert, updateType, dateTime,
				accountToken, "countSinceLastAck",
				alert.getCountSinceLastAck(), 0, comment);
		alert.setCountSinceLastAck(0L);
	}

	@Override
	@Transactional(readOnly = false)
	public void resetDisabledAlerts(final Source originator) {
		final List<MAlert> disabledAlerts = getAlertsByOriginator(originator,
				true, null, null, null);
		for (final MAlert disabledAlert : disabledAlerts) {
			disabledAlert.setDisabled(false);
			modelSpace.saveOrUpdate(disabledAlert);
			sendAlertEvent(new AlertEvent(EventType.UPDATE, disabledAlert.getId(), disabledAlert.getSource().getParent().getParent().getKey(),
					disabledAlert.getPerceivedSeverity(),disabledAlert.getStatus()));
		}
	}

	protected void sendAlertEvent(final AlertEvent event) {
		//postProcessAlert(event.getAlert());
		eventBroadcastDispatcher.broadcast(Arrays.asList(event));
	}

	/**
	 * @param alertHistoryService
	 *            the alertHistoryService to set
	 */
	public void setAlertHistoryService(
			final AlertHistoryService alertHistoryService) {
		this.alertHistoryService = alertHistoryService;
	}

	/**
	 * @param alertTypesFile
	 *            the alertTypesFile to set
	 */
	public void setAlertTypesFile(final File alertTypesFile) {
		this.alertTypesFile = alertTypesFile;
	}

	public void setAuthorizeService(AuthorizeService authorizeService) {
		this.authorizeService = authorizeService;
	}

	/**
	 * @param sourceService
	 *            the sourceService to set
	 */
	public void setSourceService(final InternalSourceService sourceService) {
		this.sourceService = sourceService;
	}

	private Map<String, Long> sortMapByValues(final Map<String, Long> map) {
		final Map<String, Long> sortedMap = new LinkedHashMap<>();

		final List<Map.Entry<String, Long>> sortedMapEntries = new ArrayList<>(
				map.entrySet());
		Collections.sort(sortedMapEntries,
				new Comparator<Map.Entry<String, Long>>() {

					@Override
					public int compare(final Entry<String, Long> left,
							final Entry<String, Long> right) {
						int result = -Long.compare(left.getValue(),
								right.getValue());
						// if the values are equals then sort by keys
						if (result == 0) {
							result = left.getKey().compareTo(right.getKey());
						}
						return result;
					}
				});

		for (final Map.Entry<String, Long> sortedMapEntry : sortedMapEntries) {
			sortedMap.put(sortedMapEntry.getKey(), sortedMapEntry.getValue());
		}

		return sortedMap;
	}

	@Override
	@Transactional(readOnly = false)
	public MAlert unAcknowledgeAlert(final MAlertIndication indication,
			final String comment, final String accountToken) {
		final MAlert alert = getAlert(indication);
		if (alert != null && alert.isDisabled()) {
			throw new AlertException("Alert was disabled");
		}
		if (alert == null) {
			throw new AlertException("Alert for indication "
					+ indication.toString() + " not found");
		}
		updateAlertPropertiesWhenUnAcknowledgeAlert(alert, indication,
				accountToken, comment);

		updateDateTime(alert, indication.getDateTime());

		modelSpace.update(alert);

		sendAlertEvent(new AlertEvent(EventType.UPDATE, alert.getId(), alert.getSource().getParent().getParent().getKey()
				,alert.getPerceivedSeverity(),alert.getStatus()));
		return alert;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void unAcknowledgeAlerts(final List<MAlertIndication> indications,
			final String comment) {
		try {
			final String accountToken = getCurrentAccount();
			for (final MAlertIndication indication : indications) {
				executeInTransaction(false, new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							final TransactionStatus status) {
						unAcknowledgeAlert(indication, comment, accountToken);
					}
				});
			}
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Unacknowle alerts  : {}"
					, Arrays.toString(indications.toArray(new MAlertIndication[0])));
		}catch (Exception e)
		{
			AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to unacknowledge alerts  : {}, reason : {} "
					, Arrays.toString(indications.toArray(new MAlertIndication[0])),e.getMessage());
			throw e;
		}
	}

	private void updateAlert(final MAlert updatedAlert,
			final MAlertIndication indication, final String accountToken) {
		boolean updateAlert = false;
		if (!updatedAlert.getSpecificReason().equals(
				indication.getSpecificReason())) {
			alertHistoryService.addAlertUpdate(updatedAlert, UpdateType.UPDATE,
					indication.getDateTime(), accountToken, "specificReason",
					updatedAlert.getSpecificReason(),
					indication.getSpecificReason(), null);
			updatedAlert.setSpecificReason(indication.getSpecificReason());
			updateAlert = true;
		}

		if ((updatedAlert.getContext() == null && indication.getContext() != null)
				|| (updatedAlert.getContext() != null && !updatedAlert
						.getContext().equals(indication.getContext()))) {
			alertHistoryService.addAlertUpdate(updatedAlert, UpdateType.UPDATE,
					indication.getDateTime(), accountToken, "context",
					updatedAlert.getContext(), indication.getContext(), null);
			updatedAlert.setContext(indication.getContext());
			updateAlert = true;
		}
		if ((updatedAlert.getExtraData() == null && indication.getExtraData() != null)
				|| (updatedAlert.getExtraData() != null && !updatedAlert
						.getExtraData().equals(indication.getExtraData()))) {
			alertHistoryService.addAlertUpdate(updatedAlert, UpdateType.UPDATE,
					indication.getDateTime(), accountToken, "extraData",
					updatedAlert.getExtraData(), indication.getExtraData(),
					null);
			updatedAlert.setExtraData(indication.getExtraData());
			updateAlert = true;
		}

		if ((updatedAlert.getDetectionValue() == null && indication
				.getDetectionValue() != null)
				|| (updatedAlert.getDetectionValue() != null && !updatedAlert
						.getDetectionValue().equals(
								indication.getDetectionValue()))) {
			alertHistoryService.addAlertUpdate(updatedAlert, UpdateType.UPDATE,
					indication.getDateTime(), accountToken, "detectionValue",
					updatedAlert.getDetectionValue(),
					indication.getDetectionValue(), null);
			updatedAlert.setDetectionValue(indication.getDetectionValue());
			updateAlert = true;
		}

		if (Status.CLEARED.equals(updatedAlert.getStatus())) {
			updatedAlert.setLastUpdateType(UpdateType.NEW);
			if(updatedAlert.getClearedDateTime()==null || updatedAlert.getClearedDateTime().before(indication.getDateTime()))
			{
				updatedAlert.setClearedDateTime(null);
			}
			updatedAlert.setCreationDateTime(indication.getDateTime());
			updatedAlert.setSeverityChangeDateTime(updatedAlert
					.getCreationDateTime());
			updatedAlert
					.setPerceivedSeverity(indication.getPerceivedSeverity());
			resetCount(updatedAlert, indication.getDateTime(), accountToken);
		} else {
			if (updateAlert) {
				updatedAlert.setLastUpdateType(UpdateType.UPDATE);
			} else {
				updatedAlert.setLastUpdateType(UpdateType.REPEAT);
			}

			if (updatedAlert.isAcknowledged()) {
				updateAlertPropertiesRelatedToAcknowledgedState(updatedAlert,
						indication, accountToken);
			} else if (updatedAlert.getPerceivedSeverity().equals(
					indication.getPerceivedSeverity())) {
				if (updateAlert) {
					updatedAlert
							.setAlertCount(updatedAlert.getAlertCount() + 1);
				} else {
					incrementCount(updatedAlert, indication.getDateTime(),
							accountToken);
				}
			} else {
				updateAlertSeverity(updatedAlert, indication, accountToken);
			}
		}
	}

	private void updateAlertPropertiesRelatedToAcknowledgedState(
			final MAlert updatedAlert, final MAlertIndication indication,
			final String accountToken) {
		if (updatedAlert.getPerceivedSeverity().equals(
				indication.getPerceivedSeverity())) {
			incrementCountSinceLastAck(updatedAlert, indication.getDateTime(),
					accountToken);
		} else if (updatedAlert.getPerceivedSeverity().less(
				indication.getPerceivedSeverity())) {
			updateAlertSeverity(updatedAlert, indication, accountToken);
			// situation becomes worse. It is necessary to reset
			// acknowledged state and countSinceLastAck.
			updatedAlert.setAcknowledged(false);
			resetCountSinceLastAck(updatedAlert, UpdateType.UNACK,
					indication.getDateTime(), accountToken, null);
		} else {
			// situation becomes better. It is not necessary to increment
			// countSinceLastAck.
			updateAlertSeverity(updatedAlert, indication, accountToken);
		}
	}

	private void updateAlertPropertiesWhenAcknowledgeAlert(
			final MAlert updatedAlert, final MAlertIndication indication,
			final String accountToken, final String comment) {
		updatedAlert.setAcknowledged(true);
		updatedAlert.setAcknowledgmentDateTime(indication.getDateTime());
		updatedAlert.setLastUpdateType(UpdateType.ACK);
		resetCountSinceLastAck(updatedAlert, indication.getDateTime(),
				accountToken, comment);
	}

	private void updateAlertPropertiesWhenUnAcknowledgeAlert(
			final MAlert updatedAlert, final MAlertIndication indication,
			final String accountToken, final String comment) {
		updatedAlert.setAcknowledged(false);
		updatedAlert.setLastUpdateType(UpdateType.UNACK);
		resetCountSinceLastAck(updatedAlert, indication.getDateTime(),
				accountToken, comment);
	}

	private void updateAlertSeverity(final MAlert alert,
			final MAlertIndication indication, final String accountToken) {
		UpdateType updateType = UpdateType.SEVERITY_DEGRADATION;
		if (alert.getPerceivedSeverity()
				.less(indication.getPerceivedSeverity())) {
			updateType = UpdateType.SEVERITY_UPGRADE;
			// situation becomes worse. It is necessary to increment count.
			alert.setAlertCount(alert.getAlertCount() + 1);
		}
		alert.setLastUpdateType(updateType);
		// severity must be updated before the addition of the alert update.
		// Otherwise new alert report will be opened with old severity.
		final PerceivedSeverity previousSeverity = alert.getPerceivedSeverity();
		alert.setPerceivedSeverity(indication.getPerceivedSeverity());
		alert.setSeverityChangeDateTime(indication.getDateTime());
		alertHistoryService.addAlertUpdate(alert, alert.getLastUpdateType(),
				indication.getDateTime(), accountToken, "perceivedSeverity",
				previousSeverity, indication.getPerceivedSeverity(), null);
	}

	private void updateDateTime(final MAlert alert, final Date dateTime) {
		alert.setLastUpdateDateTime(dateTime);
	}

	private MPolicy validateAlertOriginator(final Source originator) {
		final MPolicy domainSource = sourceService.getDomainSource(MPolicy.class, originator.getKey());
		if (domainSource == null) {
			throw new AlertException(
					"Unable to find domain source for alert originator: "
							+ originator);
		}
		return domainSource;
	}

	private MAgentTask validateAlertSource(final Source source) {
		if(source.getType() != Source.Type.TASK) {
			throw new UnknownSourceException("Usupported source type "
					+ source.getType());
		}

		final MAgentTask domainSource = sourceService.getDomainSource(MAgentTask.class, source.getKey());
		if (domainSource == null) {
			throw new AlertException(
					"Unable to find domain source for alert source: " + source);
		}
		return domainSource;
	}



}

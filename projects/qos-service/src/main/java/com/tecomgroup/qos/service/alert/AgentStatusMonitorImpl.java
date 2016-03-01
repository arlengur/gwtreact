package com.tecomgroup.qos.service.alert;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.tecomgroup.qos.domain.AlertDTO;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.modelspace.jdbc.dao.AlertServiceDao;
import com.tecomgroup.qos.service.AbstractService;


public class AgentStatusMonitorImpl extends AbstractService implements AgentStatusMonitor,InitializingBean {

    private final Map<String, Map<Long, MAlertType.PerceivedSeverity>> alertsCache = new HashMap<String, Map<Long, MAlertType.PerceivedSeverity>>();

    private final static Logger LOGGER = Logger
            .getLogger(AgentStatusMonitorImpl.class);

    @Autowired
    private AlertServiceDao alertServiceDataProvider;


    @Override
	public void afterPropertiesSet() throws Exception {
		synchronized (alertsCache) {
			loadAlertStatuses();
		}
		internalEventBroadcaster.subscribe(new QoSEventListener() {
			@Override
			public void onServerEvent(final AbstractEvent event) {
				final AgentChangeStateEvent agentChangeStateEvent = (AgentChangeStateEvent) event;
				notifySource(agentChangeStateEvent.getAgentKey());
			}
		}, new QoSEventFilter() {
			@Override
			public boolean accept(final AbstractEvent event) {
				if (event instanceof AgentChangeStateEvent) {
					if (MAgent.AgentRegistrationState.SUCCESS == (((AgentChangeStateEvent) event)
							.getState())) {
						return true;
					}
				}
				return false;
			}
		});
	}

    @Override
    public void sendStatusEvent(final Long alertId, final MAlertType.PerceivedSeverity perceivedSeverity, final MAlertType.Status alertStatus,
                                final AbstractEvent.EventType alertChangeType, final String sourceKey) {
        synchronized (alertsCache) {
            if (handleAlertEvent(alertId,perceivedSeverity,alertStatus,alertChangeType,sourceKey)) {
                notifySource(sourceKey);
            }
        }
    }

    @Override
    public void sendStatusEvent(AlertEvent event) {
        this.sendStatusEvent(event.getAlertId(),event.getSeverity(),event.getStatus(),event.getEventType(),event.getAgentKey());
    }

    private boolean handleAlertEvent(final Long alertId,final MAlertType.PerceivedSeverity perceivedSeverity,final MAlertType.Status alertStatus ,
                                     final AbstractEvent.EventType alertChangeType,final String sourceKey) {
        boolean updated;
        if(sourceKey==null)
        {
            return false;
        }
        switch (alertChangeType) {
            case CREATE :
                updated = addOrUpdateAlert(alertId, sourceKey,
                        perceivedSeverity);
                break;
            case UPDATE :
                if (alertStatus == MAlertType.Status.CLEARED) {
                    updated = deleteAlert(alertId,sourceKey);
                } else {
                    updated = addOrUpdateAlert(alertId,
                            sourceKey, perceivedSeverity);
                }
                break;
            case DELETE :
                updated = deleteAlert(alertId, sourceKey);
                break;
            default :
                throw new ServiceException("Unknown alert event type "
                        + alertChangeType);
        }
        return updated;
    }

    private boolean deleteAlert(final Long alertId, final String sourceKey) {
        boolean deleted = false;
        final Map<Long, MAlertType.PerceivedSeverity> alerts = alertsCache.get(sourceKey);
        if (alerts == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No cached alerts for "
                        + (sourceKey == null
                        ? "unckown source"
                        : sourceKey) + " and alertId="
                        + alertId);
            }
        } else {
            deleted = alerts.remove(alertId) != null;
        }
        // TODO Clean source whithout alerts from childToParent
        return deleted;
    }

    private boolean addOrUpdateAlert(final Long alertId,
                                     final String sourceKey, final MAlertType.PerceivedSeverity severity) {
        Map<Long, MAlertType.PerceivedSeverity> alerts = alertsCache.get(sourceKey);
        boolean updated = false;
        if (alerts == null) {
            alerts = new HashMap<Long, MAlertType.PerceivedSeverity>();
            alertsCache.put(sourceKey, alerts);
        }
        final MAlertType.PerceivedSeverity oldSeverity = alerts.get(alertId);
        if (oldSeverity == null || oldSeverity != severity) {
            alerts.put(alertId, severity);
            updated = true;
        }
        return updated;
    }


    private void notifySource(final String sourceKey) {

        final List<StatusEvent> events = new ArrayList<StatusEvent>();
        final MAlertType.PerceivedSeverity ownSeverity = getWorstSeverity(sourceKey);
        MAlertType.PerceivedSeverity newSeverity = null;
        newSeverity = ownSeverity;
        events.add(new StatusEvent(sourceKey, newSeverity));
        executeInTransaction(false, new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(
                    final TransactionStatus status) {
                eventBroadcastDispatcher.broadcast(events);
            }
        });
    }


    public MAlertType.PerceivedSeverity getWorstSeverity(final String sourceKey) {
        final Map<Long, MAlertType.PerceivedSeverity> alerts = new HashMap<Long, MAlertType.PerceivedSeverity>();
        addPropagatedSeverities(sourceKey, alerts);
        if (alerts.isEmpty()) {
            return null;
        } else {
            final TreeSet<MAlertType.PerceivedSeverity> values = new TreeSet<MAlertType.PerceivedSeverity>(
                    MAlert.SEVERITY_DESC_COMPARATOR);
            values.addAll(alerts.values());
            return values.iterator().next();
        }
    }

    private void addPropagatedSeverities(final String sourceKey,
                                         final Map<Long, MAlertType.PerceivedSeverity> severities) {
        if (alertsCache.containsKey(sourceKey)) {
            severities.putAll(alertsCache.get(sourceKey));
        }
    }

    private void loadAlertStatuses() {
        List<AlertDTO> activeAlerts= alertServiceDataProvider.getActiveAlerts();
        for (final AlertDTO alert : activeAlerts) {
            String agentKey = alertServiceDataProvider.getAgentKey(alert.getSourceName());
            addOrUpdateAlert(alert.getId().longValue(), agentKey,
                    alert.getPerceivedseverity());
        }
    }


}

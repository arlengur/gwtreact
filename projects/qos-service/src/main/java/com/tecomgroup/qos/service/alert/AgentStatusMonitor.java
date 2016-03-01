package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertEvent;

/**
 * Created by stroganov.d on 16.10.2015.
 */
public interface AgentStatusMonitor {
    public void sendStatusEvent(Long alertId, MAlertType.PerceivedSeverity perceivedSeverity, MAlertType.Status alertStatus,
                         AbstractEvent.EventType alertChangeType, String sourceKey);

    public void sendStatusEvent(AlertEvent event);

    public MAlertType.PerceivedSeverity getWorstSeverity(final String sourceKey);
}

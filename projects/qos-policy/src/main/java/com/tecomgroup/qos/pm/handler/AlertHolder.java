/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.pm.handler;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.pm.action.ActionHandler;
import com.tecomgroup.qos.service.PolicyManagerService;
import com.tecomgroup.qos.util.ConfigurationUtil;

import java.util.*;
/**
 * @author uvarov.m
 * 23.03.2015
 *
 */

public class AlertHolder {

    public enum AlertState {
        UNKNOWN, ACTIVE, PROCESSED
    }

    public class AlertInfo {
        private AlertState alertState;
        private Long conditionDuration;

        public AlertInfo(AlertState alertState, Long conditionDuration) {
            this.alertState = alertState;
            this.conditionDuration = conditionDuration;
        }

        public AlertState getAlertState() {
            return alertState;
        }

        public Long getConditionDuration() {
            return conditionDuration;
        }
    }

    private final MContinuousThresholdFallCondition condition;

    // "severity"=>"is_sent" map
    public final SortedMap<MAlertType.PerceivedSeverity, AlertInfo> alertStates =
            new TreeMap<MAlertType.PerceivedSeverity, AlertInfo>(
                    MAlert.SEVERITY_DESC_COMPARATOR);


    public AlertHolder(MContinuousThresholdFallCondition condition) {
        this.condition = condition;
    }

    public boolean isEmpty() {
        return alertStates.isEmpty();
    }

    public void removeSeverity(MAlertType.PerceivedSeverity severity) {
        alertStates.remove(severity);
    }

    public void addDefaultAlertServerity(MAlertType.PerceivedSeverity severity) {
        addUnknownAlertServerity(severity, 0L);
    }

    public void addProcessedAlert(MAlertType.PerceivedSeverity severity, Long duration) {
        alertStates.put(severity, new AlertInfo(AlertState.PROCESSED, duration));
    }

    public void addActiveAlert(MAlertType.PerceivedSeverity severity, Long duration) {
        alertStates.put(severity, new AlertInfo(AlertState.ACTIVE, duration));
    }
    public void addUnknownAlertServerity(MAlertType.PerceivedSeverity severity, Long duration) {
        alertStates.put(severity, new AlertInfo(AlertState.UNKNOWN, duration));
    }

    public boolean isUnknown(MAlertType.PerceivedSeverity severity) {
        if(exists(severity)) {
            return AlertState.UNKNOWN.equals(alertStates.get(severity).getAlertState());
        }
        return false;
    }

    public boolean isActive(MAlertType.PerceivedSeverity severity) {
        if(exists(severity)) {
            return AlertState.ACTIVE.equals(alertStates.get(severity).getAlertState());
        }
        return false;
    }

    public boolean isProcessed(MAlertType.PerceivedSeverity severity) {
        if(exists(severity)) {
            return AlertState.PROCESSED.equals(alertStates.get(severity).getAlertState());
        }
        return false;
    }

    public boolean exists(MAlertType.PerceivedSeverity severity) {
        return alertStates.get(severity) != null;
    }

    public  MAlertType.PerceivedSeverity findAnySeverity() {
        return alertStates.keySet().iterator().next();
    }

    public AlertState getAlertBySeverity(MAlertType.PerceivedSeverity severity){
        AlertInfo info = alertStates.get(severity);
        if (info != null) {
            return info.getAlertState();
        }
        return null;
    }

    public Long getDuration(MAlertType.PerceivedSeverity severity) {
        if(exists(severity)) {
            return alertStates.get(severity).getConditionDuration();
        }
        return null;
    }

    public MAlertType.PerceivedSeverity findWorstSeverity() {
        MAlertType.PerceivedSeverity worst = null;
        for (final MAlertType.PerceivedSeverity severity : alertStates.keySet()) {
            if (!isUnknown(severity)) {
                worst = severity;
                break;
            }
        }
        return worst;
    }

    private Map<String, Object> getDefaultAlertProperties(
            final MAlertType.PerceivedSeverity severity) {
        final Map<String, Object> alert = new HashMap<String, Object>();
        alert.put(ActionHandler.OUTPUT_PARAMETER_ALERT_SEVERITY, severity);
        // use only required properties to form settings. It is used to go to
        // Result Details page.
        final String settings = ConfigurationUtil
                .parameterIdentifierToString(
                        condition.getParameterIdentifier(),
                        true,
                        ConfigurationUtil.PROPERTY_PARAMETER_SEPARATOR_FOR_ALERT_SETTINGS);
        if (settings != null && !settings.isEmpty()) {
            alert.put(ActionHandler.OUTPUT_PARAMETER_ALERT_SETTINGS, settings);
        }

        return alert;
    }

    private Date calcAlertTimestamp(Date timestamp, Long duration) {
        Calendar c = Calendar.getInstance();
        c.setTime(timestamp);
        c.add(Calendar.SECOND, (-1) * Integer.valueOf(duration.intValue()));
        return c.getTime();
    }

    public Map<String, Object> getAlertPropertis(MAlertType.PerceivedSeverity severity,
                                                 Date timestamp, Long duration) {
        Map<String, Object> map = getDefaultAlertProperties(severity);

        map.put(PolicyManagerService.OUTPUT_PARAMETER_CURRENT_TIMESTAMP,
                calcAlertTimestamp(timestamp, duration));
        return map;
    }
}

/*
 * Copyright (C) 2015 Qligent.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertEvent;
import com.tecomgroup.qos.exception.AlertException;
import com.tecomgroup.qos.modelspace.jdbc.dao.AlertServiceDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AlertProcessor {

    private static class AlertUpdateBuilder {
        private AlertUpdateDTO update = new AlertUpdateDTO();

        public AlertUpdateBuilder alertId(BigDecimal id) {
            update.setAlert_id(id);
            return this;
        }

        public AlertUpdateBuilder dateTime(Date date) {
            update.setDatetime(date);
            return this;
        }

        public AlertUpdateBuilder type(MAlertType.UpdateType type) {
            update.setUpdatetype(type);
            return this;
        }

        public AlertUpdateBuilder user(String user) {
            update.setUser_name(user);
            return this;
        }

        public AlertUpdateBuilder field(String field) {
            update.setField(field);
            return this;
        }

        public AlertUpdateBuilder old(Object o) {
            update.setOldvalue(o);
            return this;
        }

        public AlertUpdateBuilder newly(Object n) {
            update.setNewvalue(n);
            return this;
        }

        public AlertUpdateBuilder comment(String c) {
            update.setComment(c);
            return this;
        }

        public AlertUpdateDTO build() {
            return update;
        }

    }

    private AlertServiceDao alertServiceDataProvider;

    private AlertDTO alert;
    private final MAlertIndication indication;
    private MAlertType.Status initialStatus;
    private List<AlertUpdateDTO> updates = new ArrayList<>();
    private boolean isOutdatedUpdate = false;
    private MAlertType.UpdateType oldLastUpdate = null;

    public AlertProcessor(AlertServiceDao alertServiceDataProvider, MAlertIndication indication) {
        this.alertServiceDataProvider = alertServiceDataProvider;
        this.indication = indication;
        this.alert = alertServiceDataProvider.getAlert(indication);
        if (!alertNotExist()) {
            this.initialStatus = alert.getStatus();
            this.oldLastUpdate = alert.getLastupdatetype();
        }
    }

    public List<AlertUpdateDTO> getUpdates() {
        return updates;
    }

    // Alert actions
    public AlertDTO getAlert() {
        return alert;
    }

    public boolean alertNotExist() {
        return alert == null;
    }

    public boolean alertNotDisabled() {
        return alert != null && !alert.isDisabled();
    }

    public void activateAlert() {
        alert.setStatus(MAlertType.Status.ACTIVE);
    }

    public void updateDateTime() {
        //don't update lastUpdateTime an lastUpdateType if we process outdated update.
        if(!isOutdatedUpdate) {
            alert.setLastupdatedatetime(indication.getDateTime());
        } else {
            alert.setLastupdatetype(oldLastUpdate);
        }
    }

    public void createAlert() {
        final AlertDTO alert = new AlertDTO();

        // Look for alertType
        final BigDecimal alertTypeId = alertServiceDataProvider.getAlertTypeId(indication.getAlertType().getName());
        if (alertTypeId == null) {
            throw new AlertException("AlertType "
                    + indication.getAlertType().getName() + " not found");
        }
        alert.setAlerttype_id(alertTypeId);
        alert.setAlertTypeName(indication.getAlertType().getName());

        // Look for task
        final BigDecimal sourceTaskId =
                alertServiceDataProvider.getSourceTaskId(indication.getSource().getKey());
        if (sourceTaskId == null) {
            throw new AlertException("AlertTask key "
                    + indication.getSource().getKey() + " not found");
        }
        alert.setSourceName(indication.getSource().getKey());
        alert.setSource_id(sourceTaskId);

        // Look for policy
        final BigDecimal policyId =
                alertServiceDataProvider.getPolicyId(indication.getOriginator().getKey());
        if (policyId == null) {
            throw new AlertException("Policy key "
                    + indication.getOriginator().getKey() + " not found");
        }
        alert.setOriginatorName(indication.getOriginator().getKey());
        alert.setOriginator_id(policyId);
        alert.setSettings(indication.getSettings());
        alert.setContext(indication.getContext());
        alert.setExtradata(indication.getExtraData());
        alert.setLastupdatedatetime(indication.getDateTime());
        alert.setPerceivedseverity(indication.getPerceivedSeverity());
        alert.setSpecificreason(indication.getSpecificReason());
        alert.setCreationdatetime(indication.getDateTime());
        alert.setSeveritychangedatetime(indication.getDateTime());
        alert.setDetectionvalue(indication.getDetectionValue());
        alert.setAcknowledged(false);
        alert.setStatus(MAlertType.Status.ACTIVE);
        alert.setLastupdatetype(MAlertType.UpdateType.NEW);
        alert.setAlert_count(new BigDecimal(1));
        alert.setCountsincelastack(new BigDecimal(0));
        this.alert = alert;
    }

    private void updateAlertSeverity() {
        MAlertType.UpdateType updateType = MAlertType.UpdateType.SEVERITY_DEGRADATION;
        if (alert.getPerceivedseverity()
                .less(indication.getPerceivedSeverity())) {
            updateType = MAlertType.UpdateType.SEVERITY_UPGRADE;
            // situation becomes worse. It is necessary to increment count.
            alert.getAlert_count().add(new BigDecimal(1));
        }

        alert.setLastupdatetype(updateType);
        // severity must be updated before the addition of the alert update.
        // Otherwise new alert report will be opened with old severity.
        final MAlertType.PerceivedSeverity previousSeverity = alert.getPerceivedseverity();
        alert.setPerceivedseverity(indication.getPerceivedSeverity());
        alert.setSeveritychangedatetime(indication.getDateTime());

        updates.add(
                commonUpdate()
                        .field("perceivedSeverity")
                        .old(previousSeverity)
                        .newly(indication.getPerceivedSeverity())
                        .type(alert.getLastupdatetype())
                        .build());
    }

    private void updateAlertPropertiesRelatedToAcknowledgedState() {
        if (alert.getPerceivedseverity().equals(
                indication.getPerceivedSeverity())) {
            incrementCountSinceLastAck();
        } else if (alert.getPerceivedseverity().less(
                indication.getPerceivedSeverity())) {
            updateAlertSeverity();
            // situation becomes worse. It is necessary to reset
            // acknowledged state and countSinceLastAck.
            alert.setAcknowledged(false);
            resetCountSinceLastAck();
        } else {
            // situation becomes better. It is not necessary to increment
            // countSinceLastAck.
            updateAlertSeverity();
        }
    }

    public void updateAlert(final MAlertIndication indication) {
        boolean updateAlert = false;

        if (!alert.getSpecificreason().equals(
                indication.getSpecificReason())) {
            updateSpecificReason();
            updateAlert = true;
        }

        if ((alert.getContext() == null && indication.getContext() != null)
                || (alert.getContext() != null && !alert
                .getContext().equals(indication.getContext()))) {
            updateContext();
            updateAlert = true;
        }

        if ((alert.getExtradata() == null && indication.getExtraData() != null)
                || (alert.getExtradata() != null && !alert
                .getExtradata().equals(indication.getExtraData()))) {

            updateExtraData();
            updateAlert = true;
        }

        if ((alert.getDetectionvalue() == 0.0 && indication
                .getDetectionValue() != null)
                || (alert.getDetectionvalue() != 0.0 && !alert
                .getDetectionvalue().equals(
                        indication.getDetectionValue().doubleValue()))) {
            updateDetectionValue();
            updateAlert = true;
        }

        if (MAlertType.Status.CLEARED.equals(alert.getStatus())) {
            alert.setLastupdatetype(MAlertType.UpdateType.NEW);
            alert.setCreationdatetime(indication.getDateTime());
            alert.setSeveritychangedatetime(alert.getCreationdatetime());
            alert.setPerceivedseverity(indication.getPerceivedSeverity());

            resetCount(indication);
        } else {
            if (updateAlert) {
                alert.setLastupdatetype(MAlertType.UpdateType.UPDATE);
            } else {
                alert.setLastupdatetype(MAlertType.UpdateType.REPEAT);
            }

            if (alert.isAcknowledged()) {
                updateAlertPropertiesRelatedToAcknowledgedState();
            } else if (alert.getPerceivedseverity().equals(
                    indication.getPerceivedSeverity())) {
                if (updateAlert) {
                    alert.getAlert_count().add(new BigDecimal(1));
                } else {
                    incrementCount();
                }
            } else {
                updateAlertSeverity();
            }
        }
    }

    public void resetAlertProperties() {
        alert.setStatus(MAlertType.Status.CLEARED);
        alert.setCleareddatetime(indication.getDateTime());
        alert.setLastupdatetype(indication.getIndicationType());

        updateDateTime();

        // reset acknowledged state
        if (alert.isAcknowledged()) {
            alert.setAcknowledged(false);
            alert.setCountsincelastack(new BigDecimal(0));
        }

    }

    public void activateOrMarkAlertAsOutdated() {
        if(alert.getCleareddatetime() != null &&
                alert.getCleareddatetime().after(indication.getDateTime())) {
            isOutdatedUpdate = true;
        } else {
            alert.setStatus(MAlertType.Status.ACTIVE);
        }
    }

    //Events
    public AlertEvent.EventType getEventType() {
        if(!isOutdatedUpdate) {
            if (initialStatus.equals(MAlertType.Status.CLEARED)) {
                return AbstractEvent.EventType.CREATE;
            } else {
                return AbstractEvent.EventType.UPDATE;
            }
        }else{
            return AbstractEvent.EventType.CREATE;
        }
    }

    //Updates
    public void updateUpdates() {
        for (AlertUpdateDTO update: this.updates) {
            update.setAlert_id(alert.getId());
        }
    }
    private AlertUpdateBuilder commonUpdate() {
        return new AlertUpdateBuilder()
                .alertId(alert.getId())
                .dateTime(indication.getDateTime())
                .type(MAlertType.UpdateType.UPDATE)
                .user(indication.getOriginator().getKey())
                .comment(null);
    }

    public void resetCount(final MAlertIndication indication) {
        updates.add(new AlertUpdateBuilder()
                .alertId(alert.getId())
                .dateTime(indication.getDateTime())
                .type(alert.getLastupdatetype())
                .user(alert.getOriginatorName())
                .field("count")
                .old(alert.getAlert_count())
                .newly(1)
                .comment(null)
                .build());
        alert.setAlert_count(new BigDecimal(1));
    }

    private void incrementCount() {
        BigDecimal newValue = new BigDecimal(alert.getAlert_count().longValue() + 1);

        updates.add(
                commonUpdate()
                        .type(alert.getLastupdatetype())
                        .field("count")
                        .old(alert.getAlert_count())
                        .newly(newValue)
                        .build());

        alert.setAlert_count(newValue);
    }

    private void updateSpecificReason() {
        updates.add(
                commonUpdate()
                        .field("specificReason")
                        .old(alert.getSpecificreason())
                        .newly(indication.getSpecificReason())
                        .build());
        alert.setSpecificreason(indication.getSpecificReason());
    }

    private void updateContext() {
        updates.add(
                commonUpdate()
                        .field("specificReason")
                        .old(alert.getContext())
                        .newly(indication.getContext())
                        .build());
        alert.setContext(indication.getContext());
    }

    private void updateExtraData() {
        updates.add(
                commonUpdate()
                        .field("extraData")
                        .old(alert.getExtradata())
                        .newly(indication.getExtraData())
                        .build());
        alert.setExtradata(indication.getExtraData());
    }

    private void updateDetectionValue() {
        updates.add(
                commonUpdate()
                        .field("detectionValue")
                        .old(alert.getDetectionvalue())
                        .newly(indication.getDetectionValue())
                        .build());
        alert.setDetectionvalue(indication.getDetectionValue());
    }

    private void incrementCountSinceLastAck() {
        BigDecimal newValue = new BigDecimal(alert.getCountsincelastack().longValue() + 1);

        updates.add(
                commonUpdate()
                        .type(alert.getLastupdatetype())
                        .field("countSinceLastAck")
                        .old(alert.getCountsincelastack())
                        .newly(newValue)
                        .build());

        alert.setCountsincelastack(newValue);
    }

    private void resetCountSinceLastAck() {
        updates.add(
                commonUpdate()
                        .field("countSinceLastAck")
                        .old(alert.getCountsincelastack())
                        .newly(new BigDecimal(0))
                        .type(MAlertType.UpdateType.UNACK)
                        .build());

        alert.setCountsincelastack(new BigDecimal(0));
    }

    public void clearAlertUpdate() {
        updates.add(
                commonUpdate()
                        .field(null)
                        .old(null)
                        .newly(null)
                        .type(alert.getLastupdatetype())
                        .build());

    }

    public String toString() {
        if(indication != null) {
            StringBuilder result = new StringBuilder("source: '");
            result.append(indication.getSource().getKey());
            result.append("' originator:'");
            result.append(indication.getOriginator().getKey());
            result.append("' type:'");
            result.append(indication.getIndicationType()!=null?indication.getIndicationType().name():"null");
            result.append("' date:'");
            result.append(indication.getDateTime());
            result.append("'");
            return result.toString();
        }
        return null;
    }
}

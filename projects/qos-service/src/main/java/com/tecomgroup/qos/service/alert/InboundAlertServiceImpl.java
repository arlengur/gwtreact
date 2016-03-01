/*
 * Copyright (C) 2015 Qligent.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.modelspace.jdbc.dao.AlertServiceDao;
import com.tecomgroup.qos.modelspace.jdbc.dao.recording.RecordingSchedulerServiceDao;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.AlertService;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

@Transactional(readOnly = true)
public class InboundAlertServiceImpl extends AbstractService implements InboundAlertService {

    private File alertTypesFile;

    @Autowired
    private AlertServiceDao alertServiceDataProvider;

    @Autowired
    RecordingSchedulerServiceDao recordingServiceDataProvider;

    @Autowired
    private AgentStatusMonitor statusMonitor;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final static Logger LOGGER = Logger
            .getLogger(InboundAlertServiceImpl.class);

    public void setAlertTypesFile(File alertTypesFile) {
        this.alertTypesFile = alertTypesFile;
    }

    @Transactional(readOnly = false)
    public AlertDTO activateAlert(final MAlertIndication indication) {
        AlertProcessor p = new AlertProcessor(alertServiceDataProvider, indication);
        final AlertEvent.EventType eventType;
        if (p.alertNotExist()) {
            p.createAlert();
            p.resetCount(indication);
            p.updateDateTime();
            eventType = AbstractEvent.EventType.CREATE;
            alertServiceDataProvider.insertAlert(p.getAlert());
            p.updateUpdates();
            LOGGER.info("activateAlert: alert created: " + p.toString() );
        } else {
            p.updateAlert(indication);
            p.activateOrMarkAlertAsOutdated();
            p.updateDateTime();
            eventType = p.getEventType();
            alertServiceDataProvider.updateAlert(p.getAlert());
            LOGGER.info("activateAlert: alert updated: " + p.toString() );
        }
        alertServiceDataProvider.insertUpdates(p.getUpdates());
        riseAlertReports(p);

        Long alertId = p.getAlert().getIdLongValue();
        String agentKey = alertServiceDataProvider.getAgentKey(p.getAlert().getSourceName());
        eventBroadcastDispatcher.broadcast(Arrays.asList(new AlertEvent(eventType, alertId, agentKey,
                p.getAlert().getPerceivedseverity(),p.getAlert().getStatus())));
        eventBroadcastDispatcher.broadcast(Arrays
                .asList(new ActivateAlertEvent(eventType,
                        p.getAlert().getIdLongValue(),
                        p.getAlert().getPerceivedseverity(), agentKey)));
        statusMonitor.sendStatusEvent(alertId, p.getAlert().getPerceivedseverity(), MAlertType.Status.ACTIVE, eventType, agentKey);
        return p.getAlert();
    }

    private void notifyAlertReportOpenClose(AbstractEvent.EventType type, AlertProcessor p, Date date,Long reportId) {
        String recordingRelatedTask = recordingServiceDataProvider.getRelatedRecordingTask(p.getAlert().getSourceName());

        if(recordingRelatedTask != null) {
            String agentKey = alertServiceDataProvider.getAgentKey(p.getAlert().getSourceName());
            internalEventBroadcaster.broadcast(Arrays
                    .asList(new AlertReportEvent(type,
                            p.getAlert().getId().longValue(),
                            new DateTime(date),
                            agentKey,
                            recordingRelatedTask,
                            reportId)));
        }
    }

    private void riseAlertReports(AlertProcessor p) {
        for (AlertUpdateDTO update : p.getUpdates()) {
            MAlertType.UpdateType type = update.getUpdatetype();

            if (type.isSeverityChanged() || type.isCleared()) {
                Long reportId=alertServiceDataProvider.closeAlertReport(update.getAlert_id(), update.getDatetime());
                if ( reportId==null || reportId < 1) {
                    LOGGER.error("Can't close alert report alertId: " + update.getAlert_id()
                            + " endDate:" + update.getDatetime());
                } else {
                    notifyAlertReportOpenClose(AbstractEvent.EventType.DELETE, p, update.getDatetime(),reportId);
                }
            }

            // add new alert report
            if (type == MAlertType.UpdateType.NEW || type.isSeverityChanged()) {
                Long reportId=alertServiceDataProvider.openAlertReport(update.getAlert_id(),
                        null, p.getAlert().getPerceivedseverity(), update.getDatetime());
                if(reportId!=null && reportId > 0)
                {
                    notifyAlertReportOpenClose(AbstractEvent.EventType.CREATE, p, update.getDatetime(),reportId);
                }
            }

            eventBroadcastDispatcher.broadcast(Arrays.asList(new AlertUpdateEvent(
                    AbstractEvent.EventType.CREATE, p.getAlert().getIdLongValue(), type)));
        }
    }

    @Transactional(readOnly = false)
    public AlertDTO clearAlert(final MAlertIndication indication) {
        AlertProcessor p = new AlertProcessor(alertServiceDataProvider, indication);
        if (p.alertNotDisabled()) {
            p.resetAlertProperties();
            p.clearAlertUpdate();
            alertServiceDataProvider.updateAlert(p.getAlert());
            LOGGER.info("clearAlert: alert cleared: " + p.toString() );

            alertServiceDataProvider.insertUpdates(p.getUpdates());
            riseAlertReports(p);
            Long alertId = p.getAlert().getIdLongValue();
            String agentKey = alertServiceDataProvider.getAgentKey(p.getAlert().getSourceName());
            eventBroadcastDispatcher.broadcast(Arrays.asList(
                    new AlertEvent(AbstractEvent.EventType.UPDATE, alertId, agentKey,
                            p.getAlert().getPerceivedseverity(),p.getAlert().getStatus())));
            statusMonitor.sendStatusEvent(alertId, p.getAlert().getPerceivedseverity(), MAlertType.Status.CLEARED, AbstractEvent.EventType.UPDATE, agentKey);
            return p.getAlert();
        }
        return null;
    }

    public void init() {
        if (alertTypesFile != null && alertTypesFile.exists()) {
            initialize();
        }
    }

    @Transactional(readOnly = false)
    private void initialize() {
        try {
            final AlertService.AlertTypesConfiguration configuration = jsonMapper.readValue(
                    alertTypesFile, AlertService.AlertTypesConfiguration.class);
            alertServiceDataProvider.
                    registerAlertTypes(configuration.getTypes());

        } catch (final Exception ex) {
            LOGGER.error("Cannot load default alert types from "
                    + alertTypesFile, ex);
        }
    }

    public void setAlertServiceDataProvider(AlertServiceDao alertServiceDataProvider) {
        this.alertServiceDataProvider = alertServiceDataProvider;
    }

    public void setRecordingServiceDataProvider(RecordingSchedulerServiceDao recordingServiceDataProvider) {
        this.recordingServiceDataProvider = recordingServiceDataProvider;
    }

    public AgentStatusMonitor getStatusMonitor() {
        return statusMonitor;
    }

    public void setStatusMonitor(AgentStatusMonitor statusMonitor) {
        this.statusMonitor = statusMonitor;
    }
}


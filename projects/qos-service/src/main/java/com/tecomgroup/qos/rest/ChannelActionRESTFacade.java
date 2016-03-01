package com.tecomgroup.qos.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.rest.data.Comment;
import com.tecomgroup.qos.service.AlertService;
import com.tecomgroup.qos.service.alert.AlertHistoryService;
import com.tecomgroup.qos.util.AuditLogger;

/**
 * @author galin.a
 */
@Path("channel/action")
@Component
public class ChannelActionRESTFacade {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertHistoryService alertHistoryService;

    @Qualifier("channelViewService")
    @Autowired
    private ChannelService channelViewService;

    @PUT
    @Path("acknowledge/{alertId}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.TEXT_PLAIN+"; charset=UTF-8"})
    public Map acknowledgeAlert(@PathParam("alertId") Long alertId, String comment) {
        try {
            MAlert alert = alertService.getAlert(alertId);
            MAlertIndication indication = new MAlertIndication(alert, new Date(), MAlertType.UpdateType.ACK);
            alert = alertService.acknowledgeAlert(indication, comment, channelViewService.getCurrentUser().getUsername());
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Acknowledge alert  : {}"
                    , alertId.toString());
            return getAlertParams(alert);
        }catch (Exception e)
        {
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to acknowledge alert  : {}, reason : {} "
                    , alertId.toString(), e.getMessage());
            throw e;
        }
    }

    @PUT
    @Path("unacknowledge/{alertId}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.TEXT_PLAIN+"; charset=UTF-8"})
    public Map unAcknowledgeAlert(@PathParam("alertId") Long alertId, String comment) {
        try {
            MAlert alert = alertService.getAlert(alertId);
            MAlertIndication indication = new MAlertIndication(alert, new Date(), MAlertType.UpdateType.UNACK);
            alert = alertService.unAcknowledgeAlert(indication, comment, channelViewService.getCurrentUser().getUsername());
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Unacknowle alert  : {}"
                    , alertId.toString());
            return getAlertParams(alert);
        }catch (Exception e)
        {
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to unacknowledge alert  : {}, reason : {} "
                    , alertId.toString(),e.getMessage());
            throw e;
        }
    }

    @PUT
    @Path("clear/{alertId}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.TEXT_PLAIN+"; charset=UTF-8"})
    public Map clearAlert(@PathParam("alertId") Long alertId, String comment) {
        try {
            MAlert alert = alertService.getAlert(alertId);
            MAlertIndication indication = new MAlertIndication(alert, new Date(), MAlertType.UpdateType.OPERATOR_CLEARED);
            alert = alertService.clearAlert(indication, comment, channelViewService.getCurrentUser().getUsername());
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Clear alert  : {}"
                    ,alertId.toString());
            return getAlertParams(alert);
        }catch (Exception e)
        {
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to clear alert  : {}, reason : {} "
                    , alertId.toString(), e.getMessage());
            throw e;
        }
    }

    @PUT
    @Path("comment/{alertId}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.TEXT_PLAIN+"; charset=UTF-8"})
    public Map commentAlert(@PathParam("alertId") Long alertId, String comment) {
        try {
            MAlert alert = alertService.getAlert(alertId);
            MAlertIndication indication = new MAlertIndication(alert, new Date(), MAlertType.UpdateType.COMMENT);
            alert = alertService.commentAlert(indication, comment, channelViewService.getCurrentUser().getUsername());
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.OK, "Comment alert  : {}"
                    ,alertId.toString());
            return getAlertParams(alert);
        }catch (Exception e)
        {
            AuditLogger.warning(AuditLogger.SyslogCategory.ALERT, AuditLogger.SyslogActionStatus.NOK, "Unable to comment alert  : {}, reason : {} "
                    , alertId.toString(), e.getMessage());
            throw e;
        }
    }

    private Map<String, String> getAlertParams(MAlert alert) {
        Map<String, String> params = new HashMap<>();
        params.put("acknowledged", alert.isAcknowledged().toString());
        params.put("cleared", Long.toString(alert.getClearedDateTime().getTime()));
        params.put("updated", Long.toString(alert.getLastUpdateDateTime().getTime()));
        params.put("lastupdatetype", alert.getLastUpdateType().toString());
        params.put("status", alert.getStatus().toString());
        return params;
    }

    @GET
    @Path("comments/{alertId}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.TEXT_PLAIN+"; charset=UTF-8"})
    public List<Comment> getAlertComments(@PathParam("alertId") Long alertId) {
        return alertHistoryService.getAlertReportComments(alertId);
    }
}
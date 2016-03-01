package com.tecomgroup.qos.domain;

import com.tecomgroup.qos.exception.DomainModelException;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by uvarov.m on 06.07.2015.
 */
public class AlertDTO {
    private BigDecimal id;
    private String context;
    private String extradata;
    private MAlertType.PerceivedSeverity perceivedseverity;
    private String settings;
    private MAlertType.SpecificReason specificreason;
    private BigDecimal alerttype_id;
    private String alertTypeName;
    private boolean acknowledged;
    private BigDecimal alert_count;
    private BigDecimal countsincelastack;
    private Date creationdatetime;
    private Date cleareddatetime;
    private Date lastupdatedatetime;
    private Date severitychangedatetime;
    private Date acknowledgmentdatetime;
    private boolean disabled;
    private MAlertType.UpdateType lastupdatetype;
    private MAlertType.Status status;
    private BigDecimal originator_id;
    private String originatorName;
    private BigDecimal source_id;
    private String sourceName;
    private Double detectionvalue;

    public String getAlertTypeName() {
        return alertTypeName;
    }

    public void setAlertTypeName(String alertTypeName) {
        this.alertTypeName = alertTypeName;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public BigDecimal getId() {
        return id;
    }

    public Long getIdLongValue() {
        return id.longValue();
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getExtradata() {
        return extradata;
    }

    public void setExtradata(String extradata) {
        this.extradata = extradata;
    }

    public MAlertType.PerceivedSeverity getPerceivedseverity() {
        return perceivedseverity;
    }

    public void setPerceivedseverity(MAlertType.PerceivedSeverity perceivedseverity) {
        this.perceivedseverity = perceivedseverity;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public MAlertType.SpecificReason getSpecificreason() {
        return specificreason;
    }

    public void setSpecificreason(MAlertType.SpecificReason specificreason) {
        this.specificreason = specificreason;
    }

    public BigDecimal getAlerttype_id() {
        return alerttype_id;
    }

    public void setAlerttype_id(BigDecimal alerttype_id) {
        this.alerttype_id = alerttype_id;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public BigDecimal getAlert_count() {
        return alert_count;
    }

    public void setAlert_count(BigDecimal alert_count) {
        this.alert_count = alert_count;
    }

    public BigDecimal getCountsincelastack() {
        return countsincelastack;
    }

    public void setCountsincelastack(BigDecimal countsincelastack) {
        this.countsincelastack = countsincelastack;
    }

    public Date getCreationdatetime() {
        return creationdatetime;
    }

    public void setCreationdatetime(Date creationdatetime) {
        this.creationdatetime = creationdatetime;
    }

    public Date getCleareddatetime() {
        return cleareddatetime;
    }

    public void setCleareddatetime(Date cleareddatetime) {
        this.cleareddatetime = cleareddatetime;
    }

    public Date getLastupdatedatetime() {
        return lastupdatedatetime;
    }

    public void setLastupdatedatetime(Date lastupdatedatetime) {
        this.lastupdatedatetime = lastupdatedatetime;
    }

    public Date getSeveritychangedatetime() {
        return severitychangedatetime;
    }

    public void setSeveritychangedatetime(Date severitychangedatetime) {
        this.severitychangedatetime = severitychangedatetime;
    }

    public Date getAcknowledgmentdatetime() {
        return acknowledgmentdatetime;
    }

    public void setAcknowledgmentdatetime(Date acknowledgmentdatetime) {
        this.acknowledgmentdatetime = acknowledgmentdatetime;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public MAlertType.UpdateType  getLastupdatetype() {
        return lastupdatetype;
    }

    public void setLastupdatetype(MAlertType.UpdateType  lastupdatetype) {
        this.lastupdatetype = lastupdatetype;
    }

    public MAlertType.Status getStatus() {
        return status;
    }

    public void setStatus(MAlertType.Status status) {
        this.status = status;
    }

    public BigDecimal getOriginator_id() {
        return originator_id;
    }

    public void setOriginator_id(BigDecimal originator_id) {
        this.originator_id = originator_id;
    }

    public BigDecimal getSource_id() {
        return source_id;
    }

    public void setSource_id(BigDecimal source_id) {
        this.source_id = source_id;
    }

    public Double getDetectionvalue() {
        return detectionvalue;
    }

    public void setDetectionvalue(Double detectionvalue) {
        this.detectionvalue = detectionvalue;
    }

    public Long getDuration() {
        return MAlert.getDuration(status, severitychangedatetime,
                cleareddatetime);
    }
    /**
     * Calculates duration depending on {@link com.tecomgroup.qos.domain.MAlertType.Status}.
     *
     * @param status
     * @param severityChangeDateTime
     * @param clearedDateTime
     * @return
     */
    private static Long getDuration(final MAlertType.Status status,
                                   final Date severityChangeDateTime, final Date clearedDateTime) {
        final Long duration;
        switch (status) {
            case ACTIVE :
                duration = System.currentTimeMillis()
                        - severityChangeDateTime.getTime();
                break;
            case CLEARED :
                duration = clearedDateTime.getTime()
                        - severityChangeDateTime.getTime();
                break;
            default :
                throw new DomainModelException("Unsupported alert status: "
                        + status);
        }
        return duration;
    }
}

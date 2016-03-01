package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyCondition;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.Date;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType
public class AlertData {


    /**
	 * @TODO невозможно поставить Hibernate MSource в source + originator
	 */
	public long id;
	public long sourceId;
	public long originatorId;
    public String sourceName;
    public String originatorName;
	public MAlertType.UpdateType lastUpdateType;
	public MAlertType.Status status;
	public Date acknowledgeTime;
	public Date cleared;
	public Date updated;
	public Date severityChanged;
	public Date created;
	public String cause;
	public String name;
	public String description;
	public Boolean acknowledged;
	public long alertCount;
	public String duration;
	public Double detectionValue;
    public String thresholdValue;
    public String parameterId;
    public boolean active;
    public String settings;

	public AlertData() {
	}

	public AlertData(MAlert malert) {
        this.id=malert.getId();
        this.sourceId=malert.getSource()==null?null:malert.getSource().getId();
        this.sourceName=malert.getSource()==null?"":malert.getSource().getDisplayName();
        this.originatorId=malert.getOriginator()==null?null:malert.getOriginator().getId();
        this.originatorName=malert.getOriginator()==null?"":malert.getOriginator().getDisplayName();
        this.lastUpdateType=malert.getLastUpdateType();
        this.status=malert.getStatus();
        this.updated=malert.getLastUpdateDateTime();
        this.acknowledgeTime=malert.getAcknowledgmentDateTime();
        this.severityChanged=malert.getSeverityChangeDateTime();
        this.cleared=malert.getClearedDateTime();
        this.created=malert.getCreationDateTime();
        this.acknowledged=malert.isAcknowledged();
        this.alertCount=malert.getAlertCount();
        this.duration= AlertData.formatDuration(malert.getDuration());
        this.detectionValue=malert.getDetectionValue();
        this.active=MAlert.isActive(malert.getStatus());
        this.settings=malert.getSettings();

        MAlertType type=malert.getAlertType();
        if(type!=null)
        {
            this.cause=type.getProbableCause()!=null?type.getProbableCause().name():null;
            this.name=type.getDisplayName();
            this.description=type.getDescription();

        }
        if(malert.getOriginator() instanceof MPolicy) {
            final MPolicy policy = (MPolicy) malert.getOriginator();
            final MPolicyCondition condition = policy.getCondition();
            if (condition instanceof MContinuousThresholdFallCondition) {
                final MContinuousThresholdFallCondition continuousThresholFallCondition = (MContinuousThresholdFallCondition) condition;
                ConditionLevel conditionLevel = null;
                this.parameterId=continuousThresholFallCondition.getParameterIdentifier().getName();
                if (malert.getPerceivedSeverity().equals(
                        MAlertType.PerceivedSeverity.CRITICAL)) {
                    conditionLevel = continuousThresholFallCondition
                            .getCriticalLevel();
                } else if (malert.getPerceivedSeverity().equals(
                        MAlertType.PerceivedSeverity.WARNING)) {
                    conditionLevel = continuousThresholFallCondition
                            .getWarningLevel();
                }
                if(conditionLevel!=null) {
                    this.thresholdValue = conditionLevel.getRaiseLevel();
                }
            }
        }
        
    }

	public static String formatDuration(Long duration) {
		return DurationFormatUtils
				.formatDuration(duration, "d 'days' HH:mm:ss");
	}
}

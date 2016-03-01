/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.domain;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.tecomgroup.qos.domain.pm.MPolicy;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Formula;

import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.exception.DomainModelException;

/**
 * 
 * Ключевой класс системы оповещения
 * 
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"alertType_id",
		"source_id", "originator_id", "settings"}))
public class MAlert extends MAlertSharedData implements Disabled, WidgetData {

	public final static Comparator<PerceivedSeverity> SEVERITY_ASC_COMPARATOR = new Comparator<PerceivedSeverity>() {
		@Override
		public int compare(final PerceivedSeverity o1,
				final PerceivedSeverity o2) {
			if (o1.greater(o2)) {
				return 1;
			} else if (o1.less(o2)) {
				return -1;
			}
			return 0;
		}
	};

	public final static Comparator<PerceivedSeverity> SEVERITY_DESC_COMPARATOR = new Comparator<PerceivedSeverity>() {
		@Override
		public int compare(final PerceivedSeverity o1,
				final PerceivedSeverity o2) {
			if (o1.greater(o2)) {
				return -1;
			} else if (o1.less(o2)) {
				return 1;
			}
			return 0;
		}
	};

	/**
	 * Calculates duration depending on {@link Status}.
	 * 
	 * @param status
	 * @param severityChangeDateTime
	 * @param clearedDateTime
	 * @return
	 */
	public static Long getDuration(final Status status,
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
	/**
	 * Calculates alert's duration on an interval depending on {@link Status}.
	 * 
	 * @param status
	 * @param severityChangeDateTime
	 * @param clearedDateTime
	 * @param timeInterval
	 * @return
	 */
	public static Long getDurationOnInterval(final Status status,
			final Date severityChangeDateTime, final Date clearedDateTime,
			final TimeInterval timeInterval) {
		final Long duration;
		switch (status) {
			case ACTIVE :
				duration = severityChangeDateTime.after(timeInterval
						.getStartDateTime())
						? timeInterval.getEndDateTime().getTime()
								- severityChangeDateTime.getTime()
						: timeInterval.getEndDateTime().getTime()
								- timeInterval.getStartDateTime().getTime();
				break;
			case CLEARED :
				duration = severityChangeDateTime.after(timeInterval
						.getStartDateTime()) ? clearedDateTime.getTime()
						- severityChangeDateTime.getTime() : clearedDateTime
						.getTime() - timeInterval.getStartDateTime().getTime();
				break;
			default :
				throw new DomainModelException("Unsupported alert status: "
						+ status);
		}
		return duration;
	}

	public static boolean isActive(final Status status) {
		return status == Status.ACTIVE;
	}

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE})
	private MAgentTask source;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE})
	private MPolicy originator;

	/**
	 * @uml.property name="lastUpdateType"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UpdateType lastUpdateType;

	/**
	 * @uml.property name="status"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Status status;

	/**
	 * This property allows duration sorting via hql, it returns dateTime
	 * interval as string. It is not possible to convert dateTime interval to
	 * long equally for all dbs because of limited hibernate support.
	 * 
	 * TODO: Convert dateTime interval to Long or implement other solution to
	 * calculate duration as long in sql or hql.
	 */
	@JsonIgnore
	@Formula("case status when 'ACTIVE' then (now() - severityChangeDateTime) when 'CLEARED' then (clearedDateTime - severityChangeDateTime) end")
	private String duration;

	/**
	 * @uml.property name="creationDateTime"
	 */
	@Column(nullable = false)
	private Date creationDateTime;

	/**
	 * @uml.property name="acknowledgmentDateTime"
	 */
	private Date acknowledgmentDateTime;

	/**
	 * @uml.property name="clearedDateTime"
	 */
	private Date clearedDateTime;

	/**
	 * @uml.property name="acknowledged"
	 */
	@Column(nullable = false)
	private Boolean acknowledged;

	/**
	 * @uml.property name="lastUpdateDateTime"
	 */
	private Date lastUpdateDateTime;

	@Column(nullable = false)
	private Date severityChangeDateTime;

	/**
	 * @uml.property name="alertCount"
	 */
	@Column(nullable = false, name = "ALERT_COUNT")
	private Long alertCount;

	/**
	 * @uml.property name="countSinceLastAck"
	 */
	@Column(nullable = false)
	private Long countSinceLastAck;

	@Column(nullable = false)
	private boolean disabled = false;

	public MAlert() {
		super();
	}

	public MAlert(final MAlertIndication indication) {
		setAlertType(indication.getAlertType());
		setSettings(indication.getSettings());
		setContext(indication.getContext());
		setExtraData(indication.getExtraData());
		setLastUpdateType(indication.getIndicationType());
		setLastUpdateDateTime(indication.getDateTime());
		setPerceivedSeverity(indication.getPerceivedSeverity());
		setSpecificReason(indication.getSpecificReason());
	}

	/**
	 * @return the acknowledgmentDateTime
	 * @uml.property name="acknowledgmentDateTime"
	 */
	public Date getAcknowledgmentDateTime() {
		return acknowledgmentDateTime;
	}

	/**
	 * @return the alertCount
	 * @uml.property name="alertCount"
	 */
	public Long getAlertCount() {
		return alertCount;
	}

	/**
	 * @return the clearedDateTime
	 * @uml.property name="clearedDateTime"
	 */
	public Date getClearedDateTime() {
		return clearedDateTime;
	}

	/**
	 * @return the countSinceLastAck
	 * @uml.property name="countSinceLastAck"
	 */
	public Long getCountSinceLastAck() {
		return countSinceLastAck;
	}

	/**
	 * @return the creationDateTime
	 * @uml.property name="creationDateTime"
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * Calculates duration on the fly.
	 * 
	 * @see {@link #duration}
	 * 
	 * @return the duration
	 */
	@Transient
	@JsonIgnore
	public Long getDuration() {
		return MAlert.getDuration(status, severityChangeDateTime,
				clearedDateTime);
	}

	/**
	 * @return the lastUpdateDateTime
	 * @uml.property name="lastUpdateDateTime"
	 */
	public Date getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}

	/**
	 * @return the lastUpdateType
	 * @uml.property name="lastUpdateType"
	 */
	public UpdateType getLastUpdateType() {
		return lastUpdateType;
	}

	/**
	 * @return the originator
	 */
	public MSource getOriginator() {
		return originator;
	}

	@Transient
	@JsonIgnore
	public String getRelatedRecordingTaskKey() {
		String taskKey = null;
		if (source instanceof MAgentTask) {
			final MAgentTask task = (MAgentTask) source;
			final MProperty property = task
					.getProperty(MAgentTask.RELEATED_RECORDING_TASK_PROPERTY_NAME);
			if (property != null) {
				taskKey = property.getValue();
			}
		}
		return taskKey;
	}

	public Date getSeverityChangeDateTime() {
		return severityChangeDateTime;
	}

	/**
	 * @return the source
	 */
	public MSource getSource() {
		return source;
	}

	/**
	 * @return the status
	 * @uml.property name="status"
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @return the acknowledged
	 * @uml.property name="acknowledged"
	 */
	public Boolean isAcknowledged() {
		return acknowledged;
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param acked
	 *            the acknowledged to set
	 * @uml.property name="acknowledged"
	 */
	public void setAcknowledged(final Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	/**
	 * @param acknowledgment
	 *            the acknowledgmentDateTime to set
	 * @uml.property name="acknowledgmentDateTime"
	 */
	public void setAcknowledgmentDateTime(final Date acknowledgmentDateTime) {
		this.acknowledgmentDateTime = acknowledgmentDateTime;
	}

	/**
	 * @param alertCount
	 *            the alertCount to set
	 * @uml.property name="alertCount"
	 */
	public void setAlertCount(final Long count) {
		this.alertCount = count;
	}

	/**
	 * @param clearedDateTime
	 *            the clearedDateTime to set
	 * @uml.property name="clearedDateTime"
	 */
	public void setClearedDateTime(final Date clearedDateTime) {
		this.clearedDateTime = clearedDateTime;
	}

	/**
	 * @param countSinceLastAck
	 *            the countSinceLastAck to set
	 * @uml.property name="countSinceLastAck"
	 */
	public void setCountSinceLastAck(final Long countSinceLastAck) {
		this.countSinceLastAck = countSinceLastAck;
	}

	/**
	 * @param creationDateTime
	 *            the creationDateTime to set
	 * @uml.property name="creationDateTime"
	 */
	public void setCreationDateTime(final Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	@Override
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @param lastUpdateDateTime
	 *            the lastUpdateDateTime to set
	 * @uml.property name="lastUpdateDateTime"
	 */
	public void setLastUpdateDateTime(final Date lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}

	/**
	 * @param lastUpdateType
	 *            the lastUpdateType to set
	 * @uml.property name="lastUpdateType"
	 */
	public void setLastUpdateType(final UpdateType lastUpdateType) {
		this.lastUpdateType = lastUpdateType;
	}

	/**
	 * @param originator
	 *            the originator to set
	 */
	public void setOriginator(final MPolicy originator) {
		this.originator = originator;
	}

	public void setSeverityChangeDateTime(final Date severityChangeDateTime) {
		this.severityChangeDateTime = severityChangeDateTime;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final MAgentTask source) {
		this.source = source;
	}

	/**
	 * @param status
	 *            the status to set
	 * @uml.property name="status"
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "{alertType=" + getAlertType() + ", source=" + getSource()
				+ ", originator=" + getOriginator() + ", settings="
				+ getSettings() + "}";
	}
}

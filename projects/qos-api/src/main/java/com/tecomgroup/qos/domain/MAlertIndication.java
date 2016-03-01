/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;

/**
 * Indication to send from PolicyManager, Server, Agent or other system
 * components to Server to raise or clear alert.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
public class MAlertIndication extends MAlertSharedData {

	/**
	 * @uml.property name="indicationType"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UpdateType indicationType;

	/**
	 * @uml.property name="dateTime"
	 */
	@Column(nullable = false)
	private Date dateTime;

	/**
	 * @uml.property name="source"
	 */
	@Column(nullable = false)
	@Embedded
	private Source source;

	@Column(nullable = false)
	@Embedded
	private Source originator;

	public MAlertIndication() {
		super();
	}

	public MAlertIndication(final MAlert alert, final Date dateTime,
			final UpdateType indicationType) {
		setAlertType(alert.getAlertType());
		setSource(Source.getSource(alert.getSource()));
		setOriginator(Source.getSource(alert.getOriginator()));
		setSettings(alert.getSettings());
		setPerceivedSeverity(alert.getPerceivedSeverity());
		setSpecificReason(alert.getSpecificReason());
		setContext(alert.getContext());
		setExtraData(alert.getExtraData());
		setDateTime(dateTime);
		setIndicationType(indicationType);
	}

	public MAlertIndication(final MAlertIndication alertIndication,
			final Date dateTime, final UpdateType indicationType) {
		setAlertType(alertIndication.getAlertType());
		setSource(alertIndication.getSource());
		setOriginator(alertIndication.getOriginator());
		setSettings(alertIndication.getSettings());
		setPerceivedSeverity(alertIndication.getPerceivedSeverity());
		setSpecificReason(alertIndication.getSpecificReason());
		setContext(alertIndication.getContext());
		setExtraData(alertIndication.getExtraData());
		setDateTime(dateTime);
		setIndicationType(indicationType);
	}

	/**
	 * 
	 * @param alertTypeName
	 *            - required.
	 * @param source
	 *            - required.
	 * @param settings
	 *            - required.
	 * @param severity
	 *            - required.
	 * @param dateTime
	 *            - required.
	 * @param indicationType
	 *            - can be NULL.
	 * @param taskKey
	 *            - can be NULL
	 */
	public MAlertIndication(final String alertTypeName, final Source source,
			final Source originator, final String settings,
			final PerceivedSeverity severity, final Date dateTime,
			final UpdateType indiUpdateType) {
		this();
		if (alertTypeName == null) {
			throw new IllegalArgumentException("AlertTypeName can not be NULL");
		}
		if (source == null) {
			throw new IllegalArgumentException("Source can not be NULL");
		}
		if (source.getKey() == null || source.getKey().isEmpty()) {
			throw new IllegalArgumentException(
					"Source key can not be NULL or empty");
		}
		if (source.getType() == null) {
			throw new IllegalArgumentException("Source type can not be NULL");
		}
		if (originator == null) {
			throw new IllegalArgumentException("Originator can not be NULL");
		}
		if (originator.getKey() == null || originator.getKey().isEmpty()) {
			throw new IllegalArgumentException(
					"Originator key can not be NULL or empty");
		}
		if (originator.getType() == null) {
			throw new IllegalArgumentException(
					"Originator type can not be NULL");
		}
		if (severity == null) {
			throw new IllegalArgumentException("Severity can not be NULL");
		}
		if (dateTime == null) {
			throw new IllegalArgumentException("DateTime can not be NULL");
		}
		setAlertType(new MAlertType(alertTypeName));
		setSource(source);
		setOriginator(originator);
		setSettings(settings);
		setPerceivedSeverity(severity);
		setDateTime(dateTime);
		setIndicationType(indiUpdateType);
	}

	/**
	 * Getter of the property <tt>dateTime</tt>
	 * 
	 * @return Returns the dateTime.
	 * @uml.property name="dateTime"
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * Getter of the property <tt>indicationType</tt>
	 * 
	 * @return Returns the indicationType.
	 * @uml.property name="indicationType"
	 */
	public UpdateType getIndicationType() {
		return indicationType;
	}

	/**
	 * @return the originator
	 */
	public Source getOriginator() {
		return originator;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Setter of the property <tt>dateTime</tt>
	 * 
	 * @param dateTime
	 *            The dateTime to set.
	 * @uml.property name="dateTime"
	 */
	public void setDateTime(final Date dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Setter of the property <tt>indicationType</tt>
	 * 
	 * @param indicationType
	 *            The indicationType to set.
	 * @uml.property name="indicationType"
	 */
	public void setIndicationType(final UpdateType indicationType) {
		this.indicationType = indicationType;
	}

	/**
	 * @param originator
	 *            the originator to set
	 */
	public void setOriginator(final Source originator) {
		this.originator = originator;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final Source source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "[alertType = " + getAlertType() + ", settings = "
				+ getSettings() + ", updateType = " + indicationType
				+ ", source = " + getSource() + ", perceivedSeverity = "
				+ getPerceivedSeverity() + "]";
	}
}

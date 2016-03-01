/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.*;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public abstract class MAlertSharedData extends MAbstractEntity {
	/**
	 * @uml.property name="alertType"
	 */
	@OneToOne(cascade = CascadeType.ALL)
	private MAlertType alertType;

	/**
	 * @uml.property name="perceivedSeverity"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.ORDINAL)
	private PerceivedSeverity perceivedSeverity;

	private Double detectionValue;

	/**
	 * @uml.property name="specificReason"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private SpecificReason specificReason;

	/**
	 * @uml.property name="settings"
	 */
	@Column(length = 255)
	private String settings;

	/**
	 * @uml.property name="context"
	 */
	@Column(length = 255)
	private String context;

	/**
	 * @uml.property name="extraData"
	 */
	private String extraData;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the alertType.
	 * @uml.property name="alertType"
	 */
	public MAlertType getAlertType() {
		return alertType;
	}

	/**
	 * Getter of the property <tt>context</tt>
	 * 
	 * @return Returns the context.
	 * @uml.property name="context"
	 */
	public String getContext() {
		return context;
	}

	public Double getDetectionValue() {
		return detectionValue;
	}

	/**
	 * Getter of the property <tt>extraData</tt>
	 * 
	 * @return Returns the extraData.
	 * @uml.property name="extraData"
	 */
	public String getExtraData() {
		return extraData;
	}

	/**
	 * Getter of the property <tt>perceivedSeverity</tt>
	 * 
	 * @return Returns the perceivedSeverity.
	 * @uml.property name="perceivedSeverity"
	 */
	public PerceivedSeverity getPerceivedSeverity() {
		return perceivedSeverity;
	}

	/**
	 * Getter of the property <tt>settings</tt>
	 * 
	 * @return Returns the settings.
	 * @uml.property name="settings"
	 */
	public String getSettings() {
		return settings;
	}

	/**
	 * Getter of the property <tt>specificReason</tt>
	 * 
	 * @return Returns the specificReason.
	 * @uml.property name="specificReason"
	 */
	public SpecificReason getSpecificReason() {
		return specificReason;
	}

	/**
	 * Setter of the property <tt>alertType</tt>
	 * 
	 * @param name
	 *            The name to set.
	 * @uml.property name="alertType"
	 */
	public void setAlertType(final MAlertType alertType) {
		this.alertType = alertType;
	}

	/**
	 * Setter of the property <tt>context</tt>
	 * 
	 * @param context
	 *            The context to set.
	 * @uml.property name="context"
	 */
	public void setContext(final String context) {
		this.context = context;
	}

	public void setDetectionValue(final Double detectionValue) {
		this.detectionValue = detectionValue;
	}

	/**
	 * Setter of the property <tt>extraData</tt>
	 * 
	 * @param extraData
	 *            The extraData to set.
	 * @uml.property name="extraData"
	 */
	public void setExtraData(final String extraData) {
		this.extraData = extraData;
	}

	/**
	 * Setter of the property <tt>perceivedSeverity</tt>
	 * 
	 * @param perceivedSeverity
	 *            The perceivedSeverity to set.
	 * @uml.property name="perceivedSeverity"
	 */
	public void setPerceivedSeverity(final PerceivedSeverity perceivedSeverity) {
		this.perceivedSeverity = perceivedSeverity;
	}

	/**
	 * Setter of the property <tt>settings</tt>
	 * 
	 * @param settings
	 *            The settings to set.
	 * @uml.property name="settings"
	 */
	public void setSettings(final String settings) {
		this.settings = settings;
	}

	/**
	 * Setter of the property <tt>specificReason</tt>
	 * 
	 * @param specificReason
	 *            The specificReason to set.
	 * @uml.property name="specificReason"
	 */
	public void setSpecificReason(final SpecificReason specificReason) {
		this.specificReason = specificReason;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

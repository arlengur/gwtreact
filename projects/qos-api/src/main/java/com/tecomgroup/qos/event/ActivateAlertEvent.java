/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.service.AlertService;

/**
 * It is fired every time when
 * {@link AlertService#activateAlert(com.tecomgroup.qos.domain.MAlertIndication, String)}
 * method is invoked
 * 
 * @author ivlev.e
 */
public class ActivateAlertEvent extends AbstractEvent {

	private static final long serialVersionUID = 5887313566874224126L;

	private Long alertId;

	private PerceivedSeverity severity;

	private String agentKey;

	public ActivateAlertEvent() {
		super();
	}

	/**
	 * 
	 * @param eventType
	 * @param alertId
	 * @param severity
	 */
	public ActivateAlertEvent(final EventType eventType, final Long alertId,
			final PerceivedSeverity severity, String agentKey) {
		super(eventType);
		this.alertId = alertId;
		this.severity = severity;
		this.agentKey = agentKey;
	}

	/**
	 * @return the alertId
	 */
	public Long getAlertId() {
		return alertId;
	}

	/**
	 * @return the severity
	 */
	public PerceivedSeverity getSeverity() {
		return severity;
	}

	/**
	 * @param alertId
	 *            the alertId to set
	 */
	public void setAlertId(final Long alertId) {
		this.alertId = alertId;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(final PerceivedSeverity severity) {
		this.severity = severity;
	}

	public String getAgentKey() {
		return agentKey;
	}
}

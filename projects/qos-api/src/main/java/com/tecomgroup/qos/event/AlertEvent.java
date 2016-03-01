/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.dashboard.LatestAlertsWidget;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.event.AbstractEvent.HasDomainObjects;

/**
 * @author abondin
 * 
 */
public class AlertEvent extends AbstractEvent implements HasDomainObjects {

	private static final long serialVersionUID = 8615127162178271432L;

	private Long alertId;
	private String agentKey;
	private MAlertType.PerceivedSeverity severity;
	private MAlertType.Status status;

	public AlertEvent() {
		super();
	}

	public AlertEvent(final EventType type, final Long alertId, final String agentKey,final MAlertType.PerceivedSeverity severity,MAlertType.Status status) {
		super(type, LatestAlertsWidget.EVENT_SERVICE_DOMAIN_PREFIX);
		this.alertId = alertId;
		this.agentKey = agentKey;
		this.severity=severity;
		this.status=status;
	}

	public String getAgentKey() {
		return agentKey;
	}

	/**
	 * @return the alertId
	 */
	public Long getAlertId() {
		return alertId;
	}

	/**
	 * @param alert
	 *            the alert to set
	 */
	public void setAlertId(final Long alert) {
		this.alertId = alertId;
	}

	public MAlertType.PerceivedSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(MAlertType.PerceivedSeverity severity) {
		this.severity = severity;
	}

	public MAlertType.Status getStatus() {
		return status;
	}

	public void setStatus(MAlertType.Status status) {
		this.status = status;
	}
}

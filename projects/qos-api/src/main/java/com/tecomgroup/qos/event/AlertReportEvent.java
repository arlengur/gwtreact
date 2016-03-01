/*
 * Copyright (C) 2016 Qligent
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import org.joda.time.DateTime;

public class AlertReportEvent extends AbstractEvent {
	private static final long serialVersionUID = 8615127162562271432L;
	private DateTime dateTime;
	private String taskKey;
	private String agentKey;
	private Long alertId;
	private Long reportId;

	public AlertReportEvent() {
		super();
	}

	public AlertReportEvent(final EventType type,
							final Long alertId,
							final DateTime dateTime,
							final String agentKey,
							final String taskKey,
							final Long reportId) {
		super(type);
		this.alertId = alertId;
		this.dateTime = dateTime;
		this.agentKey = agentKey;
		this.taskKey = taskKey;
		this.reportId = reportId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public String getTaskKey() {
		return taskKey;
	}

	public String getAgentKey() {
		return agentKey;
	}

	public Long getAlertId() {
		return alertId;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}

	public void setAgentKey(String agentKey) {
		this.agentKey = agentKey;
	}

	public void setAlertId(Long alertId) {
		this.alertId = alertId;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
}

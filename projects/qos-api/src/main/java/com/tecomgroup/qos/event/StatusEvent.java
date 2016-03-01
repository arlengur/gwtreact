/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class StatusEvent extends AbstractEvent {
	private String sourceKey;

	private PerceivedSeverity severity;

	/**
	 * 
	 */
	public StatusEvent() {

	}

	/**
	 * @param sourceKey
	 * @param severity
	 */
	public StatusEvent(final String sourceKey, final PerceivedSeverity severity) {
		this();
		this.severity = severity;
		this.sourceKey = sourceKey;
	}

	/**
	 * @return the severity
	 */
	public PerceivedSeverity getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(final PerceivedSeverity severity) {
		this.severity = severity;
	}

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
}

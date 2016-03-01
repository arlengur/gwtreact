/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.event.AbstractEvent.EventType;

/**
 * @author kunilov.p
 * 
 */
public class UpdatePMConfiguration extends QoSMessage {

	public static UpdatePMConfiguration updatePMConfiguration(
			final String systemComponent, final PMConfiguration configuration,
			final EventType eventType) {
		return new UpdatePMConfiguration(systemComponent, configuration,
				eventType);
	}

	private String systemCompomnent;

	private PMConfiguration configuration;

	private EventType eventType;

	public UpdatePMConfiguration() {
		super();
	}

	public UpdatePMConfiguration(final String systemComponent,
			final PMConfiguration configuration, final EventType eventType) {
		this();
		this.systemCompomnent = systemComponent;
		this.configuration = configuration;
		this.eventType = eventType;
	}

	public PMConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @return the systemCompomnent
	 */
	public String getSystemCompomnent() {
		return systemCompomnent;
	}

	public void setConfiguration(final PMConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(final EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @param systemCompomnent
	 *            the systemCompomnent to set
	 */
	public void setSystemCompomnent(final String systemCompomnent) {
		this.systemCompomnent = systemCompomnent;
	}
}

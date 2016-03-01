/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import java.io.Serializable;

/**
 * 
 * Base class for all sever to client events
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractEvent implements Serializable {

	/**
	 * Type of the event
	 * 
	 * @author abondin
	 * 
	 */
	public enum EventType {
		/**
		 * При создании
		 */
		CREATE,
		/**
		 * При изменение
		 */
		UPDATE,
		/**
		 * При удалении
		 */
		DELETE
	}

	/**
	 * Show that the event contains domain object
	 * 
	 * @author abondin
	 * 
	 */
	public static interface HasDomainObjects {
	}

	private String domainPrefix;

	private EventType eventType;

	public AbstractEvent() {
		super();
	}

	public AbstractEvent(final EventType eventType) {
		this(eventType, null);
	}

	public AbstractEvent(final EventType eventType, final String domainPrefix) {
		this();
		this.eventType = eventType;
		this.domainPrefix = domainPrefix;
	}

	/**
	 * A prefix aggregates all domains (in terms of @see <a href="
	 * https://code.google.com/p/gwteventservice/">GWTEventService</a>) with
	 * same logical functions. If event publisher wants to send event to
	 * particular a receiver it should know its name. Also it can send event to
	 * all receivers which have this prefix and each domain will make decision
	 * by applying event filter if filter was assigned for this domain.
	 * 
	 * @return - string or null
	 */
	public String getDomainPrefix() {
		return domainPrefix;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(final EventType eventType) {
		this.eventType = eventType;
	}
}

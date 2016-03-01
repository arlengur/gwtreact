/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.gwt.client.event.ServerEvent;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.filter.EventFilter;

/**
 * Filters {@link Event} objects on the server side. According to
 * GWTEventService design one {@link Domain} must have one {@link EventFilter}
 * 
 * @author ivlev.e
 * 
 */
public class ServerEventFilter implements EventFilter {

	private static final long serialVersionUID = -2383839005253690614L;

	private QoSEventFilter clientEventFilter;

	public ServerEventFilter() {
	}

	public ServerEventFilter(final QoSEventFilter clientEventFilter) {
		this.clientEventFilter = clientEventFilter;
	}

	@Override
	public boolean match(final Event event) {
		if (event instanceof ServerEvent && clientEventFilter != null) {
			return !clientEventFilter.accept(((ServerEvent) event).getEvent());
		}
		return true;
	}

	public void setClientEventFilter(final QoSEventFilter clientEventFilter) {
		this.clientEventFilter = clientEventFilter;
	}
}
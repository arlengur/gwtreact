/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.gwt.client.event.ServerEvent.ServerEventHandler;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

/**
 * @author abondin
 * 
 */
public class ServerEvent extends GwtEvent<ServerEventHandler> implements Event {
	public static interface ServerEventHandler extends EventHandler {
		void onServerEvent(ServerEvent event);
	}

	private static final long serialVersionUID = 9044770497707262724L;

	public static final Domain SERVER_EVENT_DOMAIN = DomainFactory
			.getDomain("server_event_domain");

	private AbstractEvent event;

	public final static Type<ServerEventHandler> TYPE = new Type<ServerEventHandler>();

	/**
	 */
	public ServerEvent() {
		super();
	}

	/**
	 * @param event
	 */
	public ServerEvent(final AbstractEvent event) {
		this();
		this.event = event;
	}

	@Override
	protected void dispatch(final ServerEventHandler handler) {
		handler.onServerEvent(this);
	}

	@Override
	public Type<ServerEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the event
	 */
	public AbstractEvent getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(final AbstractEvent event) {
		this.event = event;
	}
}

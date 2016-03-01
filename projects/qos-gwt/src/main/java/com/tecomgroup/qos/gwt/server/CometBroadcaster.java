/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.server;

import java.util.Collection;

import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AbstractEvent.HasDomainObjects;
import com.tecomgroup.qos.event.EventBroadcaster;
import com.tecomgroup.qos.gwt.client.event.ServerEvent;
import com.tecomgroup.qos.gwt.shared.event.DefaultEventService;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;

import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.service.RemoteEventServiceServlet;
import de.novanic.eventservice.service.registry.EventRegistry;
import de.novanic.eventservice.service.registry.EventRegistryFactory;

/**
 * @author abondin
 * 
 */
@Service("cometBroadcaster")
public class CometBroadcaster extends RemoteEventServiceServlet
		implements
			EventBroadcaster {
	private static final long serialVersionUID = 2990350461012516428L;

	private void addEvent(final Domain domain, final AbstractEvent event) {
		addEvent(domain, new ServerEvent(event));
	}

	@Override
	public void broadcast(final Collection<? extends AbstractEvent> events) {
		final EventRegistry eventRegistry = EventRegistryFactory.getInstance()
				.getEventRegistry();
		for (final AbstractEvent event : events) {
			if (event instanceof HasDomainObjects) {
				HibernateEntityConverter.convertHibernateCollections(event,
						PersistentCollection.class);
			}
			final String domainPrefix = event.getDomainPrefix();
			if (domainPrefix != null) {
				for (final Domain domain : eventRegistry.getListenDomains()) {
					if (domain.getName().startsWith(domainPrefix)) {
						addEvent(domain, event);
					}
				}
			} else {
				addEvent(DefaultEventService.getDomain(event.getClass()), event);
			}
		}
	}

}

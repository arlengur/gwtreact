/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author novohatskiy.r
 * 
 */
@Component
public class HibernateEventWiring {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SaveOrUpdateEventListener[] saveOrUpdateListeners;

	@PostConstruct
	public void registerListeners() {
		final EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory)
				.getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.SAVE_UPDATE).prependListeners(
				saveOrUpdateListeners);
	}

}
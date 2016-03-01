/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.EventBroadcaster;
import com.tecomgroup.qos.event.TransactionalEventBroadcaster;

/**
 * 
 * Find all {@link EventBroadcaster} in the system and use them to broadcast
 * events
 * 
 * @author abondin
 * 
 * @TODO Inject me in {@link TransactionManager}
 */
@Service("eventBroadcastDispatcher")
public class EventBroadcastDispatcher
		implements
			BeanFactoryAware,
			InitializingBean,
			TransactionalEventBroadcaster {

	@Autowired
	private SessionFactory sessionFactory;

	private final Set<EventBroadcaster> eventBroadcasters = new HashSet<EventBroadcaster>();

	private BeanFactory beanFactory;

	private final Map<Object, List<AbstractEvent>> activeEvents = Collections
			.synchronizedMap(new LinkedHashMap<Object, List<AbstractEvent>>());

	@Override
	public void afterPropertiesSet() throws Exception {
		eventBroadcasters.addAll(((ListableBeanFactory) beanFactory)
				.getBeansOfType(EventBroadcaster.class).values());
		eventBroadcasters.remove(this);
	}

	@Override
	public void broadcast(final Collection<? extends AbstractEvent> events) {
		List<? extends AbstractEvent> eventsCopy;
		synchronized (events) {
			eventsCopy = new ArrayList<AbstractEvent>(events);
		}
		final Transaction transaction = sessionFactory.getCurrentSession()
				.getTransaction();
		List<AbstractEvent> transactionEvents = activeEvents.get(transaction);
		if (transactionEvents == null) {
			transactionEvents = new LinkedList<AbstractEvent>();
			activeEvents.put(transaction, transactionEvents);
		}
		transactionEvents.addAll(eventsCopy);
	}

	@Override
	public void broadcastWithoutTransaction(
			final Collection<? extends AbstractEvent> events) {
		send(events);
	}

	@Override
	public void commit(final Object transaction) {
		final List<AbstractEvent> transactionEvents = activeEvents
				.remove(transaction);
		send(transactionEvents);

	}

	@Override
	public void rollback(final Object transaction) {
		final List<AbstractEvent> transactionEvents = activeEvents
				.remove(transaction);
		if (transactionEvents != null) {
			transactionEvents.clear();
		}
	}

	private void send(final Collection<? extends AbstractEvent> events) {
		if (events != null) {
			List<? extends AbstractEvent> eventsCopy;
			synchronized (events) {
				eventsCopy = new ArrayList<AbstractEvent>(events);
			}
			for (final EventBroadcaster broadcaster : eventBroadcasters) {
				broadcaster.broadcast(eventsCopy);
			}
			eventsCopy.clear();
		}

	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory)
			throws BeansException {
		this.beanFactory = beanFactory;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.QoSEventListener;

/**
 * @author abondin
 * 
 */
@Service
public class DefaultInternalBroadcaster implements InternalEventBroadcaster {

	@Autowired(required = false)
	@Qualifier("qosInternalBroadcasterTaskExecutor")
	private TaskExecutor taskExecutor;

	private final Map<QoSEventListener, QoSEventFilter> listeners = new HashMap<QoSEventListener, QoSEventFilter>();

	@Override
	public void broadcast(final Collection<? extends AbstractEvent> events) {
		synchronized (listeners) {
			for (final Map.Entry<QoSEventListener, QoSEventFilter> entry : listeners
					.entrySet()) {
				final List<AbstractEvent> filteredEvents = new ArrayList<AbstractEvent>();
				for (final AbstractEvent event : events) {
					if (entry.getValue().accept(event)) {
						filteredEvents.add(event);
					}
				}
				if (!filteredEvents.isEmpty()) {
					taskExecutor.execute(new Runnable() {
						@Override
						public void run() {
							send(entry.getKey(), filteredEvents);
						}
					});
				}

			}

		}
	}

	private void send(final QoSEventListener listener,
			final Collection<? extends AbstractEvent> events) {
		for (final AbstractEvent event : events) {
			listener.onServerEvent(event);
		}
	}
	@Override
	public void subscribe(final QoSEventListener listener,
			final QoSEventFilter filter) {
		synchronized (listeners) {
			listeners.put(listener, filter);
		}
	}
	@Override
	public void unsubscribe(final QoSEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
}

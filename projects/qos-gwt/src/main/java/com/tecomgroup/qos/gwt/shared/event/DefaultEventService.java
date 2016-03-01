/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.gwt.client.event.ServerEvent;
import com.tecomgroup.qos.gwt.shared.event.filter.ServerEventFilter;

import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * Wrapper on RemoteEventService
 * 
 * @author abondin
 * 
 */
public class DefaultEventService implements QoSEventService {

	/**
	 * Listener for {@link ServerEvent}. It is applied on the client side.
	 * 
	 * @author abondin
	 * 
	 */
	protected class ClientListener implements RemoteEventListener {

		private final QoSEventListener eventListener;

		public ClientListener(final QoSEventListener eventListener) {
			super();
			this.eventListener = eventListener;
			listeners.put(eventListener, this);
		}
		@Override
		public void apply(final Event anEvent) {
			if (anEvent instanceof ServerEvent) {
				eventListener.onServerEvent(((ServerEvent) anEvent).getEvent());
			}

		}
	}

	private final static Map<String, Domain> domains = new HashMap<String, Domain>();

	public static Domain getDomain(final Class<? extends AbstractEvent> type) {
		if (type == null) {
			return ServerEvent.SERVER_EVENT_DOMAIN;
		}
		return getDomain(type.getName());
	}

	private static Domain getDomain(final String domainName) {
		synchronized (domains) {
			Domain domain = domains.get(domainName);
			if (domain == null) {
				domain = DomainFactory.getDomain(domainName);
				domains.put(domainName, domain);
			}
			return domain;
		}
	}

	public static Domain getDomain(final String prefix, final String uniqueKey) {
		if (prefix == null || uniqueKey == null) {
			throw new NullPointerException("Arguments cannot be null");
		}
		return getDomain(prefix + uniqueKey);
	}

	private final RemoteEventService remoteEventService;

	private final Map<QoSEventListener, ClientListener> listeners = new HashMap<QoSEventListener, ClientListener>();

	public DefaultEventService() {
		remoteEventService = RemoteEventServiceFactory.getInstance()
				.getRemoteEventService();
	}

	private void doUnsubscribe(final Class<? extends AbstractEvent> type,
			final QoSEventListener listener, final AsyncCallback<Void> callback) {
		final ClientListener clientListener = listeners.remove(listener);

		if (clientListener != null) {
			final Domain domain = DefaultEventService.getDomain(type);
			if (callback != null) {
				remoteEventService.removeListener(domain, clientListener,
						callback);
			} else {
				remoteEventService.removeListener(domain, clientListener);
			}

		}
	}

	@Override
	public void subscribe(final Class<? extends AbstractEvent> type,
			final QoSEventListener listener, final QoSEventFilter filter) {
		final ClientListener clientListener = new ClientListener(listener);
		remoteEventService.addListener(DefaultEventService.getDomain(type),
				clientListener, new ServerEventFilter(filter));
	}

	@Override
	public void subscribe(final String domainPrefix, final String uniqueKey,
			final QoSEventListener listener, final QoSEventFilter filter) {
		final ClientListener clientListener = new ClientListener(listener);
		remoteEventService.addListener(
				DefaultEventService.getDomain(domainPrefix, uniqueKey),
				clientListener, new ServerEventFilter(filter));
	}

	@Override
	public void unsubscribe(final Class<? extends AbstractEvent> type,
			final QoSEventListener listener) {
		doUnsubscribe(type, listener, null);
	}

	@Override
	public void unsubscribe(final Class<? extends AbstractEvent> type,
			final QoSEventListener listener, final AsyncCallback<Void> callback) {
		doUnsubscribe(type, listener, callback);
	}

	@Override
	public void unsubscribe(final String domainPrefix, final String uniqueKey,
			final QoSEventListener listener) {
		final ClientListener clientListener = listeners.remove(listener);
		if (clientListener != null) {
			remoteEventService.removeListener(
					DefaultEventService.getDomain(domainPrefix, uniqueKey),
					clientListener);
		}
	}

}

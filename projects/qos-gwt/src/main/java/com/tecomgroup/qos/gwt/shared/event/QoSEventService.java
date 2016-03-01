/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.gwt.shared.event.filter.ServerEventFilter;

import de.novanic.eventservice.client.event.domain.Domain;

/**
 * 
 * Subscribe to events from the server
 * 
 * @author abondin
 * 
 */
public interface QoSEventService {

	/**
	 * Subscribes a listener (living on the client side) to events using a
	 * filter (living on the server side). Method uses an event type as key for
	 * {@link Domain} name.
	 * 
	 * NOTE: one domain must have no more than one filter
	 * 
	 * @param type
	 *            - an event type for computing {@link Domain}
	 * @param listener
	 *            - an instance of {@link QoSEventListener}, typically a
	 *            presenter in MVP paradigm
	 * @param filter
	 *            - a filter instance. See {@link ServerEventFilter} and
	 *            {@link QoSEventFilter} for details
	 */
	void subscribe(Class<? extends AbstractEvent> type,
			QoSEventListener listener, QoSEventFilter filter);

	/**
	 * Subscribes a listener (living on the client side) to events using a
	 * filter (living on the server side). Method uses the a compound name
	 * (domainPrefix + uniqueKey) for the {@link Domain} definition.
	 * 
	 * NOTE: one domain must have no more than one filter
	 * 
	 * @param domainPrefix
	 *            - a string constant
	 * @param uniqueKey
	 *            - unique key of an event receiver
	 * @param listener
	 *            - an instance of {@link QoSEventListener}, typically a
	 *            presenter in MVP paradigm
	 * @param filter
	 *            - a filter instance. See {@link ServerEventFilter} and
	 *            {@link QoSEventFilter} for details
	 */
	void subscribe(String domainPrefix, String uniqueKey,
			QoSEventListener listener, QoSEventFilter filter);

	/**
	 * Unsubscribes a listener from {@link Domain} by an event type
	 * 
	 * @param type
	 * @param listener
	 */
	void unsubscribe(Class<? extends AbstractEvent> type,
			QoSEventListener listener);

	/**
	 * @see QoSEventService#unsubscribe(Class, QoSEventListener)
	 * @param type
	 * @param listener
	 * @param callback
	 */
	void unsubscribe(Class<? extends AbstractEvent> type,
			QoSEventListener listener, AsyncCallback<Void> callback);

	/**
	 * Unsubscribes a listener from {@link Domain}. Method uses a compound name
	 * (domainPrefix + uniqueKey) for the domain definition.
	 * 
	 * @param domainPrefix
	 * @param uniqueKey
	 * @param listener
	 */
	void unsubscribe(String domainPrefix, String uniqueKey,
			QoSEventListener listener);

}

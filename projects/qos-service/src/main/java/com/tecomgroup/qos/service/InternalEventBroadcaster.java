/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.event.EventBroadcaster;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.QoSEventListener;

/**
 * 
 * Сервис для раасылки событий в рамках сервера
 * 
 * @author abondin
 * 
 */
public interface InternalEventBroadcaster extends EventBroadcaster {

	/**
	 * 
	 * @param listener
	 * @param filter
	 */
	public void subscribe(QoSEventListener listener, QoSEventFilter filter);

	public void unsubscribe(QoSEventListener listener);

}

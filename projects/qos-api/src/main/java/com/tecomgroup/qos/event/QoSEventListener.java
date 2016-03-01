/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;


/**
 * 
 * Listen QoS Events
 * 
 * @author abondin
 * 
 */
public interface QoSEventListener {

	/**
	 * 
	 * @param event
	 */
	void onServerEvent(AbstractEvent event);
}

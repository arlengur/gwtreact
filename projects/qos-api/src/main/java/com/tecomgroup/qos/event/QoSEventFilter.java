/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import java.io.Serializable;

/**
 * Filter QoS events.
 * 
 * @author abondin
 * 
 */
public interface QoSEventFilter extends Serializable {
	/**
	 * 
	 * @param event
	 * @return true if event should be handled by client
	 * 
	 */
	boolean accept(AbstractEvent event);
}
/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import java.util.Collection;

/**
 * Event broadcaster.
 * 
 * Implement this interface and create a spring bean.<br>
 * System will automatically send all server events to you
 * 
 * @author abondin
 * 
 */
public interface EventBroadcaster {
	void broadcast(Collection<? extends AbstractEvent> events);
}

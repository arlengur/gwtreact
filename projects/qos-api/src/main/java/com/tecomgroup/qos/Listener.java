/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import com.tecomgroup.qos.event.AbstractEvent;

/**
 * @author kunilov.p
 * 
 */
public interface Listener<T extends AbstractEvent> {
	void handleEvent(T event);
}

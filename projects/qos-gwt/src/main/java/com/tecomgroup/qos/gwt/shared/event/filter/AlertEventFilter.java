/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertEvent;
import com.tecomgroup.qos.event.QoSEventFilter;

/**
 * 
 * Filter alert events from server
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
// TODO Implement me
public class AlertEventFilter implements QoSEventFilter {
	@Override
	public boolean accept(final AbstractEvent event) {
		return event instanceof AlertEvent;
	}

}

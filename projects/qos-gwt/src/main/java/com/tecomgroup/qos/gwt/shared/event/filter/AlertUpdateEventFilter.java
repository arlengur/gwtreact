/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertUpdateEvent;
import com.tecomgroup.qos.event.QoSEventFilter;

/**
 * Filter for alert history
 * 
 * @author abondin
 * 
 */
// TODO Implement me
@SuppressWarnings("serial")
public class AlertUpdateEventFilter implements QoSEventFilter {
	@Override
	public boolean accept(final AbstractEvent event) {
		return event instanceof AlertUpdateEvent;
	}

}

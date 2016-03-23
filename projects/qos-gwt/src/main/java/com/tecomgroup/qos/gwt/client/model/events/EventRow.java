/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.events;

import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

@SuppressWarnings("serial")
public class EventRow implements TreeGridRow {

	private final MProbeEvent event;

	public EventRow(final MProbeEvent event) {
		super();
		this.event = event;
	}

	@Override
	public String getKey() {
		return event.getClass().getName() + event.getKey();
	}

	@Override
	public String getName() {
		return event.getKey();
	}

	public MProbeEvent getEvent() {
		return event;
	}
}

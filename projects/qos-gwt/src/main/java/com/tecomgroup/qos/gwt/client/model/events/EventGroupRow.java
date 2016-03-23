/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.model.events;

import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

@SuppressWarnings("serial")
public class EventGroupRow implements TreeGridRow {

	private final String displayName;
	private final MProbeEvent.EventType type;

	public EventGroupRow(final String displayName, final MProbeEvent.EventType type) {
		super();
		this.displayName = displayName;
		this.type = type;
	}

	@Override
	public String getKey() {
		return type.getEventClassName();
	}

	@Override
	public String getName() {
		return displayName;
	}

	public MProbeEvent.EventType getType() {
		return type;
	}

}

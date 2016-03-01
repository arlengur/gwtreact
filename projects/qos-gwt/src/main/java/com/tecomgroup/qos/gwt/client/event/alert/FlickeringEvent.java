/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.event.alert.FlickeringEvent.FlickeringEventHandler;

/**
 * The receiver of this event starts flickering(flashing) effect
 * 
 * @author ivlev.e
 */
public class FlickeringEvent extends GwtEvent<FlickeringEventHandler> {

	public static interface FlickeringEventHandler extends EventHandler {
		void onFlickerEventReceived(FlickeringEvent event);
	}

	public final static Type<FlickeringEventHandler> TYPE = new Type<FlickeringEventHandler>();

	private final PerceivedSeverity severity;

	public FlickeringEvent(final PerceivedSeverity severity) {
		this.severity = severity;
	}

	@Override
	protected void dispatch(final FlickeringEventHandler handler) {
		handler.onFlickerEventReceived(this);
	}

	@Override
	public GwtEvent.Type<FlickeringEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the severity
	 */
	public PerceivedSeverity getSeverity() {
		return severity;
	}
}

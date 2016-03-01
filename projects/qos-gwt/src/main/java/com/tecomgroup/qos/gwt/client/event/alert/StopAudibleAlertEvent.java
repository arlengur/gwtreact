/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.alert.StopAudibleAlertEvent.StopAudibleAlertEventHandler;
import com.tecomgroup.qos.gwt.client.sound.AudibleAlert;

/**
 * The receiver of this event stops the playing of current {@link AudibleAlert}
 * 
 * @author ivlev.e
 */
public class StopAudibleAlertEvent
		extends
			GwtEvent<StopAudibleAlertEventHandler> {

	public static interface StopAudibleAlertEventHandler extends EventHandler {
		void onStopAudibleAlert(StopAudibleAlertEvent event);
	}

	public final static Type<StopAudibleAlertEventHandler> TYPE = new Type<StopAudibleAlertEventHandler>();

	@Override
	protected void dispatch(final StopAudibleAlertEventHandler handler) {
		handler.onStopAudibleAlert(this);
	}

	@Override
	public Type<StopAudibleAlertEventHandler> getAssociatedType() {
		return TYPE;
	}
}

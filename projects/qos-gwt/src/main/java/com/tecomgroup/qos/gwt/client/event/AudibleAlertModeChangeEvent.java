/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.gwt.client.event.AudibleAlertModeChangeEvent.AudibleAlertModeChangeHandler;

/**
 * An event is fired when alert notification mode is changed (i.e.: user changes
 * settings)
 * 
 * @author sviyazov.a
 * 
 */
public class AudibleAlertModeChangeEvent
		extends
			GwtEvent<AudibleAlertModeChangeHandler> {

	public interface AudibleAlertModeChangeHandler extends EventHandler {
		void onAudibleAlertModeChange(AudibleAlertModeChangeEvent event);
	}

	private AudibleAlertFeatureMode mode;

	public final static Type<AudibleAlertModeChangeHandler> TYPE = new Type<AudibleAlertModeChangeHandler>();

	public AudibleAlertModeChangeEvent() {

	}

	public AudibleAlertModeChangeEvent(final AudibleAlertFeatureMode mode) {
		this.mode = mode;
	}

	@Override
	protected void dispatch(final AudibleAlertModeChangeHandler handler) {
		handler.onAudibleAlertModeChange(this);
	}

	@Override
	public Type<AudibleAlertModeChangeHandler> getAssociatedType() {
		return TYPE;
	}

	public AudibleAlertFeatureMode getMode() {
		return mode;
	}

	public void setMode(final AudibleAlertFeatureMode mode) {
		this.mode = mode;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.ActivateAlertEvent;
import com.tecomgroup.qos.event.QoSEventFilter;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
public class ActivateAlertEventFilter implements QoSEventFilter {

	private AudibleAlertFeatureMode audibleAlertFeatureMode;

	public ActivateAlertEventFilter() {
		super();
	}

	public ActivateAlertEventFilter(
			final AudibleAlertFeatureMode audibleAlertFeatureMode) {
		super();
		this.audibleAlertFeatureMode = audibleAlertFeatureMode;
	}

	@Override
	public boolean accept(final AbstractEvent event) {
		switch (audibleAlertFeatureMode) {
			case ON :
			case MUTE :
				if (event instanceof ActivateAlertEvent) {
					return true;
				}
				break;
			default :
				break;
		}
		return false;
	}

}

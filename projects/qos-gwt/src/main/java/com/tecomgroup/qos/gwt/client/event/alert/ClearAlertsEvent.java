/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import java.util.Collection;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.event.alert.ClearAlertsEvent.ClearAlertsEventHandler;

/**
 * Event for Clear alerts action.
 * 
 * @author novohatskiy.r
 * 
 */
public class ClearAlertsEvent
		extends
			AlertsEventWithComment<ClearAlertsEventHandler> {

	public static interface ClearAlertsEventHandler
			extends
				AlertsEventWithComment.AlertsEventWithCommentHandler {
		void onClearAlerts(ClearAlertsEvent event);
	}

	public final static Type<ClearAlertsEventHandler> TYPE = new Type<ClearAlertsEventHandler>();

	/**
	 * @param alerts
	 * @param comment
	 * @param callback
	 */
	public ClearAlertsEvent(final Collection<MAlert> alerts,
			final String comment, final PostActionCallback callback) {
		super(alerts, comment, callback);
	}

	@Override
	protected void dispatch(final ClearAlertsEventHandler handler) {
		handler.onClearAlerts(this);
	}

	@Override
	public Type<ClearAlertsEventHandler> getAssociatedType() {
		return TYPE;
	}

}

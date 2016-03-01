/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import java.util.Collection;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.event.alert.UnacknowledgeAlertsEvent.UnacknowledgeAlertsEventHandler;

/**
 * Event for unacknowledge alerts action.
 * 
 * @author novohatskiy.r
 * 
 */
public class UnacknowledgeAlertsEvent
		extends
			AlertsEventWithComment<UnacknowledgeAlertsEventHandler> {

	public static interface UnacknowledgeAlertsEventHandler
			extends
				AlertsEventWithComment.AlertsEventWithCommentHandler {
		void onUnacknowledgeAlerts(UnacknowledgeAlertsEvent event);
	}

	public final static Type<UnacknowledgeAlertsEventHandler> TYPE = new Type<UnacknowledgeAlertsEventHandler>();

	/**
	 * @param alerts
	 * @param comment
	 * @param callback
	 */
	public UnacknowledgeAlertsEvent(final Collection<MAlert> alerts,
			final String comment, final PostActionCallback callback) {
		super(alerts, comment, callback);
	}

	@Override
	protected void dispatch(final UnacknowledgeAlertsEventHandler handler) {
		handler.onUnacknowledgeAlerts(this);
	}

	@Override
	public Type<UnacknowledgeAlertsEventHandler> getAssociatedType() {
		return TYPE;
	}

}

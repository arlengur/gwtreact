/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import java.util.Collection;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.event.alert.AcknowledgeAlertsEvent.AcknowledgeAlertsEventHandler;

/**
 * Event for acknowledge alerts action.
 * 
 * @author novohatskiy.r
 * 
 */
public class AcknowledgeAlertsEvent
		extends
			AlertsEventWithComment<AcknowledgeAlertsEventHandler> {

	public static interface AcknowledgeAlertsEventHandler
			extends
				AlertsEventWithComment.AlertsEventWithCommentHandler {
		void onAcknowledgeAlerts(AcknowledgeAlertsEvent event);
	}

	public final static Type<AcknowledgeAlertsEventHandler> TYPE = new Type<AcknowledgeAlertsEventHandler>();

	/**
	 * @param alerts
	 * @param comment
	 * @param callback
	 */
	public AcknowledgeAlertsEvent(final Collection<MAlert> alerts,
			final String comment, final PostActionCallback callback) {
		super(alerts, comment, callback);
	}

	@Override
	protected void dispatch(final AcknowledgeAlertsEventHandler handler) {
		handler.onAcknowledgeAlerts(this);
	}

	@Override
	public Type<AcknowledgeAlertsEventHandler> getAssociatedType() {
		return TYPE;
	}

}

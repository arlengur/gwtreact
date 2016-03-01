/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import java.util.Collection;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.event.alert.CommentAlertsEvent.CommentAlertsEventHandler;

/**
 * Event for Comment alerts action.
 * 
 * @author novohatskiy.r
 * 
 */
public class CommentAlertsEvent
		extends
			AlertsEventWithComment<CommentAlertsEventHandler> {

	public static interface CommentAlertsEventHandler
			extends
				AlertsEventWithComment.AlertsEventWithCommentHandler {
		void onCommentAlers(CommentAlertsEvent event);
	}

	public final static Type<CommentAlertsEventHandler> TYPE = new Type<CommentAlertsEventHandler>();

	/**
	 * @param alerts
	 * @param comment
	 * @param callback
	 */
	public CommentAlertsEvent(final Collection<MAlert> alerts,
			final String comment, final PostActionCallback callback) {
		super(alerts, comment, callback);
	}

	@Override
	protected void dispatch(final CommentAlertsEventHandler handler) {
		handler.onCommentAlers(this);
	}

	@Override
	public Type<CommentAlertsEventHandler> getAssociatedType() {
		return TYPE;
	}

}

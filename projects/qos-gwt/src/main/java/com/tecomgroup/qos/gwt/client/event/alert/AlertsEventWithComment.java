/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.alert;

import java.util.Collection;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.event.EventWithComment;
import com.tecomgroup.qos.gwt.client.event.HasPostActionCallback;
import com.tecomgroup.qos.gwt.client.event.alert.AlertsEventWithComment.AlertsEventWithCommentHandler;

/**
 * {@link EventWithComment} for a bunch of alerts
 * 
 * @author novohatskiy.r
 * 
 */
public abstract class AlertsEventWithComment<T extends AlertsEventWithCommentHandler>
		extends
			EventWithComment<T> implements HasPostActionCallback {

	public static interface AlertsEventWithCommentHandler
			extends
				EventWithCommentHandler {
	}

	private Collection<MAlert> alerts;
	private final PostActionCallback callback;

	/**
	 * @param alerts
	 * @param comment
	 * @param callback
	 */
	public AlertsEventWithComment(final Collection<MAlert> alerts,
			final String comment, final PostActionCallback callback) {
		super(comment);
		this.alerts = alerts;
		this.callback = callback;
	}

	/**
	 * @return the alerts
	 */
	public Collection<MAlert> getAlerts() {
		return alerts;
	}

	/**
	 * @return the callback
	 */
	@Override
	public PostActionCallback getCallback() {
		return callback;
	}

	/**
	 * @param alerts
	 *            the alerts to set
	 */
	public void setAlerts(final Collection<MAlert> alerts) {
		this.alerts = alerts;
	}
}

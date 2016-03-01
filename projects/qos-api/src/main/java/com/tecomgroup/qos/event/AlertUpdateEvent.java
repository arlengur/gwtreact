/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
/**
 * @author abondin
 * 
 */
public class AlertUpdateEvent extends AbstractEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8615127162178271432L;

	private Long alertId;
	private UpdateType updateType;

	/**
	 * 
	 */
	public AlertUpdateEvent() {
		super();
	}

	/**
	 * 
	 */
	public AlertUpdateEvent(final EventType type, final Long alertId, UpdateType updateType) {
		super(type);
		this.alertId = alertId;
		this.updateType = updateType;
	}

	/**
	 * @return the alertUpdate
	 */
	public Long getAlertId() {
		return alertId;
	}


	/**
	 * @param alertId
	 *            the alertUpdate to set
	 */
	public void setAlertId(final Long alertId) {
		this.alertId = alertId;
	}


	public UpdateType getUpdateType() {
		return updateType;
	}

	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}
}

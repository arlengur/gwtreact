/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import com.tecomgroup.qos.domain.MAlertIndication;

/**
 * @author abondin
 * 
 */
public class AlertMessage extends QoSMessage {
	public static enum AlertAction {
		CLEAR, ACTIVATE
	}
	public static AlertMessage activateAlert(final MAlertIndication alert) {
		final AlertMessage message = new AlertMessage();
		message.setAlert(alert);
		message.setAction(AlertAction.ACTIVATE);
		return message;
	}
	public static AlertMessage clearAlert(final MAlertIndication alert) {
		final AlertMessage message = new AlertMessage();
		message.setAlert(alert);
		message.setAction(AlertAction.CLEAR);
		return message;
	}

	private AlertAction action;

	private MAlertIndication alert;

	/**
	 * @return the action
	 */
	public AlertAction getAction() {
		return action;
	}
	/**
	 * @return the alert
	 */
	public MAlertIndication getAlert() {
		return alert;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(final AlertAction action) {
		this.action = action;
	}
	/**
	 * @param alert
	 *            the alert to set
	 */
	public void setAlert(final MAlertIndication alert) {
		this.alert = alert;
	}
}

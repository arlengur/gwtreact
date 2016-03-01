/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.service.alert.InboundAlertService;
import org.apache.log4j.Logger;

import com.tecomgroup.qos.communication.message.AlertMessage;
import com.tecomgroup.qos.communication.message.AlertMessage.AlertAction;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.exception.SourceNotFoundException;
import com.tecomgroup.qos.exception.UnknownSourceException;

/**
 * 
 * Обработка результаов измерений
 * 
 * @author muvarov
 * 
 */
public class AlertMessageListener extends QoSMessageListener<AlertMessage> {

	private InboundAlertService alertService;

	private final Logger LOGGER = Logger.getLogger(AlertMessageListener.class);

	@Override
	public RequestResponse handleQosMessage(final AlertMessage message) {
		final MAlertIndication indication = message.getAlert();

		try {
			if (indication.getOriginator() == null
					|| !Source.Type.POLICY.equals(indication.getOriginator()
							.getType())) {

				throw new SourceNotFoundException("Originator (Policy) "
							+ indication.getOriginator().getKey()
							+ " not found. Possibly it was already deleted.");
			}

			if (indication.getSource() == null
					|| !Source.Type.TASK.equals(indication.getSource().getType())) {
				throw new UnknownSourceException(
						"Alert source must be only the task: "
								+ indication.getSource());
			}

			if (message.getAction() == AlertAction.CLEAR) {
				indication.setIndicationType(UpdateType.AUTO_CLEARED);
				alertService.clearAlert(indication);
			} else {
				alertService.activateAlert(indication);
			}
		} catch (final Exception ex) {
			LOGGER.error("Cannot handle alert message: " + message, ex);
			return new RequestResponse(ex);
		}

		return new RequestResponse();
	}

	/**
	 * @param alertService
	 *            the alertService to set
	 */
	public void setAlertService(final InboundAlertService alertService) {
		this.alertService = alertService;
	}
}

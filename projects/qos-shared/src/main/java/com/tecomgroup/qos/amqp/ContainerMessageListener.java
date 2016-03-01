/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import com.tecomgroup.qos.communication.message.QoSMessage;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * @author kunilov.p
 * 
 */
public class ContainerMessageListener extends QoSMessageListener<QoSMessage> {

	private final static Logger LOGGER = Logger
			.getLogger(ContainerMessageListener.class);

	private final Map<Class<? extends QoSMessage>, QoSMessageListener<? extends QoSMessage>> listeners = new HashMap<Class<? extends QoSMessage>, QoSMessageListener<? extends QoSMessage>>();

	public ContainerMessageListener(final AmqpTemplate amqpTemplate,
			final MessageConverter messageConverter) {
		super();
		setAmqpTemplate(amqpTemplate);
		setMessageConverter(messageConverter);
		setEnabled(true);
	}

	public void addListener(final Class<? extends QoSMessage> processedType,
			final QoSMessageListener<? extends QoSMessage> listener) {
		listeners.put(processedType, listener);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public RequestResponse handleQosMessage(final QoSMessage message) {
		RequestResponse response = null;
		final QoSMessageListener listener = listeners.get(message.getClass());
		if (listener == null) {
			final ServiceException listenerNotFoundException = new ServiceException(
					"Listener for " + message.getClass().getSimpleName()
							+ " not found");
			response = new RequestResponse(listenerNotFoundException);
			LOGGER.error("Cannot handle message: " + message, listenerNotFoundException);
		} else {
			response = listener.handleQosMessage(message);
		}

		return response;
	}
}

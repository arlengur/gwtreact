/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import com.tecomgroup.qos.communication.message.VideoResultMessage;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.service.VideoResultService;

/**
 * @author kunilov.p
 * 
 */
public class VideoResultMessageListener
		extends
			QoSMessageListener<VideoResultMessage> {

	private final static Logger LOGGER = Logger
			.getLogger(VideoResultMessageListener.class);

	private final VideoResultService handler;

	public VideoResultMessageListener(final AmqpTemplate amqpTemplate,
			final MessageConverter messageConverter,
			final VideoResultService handler) {
		super();
		setAmqpTemplate(amqpTemplate);
		setMessageConverter(messageConverter);
		setEnabled(true);
		this.handler = handler;
	}

	@Override
	public RequestResponse handleQosMessage(final VideoResultMessage message) {
		RequestResponse response = null;
		if (handler != null) {
			try {
				// redundant code. Its destiny will be decided in http://rnd.tecom.nnov.ru/issues/2625
				// handler.addResults(message.getTaskKey(), message.getResults());
				response = new RequestResponse();
			} catch (final Exception ex) {
				LOGGER.error("Cannot handle message: " + message, ex);
				response = new RequestResponse(ex);
			}
		}
		return response;
	}
}

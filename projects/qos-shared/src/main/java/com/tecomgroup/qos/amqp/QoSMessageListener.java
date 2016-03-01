/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.support.converter.MessageConverter;

import com.tecomgroup.qos.communication.message.QoSMessage;
import com.tecomgroup.qos.communication.response.RequestResponse;

/**
 * 
 * Корневой класс для обработки JSON сообщений
 * 
 * @author abondin
 * 
 */
public abstract class QoSMessageListener<T extends QoSMessage> implements
		MessageListener {

	private final static Logger LOGGER = Logger
			.getLogger(QoSMessageListener.class);

	protected boolean enabled = true;

	protected AmqpTemplate amqpTemplate;

	protected MessageConverter messageConverter;

	public void error(final String replyTo, final byte[] correlationId,
			final Throwable error) {
		LOGGER.error("Error handling AMQP message", error);
		if (replyTo != null && correlationId != null) {
			try {
				amqpTemplate.convertAndSend(replyTo,
						new RequestResponse(error), new MessagePostProcessor() {
							@Override
							public Message postProcessMessage(
									final Message message) throws AmqpException {
								message.getMessageProperties()
										.setCorrelationId(correlationId);
								return message;
							}
						});
			} catch (final Exception e) {
				LOGGER.error("Cannot send error message", error);
			}
		}
	}

	/**
	 * @return the amqpTemplate
	 */
	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	/**
	 * Метод для обработки сообщения
	 * 
	 * @param message
	 */
	public abstract RequestResponse handleQosMessage(final T message);

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(final Message message) {
		if (isEnabled()) {
			final byte[] correlationId = message.getMessageProperties()
					.getCorrelationId();
			final String replyTo = message.getMessageProperties().getReplyTo();
			try {
				final RequestResponse response = processMessage(message);
				if (response != null) {
					response(correlationId,replyTo, response);
				}
			} catch (final Exception ex) {
				error(replyTo, correlationId, ex);
			}
		} else {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace(this.getClass().getSimpleName()
						+ " is not enabled");
			}
		}
	}

	protected RequestResponse processMessage(final Message message)
	{
		final byte[] correlationId = message.getMessageProperties()
				.getCorrelationId();
		final String replyTo = message.getMessageProperties().getReplyTo();
		final T object = (T) messageConverter.fromMessage(message);
		object.setCorrelationId(correlationId);
		object.setReplyTo(replyTo);
		object.setMessageId(message.getMessageProperties()
				.getMessageId());
		return handleQosMessage(object);
	}

	protected void response(final T request, final RequestResponse response) {
		if (request.getReplyTo() != null) {
			try {
				amqpTemplate.convertAndSend(request.getReplyTo(), response,
						new MessagePostProcessor() {
							@Override
							public Message postProcessMessage(
									final Message message) throws AmqpException {
								if (request.getCorrelationId() != null) {
									message.getMessageProperties()
											.setCorrelationId(
													request.getCorrelationId());
								}
								return message;
							}
						});
			} catch (final Exception ex) {
				LOGGER.error("Cannot reply", ex);
			}
		}
	}

	protected void response(final byte[] correlationId,final String replyTo, final RequestResponse response) {
		if (replyTo != null) {
			try {
				amqpTemplate.convertAndSend(replyTo, response,
						new MessagePostProcessor() {
							@Override
							public Message postProcessMessage(
									final Message message) throws AmqpException {
								if (correlationId != null) {
									message.getMessageProperties()
											.setCorrelationId(correlationId);
								}
								return message;
							}
						});
			} catch (final Exception ex) {
				LOGGER.error("Cannot reply", ex);
			}
		}
	}

	/**
	 * @param amqpTemplate
	 *            the amqpTemplate to set
	 */
	public void setAmqpTemplate(final AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @param messageConverter
	 *            the messageConvertor to set
	 */
	public void setMessageConverter(final MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
}

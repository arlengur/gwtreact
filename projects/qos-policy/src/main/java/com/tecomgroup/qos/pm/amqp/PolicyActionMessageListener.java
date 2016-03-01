/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.amqp;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.TopicExchange;

import com.tecomgroup.qos.AbstractSender;
import com.tecomgroup.qos.AbstractSender.Status;
import com.tecomgroup.qos.amqp.QoSMessageListener;
import com.tecomgroup.qos.communication.message.PolicySendActionMessage;
import com.tecomgroup.qos.communication.response.RequestResponse;

/**
 * @author ivlev.e
 * 
 */
public class PolicyActionMessageListener
		extends
			QoSMessageListener<PolicySendActionMessage> {

	public static class MessageProcessingException extends RuntimeException {
		private static final long serialVersionUID = -5845911726180393995L;
	}

	private final Logger LOGGER = Logger
			.getLogger(PolicyActionMessageListener.class);

	private long errorSleepTimeSeconds;

	private AbstractSender sender;

	private TopicExchange actionExchange;

	private String queueRoutingKey;

	public AbstractSender getSender() {
		return sender;
	}

	@Override
	public RequestResponse handleQosMessage(
			final PolicySendActionMessage message) {
		try {
			sendMessage(message.getSubject(), message.getBody(),
					message.getContacts(), message.getOutputParameters());
		} catch (final MessageProcessingException e) {
			// return message to the end of the queue
			amqpTemplate.convertAndSend(actionExchange.getName(),
					queueRoutingKey, message);
		}

		// no response is needed
		return null;
	}

	private void sendMessage(final String subject, final String body,
			final Set<String> destinations,
			final Map<String, Object> outputParameters) {
		final Status messageSendingStatus = sender.sendTemplatedMessage(
				destinations, subject, body, outputParameters);

		if (Status.SOLVABLE_PROBLEM_OCCURED.equals(messageSendingStatus)) {
			LOGGER.warn("Message could not be sent. Returning message back to queue.");
			if (errorSleepTimeSeconds > 0) {
				try {
					Thread.sleep(errorSleepTimeSeconds * 1000);
				} catch (final InterruptedException e) {
					LOGGER.error(e);
				}
			}
			throw new MessageProcessingException();
		} else if (Status.INCORRECT_INPUT.equals(messageSendingStatus)) {
			LOGGER.warn("Cannot send message with subject. Dropping this message from queue.");
		}
	}

	public void setActionExchange(final TopicExchange actionExchange) {
		this.actionExchange = actionExchange;
	}

	public void setErrorSleepTimeSeconds(final long errorSleepTimeSeconds) {
		this.errorSleepTimeSeconds = errorSleepTimeSeconds;
	}

	public void setQueueRoutingKey(final String queueRoutingKey) {
		this.queueRoutingKey = queueRoutingKey;
	}

	public void setSender(final AbstractSender sender) {
		this.sender = sender;
	}
}

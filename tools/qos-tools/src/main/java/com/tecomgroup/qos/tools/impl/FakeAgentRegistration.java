/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.IOException;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.communication.request.RegisterAgent;
import com.tecomgroup.qos.communication.response.RegisterAgentResponse;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.tools.QoSTool;

/**
 * @author abondin
 * 
 */
@Service
public class FakeAgentRegistration implements QoSTool {

	@Autowired
	private TopicExchange serviceExchange;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private ConnectionFactory connectionFactory;

	@Autowired
	private MessageConverter messageConverter;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	private boolean started;

	@Override
	public void execute() {
		started = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Queue queue = rabbitAdmin.declareQueue();
				rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
						.to(serviceExchange).with("server"));
				while (started) {
					final Message message = amqpTemplate.receive(queue
							.getName());
					if (message == null) {
						try {
							Thread.sleep(1000);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					final Object obj = messageConverter.fromMessage(message);
					if (obj != null && obj instanceof RegisterAgent) {
						final RegisterAgent registerAgent = new RegisterAgent();
						registerAgent.setReplyTo(message.getMessageProperties()
								.getReplyTo());
						registerAgent.setMessageId(message
								.getMessageProperties().getMessageId());
						if (registerAgent.getReplyTo() != null) {
							final RequestResponse responce = new RegisterAgentResponse();
							amqpTemplate.convertAndSend(
									registerAgent.getReplyTo(), responce,
									new MessagePostProcessor() {

										@Override
										public Message postProcessMessage(
												final Message message)
												throws AmqpException {
											message.getMessageProperties()
													.setCorrelationId(
															registerAgent
																	.getMessageId()
																	.getBytes());
											message.getMessageProperties()
													.setExpiration("60000");
											return message;
										}
									});
						}
					}
				}
			}
		}).start();
		try {
			System.in.read();
			started = false;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String getDescription() {
		return "FakeAgentRegistration: Send responce to RegisterAgent message. Press any key to stop application";
	}

}

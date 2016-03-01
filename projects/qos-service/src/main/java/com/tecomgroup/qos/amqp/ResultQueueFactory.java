/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.communication.message.ResultMessage;
import com.tecomgroup.qos.communication.message.VideoResultMessage;
import com.tecomgroup.qos.service.ResultService;
import com.tecomgroup.qos.service.VideoResultService;
import com.tecomgroup.qos.util.Utils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Фабрика для создания очередей получения результатов
 * 
 * @author abondin
 * 
 */
@Component
public class ResultQueueFactory {

	@Autowired
	private TopicExchange resultExchange;

	@Autowired
	private RabbitAdmin rabbitAdmin;
	@Autowired
	private ConnectionFactory rabbitConnectionFactory;
	@Autowired
	private ResultService resultService;
	@Autowired
	private VideoResultService videoResultService;

	@Value("${amqp.result.queue.prefix}")
	private String queuePrefix;

	@Value("${amqp.qos.handle.results}")
	private Boolean enabled;

	@Value("${qos.hostname}")
	private String serverName;

	@Autowired
	private MessageConverter messageConverter;

	@Autowired
	private AmqpTemplate amqpTemplate;

	private final static Logger LOGGER = Logger.getLogger(ResultQueueFactory.class);

	public String registerQueueForAgent(final String agentKey) {
		if (enabled) {
			final String routingKey = queuePrefix + agentKey;
			final String queueName = Utils.getUniqueQueueName(resultExchange.getName() + ".queue." + routingKey, serverName);
			final Queue queue = new Queue(queueName);
			rabbitAdmin.declareQueue(queue);
			rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(resultExchange).with(routingKey));

			LOGGER.info("Register result queue from agent " + agentKey);
			return queueName;
		}

		LOGGER.warn("Skip registring result queue for agent " + agentKey);
		return null;
	}

	public String deleteQueueForAgent(final String agentKey) {
		if (enabled) {
			final String routingKey = queuePrefix + agentKey;
			final String queueName = Utils.getUniqueQueueName(resultExchange.getName() + ".queue." + routingKey, serverName);
			rabbitAdmin.deleteQueue(queueName);

			LOGGER.info("Unregister result queue for agent " + agentKey);
			return queueName;
		}

		LOGGER.warn("Skip unregistring result queue for agent " + agentKey);
		return null;
	}

	public SimpleMessageListenerContainer createQueueListener(final String queueName) {
		if (enabled) {
			final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
					rabbitConnectionFactory);
			container.setQueueNames(queueName);
			final ContainerMessageListener containerMessageListener = new ContainerMessageListener(
					amqpTemplate, messageConverter);
			containerMessageListener.addListener(ResultMessage.class,
					new ResultMessageListener(amqpTemplate, messageConverter,
							resultService));
			containerMessageListener.addListener(VideoResultMessage.class,
					new VideoResultMessageListener(amqpTemplate,
							messageConverter, videoResultService));
			container.setMessageListener(containerMessageListener);
			container.start();
			container.getActiveConsumerCount();
			LOGGER.info("Start listen results from agent " + queueName);
			return container;

		}

		LOGGER.warn("Skip creating listener on result queue " + queueName);
		return null;
	}

	/**
	 * @param resultService
	 *            the resultService to set
	 */
	public void setResultService(final ResultService resultService) {
		this.resultService = resultService;
	}
}

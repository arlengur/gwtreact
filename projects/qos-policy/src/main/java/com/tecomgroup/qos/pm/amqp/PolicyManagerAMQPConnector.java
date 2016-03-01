/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.amqp;

import com.tecomgroup.qos.amqp.ContainerMessageListener;
import com.tecomgroup.qos.amqp.ResultMessageListener;
import com.tecomgroup.qos.amqp.VideoResultMessageListener;
import com.tecomgroup.qos.communication.message.*;
import com.tecomgroup.qos.communication.request.RegisterPolicyManager;
import com.tecomgroup.qos.communication.response.RegisterPolicyManagerResponse;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.service.PolicyManagerService;
import com.tecomgroup.qos.util.Utils;
import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author abondin
 * 
 */
@Component
public class PolicyManagerAMQPConnector extends SimpleMessageListenerContainer
		implements
			MessageListener {

	private class RegisterPolicyMessagePostProcessor
			implements
				MessagePostProcessor {

		@Override
		public Message postProcessMessage(final Message message)
				throws AmqpException {
			message.getMessageProperties().setExpiration(
					pmRegistrationTimeout.toString());
			return message;
		}
	}

	private class ServerStartedMessageListener implements MessageListener {

		@Override
		public void onMessage(final Message message) {
			if (isEnabled()) {
				try {
					final Object object = messageConverter.fromMessage(message);
					if (object instanceof ServerStarted) {
						registerPolicyManager();
					}
				} catch (final Exception ex) {
					LOGGER.error("Cannot handle message", ex);
				}
			} else {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace(this.getClass().getSimpleName()
							+ " is not enabled");
				}
			}
		}
	}

	private class UpdatePMConfigurationListener implements MessageListener {

		@Override
		public void onMessage(final Message message) {
			if (isEnabled() && isPolicyManagerRegistered) {
				try {
					final Object object = messageConverter.fromMessage(message);
					if (object instanceof UpdatePMConfiguration) {
						updatePMConfiguration((UpdatePMConfiguration) object);
					}
				} catch (final Exception ex) {
					LOGGER.error("Cannot handle message", ex);
				}
			} else {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace(this.getClass().getSimpleName()
							+ " is not enabled");
				}
			}
		}
	}

	private final List<MessageListener> serviceMessageListeners = new ArrayList<MessageListener>();

	private final Set<String> configuiredAgents = new HashSet<>();

	@Value("${pm.name}")
	private String policyManagerName;

	@Value("${pm.registration.timeout}")
	private Long pmRegistrationTimeout;

	@Value("${pm.registration.interval}")
	private Long pmRegistrationInterval;

	@Autowired
	private TopicExchange resultExchange;

	private final static Logger LOGGER = Logger
			.getLogger(PolicyManagerAMQPConnector.class);

	@Value("${amqp.qos.handle.results}")
	protected boolean enabled = true;

	@Value("${amqp.result.queue.prefix}")
	private String queuePrefix;

	@Value("${amqp.server.routing.key}")
	private String serverRoutingKey;

	@Autowired
	private MessageConverter messageConverter;

	@Autowired
	private PolicyManagerService policyManagerService;

	@Autowired
	private RabbitAdmin rabbitAdmin;

	@Autowired
	private ConnectionFactory rabbitConnectionFactory;

	@Autowired
	private TopicExchange serviceExchange;

	@Autowired
	private FanoutExchange alertExchange;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Value("${pm.hostname}")
	private String hostname;

	private boolean isPolicyManagerRegistered = false;

	private final RegisterPolicyMessagePostProcessor messagePostProcessor = new RegisterPolicyMessagePostProcessor();

	/**
	 * Set of the result queues that already have message listener
	 */
	private final Set<String> subscribedQueues = new HashSet<>();

	/**
	 * @param connectionFactory
	 */
	public PolicyManagerAMQPConnector(final ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	@Override
	protected void doStart() throws Exception {
		registerPolicyManager();
		registerServiceMessageListeners();
		this.setMessageListener(this);

		super.doStart();
	}

	/**
	 * @return the messageConverter
	 */
	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	/**
	 * @return the policyManagerService
	 */
	public PolicyManagerService getPolicyManagerService() {
		return policyManagerService;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void onMessage(final Message message) {
		for (final MessageListener messageListener : serviceMessageListeners) {
			messageListener.onMessage(message);
		}
	}

	private void registerAgentQueue(final String queueName,
			final String routingKey) {
		final Queue queue = new Queue(queueName);
		rabbitAdmin.declareQueue(queue);
		rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
				.to(resultExchange).with(routingKey));
		final SimpleMessageListenerContainer resultMessageListenerContainer = new SimpleMessageListenerContainer(
				rabbitConnectionFactory);
		resultMessageListenerContainer.setQueueNames(queue.getName());
		final ContainerMessageListener containerMessageListener = new ContainerMessageListener(
				amqpTemplate, messageConverter);
		containerMessageListener.addListener(ResultMessage.class,
				new ResultMessageListener(amqpTemplate, messageConverter,
						policyManagerService));
		containerMessageListener.addListener(VideoResultMessage.class,
				new VideoResultMessageListener(amqpTemplate, messageConverter,
						null));
		resultMessageListenerContainer
				.setMessageListener(containerMessageListener);
		resultMessageListenerContainer.start();
	}

	private void registerAgentQueues(final Collection<String> registeredAgents) {
		final List<String> agents = new ArrayList<>();
		if (configuiredAgents.isEmpty()) {
			agents.addAll(registeredAgents);
		} else {
			for (final String agent : registeredAgents) {
				if (configuiredAgents.contains(agent)) {
					agents.add(agent);
				} else {
					LOGGER.warn("Agent " + agent
							+ " is not configured for the Policy Manager");
				}
			}
		}
		for (final String agentName : agents) {
			final String routingKey = queuePrefix + agentName;
			final String queueName = Utils.getUniqueQueueName("pm-"
					+ resultExchange.getName() + ".queue." + routingKey, hostname);
			synchronized (subscribedQueues) {
				if (!subscribedQueues.contains(queueName)) {
					subscribedQueues.add(queueName);
					registerAgentQueue(queueName, routingKey);
					LOGGER.info("Start listening results from agent "
							+ agentName);
				} else if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Queue " + queueName + " is already binded");
				}
			}
		}
	}

	private void registerPolicyManager() {
		final Long startTime = System.currentTimeMillis();
		RegisterPolicyManagerResponse response = null;
		final RegisterPolicyManager registerPolicyManager = new RegisterPolicyManager(
				policyManagerName, configuiredAgents);
		while (System.currentTimeMillis() - startTime < pmRegistrationInterval) {
			try {
				response = (RegisterPolicyManagerResponse) amqpTemplate
						.convertSendAndReceive(serviceExchange.getName(),
								serverRoutingKey, registerPolicyManager,
								messagePostProcessor);
				if (response != null) {
					if (RequestResponse.Status.OK.equals(response.getStatus())) {
						final Set<String> registeredAgents = response
								.getRegisteredAgents();
						registerAgentQueues(registeredAgents);
						policyManagerService
								.applyConfiguration(new PolicyManagerConfiguration(
										registeredAgents, response
												.getPmConfigurations()));
						LOGGER.info("PolicyManager registration on server "
								+ response.getServerName() + " is successful");
						isPolicyManagerRegistered = true;
						break;
					} else {
						isPolicyManagerRegistered = false;
						LOGGER.error("Unable to register policyManager due to "
								+ response.getReason()
								+ ". Trying once again ...");
					}
				}
			} catch (final Exception ex) {
				isPolicyManagerRegistered = false;
				LOGGER.error(
						"Unable to register policyManager due to "
								+ ex.getMessage() + ". Trying once again ...",
						ex);
			}
			try {
				Thread.sleep(pmRegistrationTimeout);
			} catch (final InterruptedException e) {
				// ignore
			}
		}
		if (response == null) {
			registrationFailureHandler("timeout exceeded", null);
			// server is unreachable, load local configuration
			policyManagerService.loadLocalConfiguration();
			registerAgentQueues(policyManagerService.getConfiguration()
					.getAgents());
			LOGGER.warn("Local policy configuration is loaded");
		} else if (RequestResponse.Status.ERROR.equals(response.getStatus())) {
			registrationFailureHandler(response.getReason(),
					response.getServerName());
			// server is reachable, but there are registration errors due to
			// wrong configuration.
			throw new ServiceException(
					"Policy manager stops due to above registration errors. Please fix policy manager configuration.");
		}
	}

	private void registerServiceMessageListeners() {
		serviceMessageListeners.add(new ServerStartedMessageListener());
		serviceMessageListeners.add(new UpdatePMConfigurationListener());
	}

	private void registrationFailureHandler(final String failureReason,
			final String serverName) {
		String errorMessage = "PolicyManager registration";
		if (serverName != null) {
			errorMessage += " on server " + serverName;
		}
		errorMessage += " is failed due to " + failureReason;
		LOGGER.error(errorMessage);
		sendFailedRegistrationAlert(failureReason);
	}

	private void sendFailedRegistrationAlert(final String reason) {
		// TODO: implement according to requirement of the system alerts
	}

	@Value("${pm.agents}")
	private void setConfiguiredAgents(final String[] configuiredAgents) {
		final List<String> trimmedAgents = new ArrayList<>();
		for (final String agent : configuiredAgents) {
			final String trimmedAgentName = agent.trim();
			if (!trimmedAgentName.isEmpty()) {
				trimmedAgents.add(trimmedAgentName);
			}
		}
		this.configuiredAgents.clear();
		this.configuiredAgents.addAll(trimmedAgents);
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
	 *            the messageConverter to set
	 */
	public void setMessageConverter(final MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	/**
	 * @param policyManagerService
	 *            the policyManagerService to set
	 */
	public void setPolicyManagerService(
			final PolicyManagerService policyManagerService) {
		this.policyManagerService = policyManagerService;
	}

	private void updatePMConfiguration(final UpdatePMConfiguration updateInfo) {
		switch (updateInfo.getEventType()) {
			case CREATE : {
				// go to UPDATE case
			}
			case UPDATE : {
				policyManagerService.updatePMConfiguration(updateInfo
						.getConfiguration());
				registerAgentQueues(Arrays.asList(updateInfo
						.getSystemCompomnent()));
				break;
			}
			case DELETE : {
				policyManagerService.removePMConfiguration(updateInfo
						.getConfiguration());
				break;
			}
			default : {
				throw new UnsupportedOperationException(
						"Unsupported EventType: " + updateInfo.getEventType());
			}
		}
	}

	@Override
	protected void validateConfiguration() {
		Assert.notNull(policyManagerName);
		super.validateConfiguration();
	}
}

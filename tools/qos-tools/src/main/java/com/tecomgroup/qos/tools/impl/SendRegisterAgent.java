/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.tecomgroup.qos.communication.request.RegisterAgent;
import com.tecomgroup.qos.communication.response.RegisterAgentResponse;
import com.tecomgroup.qos.communication.response.RequestResponse.Status;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.domain.MProperty;

/**
 * @author kunilov.p
 * 
 */
@Component
public class SendRegisterAgent implements QoSTool {

	@Value("${result.send.agent.name}")
	private String agentKey;

	@Value("${task.key}")
	private String[] taskKeys;

	@Value("${task.program.names}")
	private String progNames;
	
	@Value("${register.threshold.warning.level}")
	private Boolean thresholdWarningLevel;

	@Value("${register.threshold.critical.level}")
	private Boolean thresholdCriticalLevel;
	
	@Value("${register.agent.result.sampling.rate}")
	private Long samplingRate;

	@Value("${amqp.qos.service.exchange}")
	private String serviceExchange;

	@Value("${amqp.qos.server.route}")
	private String serverRoutingKey;

	@Value("${register.agent.attempt.count}")
	private int maxAttemptCount;

	@Value("${register.agent.json.path}")
	private String jsonFilePath;

	@Value("${register.agent.generate.streams}")
	private boolean generateStreams;

	@Value("${register.agent.mail.subject}")
	private String policyMailSubject;

	@Value("${register.agent.mail.body}")
	private String policyMailBody;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private MessageConverter messageConverter;

	private final String messageCorrelationId = java.util.UUID.randomUUID()
			.toString();

	private final static Logger LOGGER = Logger
			.getLogger(SendRegisterAgent.class);

	@Value("${register.agents.count}")
	private Long agentsCount;

	private final static int THREAD_POOL_SIZE = 20;

	private volatile int handledAgentCount = 0;

	private MAgentTask createAgentTask(final MAgentModule module,
			final String taskKey) {
		final MAgentTask task = MediaModelConfiguration.createAgentTask(module,
				taskKey);

		final MResultConfiguration resultConfiguration = new MResultConfiguration();
		resultConfiguration.setSamplingRate(samplingRate);
		task.setResultConfiguration(resultConfiguration);

		final MAgentModule emptyModuleWithKey = new MAgentModule();
		emptyModuleWithKey.setKey(task.getParent().getKey());
		task.setParent(emptyModuleWithKey);

		return task;
	}

	private List<MPolicyAction> createTemplatePolicyActions(
			final String parameterName) {
		final List<MPolicyAction> actions = new ArrayList<MPolicyAction>();
		actions.add(MediaModelConfiguration.createPolicySendAlertAction(
				"policyTemplateSendAlertAction" + System.currentTimeMillis(),
				SharedModelConfiguration.IT09A_ALERT_TYPE_PREFIX
						+ parameterName));
		actions.add(MediaModelConfiguration.createPolicySendEmailAction(
				"policyTemplateSendEmailAction" + System.currentTimeMillis(),
				policyMailSubject, policyMailBody));
		return actions;
	}

	@Override
	public void execute() {

		if (jsonFilePath == null || jsonFilePath.isEmpty()) {
			generateAndSend();
		} else {
			readJsonAndSend();
		}
	}

	public void execute(final String agentKey) {
		final RegisterAgent registerAgent = generateMessage(agentKey);
		sendMessage(registerAgent);
	}

	private void generateAndSend() {
		Assert.isTrue(agentsCount != null && agentsCount > 0,
				"Agents count be > 0");
		if (agentsCount == 1l) {
			execute(agentKey);
		} else {
			final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
			taskExecutor.setCorePoolSize(THREAD_POOL_SIZE);
			taskExecutor.setMaxPoolSize(THREAD_POOL_SIZE);
			taskExecutor.afterPropertiesSet();
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					for (int index = 1; index <= agentsCount; index++) {
						if (index == 1) {
							execute(agentKey);
						} else {
							execute(agentKey + "-" + index);
						}

					}
				}
			});
			final Long startTimestamp = System.currentTimeMillis();
			while (handledAgentCount < agentsCount) {
				try {
					Thread.sleep(1000l);
				} catch (final InterruptedException e) {
					// Do nothing
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(handledAgentCount + " agents handled in "
							+ (System.currentTimeMillis() - startTimestamp)
							/ 1000 + " seconds.");
				}
			}
			taskExecutor.destroy();
		}
	}

	private RegisterAgent generateMessage(final String agentKey) {
		final RegisterAgent registerAgent = new RegisterAgent();
		final MAgent agent = MediaModelConfiguration
				.createComplexAgent(agentKey);
		registerAgent.setAgent(agent);
		final MAgentModule module = MediaModelConfiguration
				.createMediaAgentModule(agent, generateStreams);

		// add agentPolicyTemplates and alertTypes
		final List<MPolicy> policies = new ArrayList<MPolicy>();
		final List<MAlertType> alertTypes = new ArrayList<MAlertType>();
		for (final MResultParameterConfiguration parameter : module
				.getTemplateResultConfiguration().getParameterConfigurations()) {
			final String parameterName = parameter.getName();
			MPolicy tmpPolicy  = MediaModelConfiguration
				.createPolicyTemplate(agent.getKey(), 
									  module.getKey(), 
									  new ParameterIdentifier(parameterName, 
															  module
															  .getTemplateResultConfiguration()
															  .getProperties()), 
									  parameter.getType(),
									  createTemplatePolicyActions(parameterName));
			tmpPolicy.setDisplayName("policyFor."+parameterName);
			tmpPolicy.setKey(agent.getKey()
							 + "."
							 + module.getKey()
							 + "."
							 + parameterName);
			if(thresholdWarningLevel && parameter.getType() != ParameterType.BOOL) {
				MContinuousThresholdFallCondition tfc = (MContinuousThresholdFallCondition)tmpPolicy.getCondition();
				tfc.getWarningLevel().setRaiseLevel(ConditionLevel.THRESHOLD_WARNING_LEVEL);
				tfc.getWarningLevel().setCeaseLevel(ConditionLevel.THRESHOLD_WARNING_LEVEL);
			}

			if(thresholdCriticalLevel && parameter.getType() != ParameterType.BOOL) {
				MContinuousThresholdFallCondition tfc = (MContinuousThresholdFallCondition)tmpPolicy.getCondition();
				tfc.getCriticalLevel().setRaiseLevel(ConditionLevel.THRESHOLD_CRITICAL_LEVEL);
				tfc.getCriticalLevel().setCeaseLevel(ConditionLevel.THRESHOLD_CRITICAL_LEVEL);
			}

			policies.add(tmpPolicy);

			alertTypes.add(SharedModelConfiguration.createAlertType(
					SharedModelConfiguration.IT09A_ALERT_TYPE_PREFIX
							+ parameterName,
					StringUtils.capitalize(parameterName)
							+ " crosses threshold"));
		}

		// add tasks and taskPolicyTemplates
		Iterator<String> pNames = Arrays.asList(progNames.split(",")).iterator();
		final List<MAgentTask> tasks = new ArrayList<MAgentTask>();

		for (final String taskKey : taskKeys) {
			final MAgentTask task = createAgentTask(module, taskKey);

			//add program names to task
			for(MProperty prop : task.getProperties()) {
				if(prop.getName().equals("programName")) {
					if(pNames.hasNext() == false) {
						pNames = Arrays.asList(progNames.split(",")).iterator();
					}
					prop.setValue(pNames.next());
				}
				//TODO: set PROGRAM_NUMBER
			}
			tasks.add(task);
		}
		registerAgent.setTasks(tasks);
		registerAgent.setPolicies(policies);
		registerAgent.setAlertTypes(alertTypes);
		registerAgent.setOriginName(agentKey);
		registerAgent.setModules(Arrays.<MAgentModule> asList(module));

		// Removes excessive information to emulate real registration message.
		for (final MAgentModule agentModule : registerAgent.getModules()) {
			agentModule.setParent(null);
		}

		return registerAgent;
	}
	/**
	 * @return the agentKey
	 */
	public String getAgentKey() {
		return agentKey;
	}

	@Override
	public String getDescription() {
		return "Sends RegisterAgent message to broker"
				+ "\nSupported VM arguments:"
				+ "\n\tresult.send.agent.name - agent name (agent key)"
				+ "\n\tregister.agent.attempt.count - number of attempt of sending register agent message"
				+ "\n\tregister.agent.message.expiration.in.sec - expiration time for amqp messages"
				+ "\n\tregister.agents.count - number of agents to register (1 by default)"
				+ "\n\tregister.agent.json.path - path to a JSON-formatted input file. If blank, sends generated agents info";
	}

	private RegisterAgent parseJsonInput(final File inputJsonFile) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		RegisterAgent registerAgent = null;
		try {
			registerAgent = mapper
					.readValue(inputJsonFile, RegisterAgent.class);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return registerAgent;
	}

	private void readJsonAndSend() {
		final File inputJsonFile = new File(jsonFilePath);

		if (inputJsonFile.exists()) {
			LOGGER.debug("Loading file: " + inputJsonFile.getAbsolutePath());
			final RegisterAgent registerAgent = parseJsonInput(inputJsonFile);

			if (registerAgent != null) {
				sendMessage(registerAgent);
			}
		} else {
			LOGGER.warn("Input file specified is not exist: " + jsonFilePath);
		}
	}

	private void sendMessage(final RegisterAgent registerAgent) {
		final String agentKey = registerAgent.getAgent().getKey();
		for (int attemptCount = 0; attemptCount < maxAttemptCount; attemptCount++) {

			LOGGER.info("Sending register agent (" + agentKey
					+ ") message, attempt #" + (attemptCount + 1) + " out of "
					+ maxAttemptCount);

			final Object response = amqpTemplate.convertSendAndReceive(
					serviceExchange, serverRoutingKey, registerAgent,
					new MessagePostProcessor() {
						@Override
						public Message postProcessMessage(final Message message)
								throws AmqpException {
							message.getMessageProperties().setCorrelationId(
									messageCorrelationId.getBytes());
							return message;
						}
					});
			if (response != null) {
				handledAgentCount++;
				final RegisterAgentResponse registerAgentResponse = (RegisterAgentResponse) response;
				if (registerAgentResponse.getStatus() == Status.OK) {
					LOGGER.info("Agent " + agentKey + " is registered");
				} else {
					LOGGER.error("Failed to register " + agentKey + ": "
							+ registerAgentResponse.getReason());
				}
				break;
			} else if (attemptCount == maxAttemptCount - 1) {
				handledAgentCount++;
			}
		}
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.communication.message.*;
import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.communication.pm.PMTaskConfiguration;
import com.tecomgroup.qos.communication.request.*;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.exception.DeletedSourceException;
import com.tecomgroup.qos.exception.RegistrationMinorErrorsException;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.service.*;
import com.tecomgroup.qos.service.probeconfig.ProbeConfigStorageService;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Exchange;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.*;

/**
 * 
 * Обработка AMQP запросов
 * 
 * @author abondin
 * 
 */
public class ServiceMessageListener extends QoSMessageListener<QoSMessage>
		implements
			InitializingBean,RegisterAgentProcessor {

	private AgentService agentService;

	private Exchange serviceExchange;

	private InternalTaskService taskService;

	private InternalPolicyConfigurationService policyConfigurationService;

	private ProbeConfigStorageService configStorage;

	private AlertService alertService;

	private String agentBroadcastRoutingKey;

	private String pmBroadcastRoutingKey;

	private ResultQueueRegister resultQueueRegister;

	@Value("${qos.hostname}")
	private String serverName;

	private final static Logger LOGGER = Logger
			.getLogger(ServiceMessageListener.class);

	@Autowired
	private InternalEventBroadcaster internalEventBroadcaster;

	private AgentHeartbeatListener agentHeartbeatListener;

	private ProbeEventServiceInternal probeEventService;

	@Override
	public void afterPropertiesSet() throws Exception {
		internalEventBroadcaster.subscribe(new QoSEventListener() {
			@Override
			public void onServerEvent(final AbstractEvent event) {
				if (event instanceof PolicyEvent) {
					final PolicyEvent policyEvent = (PolicyEvent) event;
					final Source systemComponent = policyEvent
							.getSystemComponent();

					sendUpdatePolicyConfiguration(systemComponent.getKey(),
							new PMTaskConfiguration(systemComponent,
									policyEvent.getPolicy().getSource(), null,
									Arrays.asList(policyEvent.getPolicy())),
							event.getEventType());
				} else if (event instanceof PoliciesEvent) {
					final PoliciesEvent policiesEvent = (PoliciesEvent) event;

					final Source systemComponent = policiesEvent
							.getSystemComponent();
					sendUpdatePolicyConfiguration(systemComponent.getKey(),
							new PMTaskConfiguration(systemComponent,
									policiesEvent.getSource(), null,
									policiesEvent.getPolicies()), event
									.getEventType());
				} else if (event instanceof TaskEvent) {
					final TaskEvent taskEvent = (TaskEvent) event;
					final MAgentTask task = taskEvent.getTask();

					final MSystemComponent systemComponent = SimpleUtils
							.findSystemComponent(task);
					final Source agentSource = Source
							.getAgentSource(systemComponent.getKey());
					final Source taskSource = Source.getTaskSource(task
							.getKey());
					agentSource.setDisplayName(systemComponent.getDisplayName());
					taskSource.setDisplayName(task.getDisplayName());
					sendUpdatePolicyConfiguration(systemComponent.getKey(),
							new PMTaskConfiguration(agentSource, taskSource,
									task.getResultConfiguration(), null), event
									.getEventType());
				} else if (event instanceof AgentEvent) {
					final MAgent agent = ((AgentEvent) event).getAgent();
					final Source agentSource = Source.getAgentSource(agent
							.getKey());
					agentSource.setDisplayName(agent.getDisplayName());
					sendUpdatePolicyConfiguration(agent.getKey(),
							new PMTaskConfiguration(agentSource, null, null,
									null), event.getEventType());
				}
			}
		}, new QoSEventFilter() {
			private static final long serialVersionUID = 79302649626908752L;

			@Override
			public boolean accept(final AbstractEvent event) {
				if (event instanceof PolicyEvent) {
					final PolicyEvent policyEvent = (PolicyEvent) event;
					if (policyEvent.getPolicy() != null
							&& policyEvent.getEventType() != null
							&& policyEvent.getPolicy().getSource() != null
							&& Source.Type.TASK.equals(policyEvent.getPolicy()
									.getSource().getType())) {
						return true;
					}
				} else if (event instanceof PoliciesEvent) {
					final PoliciesEvent policiesEvent = (PoliciesEvent) event;
					if (policiesEvent.getPolicies() != null
							&& policiesEvent.getSource() != null
							&& policiesEvent.getEventType() != null
							&& Source.Type.TASK.equals(policiesEvent
									.getSource().getType())) {
						return true;
					}
				} else if (event instanceof TaskEvent
						&& ((TaskEvent) event).getTask() != null
						&& event.getEventType() != null) {
					return true;
				} else if (event instanceof AgentEvent
						&& ((AgentEvent) event).getAgent() != null
						&& event.getEventType() == EventType.UPDATE) {
					return true;
				}
				return false;
			}
		});
		sendServerStartedMessage(null);
	}

	private List<MAgentTask> getDeletedTasks(final String agentName,
			final List<MAgentTask> tasks) {
		final List<MAgentTask> deletedTasks = new ArrayList<MAgentTask>();
		final Set<String> agentDeletedTasksKeys = SimpleUtils
				.getKeys(taskService.getAgentDeletedTasks(agentName));

		for (final MAgentTask task : tasks) {
			if (agentDeletedTasksKeys.contains(task.getKey())) {
				deletedTasks.add(task);
			}
		}
		return deletedTasks;
	}

	protected RequestResponse getPMConfigurations(
			final Set<String> registeredAgents,
			final RegisterPolicyManager request) {
		RequestResponse requestResponse = null;
		try {
			final Map<Source, PMConfiguration> pmConfigurations = policyConfigurationService
					.getPMConfigurations(request.getPolicyManagerName());
			requestResponse = request.responseOk(serverName, registeredAgents,
					pmConfigurations);
		} catch (final Exception ex) {
			requestResponse = request.responseError(serverName, ex);
		}

		return requestResponse;
	}

	@Override
	public RequestResponse handleQosMessage(final QoSMessage message) {
		if (message instanceof RegisterAgent) {
			return registerAgent((RegisterAgent) message);
		} else if (message instanceof RegisterPolicyManager) {
			return registerPolicyManager((RegisterPolicyManager) message);
		} else if (message instanceof UpdateTasks) {
			return updateTasks((UpdateTasks) message);
		} else if (message instanceof UpdateModules) {
			return updateModules((UpdateModules) message);
		} else if (message instanceof HeartbeatMessage) {
			return onHeartbeat((HeartbeatMessage) message);
		} else if (message instanceof ProbeConfigSync) {
			return handleUpdateProbeConfig((ProbeConfigSync) message);
		} else if (message instanceof AgentActionStatus) {
			return handleTaskStatus((AgentActionStatus) message);
		}
		throw new ServiceException("Unknown type of QoSRequest "
				+ message.getClass().getName());
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	/**
	 * @param heartbeat
	 * @return
	 */
	private RequestResponse onHeartbeat(final HeartbeatMessage heartbeat) {
		if (isEnabled() && agentHeartbeatListener != null) {
			agentHeartbeatListener.onHeartbeat(heartbeat);
		}
		return null;
	}

	private RequestResponse handleUpdateProbeConfig(final ProbeConfigSync config) {
		try {
			configStorage.uploadProbeConfiguration(
					config.getConfiguration(), config.getSchema(), config.getAgentKey());
			return config.responseOk(serverName);
		} catch (IOException e) {
			LOGGER.error("Unable to store agent file:", e);
			return config.responseError(e);
		}
	}

	private RequestResponse handleTaskStatus(AgentActionStatus taskStatus) {
		try {
			probeEventService.updateEvent(taskStatus);
			return taskStatus.responseOk(serverName);
		} catch (Exception e) {
			LOGGER.error("Unable to update task status row:", e);
			return taskStatus.responseError(e);
		}
	}

	public RequestResponse registerAgent(final RegisterAgent registrationInfo) {
		RequestResponse requestResponse;
		MAgent agent = null;
		Long agentId;
		String agentKey;
		try {
			agent = registrationInfo.getAgent();
			if (agent.getKey() == null) {
				agent.setKey(registrationInfo.getOriginName());
			}

			agent = agentService.registerAgent(registrationInfo.getAgent(), registrationInfo.getModules());
			agentId = agent.getId();
			agentKey = agent.getKey();
			String agentVersion = null;
			if (registrationInfo.getAgent_properties() != null) {
				String configurationXML = registrationInfo.getStringProperty(RegisterAgent.AGENT_CONFIGURATION_PROPERTY_KEY);
				String schema = registrationInfo.getStringProperty(RegisterAgent.AGENT_CONFIGURATION_SCHEMA_PROPERTY_KEY);
				if (configurationXML != null) {
					try {
						configStorage.uploadProbeConfiguration(configurationXML, schema, agentKey);
					} catch (Exception e) {
						LOGGER.error("Unable to store agent file:", e);
					}
				}
				agentVersion = registrationInfo.getStringProperty(RegisterAgent.AGENT_VERSION_PROPERTY_KEY);
			}
			internalEventBroadcaster.broadcast(Arrays
					.asList(new AgentChangeStateEvent(MAgent.AgentRegistrationState.IN_PROGRESS, agent,agentVersion,new Date())));

			policyConfigurationService.addAgentToRegisteredPMInfo(agent.getKey());

			final List<Throwable> registrationMinorErrors = new ArrayList<Throwable>();

			final List<MAgentTask> tasksToRegister = new LinkedList<MAgentTask>(
					registrationInfo.getTasks());
			final List<MAgentTask> deletedTasks = getDeletedTasks(
					agent.getKey(), tasksToRegister);

			for (final MAgentTask deletedTask : deletedTasks) {
				registrationMinorErrors.add(new DeletedSourceException("Task "
						+ deletedTask.getKey()
						+ " was previously deleted and has been ignored."));
				tasksToRegister.remove(deletedTask);
			}

			taskService.registerTasks(agent.getKey(), tasksToRegister);

			for (final MAlertType alertType : registrationInfo.getAlertTypes()) {
				try {
					alertService.registerAlertType(alertType);
				} catch (final Exception ex) {
					registrationMinorErrors.add(ex);
					LOGGER.error("Agent " + agent.getKey()
							+ " registration is not fully completed: ", ex);
				}
			}

			for (final MPolicy policy : registrationInfo.getPolicies()) {
				try {
					policyConfigurationService.registerPolicy(agent.getKey(),
							policy);
				} catch (final Exception ex) {
					registrationMinorErrors.add(ex);
					LOGGER.error("Agent " + agent.getKey()
							+ " registration is not fully completed: ", ex);
				}
			}

			resultQueueRegister.register(agent.getKey());

			if (registrationMinorErrors.size() == 0) {
				requestResponse = registrationInfo.responseOk(serverName,
						agentId.toString());
				internalEventBroadcaster.broadcast(Arrays
						.asList(new AgentChangeStateEvent(MAgent.AgentRegistrationState.SUCCESS, agent,new Date(),tasksToRegister)));
				//agentService.setAgentState(agentId,MAgent.AgentRegistrationState.SUCCESS);
				LOGGER.info("Agent " + agent
						+ " registration is successfully completed");
			} else {
				internalEventBroadcaster.broadcast(Arrays
						.asList(new AgentChangeStateEvent(MAgent.AgentRegistrationState.PARTIALLY, agent,new Date(),tasksToRegister)));
				//agentService.setAgentState(agentId,MAgent.AgentRegistrationState.PARTIALLY);
				requestResponse = registrationInfo.responseOkWithMinorError(
						serverName, agentId.toString(),
						new RegistrationMinorErrorsException(
								registrationMinorErrors));
			}

		} catch (final DeletedSourceException dse) {
			resultQueueRegister.unregister(agent.getKey());
			internalEventBroadcaster.broadcast(Arrays.asList(new AgentEvent(agent, EventType.DELETE)));
			requestResponse = registrationInfo.responseError(serverName, dse);
			LOGGER.error("Agent " + agent.getKey() + " registration failed: ", dse);
		} catch (final Exception ex) {
			internalEventBroadcaster.broadcast(Arrays.asList(new AgentChangeStateEvent(MAgent.AgentRegistrationState.FAILED, agent,new Date())));
			requestResponse = registrationInfo.responseError(serverName, ex);
			LOGGER.error("Agent " + agent.getKey() + " registration failed: ", ex);
		}
		return requestResponse;
	}

	protected RequestResponse registerPolicyManager(
			final RegisterPolicyManager registrationInfo) {
		RequestResponse requestResponse = null;
		try {
			final Set<String> registeredAgents = policyConfigurationService
					.registerPolicyManager(
							registrationInfo.getPolicyManagerName(),
							registrationInfo.getSupportedAgents());
			requestResponse = getPMConfigurations(registeredAgents,
					registrationInfo);
		} catch (final Exception ex) {
			requestResponse = registrationInfo.responseError(
					serverName, ex);
		}

		return requestResponse;
	}

	/**
	 * Sends server started message
	 * 
	 * @param agentKey
	 *            - agent which should receive this message. null - means all
	 *            agents (broadcast)
	 */
	public void sendServerStartedMessage(final String agentKey) {
		if (isEnabled()) {
			final String routingKey = Utils.getAgentRoutingKey(
					agentBroadcastRoutingKey, agentKey);
			LOGGER.info("Send server startup message to " + routingKey);
			try {
				amqpTemplate.convertAndSend(serviceExchange.getName(),
						routingKey,
						ServerStarted.serverStarted(serverName));
			} catch (final Exception ex) {
				LOGGER.error("Unable to send server startup message", ex);
			}
		}

	}

	private void sendUpdatePolicyConfiguration(final String systemComponentKey,
			final PMTaskConfiguration pmTaskConfiguration,
			final EventType eventType) {
		final Set<String> registeredPolicyManagers = policyConfigurationService
				.getRegisteredPolicyManagersBySystemComponent(systemComponentKey);
		for (final String pmName : registeredPolicyManagers) {
			amqpTemplate.convertAndSend(serviceExchange.getName(),
					pmBroadcastRoutingKey + "-" + pmName, UpdatePMConfiguration
							.updatePMConfiguration(systemComponentKey,
									pmTaskConfiguration, eventType));
		}
	}

	/**
	 * @param agentBroadcastRoutingKey
	 *            the agentBroadcastRoutingKey to set
	 */
	public void setAgentBroadcastRoutingKey(
			final String agentBroadcastRoutingKey) {
		this.agentBroadcastRoutingKey = agentBroadcastRoutingKey;
	}

	/**
	 * @param agentHeartbeatListener
	 *            the agentHeartbeatListener to set
	 */
	public void setAgentHeartbeatListener(
			final AgentHeartbeatListener agentHeartbeatListener) {
		this.agentHeartbeatListener = agentHeartbeatListener;
	}

	/**
	 * @param agentService
	 *            the agentService to set
	 */
	public void setAgentService(final AgentService agentService) {
		this.agentService = agentService;
	}

	/**
	 * @param alertService
	 *            the alertService to set
	 */
	public void setAlertService(final AlertService alertService) {
		this.alertService = alertService;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
	}

	/**
	 * @param pmBroadcastRoutingKey
	 *            the pmBroadcastRoutingKey to set
	 */
	public void setPmBroadcastRoutingKey(final String pmBroadcastRoutingKey) {
		this.pmBroadcastRoutingKey = pmBroadcastRoutingKey;
	}

	/**
	 * @param policyConfigurationService
	 *            the policyConfigurationService to set
	 */
	public void setPolicyConfigurationService(
			final InternalPolicyConfigurationService policyConfigurationService) {
		this.policyConfigurationService = policyConfigurationService;
	}

	/**
	 * @param resultQueueRegister
	 *            the resultQueueRegister to set
	 */
	public void setResultQueueRegister(
			final ResultQueueRegister resultQueueRegister) {
		this.resultQueueRegister = resultQueueRegister;
	}

	public void setConfigStorage(ProbeConfigStorageService configStorage) {
		this.configStorage = configStorage;
	}


	public void setProbeEventService(ProbeEventServiceInternal probeEventService) {
		this.probeEventService = probeEventService;
	}
	/**
	 * @param serviceExchange
	 *            the serviceExchange to set
	 */
	public void setServiceExchange(final Exchange serviceExchange) {
		this.serviceExchange = serviceExchange;
	}

	/**
	 * @param taskService
	 *            the taskService to set
	 */
	public void setTaskService(final InternalTaskService taskService) {
		this.taskService = taskService;
	}

	private RequestResponse updateModules(final UpdateModules modulesInfo) {
		RequestResponse requestResponse = modulesInfo.responseOk(serverName);

		try {
			final String agentName = modulesInfo.getOriginName();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Start updating modules from " + agentName);
			}
			agentService.updateModules(agentName, modulesInfo.getModules());

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Modules for " + agentName + " were updated");
			}
		} catch (final Exception ex) {
			requestResponse = modulesInfo.responseError(serverName, ex);
		}

		return requestResponse;
	}

	private RequestResponse updateTasks(final UpdateTasks tasksInfo) {
		RequestResponse requestResponse = tasksInfo.responseOk(serverName);

		try {
			final String agentName = tasksInfo.getOriginName();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Start updating tasks from " + agentName);
			}
			taskService.updateTasks(agentName, tasksInfo.getTasks());
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Tasks from " + agentName + " were updated");
			}
		} catch (final Exception ex) {
			requestResponse = tasksInfo.responseError(serverName, ex);
		}

		return requestResponse;
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.util.Arrays;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.communication.request.UpdateModules;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MMediaAgentModule;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.AggregationType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author kunilov.p
 * 
 */
@Component
public class SendUpdateModules implements QoSTool {

	@Value("${result.send.agent.name}")
	private String agentName;

	@Value("${task.key}")
	private String[] taskKeys;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Override
	public void execute() {
		// Register agent
		// final SendRegisterAgent registerAgent = new SendRegisterAgent();
		// registerAgent.setAmqpTemplate(amqpTemplate);
		// registerAgent.setAgentName(agentName);
		// registerAgent.setTaskKeys(taskKeys);
		// registerAgent.execute();

		// Update modules
		final UpdateModules updateModules = new UpdateModules();
		final MAgent agent = MediaModelConfiguration
				.createComplexAgent(agentName);
		final MMediaAgentModule defaultModule = MediaModelConfiguration
				.createMediaAgentModule(agent);
		// add new stream
		defaultModule.getTemplateStreams().add(
				MediaModelConfiguration.createTemplateLiveStream(0,
						MediaModelConfiguration.LIVE_STREAM_1));
		// remove parameter
		defaultModule.getTemplateResultConfiguration()
				.getParameterConfigurations().remove(0);
		// modify parameter threshold
		final MResultParameterConfiguration parameterConfiguration = defaultModule
				.getTemplateResultConfiguration().getParameterConfigurations()
				.iterator().next();
		parameterConfiguration.getThreshold().setCriticalLevel(1000D);
		parameterConfiguration.setProperties(SharedModelConfiguration
				.createEmptyPropertyConfigurations());
		// add new parameter
		defaultModule.getTemplateResultConfiguration()
				.addParameterConfiguration(
						MediaModelConfiguration
								.createResultParameterConfiguration(
										"newParameter", AggregationType.MAX,
										ParameterType.LEVEL));
		// add new module
		final MAgentModule newModule = MediaModelConfiguration
				.createMediaAgentModule(agent, "newModule");
		// module.setTemplateResultConfiguration(null);
		updateModules.setOriginName(agentName);
		updateModules.setModules(Arrays.<MAgentModule> asList(defaultModule,
				newModule));
		amqpTemplate.convertAndSend("qos.service", "server", updateModules);

	}

	@Override
	public String getDescription() {
		return "Send UpdateModule message";
	}

}

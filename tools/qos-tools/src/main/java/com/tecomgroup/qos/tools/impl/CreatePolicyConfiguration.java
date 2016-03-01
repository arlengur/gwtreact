/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.communication.message.PolicyManagerConfiguration;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.AggregationType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.MediaModelConfiguration;

/**
 * @author abondin
 * 
 */
@Component
public class CreatePolicyConfiguration implements QoSTool {
	@Value("${task.key}")
	private String taskKey;

	@Value("${pm.config.file}")
	private File pmConfig;

	@Value("${result.send.agent.name}")
	private String agentName;

	@Override
	public void execute() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration();
		final MResultConfiguration resultConfiguration = new MResultConfiguration();
		final String fullTaskKey = MediaModelConfiguration.createTaskKey(
				agentName, null, taskKey);

		resultConfiguration.setSamplingRate(1l);
		resultConfiguration
				.addParameterConfiguration(new MResultParameterConfiguration(
						"signalLevel", "signalLevel", AggregationType.MIN,
						ParameterType.LEVEL, new MParameterThreshold(
								ThresholdType.LESS, 50.0, 40.0)));
		final MPolicy policy = new MPolicy();
		policy.setSource(Source.getTaskSource(fullTaskKey));
		final MPolicySendAlert action = new MPolicySendAlert();
		action.setAlertType("qos.it09a.signalLevel");
		action.setName("Send " + action.getAlertType() + " alert");
		policy.addAction(action);
		final MContinuousThresholdFallCondition condition = new MContinuousThresholdFallCondition();
		condition.setParameterIdentifier(new ParameterIdentifier("signalLevel",
				null));
		condition.setCriticalLevel(new ConditionLevel(
				ConditionLevel.THRESHOLD_CRITICAL_LEVEL, 5l,
				ConditionLevel.THRESHOLD_CRITICAL_LEVEL + "+2", 5l));
		condition.setWarningLevel(new ConditionLevel(
				ConditionLevel.THRESHOLD_WARNING_LEVEL, 5l,
				ConditionLevel.THRESHOLD_WARNING_LEVEL + "+2", 5l));
		condition.setThresholdType(ThresholdType.LESS);
		policy.setCondition(condition);
		configuration.addTaskConfiguration(agentName, fullTaskKey, resultConfiguration, policy);
		try {
			new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(
					pmConfig, configuration);
			System.out.println("Configuration saved to "
					+ pmConfig.getAbsoluteFile());
		} catch (final JsonGenerationException e) {
			e.printStackTrace();
		} catch (final JsonMappingException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tecomgroup.qos.tools.QoSTool#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Create simple policy configuration"
				+ "\nSupported VM arguments:"
				+ "\n\tpmConfigFile - path to file" + "\n\ttaskKey - task key";
	}
}

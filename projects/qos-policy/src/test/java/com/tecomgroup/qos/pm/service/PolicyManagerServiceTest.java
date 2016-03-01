/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.service.PolicyManagerService;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.tecomgroup.qos.communication.message.PolicyManagerConfiguration;
import com.tecomgroup.qos.communication.message.ResultMessage;
import com.tecomgroup.qos.communication.pm.PMTaskConfiguration;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.AggregationType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.pm.action.ActionHandler;

/**
 * @author abondin
 *
 */
public class PolicyManagerServiceTest {

	private final String AGENT_NAME = "agent-1";

	private final Source AGENT_SOURCE = Source.getAgentSource(AGENT_NAME);

	private final String lowRSSITaskKey = "108";

	private final String signalLevelTaskKey = "384";

	private final String blackScreenTaskKey = "385";

	private MPolicy lowRSSIPolicy;

	private MPolicy signalLevelPolicy;

	private MPolicy blackScreenPolicy;

	private final String BLACK_SCREEN_PARAMETER_NAME = "blackScreen";

	private final String SIGNAL_LEVEL_PARAMETER_NAME = "signalLevel";

	private final String LOW_RSSI_PARAMETER_NAME = "lowRSSI";

	private ActionHandler actionHandler;

	private DefaultPolicyManagerService policyManager;

	@Before
	public void before() {
		actionHandler = EasyMock.createStrictMock(ActionHandler.class);
		policyManager = new DefaultPolicyManagerService();
		policyManager.setActionHandler(actionHandler);

		lowRSSIPolicy = createPolicy(
				221L,
				lowRSSITaskKey,
				LOW_RSSI_PARAMETER_NAME,
				ThresholdType.LESS,
				new ConditionLevel("36.0", 30l, "40.0", 30l),
				new ConditionLevel("34.0", 10l, "36.0", 20l));

		signalLevelPolicy = createPolicy(
				222L,
				signalLevelTaskKey,
				SIGNAL_LEVEL_PARAMETER_NAME,
				ThresholdType.LESS,
				new ConditionLevel("thresholdWarningLevel-20.0", 5l, "35.0", 5l),
				new ConditionLevel("thresholdCriticalLevel-30.0", 5l, "20.0", 3l));

		blackScreenPolicy = createPolicy(
				223L,
				blackScreenTaskKey,
				BLACK_SCREEN_PARAMETER_NAME,
				ThresholdType.EQUALS,
				new ConditionLevel("1.0", 10l, "1.0", 10l),
				null);

		final PolicyManagerConfiguration config = new PolicyManagerConfiguration();
		config.addTaskConfiguration(
				AGENT_NAME, lowRSSITaskKey,
				createResultConfiguration(new MResultParameterConfiguration(
						LOW_RSSI_PARAMETER_NAME, "Low RSSI",
						AggregationType.MIN, ParameterType.LEVEL,
						new MParameterThreshold(ThresholdType.LESS, 36.0, 34.0))),
				lowRSSIPolicy);
		config.addTaskConfiguration(
				AGENT_NAME, signalLevelTaskKey,
				createResultConfiguration(new MResultParameterConfiguration(
						SIGNAL_LEVEL_PARAMETER_NAME, "Signal Level",
						AggregationType.MIN, ParameterType.LEVEL,
						new MParameterThreshold(ThresholdType.LESS, 50.0, 40.0))),
				signalLevelPolicy);
		config.addTaskConfiguration(
				AGENT_NAME, blackScreenTaskKey,
				createResultConfiguration(new MResultParameterConfiguration(
						BLACK_SCREEN_PARAMETER_NAME, "Black screen",
						AggregationType.MAX, ParameterType.LEVEL,
						new MParameterThreshold(ThresholdType.EQUALS, 1d, null))),
				blackScreenPolicy);

		policyManager.applyConfiguration(config);
	}

	private MPolicy createPolicy(final Long policyId, final String taskKey,
								 final String parameterName, final ThresholdType thresholdType,
								 final ConditionLevel warningLevel,
								 final ConditionLevel criticalLevel) {
		final MPolicy policy = new MPolicy();
		policy.setId(policyId);
		policy.setKey("key" + UUID.randomUUID());
		policy.setSource(Source.getTaskSource(taskKey));
		final MContinuousThresholdFallCondition condition = new MContinuousThresholdFallCondition();
		condition.setParameterIdentifier(new ParameterIdentifier(parameterName,
				null));
		condition.setThresholdType(thresholdType);

		if (warningLevel != null) {
			condition.setWarningLevel(warningLevel);
		}

		if (criticalLevel != null) {
			condition.setCriticalLevel(criticalLevel);
		}

		policy.setCondition(condition);
		return policy;
	}

	public MResultConfiguration createResultConfiguration(
			final MResultParameterConfiguration resultParameterConfiguration) {
		final MResultConfiguration configuration = new MResultConfiguration();
		configuration.setSamplingRate(1l);
		configuration.addParameterConfiguration(resultParameterConfiguration);
		return configuration;
	}

	private ResultMessage getResults(final Date startTime,
									 final String taskKey,
									 final String parameterName,
									 final int step,
									 final Double... values) {
		Date now = new Date(startTime.getTime());
		final List<Result> results = new ArrayList<Result>();
		for (final Double value : values) {
			final Result result = new Result();
			result.setResultDateTime((new SimpleDateFormat(
					Result.RESULT_DATE_FORMAT)).format(now));
			final Map<String, Double> parameters = new HashMap<String, Double>();
			parameters.put(parameterName, value);
			result.setParameters(parameters);
			results.add(result);
			now = new Date(now.getTime() + step);
		}
		final ResultMessage resultMessage = new ResultMessage();
		resultMessage.setTaskKey(taskKey);
		resultMessage.setResults(results);
		return resultMessage;
	}

	private ResultMessage getResults(final Double... values) {
		return getResults(new Date(), signalLevelTaskKey,
				SIGNAL_LEVEL_PARAMETER_NAME, 1000, values);
	}

	private ResultMessage getFixedTimeResults(Date startTime, final Double... values) {
		return getResults(startTime, signalLevelTaskKey,
				SIGNAL_LEVEL_PARAMETER_NAME, 1000, values);
	}

	private void expectNullAlert() {
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Map<String, Object> params = (Map<String, Object>) EasyMock
						.getCurrentArguments()[1];
				Assert.assertNull(params
						.get(ActionHandler.OUTPUT_PARAMETER_ALERT_SEVERITY));
				return null;
			}
		});
	}

	private void expectAlert(final String alertSeverity) {
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Map<String, Object> params = (Map<String, Object>) EasyMock
						.getCurrentArguments()[1];
				Assert.assertEquals(
						alertSeverity,
						params.get(
								ActionHandler.OUTPUT_PARAMETER_ALERT_SEVERITY)
								.toString());
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void lowRssiAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectAlert(PerceivedSeverity.CRITICAL.toString());

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectNullAlert();

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getResults(new Date(),
				lowRSSITaskKey, LOW_RSSI_PARAMETER_NAME, 10000, 35.0, 35.0, 35.0,
				35.0, 33.0, 33.0, 33.0, 33.0, 37.0, 37.0, 37.0, 37.0, 41.0, 41.0, 41.0,
				41.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void lowRssiClearAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectNullAlert();

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getResults(new Date(),
				lowRSSITaskKey, LOW_RSSI_PARAMETER_NAME, 10000, 75.0, 75.0, 75.0,
				75.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void raiseAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectAlert(PerceivedSeverity.CRITICAL.toString());

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getResults(30.0, 20.0, 11.0, 9.0,
				9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 7.0, 15.0,
				20.0, 21.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void raiseAlertTimeCheck() {

		final Date now = new Date();

		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Map<String, Object> params = (Map<String, Object>) EasyMock
						.getCurrentArguments()[1];
				Assert.assertEquals(
						PerceivedSeverity.WARNING.toString(),
						params.get(
								ActionHandler.OUTPUT_PARAMETER_ALERT_SEVERITY)
								.toString());
				Date expectedWarningDate = new Date(now.getTime() + 1000);
				Date warningStartDate = (Date) params.get(PolicyManagerService.OUTPUT_PARAMETER_CURRENT_TIMESTAMP);
				Assert.assertEquals (expectedWarningDate.toString(),warningStartDate.toString());
				return null;
			}
		});

		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Map<String, Object> params = (Map<String, Object>) EasyMock
						.getCurrentArguments()[1];
				Assert.assertEquals(
						PerceivedSeverity.CRITICAL.toString(),
						params.get(
								ActionHandler.OUTPUT_PARAMETER_ALERT_SEVERITY)
								.toString());
				Date expectedCriticalDate = new Date(now.getTime() + 3000);
				Date criticalStartDate = (Date) params.get(PolicyManagerService.OUTPUT_PARAMETER_CURRENT_TIMESTAMP);
				Assert.assertEquals (expectedCriticalDate.toString(),criticalStartDate.toString());
				return null;
			}
		});

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getFixedTimeResults(new Date(), 30.0, 20.0, 11.0, 9.0,
				9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 7.0, 15.0,
				20.0, 21.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void raiseAndClearAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectAlert(PerceivedSeverity.CRITICAL.toString());

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectNullAlert();

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getResults(30.0, 20.0, 11.0, 9.0,
				9.0, 8.0, 7.0, 15.0, 9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 7.0, 15.0,
				40.0, 20.0, 21.0, 25.0, 25.0, 25.0, 25.0, 26.0, 40.0, 40.0,
				40.0, 40.0, 40.0, 40.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void raiseWCWCWAndClearAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectAlert(PerceivedSeverity.CRITICAL.toString());

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectAlert(PerceivedSeverity.CRITICAL.toString());

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectNullAlert();

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getResults(20.0, 11.0, 9.0,
				9.0, 8.0, 7.0, 9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 39.0, 20.0,
				21.0, 25.0, 9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 40.0, 40.0, 40.0, 40.0, 40.0, 40.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void raiseCWAndClearAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectAlert(PerceivedSeverity.CRITICAL.toString());

		expectAlert(PerceivedSeverity.WARNING.toString());

		expectNullAlert();

		EasyMock.replay(actionHandler);
		final ResultMessage resultMessage = getResults(9.0, 8.0, 7.0, 9.0, 9.0, 8.0, 40.0, 40.0, 40.0, 40.0, 40.0, 40.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void raiseBlackScreenAlert() {
		actionHandler.doAction(EasyMock.anyObject(MPolicy.class),
				EasyMock.anyObject(Map.class));

		expectNullAlert();

		expectAlert(PerceivedSeverity.WARNING.toString());

		EasyMock.replay(actionHandler);

		final ResultMessage resultMessage = getResults(new Date(),
				blackScreenTaskKey, BLACK_SCREEN_PARAMETER_NAME, 1000, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0,
				1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		policyManager.handleSingleValueResult(resultMessage.getTaskKey(),
				resultMessage.getResults());
		EasyMock.verify(actionHandler);
	}

	@Test
	public void testRemovePMConfigurationByDeletingPolicies() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration(
				policyManager.getConfiguration().getAgents(), policyManager
				.getConfiguration().getConfigurations());

		final Source taskSource = Source.getTaskSource(signalLevelTaskKey);
		final List<MPolicy> policies = new ArrayList<MPolicy>(configuration
				.getConfigurations().get(taskSource).getPolicies());

		policyManager.removePMConfiguration(new PMTaskConfiguration(AGENT_SOURCE, taskSource,
				null, Arrays.asList(signalLevelPolicy)));

		Assert.assertEquals(configuration.getConfigurations().size(),
				policyManager.getConfiguration().getConfigurations().size());

		Assert.assertEquals(policies.size() - 1, policyManager
				.getConfiguration().getConfigurations().get(taskSource)
				.getPolicies().size());
	}

	@Test
	public void testRemovePMConfigurationByDeletingTaskConfiguration() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration(
				policyManager.getConfiguration().getAgents(), policyManager
				.getConfiguration().getConfigurations());

		policyManager
				.removePMConfiguration(new PMTaskConfiguration(
						AGENT_SOURCE, Source.getTaskSource(signalLevelTaskKey),
						createResultConfiguration(new MResultParameterConfiguration(
								SIGNAL_LEVEL_PARAMETER_NAME, "Signal Level",
								AggregationType.MIN, ParameterType.LEVEL,
								new MParameterThreshold(ThresholdType.LESS,
										50.0, 40.0))), null));

		Assert.assertEquals(configuration.getConfigurations().size() - 1,
				policyManager.getConfiguration().getConfigurations().size());
	}

	@Test
	public void testUpdatePMConfigurationByAddingNewTaskConfiguration() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration(
				policyManager.getConfiguration().getAgents(), policyManager
				.getConfiguration().getConfigurations());

		policyManager
				.updatePMConfiguration(new PMTaskConfiguration(
						AGENT_SOURCE, Source.getTaskSource("390"),
						createResultConfiguration(new MResultParameterConfiguration(
								SIGNAL_LEVEL_PARAMETER_NAME, "Signal Level",
								AggregationType.MIN, ParameterType.LEVEL,
								new MParameterThreshold(ThresholdType.LESS,
										50.0, 40.0))), null));

		Assert.assertEquals(configuration.getConfigurations().size() + 1,
				policyManager.getConfiguration().getConfigurations().size());
	}

	@Test
	public void testUpdatePMConfigurationByAddingPolicy() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration(
				policyManager.getConfiguration().getAgents(), policyManager
				.getConfiguration().getConfigurations());

		final Source taskSource = Source.getTaskSource(signalLevelTaskKey);
		final List<MPolicy> policies = new ArrayList<MPolicy>(configuration
				.getConfigurations().get(taskSource).getPolicies());

		final MPolicy signalLevelPolicy = createPolicy(333L,
				signalLevelTaskKey, SIGNAL_LEVEL_PARAMETER_NAME,
				ThresholdType.LESS, new ConditionLevel(
						"thresholdWarningLevel-20.0", 5l, "35.0", 5l),
				new ConditionLevel("thresholdCriticalLevel-30.0", 5l, "20.0",
						3l));
		policyManager.updatePMConfiguration(new PMTaskConfiguration(AGENT_SOURCE, taskSource,
				null, Arrays.asList(signalLevelPolicy)));

		Assert.assertEquals(configuration.getConfigurations().size(),
				policyManager.getConfiguration().getConfigurations().size());

		Assert.assertEquals(policies.size() + 1, policyManager
				.getConfiguration().getConfigurations().get(taskSource)
				.getPolicies().size());
	}

	@Test
	public void testUpdatePMConfigurationByUpdatingPolicy() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration(
				policyManager.getConfiguration().getAgents(), policyManager
				.getConfiguration().getConfigurations());

		final Source taskSource = Source.getTaskSource(signalLevelTaskKey);

		signalLevelPolicy.setSource(Source.getTaskSource("390"));
		policyManager.updatePMConfiguration(new PMTaskConfiguration(AGENT_SOURCE, taskSource,
				null, Arrays.asList(signalLevelPolicy)));

		Assert.assertEquals(configuration.getConfigurations().size(),
				policyManager.getConfiguration().getConfigurations().size());

		Assert.assertEquals(configuration.getConfigurations().get(taskSource)
				.getPolicies().size(), policyManager.getConfiguration()
				.getConfigurations().get(taskSource).getPolicies().size());
	}

	@Test
	public void testUpdatePMConfigurationByUpdatingTaskConfiguration() {
		final PolicyManagerConfiguration configuration = new PolicyManagerConfiguration(
				policyManager.getConfiguration().getAgents(), policyManager
				.getConfiguration().getConfigurations());

		policyManager
				.updatePMConfiguration(new PMTaskConfiguration(
						AGENT_SOURCE, Source.getTaskSource(signalLevelTaskKey),
						createResultConfiguration(new MResultParameterConfiguration(
								SIGNAL_LEVEL_PARAMETER_NAME, "Signal Level",
								AggregationType.MIN, ParameterType.LEVEL,
								new MParameterThreshold(ThresholdType.LESS,
										50.0, 40.0))), null));

		Assert.assertEquals(configuration.getConfigurations().size(),
				policyManager.getConfiguration().getConfigurations().size());
	}
}

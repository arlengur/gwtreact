/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.handler;

import java.util.*;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.messages.DefaultPolicyValidationMessages;
import com.tecomgroup.qos.util.JSEvaluator;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * @author abondin
 * 
 */
public class ContinuousThresholdFallHandler
		implements ParameterPolicyHandler {

	private final MPolicy policy;
	private final MResultConfiguration resultConfiguration;

	private final MContinuousThresholdFallCondition condition;
	private final Map<PerceivedSeverity, Long> pendingRaise = new HashMap<PerceivedSeverity, Long>();

	private final Map<PerceivedSeverity, Long> pendingCease = new HashMap<PerceivedSeverity, Long>();

	private final AlertHolder alerts;

	private final SortedMap<PerceivedSeverity, ConditionLevel> ascConditionLevels = new TreeMap<PerceivedSeverity, ConditionLevel>(
			MAlert.SEVERITY_ASC_COMPARATOR);

	private final SortedMap<PerceivedSeverity, ConditionLevel> descConditionLevels = new TreeMap<PerceivedSeverity, ConditionLevel>(
			MAlert.SEVERITY_DESC_COMPARATOR);

	private boolean initialized = false;

	/**
	 * @param policy
	 */
	public ContinuousThresholdFallHandler(final MPolicy policy,
										  final MResultConfiguration resultConfiguration) {
		super();
		this.policy = policy;
		this.resultConfiguration = resultConfiguration;
		this.condition = (MContinuousThresholdFallCondition) policy
				.getCondition();
		this.alerts = new AlertHolder(condition);
		initializeConditionLevels();
	}

	private Map<PerceivedSeverity, ConditionLevel> getConditionLevels() {
		final Map<PerceivedSeverity, ConditionLevel> conditionLevels = new HashMap<PerceivedSeverity, ConditionLevel>();
		if (condition.getWarningLevel() != null) {
			conditionLevels.put(PerceivedSeverity.WARNING,
					this.condition.getWarningLevel());
		}
		if (condition.getCriticalLevel() != null) {
			conditionLevels.put(PerceivedSeverity.CRITICAL,
					this.condition.getCriticalLevel());
		}

		return conditionLevels;
	}

	@Override
	public ParameterIdentifier getParameterIdentifier() {
		return condition.getParameterIdentifier();
	}

	@Override
	public MPolicy getPolicy() {
		return policy;
	}

	/**
	 * @return the resultConfiguration
	 */
	public MResultConfiguration getResultConfiguration() {
		return resultConfiguration;
	}

	@Override
	public Map<String, Object> handleResult(final Date timestamp, final Double value) {
		if (!initialized) {
			return null;
		}

		// process raise level
		for (final PerceivedSeverity severity : ascConditionLevels.keySet()) {
			final ConditionLevel conditionLevel = ascConditionLevels.get(severity);
			if (condition.getThresholdType().accept(value, conditionLevel.getRaiseLevelDouble())) {
				pendingCease.remove(severity);
				if (startRaiseConditionProcessing(severity)) {
					Long startTime = pendingRaise.get(severity);
					if (startTime == null) {
						startTime = timestamp.getTime();
						pendingRaise.put(severity, startTime);
					}
					final Long duration = (timestamp.getTime() - startTime) / 1000;
					if (duration >= conditionLevel.getRaiseDuration()) {
						alerts.addActiveAlert(severity, duration);
						pendingRaise.remove(severity);
					}
				}
			}
		}

		Long ceaseDuration = null;
		boolean isCleared = false;
		// process cease level
		for (final PerceivedSeverity severity : descConditionLevels.keySet()) {
			final ConditionLevel conditionLevel = descConditionLevels.get(severity);
			if (startCeaseConditionProcessing(severity)) {
				Boolean conditionValue = null;

				if(alerts.exists(severity) && alerts.isUnknown(severity)) {
					conditionValue = !(condition.getThresholdType().accept(value, conditionLevel.getRaiseLevelDouble()));
				} else {
					conditionValue = condition.getThresholdType().inverse().accept(value, conditionLevel.getCeaseLevelDouble());
				}
				if (Boolean.TRUE.equals(conditionValue)) {
					pendingRaise.remove(severity);
					Long startTime = pendingCease.get(severity);
					if (startTime == null) {
						startTime = timestamp.getTime();
						pendingCease.put(severity, startTime);
					}
					final Long duration = (timestamp.getTime() - startTime) / 1000;
					if (duration >= conditionLevel.getCeaseDuration()) {
						if (alerts.exists(severity)) {
							isCleared = true;
							ceaseDuration = duration;
							alerts.removeSeverity(severity);
						}
						pendingCease.remove(severity);
					}
				}
			}
		}

		if (isCleared) {
			if (alerts.isEmpty()) {
				return alerts.getAlertPropertis(null, timestamp, ceaseDuration);
			} else {
				final PerceivedSeverity worstSeverity = alerts.findAnySeverity();
				if (!alerts.isUnknown(worstSeverity)) {
					Long duration = alerts.getDuration(worstSeverity);
					alerts.addProcessedAlert(worstSeverity, duration);
					return alerts.getAlertPropertis(worstSeverity, timestamp, duration);
				}
			}
		}

		if (!alerts.isEmpty()) {
			final PerceivedSeverity worst = alerts.findWorstSeverity();
			if (worst != null && alerts.isActive(worst)) {
				Long duration = alerts.getDuration(worst);
				alerts.addProcessedAlert(worst, duration);
				return alerts.getAlertPropertis(worst, timestamp, duration);
			}
		}

		return null;
	}

	private void initializeConditionLevels() {
		try {
			PolicyUtils.validateAndInitPolicyCondition(policy,
					resultConfiguration, JSEvaluator.getInstance(),
					DefaultPolicyValidationMessages.getInstance());
			initialized = true;
		} catch (final Exception ex) {
			initialized = false;
		}
		if (initialized) {
			final Map<PerceivedSeverity, ConditionLevel> conditionLevels = getConditionLevels();
			for (final Map.Entry<PerceivedSeverity, ConditionLevel> conditionLevelEntry : conditionLevels
					.entrySet()) {
				final ConditionLevel conditionLevel = conditionLevelEntry
						.getValue();
				final PerceivedSeverity severity = conditionLevelEntry.getKey();
				ascConditionLevels.put(severity, conditionLevel);
				descConditionLevels.put(severity, conditionLevel);
				alerts.addDefaultAlertServerity(severity);
			}
		}
	}

	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	private boolean startCeaseConditionProcessing(
			final PerceivedSeverity severity) {
		return alerts.exists(severity) || pendingRaise.get(severity) != null;
	}

	private boolean startRaiseConditionProcessing(
			final PerceivedSeverity severity) {
		return !alerts.exists(severity) || alerts.isUnknown(severity);
	}
}

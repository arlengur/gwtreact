/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import java.io.Serializable;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages.DefaultFormattedResultMessages;
import com.tecomgroup.qos.gwt.client.model.results.ParameterRow;
import com.tecomgroup.qos.gwt.shared.JSEvaluator;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * Client wrapper to display information about policy
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class PolicyWrapper implements Serializable, PolicyTreeGridRow {

	private final MPolicy policy;

	private String parameterDisplayName;

	private String taskDisplayName;

	private String agentDisplayName;

	private final String key;

	private final String displayName;

	private String criticalRaise;

	private String criticalCease;

	private String warningRaise;

	private String warningCease;

	private final QoSMessages messages;

	private String actionsTemplateName;

	private String conditionsTemplateName;

	public PolicyWrapper(final MPolicy policy, final MAgentTask task,
			final QoSMessages messages) {
		this.messages = messages;
		this.policy = policy;
		this.key = policy.getKey();

		actionsTemplateName = getTemplateName(policy.getActionsTemplate());
		conditionsTemplateName = getTemplateName(policy.getConditionsTemplate());

		final String displayNameCandidate = policy.getDisplayName();
		this.displayName = displayNameCandidate == null
				|| displayNameCandidate.isEmpty()
				? policy.getKey()
				: displayNameCandidate;

		init(policy, task);
	}

	@Override
    public String getActionsTemplateName() {
		return actionsTemplateName;
	}

	/**
	 * @return the agent
	 */
	@Override
    public String getAgent() {
		return agentDisplayName;
	}

	public String getConditionsTemplateName() {
		return conditionsTemplateName;
	}

	/**
	 * @return the criticalCease
	 */
	@Override
    public String getCriticalCease() {
		return criticalCease;
	}

	/**
	 * @return the criticalRaise
	 */
	@Override
    public String getCriticalRaise() {
		return criticalRaise;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	private String getFormmatedLevel(final ConditionLevel conditionLevel,
			final ThresholdType thresholdType,
			final ParameterType parameterType, final boolean raise) {
		final Double value;
		final Long duration;
		final ThresholdType operation;
		if (raise) {
			value = conditionLevel.getRaiseLevelDouble();
			duration = conditionLevel.getRaiseDuration();
			operation = thresholdType;
		} else {
			value = conditionLevel.getCeaseLevelDouble();
			duration = conditionLevel.getCeaseDuration();
			operation = thresholdType.inverse();
		}
		final StringBuilder builder = new StringBuilder();
		if (value != null) {
			if (parameterType != ParameterType.BOOL) {
				builder.append(operation);
				builder.append(" ");
			}
			builder.append(ParameterRow.formatValue(value, parameterType,
					new DefaultFormattedResultMessages(messages), !raise));
			if (duration != null && !duration.equals(0l)) {
				builder.append(" (");
				builder.append(duration);
				builder.append(" ");
				builder.append(messages.secondsShort());
				builder.append(")");
			}
		}
		return builder.toString();
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

    @Override
    public String getName() {
        return policy.getDisplayName();
    }

    /**
	 * @return the parameterDisplayName
	 */
	@Override
    public String getParameterDisplayName() {
		return parameterDisplayName;
	}

	/**
	 * @return the policy
	 */
	public MPolicy getPolicy() {
		return policy;
	}

	/**
	 * @return the taskDisplayName
	 */
	@Override
    public String getSourceDisplayName() {
		return taskDisplayName;
	}

	private String getTemplateName(final MPolicyComponentTemplate template) {
		return template == null ? null : template.getName();
	}

	/**
	 * @return the warningCease
	 */
	@Override
    public String getWarningCease() {
		return warningCease;
	}

	/**
	 * @return the warningRaise
	 */
	@Override
    public String getWarningRaise() {
		return warningRaise;
	}

	private void init(final MPolicy policy, final MAgentTask task) {
		if (policy != null && task != null) {
			MResultParameterConfiguration parameterConfiguration = null;
			// init policy
			if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
				final MContinuousThresholdFallCondition policyCondition = (MContinuousThresholdFallCondition) policy
						.getCondition();
				PolicyUtils.validateAndInitPolicyCondition(policy,
						task.getResultConfiguration(),
						JSEvaluator.getInstance(), messages);

				final ParameterIdentifier parameterIdentifier = policyCondition
						.getParameterIdentifier();
				parameterDisplayName = parameterIdentifier.getName();
				ConditionLevel criticalLevel = policyCondition
						.getCriticalLevel();
				ConditionLevel warningLevel = policyCondition.getWarningLevel();
				if (criticalLevel == null) {
					criticalLevel = new ConditionLevel(null, null, null, null);
				}
				if (warningLevel == null) {
					warningLevel = new ConditionLevel(null, null, null, null);
				}
				parameterConfiguration = task.getResultConfiguration()
						.findParameterConfiguration(parameterIdentifier);
				if (parameterConfiguration != null) {
					ThresholdType thresholdType = policyCondition
							.getThresholdType();
					if (thresholdType == null) {
						thresholdType = parameterConfiguration.getThreshold()
								.getType();
					}
					criticalRaise = getFormmatedLevel(criticalLevel,
							thresholdType, parameterConfiguration.getType(),
							true);
					criticalCease = getFormmatedLevel(criticalLevel,
							thresholdType, parameterConfiguration.getType(),
							false);
					warningRaise = getFormmatedLevel(warningLevel,
							thresholdType, parameterConfiguration.getType(),
							true);
					warningCease = getFormmatedLevel(warningLevel,
							thresholdType, parameterConfiguration.getType(),
							false);
				}
			}
			// init displayNames
			agentDisplayName = task.getModule().getAgent().getDisplayName();
			if (parameterConfiguration != null) {
				parameterDisplayName = parameterConfiguration
						.getParsedDisplayFormat();
			}
			taskDisplayName = task.getDisplayName();
		}
	}

	public void setActionsTemplateName(final String actionsTemplateName) {
		this.actionsTemplateName = actionsTemplateName;
	}

	public void setConditionsTemplateName(final String conditionsTemplateName) {
		this.conditionsTemplateName = conditionsTemplateName;
	}
}
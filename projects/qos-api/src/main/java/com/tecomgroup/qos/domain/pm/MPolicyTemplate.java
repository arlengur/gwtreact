/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MPolicyTemplate extends MPolicySharedData {

	private String agentName;

	public MPolicyTemplate() {
		super();
	}

	public MPolicyTemplate(final String agentName,
			final MPolicySharedData policy) {
		super(policy);
		this.agentName = agentName;
		this.key = policy.getKey();
	}

	/**
	 * Create policy from policy template. If the parameter of the task for
	 * created policy is disabled then policy will be disabled too.
	 * 
	 * @param policyKey
	 * @param task
	 * @param parameterIdentifier
	 *            identifier of task parameter. ParameterIdentifier of policy
	 *            condition should be obligatory replaced by provided one which
	 *            is a parameter identifier of a real task parameter.
	 * @return
	 */
	public MPolicy createPolicy(final String policyKey, final MAgentTask task,
			final ParameterIdentifier parameterIdentifier) {
		final MPolicy policy = new MPolicy();
		final List<MPolicyAction> policyActions = new ArrayList<MPolicyAction>();
		for (final MPolicyAction policyAction : this.getActions()) {
			policyActions.add(policyAction.copy());
		}
		policy.setActions(policyActions);
		policy.setKey(policyKey);
		policy.setDisplayName(this.getDisplayName());
		final MContinuousThresholdFallCondition condition = (MContinuousThresholdFallCondition) this
				.getCondition().copy();
		condition.setParameterIdentifier(new ParameterIdentifier(
				parameterIdentifier));
		policy.setCondition(condition);
		policy.setSource(Source.getTaskSource(task.getKey()));
		policy.setDisabled(task.isParameterDisabled(parameterIdentifier)
				|| task.isDisabled());
		policy.setDeleted(task.isDeleted());
		return policy;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(final String agentName) {
		this.agentName = agentName;
	}

	@Override
	public String toString() {
		return "{key = " + getKey() + ", agentKey = " + getAgentName()
				+ ", source = " + getSource() + ", displayName = "
				+ getDisplayName() + "}";
	}

	@Override
	public boolean updateSimpleFields(final MSource source) {
		// agenName (agentKey) must not be changed, it is a part of the unique
		// key of the policy template.
		return super.updateSimpleFields(source);
	}
}

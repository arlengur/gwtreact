/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;

/**
 * @author abondin
 * 
 */
@Entity(name = "MContinuousFallCondition")
@SuppressWarnings("serial")
public class MContinuousThresholdFallCondition extends MPolicyConditionLevels {

	@Column(nullable = false)
	@Embedded
	private ParameterIdentifier parameterIdentifier;

	public MContinuousThresholdFallCondition() {
		super();
	}

	public MContinuousThresholdFallCondition(
			final MContinuousThresholdFallCondition condition) {
		super(condition);
		this.parameterIdentifier = new ParameterIdentifier(
				condition.getParameterIdentifier());
	}

	public MContinuousThresholdFallCondition(
			final MPolicyConditionLevels conditionLevels) {
		super(conditionLevels);
	}

	@Override
	public MContinuousThresholdFallCondition copy() {
		return new MContinuousThresholdFallCondition(this);
	}

	/**
	 * @return the parameterIdentifier
	 */
	public ParameterIdentifier getParameterIdentifier() {
		return parameterIdentifier;
	}

	/**
	 * @param parameterIdentifier
	 *            the parameterIdentifier to set
	 */
	public void setParameterIdentifier(
			final ParameterIdentifier parameterIdentifier) {
		this.parameterIdentifier = parameterIdentifier;
	}

	@Override
	public boolean updateSimpleFields(final MPolicyCondition condition) {
		// parameterIdentifier is not updatable. It is a key field of the
		// condition.
		return super.updateSimpleFields(condition);
	}
}

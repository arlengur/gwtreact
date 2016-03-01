/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;

/**
 * A template of {@link MPolicyCondition}. Can be applied to a policy.
 * 
 * @author kunilov.p
 * 
 */
@Entity
public class MPolicyConditionsTemplate extends MPolicyComponentTemplate {

	private static final long serialVersionUID = -9098604403487260054L;

	@OneToOne(cascade = CascadeType.ALL, optional = false)
	private MPolicyConditionLevels conditionLevels;

	/**
	 * Template can be applied to policies with same parameter type.
	 */
	private ParameterType parameterType;

	public MPolicyConditionsTemplate() {
		super();
	}

	public MPolicyConditionsTemplate(
			final MPolicyConditionsTemplate policyConditionsTemplate) {
		super(policyConditionsTemplate);
	}

	public MPolicyConditionLevels getConditionLevels() {
		return conditionLevels;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setConditionLevels(final MPolicyConditionLevels conditionLevels) {
		this.conditionLevels = conditionLevels;
	}

	public void setParameterType(final ParameterType parameterType) {
		this.parameterType = parameterType;
	}
}

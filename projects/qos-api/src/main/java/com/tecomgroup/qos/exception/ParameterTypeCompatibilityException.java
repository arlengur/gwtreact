/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;

/**
 * @author sviyazov.a
 * 
 */
public class ParameterTypeCompatibilityException extends ServiceException {

	private static final long serialVersionUID = -7088475927146007914L;

	private ParameterType policyParameterType;

	private ParameterType templateParameterType;

	public ParameterTypeCompatibilityException() {
		super();
	}

	public ParameterTypeCompatibilityException(final String message) {
		super(message);
	}

	public ParameterTypeCompatibilityException(final String message,
			final ParameterType policyParameterType,
			final ParameterType templateParameterType) {
		this();
		this.policyParameterType = policyParameterType;
		this.templateParameterType = templateParameterType;
	}

	public ParameterTypeCompatibilityException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public ParameterTypeCompatibilityException(final Throwable cause) {
		super(cause);
	}

	public ParameterType getPolicyParameterType() {
		return policyParameterType;
	}

	public ParameterType getTemplateParameterType() {
		return templateParameterType;
	}

	public void setPolicyParameterType(final ParameterType policyParameterType) {
		this.policyParameterType = policyParameterType;
	}

	public void setTemplateParameterType(
			final ParameterType templateParameterType) {
		this.templateParameterType = templateParameterType;
	}
}

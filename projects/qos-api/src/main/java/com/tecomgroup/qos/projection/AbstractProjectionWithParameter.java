/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

/**
 * Projection with parameter and type.
 * 
 * @author kunilov.p
 * 
 */
public abstract class AbstractProjectionWithParameter
		extends
			AbstractProjection {
	private static final long serialVersionUID = 5963206126282176373L;

	protected String parameter;

	public AbstractProjectionWithParameter(final String parameter,
			final Operator operator) {
		super(operator);
		this.parameter = parameter;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}
}

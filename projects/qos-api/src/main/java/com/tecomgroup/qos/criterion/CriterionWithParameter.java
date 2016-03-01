/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.io.Serializable;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public abstract class CriterionWithParameter implements Criterion, Serializable {
	protected String parameter;

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}
	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}
}

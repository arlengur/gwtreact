/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

import java.io.Serializable;

/**
 * Projection with type.
 * 
 * @author kunilov.p
 * 
 */
public abstract class AbstractProjection implements Projection, Serializable {
	private static final long serialVersionUID = -7063049255742220937L;

	protected final Operator operator;

	public AbstractProjection(final Operator operator) {
		this.operator = operator;
	}

	/**
	 * @return the type
	 */
	@Override
	public Operator getOperator() {
		return operator;
	}
}

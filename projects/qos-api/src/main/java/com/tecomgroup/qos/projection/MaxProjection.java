/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

/**
 * Projection to get maximum value of provided parameter.
 * 
 * @author novohatskiy.r
 * 
 */
public class MaxProjection extends AbstractProjectionWithParameter {

	private static final long serialVersionUID = -2636581092641154064L;

	public MaxProjection(final String parameter) {
		super(parameter, Operator.max);
	}
}

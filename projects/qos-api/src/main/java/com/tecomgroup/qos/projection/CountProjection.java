/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

/**
 * Projection to get count of provided parameter.
 * 
 * @author kunilov.p
 * 
 */
public class CountProjection extends AbstractProjectionWithParameter {
	private static final long serialVersionUID = -4099227379814756728L;

	public CountProjection(final String parameter) {
		super(parameter, Operator.count);
	}

}

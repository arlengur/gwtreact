/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

/**
 * Projection to get count of rows.
 * 
 * @author kunilov.p
 */
public class RowCountProjection extends AbstractProjection {
	private static final long serialVersionUID = -7032599203035073790L;

	public RowCountProjection() {
		super(Operator.rowCount);
	}

}

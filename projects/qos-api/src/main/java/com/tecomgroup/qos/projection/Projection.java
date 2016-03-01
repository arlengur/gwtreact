/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

/**
 * General <tt>Projection<tt> interface.
 * 
 * An object-oriented representation of a query projection.
 * <tt>Projection<tt> is used for different operators (aggregation functions) like
 * count, rowCount, avg, max, min and others to get unique result of this operator.
 * 
 * Built-in projection types are provided  by the {@link ProjectionFactory} factory class.  
 * This interface might be implemented by application classes that define custom projections. 
 * 
 * There are several abstract classes {@link AbstractProjection}, {@link AbstractProjectionWithParameter} 
 * that shoud be regarded before custom implementation of this interface.
 * 
 * @author kunilov.p
 * 
 */
public interface Projection {
	public enum Operator {
		rowCount, count, max, min
	}

	Operator getOperator();
}

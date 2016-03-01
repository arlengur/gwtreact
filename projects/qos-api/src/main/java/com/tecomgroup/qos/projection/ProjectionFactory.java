/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;

/**
 * <tt>ProjectionFactory</tt> may be used by applications as a framework for
 * building new kinds of <tt>Projection</tt>. However, it is intended that most
 * applications will simply use the built-in projection types via the static
 * factory methods of this class.
 * 
 * @author kunilov.p
 * 
 */
public final class ProjectionFactory {

	public static Projection count(final String parameter) {
		return new CountProjection(parameter);
	}

	public static Projection max(final String parameter) {
		return new MaxProjection(parameter);
	}

	public static Projection rowCount() {
		return new RowCountProjection();
	}

    public static Projection min(final String parameter) {
        return new MinProjection(parameter);
    }
}

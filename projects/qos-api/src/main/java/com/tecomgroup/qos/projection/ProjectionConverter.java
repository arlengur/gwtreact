/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.projection;


/**
 * @author kunilov.p
 * 
 */
public interface ProjectionConverter {

	/**
	 * @param rootEntityAlias
	 *            Alias for root entity.
	 * @param projection
	 * @return native projection
	 */
	Object toNativeProjection(String rootEntityAlias, Projection projection);
}

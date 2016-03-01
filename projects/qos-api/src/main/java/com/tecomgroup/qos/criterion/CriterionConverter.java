/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

/**
 * @author kunilov.p
 * 
 */
public interface CriterionConverter {
	/**
	 * @param rootEntityAlias
	 *            Alias for root entity.
	 * @param criterion
	 * @return native criterion
	 */
	Object toNativeCriterion(String rootEntityAlias, Criterion criterion);
}

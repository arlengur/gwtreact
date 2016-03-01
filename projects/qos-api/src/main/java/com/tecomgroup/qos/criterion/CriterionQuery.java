/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.util.Collection;

/**
 * @author abondin
 * 
 */
public interface CriterionQuery {

	/**
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	Criterion and(Criterion left, Criterion right);

	/**
	 * 
	 * @param propertyName
	 * @param low
	 * @param high
	 * @return
	 */
	Criterion between(String propertyName, Object low, Object high);

	/**
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	Criterion collectionContains(String propertyName, Object value);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Criterion eq(String propertyName, Object value);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Criterion ge(String propertyName, Object value);

	/**
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	Criterion ilike(String propertyName, Object value);
	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Criterion in(String propertyName, Collection<?> value);

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	Criterion isEmpty(String propertyName);

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	Criterion isNotEmpty(String propertyName);

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	Criterion isNotNull(String propertyName);

	/**
	 * 
	 * @param propertyName
	 * @return
	 */
	Criterion isNull(String propertyName);

	/**
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	Criterion istringContains(String propertyName, Object value);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Criterion le(String propertyName, Object value);

	/**
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	Criterion like(String propertyName, Object value);

	/**
	 * 
	 * @param criterion
	 * @return
	 */
	Criterion not(Criterion criterion);

	/**
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	Criterion or(Criterion left, Criterion right);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * */
	Criterion stringContains(String propertyName, Object value);
}

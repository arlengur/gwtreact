/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;


/**
 * @author abondin
 * 
 */
public interface UpdatableEntity<M> {
	/**
	 * Updates all simple not key fields of the entity
	 * 
	 * @param source
	 * 
	 * @return true if entity was changed otherwise false
	 */
	boolean updateSimpleFields(M source);
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import com.tecomgroup.qos.domain.MAbstractEntity;

/**
 * @author kunilov.p
 * 
 */
public interface CopyingEntity<T extends MAbstractEntity> {

	void copyTo(final T entity);
}

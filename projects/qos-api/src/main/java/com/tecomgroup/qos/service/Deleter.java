/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.Deleted;

/**
 * An interface of the service which can delete objects.
 * 
 * @author kunilov.p
 * 
 */
public interface Deleter<M extends Deleted> {

	void delete(M entity);
}

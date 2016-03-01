/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.Disabled;

/**
 * An interface of the service which can disable objects.
 * 
 * @author kunilov.p
 * 
 */
public interface Disabler<M extends Disabled> {

	void disable(M entiry);
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * The Disposable interface is used when components need to deallocate and
 * dispose resources prior to their destruction.
 * 
 * @see Initializable
 * @see Disposable
 * @see Destroyable
 * 
 * @author ivlev.e
 * 
 */
public interface Disposable {

	void dispose();
}

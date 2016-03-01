/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * The Initializable interface is used by components that need to allocate
 * resources prior to them becoming active.
 * 
 * @see Initializable
 * @see Disposable
 * @see Destroyable
 * 
 * @author ivlev.e
 */
public interface Initializable {

	void initialize();
}

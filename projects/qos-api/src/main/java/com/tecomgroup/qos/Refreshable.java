/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * The refreshable interface is used by components that need to be refreshed on
 * resizing.
 *
 * @author tabolin.a
 * @see Initializable
 * @see Disposable
 * @see Destroyable
 */
public interface Refreshable {

	void refresh();

}

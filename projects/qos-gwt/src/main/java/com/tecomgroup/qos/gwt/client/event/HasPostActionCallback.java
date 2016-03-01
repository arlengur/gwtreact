/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

/**
 * Events implementing this interface has callback, which is called after action
 * execution
 * 
 * @author novohatskiy.r
 * 
 */
public interface HasPostActionCallback {

	public static interface PostActionCallback {
		void actionPerformed(Object executionResult);
	}

	PostActionCallback getCallback();
}

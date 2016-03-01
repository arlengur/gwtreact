/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * Interface for object which saves its state.
 * 
 * @author kunilov.p
 * 
 */
public interface Statefull {

	/**
	 * Clears current state.
	 */
	void clearState();

	/**
	 * Loads current state.
	 */
	void loadState();

	/**
	 * Save current state.
	 */
	void saveState();
}

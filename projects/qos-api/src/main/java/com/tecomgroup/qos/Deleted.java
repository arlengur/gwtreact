/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * An interface of the object which can be deleted.
 * 
 * @author kunilov.p
 * 
 */
public interface Deleted {

	boolean isDeleted();

	void setDeleted(boolean deleted);
}

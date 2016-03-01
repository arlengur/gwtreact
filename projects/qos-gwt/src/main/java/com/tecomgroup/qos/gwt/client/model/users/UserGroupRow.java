/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.users;

import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

/**
 * @author ivlev.e
 * 
 */
public class UserGroupRow implements TreeGridRow {
	private static final long serialVersionUID = -3626070565457752787L;

	private final String name;

	public UserGroupRow(final String name) {
		this.name = name;
	}

	@Override
	public String getKey() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}
}

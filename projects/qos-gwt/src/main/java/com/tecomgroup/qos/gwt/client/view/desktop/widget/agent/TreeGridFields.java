/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

/**
 * @author ivlev.e
 * 
 */
public enum TreeGridFields {

	NAME("name"), VALUE("value"), DATE("date"), PADDING("padding");

	private String fieldName;

	TreeGridFields(final String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String toString() {
		return fieldName;
	}
}

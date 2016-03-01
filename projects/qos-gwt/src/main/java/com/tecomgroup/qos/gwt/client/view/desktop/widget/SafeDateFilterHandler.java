/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Date;

import com.sencha.gxt.data.shared.loader.DateFilterHandler;

/**
 * 
 * @author kunilov.p
 * 
 */
public class SafeDateFilterHandler extends DateFilterHandler {
	@Override
	public Date convertToObject(final String value) {
		return super.convertToObject(value);
	}

	@Override
	public String convertToString(final Date date) {
		String result = "";
		if (date != null) {
			result = super.convertToString(date);
		}
		return result;
	}
}

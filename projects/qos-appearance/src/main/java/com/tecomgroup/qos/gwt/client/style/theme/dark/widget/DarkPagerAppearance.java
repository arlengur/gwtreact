/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.widget;

import com.tecomgroup.qos.gwt.client.style.common.PagerAppearance;

/**
 * @author kshnyakin.m
 * 
 */
public class DarkPagerAppearance implements PagerAppearance {

	private final static String ARROW_COLOR = "#474747";

	private final static String INACTIVE_PAGE_COLOR = "#333333";

	private final static String ACTIVE_PAGE_COLOR = "#2E3A6B";

	public DarkPagerAppearance() {

	}

	@Override
	public String getActivePageColor() {
		return ACTIVE_PAGE_COLOR;
	}

	@Override
	public String getArrowColor() {
		return ARROW_COLOR;
	}

	@Override
	public String getInactivePageColor() {
		return INACTIVE_PAGE_COLOR;
	}

}

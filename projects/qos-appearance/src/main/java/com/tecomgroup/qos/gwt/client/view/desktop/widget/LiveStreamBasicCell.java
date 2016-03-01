/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.style.common.cell.LiveStreamBasicCellAppearance;

/**
 * @author ivlev.e
 * 
 */
public class LiveStreamBasicCell<C extends Map<String, String>>
		extends
			AbstractCell<C> {

	public static final String PROGRAM_NAME_PROPERTY = "programName";

	protected LiveStreamBasicCellAppearance<C> appearance;

	protected String headerPropertyName;

	public LiveStreamBasicCell(
			final LiveStreamBasicCellAppearance<C> appearance,
			final String headerPropertyName) {
		super("mousedown");
		this.appearance = appearance;
		this.headerPropertyName = headerPropertyName;
	}

	public LiveStreamBasicCell(final String headerPropertyName) {
		this(
				GWT.<LiveStreamBasicCellAppearance<C>> create(LiveStreamBasicCellAppearance.class),
				headerPropertyName);
	}

	@Override
	public void render(final Context context, final C value,
			final SafeHtmlBuilder sb) {
		appearance.render(this, headerPropertyName, context, value, sb);
	}

}

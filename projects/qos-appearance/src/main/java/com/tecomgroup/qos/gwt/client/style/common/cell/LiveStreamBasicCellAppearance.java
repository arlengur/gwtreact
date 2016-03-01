/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.cell;

import java.util.Map;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamBasicCell;

/**
 * @author ivlev.e
 * 
 */
public interface LiveStreamBasicCellAppearance<C extends Map<String, String>> {

	void render(LiveStreamBasicCell<C> cell, String headerPropertyName,
			final Context context, final C value, final SafeHtmlBuilder sb);

}

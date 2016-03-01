/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.cell;

import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.StreamCell;

/**
 * @author ivlev.e
 * 
 */
public interface StreamCellAppearance<C extends Map<String, String>> {

	boolean isRemoveButtonPressed(final XElement element);

	void render(StreamCell<C> cell, Set<String> excludedProperties,
			final Context context, final C value, final SafeHtmlBuilder sb);

}

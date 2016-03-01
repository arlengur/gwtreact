/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell;

/**
 * @author ivlev.e
 * 
 */
public interface ButtonedGroupingCellAppearance<C> {

	boolean isRemoveButtonPressed(final XElement element);

	void render(ButtonedGroupingCell<C> cell, final Context context, final C value,
			final SafeHtmlBuilder sb);

}

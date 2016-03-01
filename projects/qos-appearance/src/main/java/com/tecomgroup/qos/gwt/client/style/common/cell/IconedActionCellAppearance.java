/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;

/**
 * @author kunilov.p
 * 
 */
public interface IconedActionCellAppearance<C> {

	void render(final IconedActionCell<C> cell, final Context context,
			final C value, final SafeHtmlBuilder sb);
}

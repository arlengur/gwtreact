/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.tecomgroup.qos.gwt.client.style.common.cell.IconedActionCellAppearance;

/**
 * Action cell with icon instead of raw button
 * 
 * @author meleshin.o
 * 
 */
public class IconedActionCell<C> extends ActionCell<C> {
	private final IconedActionCellAppearance<C> appearance;

	public IconedActionCell(final IconedActionCellAppearance<C> appearance,
			final SafeHtml message, final ActionCell.Delegate<C> delegate) {
		super(message, delegate);
		this.appearance = appearance;
	}

	public IconedActionCell(final IconedActionCellAppearance<C> appearance,
			final String message, final ActionCell.Delegate<C> delegate) {
		this(appearance, SafeHtmlUtils.fromString(message), delegate);
	}

	@Override
	public void render(final Context context, final C value,
			final SafeHtmlBuilder sb) {
		if (value != null) {
			appearance.render(this, context, value, sb);
		}
	}
}

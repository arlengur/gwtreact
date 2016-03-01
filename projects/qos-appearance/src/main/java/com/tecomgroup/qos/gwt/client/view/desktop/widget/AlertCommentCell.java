/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.bean.AlertCommentDetails;
import com.tecomgroup.qos.gwt.client.style.common.cell.AlertCommentCellAppearance;

/**
 * @author novohatskiy.r
 * 
 */
public class AlertCommentCell extends AbstractCell<AlertCommentDetails> {

	private final AlertCommentCellAppearance appearance;

	public AlertCommentCell(final AlertCommentCellAppearance appearance) {
		this.appearance = appearance;
	}

	@Override
	public void render(final com.google.gwt.cell.client.Cell.Context context,
			final AlertCommentDetails commentDetails, final SafeHtmlBuilder sb) {
		appearance.render(this, context, commentDetails, sb);
	}
}

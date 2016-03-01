/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.bean.AlertCommentDetails;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AlertCommentCell;

/**
 * @author novohatskiy.r
 * 
 */
public interface AlertCommentCellAppearance {

	void render(AlertCommentCell cell, Context context,
			AlertCommentDetails commentDetails, SafeHtmlBuilder sb);

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.cell;

import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedStreamCell;

/**
 * @author novohatskiy.r
 * 
 */
public interface RecordedStreamCellAppearance<C extends Map<String, String>>
		extends
			StreamCellAppearance<C> {

	void render(RecordedStreamCell<C> cell, Set<Set<String>> keysByGroup,
			final Context context, final C value, final SafeHtmlBuilder sb);

}

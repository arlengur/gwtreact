/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * {@link Cell} which is able to choose one of many registered cells according
 * to the type of containing object
 * 
 * @author novohatskiy.r
 * 
 */
public class PropertyGridMultiTypedCell extends AbstractCell<Object> {

	private final Map<Class<?>, Cell<?>> cells = new HashMap<Class<?>, Cell<?>>();

	public void addCell(final Class<?> clazz, final Cell<?> cell) {
		cells.put(clazz, cell);
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public void render(final Context context, final Object value,
			final SafeHtmlBuilder sb) {
		final Cell<Object> cell = (Cell<Object>) cells.get(value.getClass());
		if (cell != null) {
			cell.render(context, value, sb);
		} else {
			sb.append(SafeHtmlUtils.fromString(value.toString()));
		}
	}
}

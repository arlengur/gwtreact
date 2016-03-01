/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.style.common.cell.StreamCellAppearance;

/**
 * @author ivlev.e
 * 
 */
public class StreamCell<C extends Map<String, String>> extends AbstractCell<C> {

	public static interface StreamCellHandler {
		void onRemoveButtonPressed(List<String> modelKeys);
	}

	protected ImageResource removeIcon;

	protected ImageResource rowIcon;

	protected StreamCellAppearance<C> appearance;

	protected StreamCellHandler handler;

	protected Set<String> excludedProperties;

	public StreamCell(final StreamCellAppearance<C> appearance,
			final StreamCellHandler handler,
			final Set<String> excludedProperties) {
		super("mousedown");
		this.appearance = appearance;
		this.handler = handler;
		this.excludedProperties = excludedProperties;
	}

	public StreamCell(final StreamCellHandler handler,
			final Set<String> excludedProperties) {
		this(GWT.<StreamCellAppearance<C>> create(StreamCellAppearance.class),
				handler, excludedProperties);
	}

	/**
	 * @return the removeIcon
	 */
	public ImageResource getRemoveIcon() {
		return removeIcon;
	}

	/**
	 * @return the rowIcon
	 */
	public ImageResource getRowIcon() {
		return rowIcon;
	}

	@Override
	public void onBrowserEvent(
			final com.google.gwt.cell.client.Cell.Context context,
			final Element parent, final C value, final NativeEvent event,
			final ValueUpdater<C> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);

		final Element target = event.getEventTarget().cast();

		final XElement t = target.cast();

		final String eventType = event.getType();
		if ("mousedown".equals(eventType)) {
			if (appearance.isRemoveButtonPressed(t)) {
				handler.onRemoveButtonPressed(Arrays.asList((String) context
						.getKey()));
			}
		}
	}

	@Override
	public void render(final com.google.gwt.cell.client.Cell.Context context,
			final C value, final SafeHtmlBuilder sb) {
		appearance.render(this, excludedProperties, context, value, sb);
	}

	/**
	 * @param removeIcon
	 *            the removeIcon to set
	 */
	public void setRemoveIcon(final ImageResource removeIcon) {
		this.removeIcon = removeIcon;
	}

	/**
	 * @param rowIcon
	 *            the rowIcon to set
	 */
	public void setRowIcon(final ImageResource rowIcon) {
		this.rowIcon = rowIcon;
	}

}

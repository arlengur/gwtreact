/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.style.common.cell.ButtonedGroupingCellAppearance;

/**
 * @author ivlev.e
 * 
 */
public class ButtonedGroupingCell<C> extends AbstractCell<C> {

	public static interface ButtonedGroupingCellHandler {
		void onRemovedButtonPressed(List<String> modelKeys);
	}

	protected ImageResource removeSeriesIcon;

	protected ImageResource rowIcon;

	protected ButtonedGroupingCellAppearance<C> appearance;

	protected ButtonedGroupingCellHandler handler;

	public ButtonedGroupingCell(final ButtonedGroupingCellAppearance<C> appearance,
			final ButtonedGroupingCellHandler handler) {
		super("mousedown");
		this.appearance = appearance;
		this.handler = handler;
	}

	public ButtonedGroupingCell(final ButtonedGroupingCellHandler handler) {
		this(
				GWT.<ButtonedGroupingCellAppearance<C>> create(ButtonedGroupingCellAppearance.class),
				handler);
	}

	/**
	 * @return the removeSeriesIcon
	 */
	public ImageResource getRemoveSeriesIcon() {
		return removeSeriesIcon;
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
		final Element target = event.getEventTarget().cast();

		final XElement t = target.cast();

		final String eventType = event.getType();
		if ("mousedown".equals(eventType)) {
			if (appearance.isRemoveButtonPressed(t)) {
				handler.onRemovedButtonPressed(Arrays.asList((String) context.getKey()));
			}
		}

		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}
	@Override
	public void render(final Context context, final C value,
			final SafeHtmlBuilder sb) {
		appearance.render(this, context, value, sb);
	}

	/**
	 * @param removeSeriesIcon
	 *            the removeSeriesIcon to set
	 */
	public void setRemoveSeriesIcon(final ImageResource removeSeriesIcon) {
		this.removeSeriesIcon = removeSeriesIcon;
	}

	/**
	 * @param rowIcon
	 *            the rowIcon to set
	 */
	public void setRowIcon(final ImageResource rowIcon) {
		this.rowIcon = rowIcon;
	}

}

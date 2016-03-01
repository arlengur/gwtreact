/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.dashboard.DashboardWidget;

/**
 * The tile element containing {@link DashboardWidget} as model.
 *
 * @author kunilov.p
 *
 */
public abstract class AbstractWidgetTileContentElement<M extends DashboardWidget>
		implements
			TileContentElement {

	protected final M model;

	public AbstractWidgetTileContentElement(final M model) {
		this.model = model;
	}

	/**
	 * Calls {@link TileContentElement#dispose()} before
	 * {@link TileContentElement#destroy()} to free resources, to close any
	 * connections etc.
	 */
	@Override
	public void destroy() {
		dispose();
		getContentElement().removeFromParent();
	}

	@Override
	public M getModel() {
		return model;
	}

	protected void hideWidget(final Widget w, final boolean hide) {
		w.getElement().getStyle()
		.setVisibility(hide ? Visibility.HIDDEN : Visibility.VISIBLE);
	}

	@Override
	public void refresh() {
	}

}

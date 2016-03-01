/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.panel;

import com.google.gwt.core.shared.GWT;
import com.tecomgroup.qos.gwt.client.style.theme.base.panel.TileBaseAppearance;

/**
 * @author ivlev.e
 *
 */
public class DarkTileAppearance extends TileBaseAppearance {

	public interface DarkTileResources extends TileBaseResources {

		@Override
		@Source({
			"com/tecomgroup/qos/gwt/client/style/theme/base/panel/TileBase.css",
		"DarkTile.css"})
		TileBaseStyle style();
	}

	public DarkTileAppearance(final boolean hasSaveButton, final boolean hasChartButton) {
		this(hasSaveButton, hasChartButton, 1, 1);
	}

	public DarkTileAppearance(final boolean hasSaveButton, final boolean hasChartButton, final int rowSpan,
			final int colSpan) {
		super((DarkTileResources) GWT.create(DarkTileResources.class),
				hasSaveButton, hasChartButton, rowSpan, colSpan);
	}
}

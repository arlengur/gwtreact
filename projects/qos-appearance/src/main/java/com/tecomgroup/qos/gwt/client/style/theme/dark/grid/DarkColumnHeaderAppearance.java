/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.tecomgroup.qos.gwt.client.style.common.grid.FilteredColumnHeaderAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkColumnHeaderAppearance extends FilteredColumnHeaderAppearance {

	public interface DarkColumnHeaderResources
			extends
				FilteredColumnHeaderResources {
		// preventInlining only need for ie6 ie7 because of bottom alignment
		@Override
		@ImageOptions(repeatStyle = RepeatStyle.Horizontal, preventInlining = true)
		ImageResource columnHeader();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
		ImageResource columnHeaderOver();

		@Override
		ImageResource columnMoveBottom();

		@Override
		ImageResource columnMoveTop();

		@Override
		ImageResource columnsIcon();

		@Override
		ImageResource filterIcon();

		@Override
		ImageResource sortAsc();

		@Override
		ImageResource sortAscendingIcon();

		@Override
		ImageResource sortDesc();

		@Override
		ImageResource sortDescendingIcon();

		@Override
		@Source("DarkColumnHeader.css")
		DarkColumnHeaderStyle style();

	}

	public interface DarkColumnHeaderStyle extends FilteredColumnHeaderStyle {
	}

	/**
	 * 
	 */
	public DarkColumnHeaderAppearance() {
		super(
				GWT.<DarkColumnHeaderResources> create(DarkColumnHeaderResources.class));

	}

	/**
	 * @param resources
	 */
	public DarkColumnHeaderAppearance(final DarkColumnHeaderResources resources) {
		super(resources);
	}

}

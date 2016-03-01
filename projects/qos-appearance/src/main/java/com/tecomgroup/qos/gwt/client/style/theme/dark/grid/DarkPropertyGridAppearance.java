/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.style.common.grid.PropertyGridAppearance;

/**
 * @author novohatskiy.r
 * 
 */
public class DarkPropertyGridAppearance extends DarkGridStandardAppearance
		implements
			PropertyGridAppearance {

	public interface DarkPropertyGridResources
			extends
				GridResources,
				PropertyGridResources {

		@Source({"DarkGridStandard.css", "DarkPropertyGrid.css"})
		@Override
		DarkPropertyGridStyle css();
	}

	public static interface DarkPropertyGridStyle
			extends
				GridStyle,
				PropertyGridStyle {
	}

	public DarkPropertyGridAppearance() {
		this(
				GWT.<DarkPropertyGridResources> create(DarkPropertyGridResources.class));
	}

	public DarkPropertyGridAppearance(final GridResources resources) {
		super(resources);
	}

	@Override
	public PropertyGridResources getResources() {
		return (PropertyGridResources) resources;
	}
}
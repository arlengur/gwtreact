/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.style.common.grid.TemplatesGridAppearance;

/**
 * @author meleshin.o
 * 
 */
public class DarkTemplatesGridAppearance extends DarkGridStandardAppearance
		implements
			TemplatesGridAppearance {

	public interface DarkTemplatesGridResources
			extends
				GridResources,
				TemplatesGridResources {

		@Source({"DarkGridStandard.css", "DarkTemplatesGrid.css"})
		@Override
		DarkTemplatesGridStandardStyle css();
	}

	public static interface DarkTemplatesGridStandardStyle
			extends
				GridStyle,
				TemplatesGridStyle {
	}

	public DarkTemplatesGridAppearance() {
		this(
				GWT.<DarkTemplatesGridResources> create(DarkTemplatesGridResources.class));
	}

	public DarkTemplatesGridAppearance(final GridResources resources) {
		super(resources);
	}

	@Override
	public TemplatesGridResources getResources() {
		return (DarkTemplatesGridResources) resources;
	}

}

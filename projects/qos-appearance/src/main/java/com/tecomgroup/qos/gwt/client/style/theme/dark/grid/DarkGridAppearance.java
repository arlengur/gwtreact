/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;

/**
 * @author abondin
 * 
 */
public class DarkGridAppearance extends GridBaseAppearance {

	public interface DarkGridStyle extends GridStyle {

	}

	public interface DarkGridResources extends GridResources {

		@Source({"DarkGrid.css"})
		@Override
		DarkGridStyle css();
	}

	public DarkGridAppearance() {
		this(GWT.<DarkGridResources> create(DarkGridResources.class));
	}

	public DarkGridAppearance(final GridResources resources) {
		super(resources);
	}
}

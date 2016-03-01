/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkGridStandardAppearance extends GridBaseAppearance {

	public interface DarkGridStandardResources extends GridResources {

		@Source({"DarkGridStandard.css"})
		@Override
		DarkGridStandardStyle css();
	}

	public interface DarkGridStandardStyle extends GridStyle {

	}

	public DarkGridStandardAppearance() {
		this(
				GWT.<DarkGridStandardResources> create(DarkGridStandardResources.class));
	}

	public DarkGridStandardAppearance(final GridResources resources) {
		super(resources);
	}

}

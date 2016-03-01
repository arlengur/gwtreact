/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.grid;

import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;

/**
 * @author meleshin.o
 * 
 */
public interface TemplatesGridAppearance extends GridAppearance {
	public static interface TemplatesGridResources {
		TemplatesGridStyle css();
	}

	public static interface TemplatesGridStyle {

	}

	TemplatesGridResources getResources();
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.grid;

import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;

/**
 * @author novohatskiy.r
 * 
 */
public interface PropertyGridAppearance extends GridAppearance {

	public static interface PropertyGridResources {
		PropertyGridStyle css();
	}

	public static interface PropertyGridStyle {
	}

	PropertyGridResources getResources();
}

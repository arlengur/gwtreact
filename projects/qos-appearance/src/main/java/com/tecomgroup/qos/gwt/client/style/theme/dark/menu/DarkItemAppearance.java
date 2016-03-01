/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.style.theme.dark.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.menu.ItemBaseAppearance;

/**
 * @author sviyazov.a
 * 
 */
public class DarkItemAppearance extends ItemBaseAppearance {

	public interface DarkItemResources
			extends
				ItemBaseAppearance.ItemResources,
				ClientBundle {

		@Override
		@Source({"com/sencha/gxt/theme/base/client/menu/Item.css",
				"DarkItem.css"})
		DarkItemStyle style();
	}

	public interface DarkItemStyle extends ItemStyle {
		@Override
		String active();
	}

	public DarkItemAppearance() {
		this(GWT.<DarkItemResources> create(DarkItemResources.class));
	}

	public DarkItemAppearance(final DarkItemResources resources) {
		super(resources);
	}
}

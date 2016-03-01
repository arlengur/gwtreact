/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.style.theme.dark.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.menu.MenuItemBaseAppearance;

/**
 * @author sviyazov.a
 * 
 */
public class DarkMenuItemAppearance extends MenuItemBaseAppearance {

	public interface DarkMenuItemResources
			extends
				MenuItemBaseAppearance.MenuItemResources,
				ClientBundle {

		@Override
		@Source({"com/sencha/gxt/theme/base/client/menu/MenuItem.css",
				"DarkMenuItem.css"})
		DarkMenuItemStyle style();
	}

	public interface DarkMenuItemStyle
			extends
				MenuItemBaseAppearance.MenuItemStyle {
	}

	public DarkMenuItemAppearance() {
		this(
				GWT.<DarkMenuItemResources> create(DarkMenuItemResources.class),
				GWT.<MenuItemBaseAppearance.MenuItemTemplate> create(MenuItemBaseAppearance.MenuItemTemplate.class));
	}

	public DarkMenuItemAppearance(final DarkMenuItemResources resources,
			final MenuItemBaseAppearance.MenuItemTemplate template) {
		super(resources, template);
	}
}

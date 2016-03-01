/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.style.theme.dark.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.menu.MenuBaseAppearance;

/**
 * @author sviyazov.a
 * 
 */
public class DarkMenuAppearance extends MenuBaseAppearance {

	public interface DarkMenuResources
			extends
				MenuBaseAppearance.MenuResources,
				ClientBundle {
		@Override
		@Source({"com/sencha/gxt/theme/base/client/menu/Menu.css",
				"DarkMenu.css"})
		DarkMenuStyle style();
	}

	public interface DarkMenuStyle extends MenuStyle {
	}

	public DarkMenuAppearance() {
		this(GWT.<DarkMenuResources> create(DarkMenuResources.class), GWT
				.<BaseMenuTemplate> create(BaseMenuTemplate.class));
	}

	public DarkMenuAppearance(final DarkMenuResources resources,
			final BaseMenuTemplate template) {
		super(resources, template);
	}
}

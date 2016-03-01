/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.base.client.toolbar.ToolBarBaseAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkAnchorAppearance.DarkAnchorStyle;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkAnchorFlickeringAppearance.DarkAnchorFlickeringStyle;

/**
 * @author ivlev.e
 * 
 */
public class DarkToolBarAppearance extends ToolBarBaseAppearance {

	public interface DarkToolBarResources extends ClientBundle {

		@Source({"com/sencha/gxt/theme/base/client/toolbar/ToolBarBase.css",
				"DarkToolBar.css"})
		@Import({DarkAnchorStyle.class, DarkAnchorFlickeringStyle.class})
		DarkToolBarStyle style();
	}

	public interface DarkToolBarStyle extends ToolBarBaseStyle, CssResource {

	}

	private final DarkToolBarStyle style;

	private final DarkToolBarResources resources;

	public DarkToolBarAppearance() {
		this(GWT.<DarkToolBarResources> create(DarkToolBarResources.class));
	}

	public DarkToolBarAppearance(final DarkToolBarResources resources) {
		this.resources = resources;
		this.style = this.resources.style();

		StyleInjectorHelper.ensureInjected(style, true);
	}

	@Override
	public String toolBarClassName() {
		return style.toolBar();
	}

}

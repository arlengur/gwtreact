/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.button;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorFlickeringAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources.DarkStyle;

/**
 * @author ivlev.e
 * 
 */
public class DarkAnchorFlickeringAppearance extends AnchorFlickeringAppearance {

	public interface DarkAnchorFlickeringResources
			extends
				AnchorFlickeringResources {
		@Source({
				"com/tecomgroup/qos/gwt/client/style/theme/base/button/AnchorBaseAppearance.css",
				"com/tecomgroup/qos/gwt/client/style/theme/base/button/AnchorFlickeringAppearance.css",
				"com/tecomgroup/qos/gwt/client/style/theme/dark/grid/DarkAlertSeverityStyle.css",
				"DarkAnchorAppearance.css",
				"DarkAnchorFlickeringAppearance.css"})
		@Import(DarkStyle.class)
		@Override
		DarkAnchorFlickeringStyle style();
	}

	@ImportedWithPrefix("darkFlickeringAnchor")
	public interface DarkAnchorFlickeringStyle extends AnchorFlickeringStyle {
	}

	public DarkAnchorFlickeringAppearance() {
		this((DarkAnchorFlickeringResources) GWT
				.create(DarkAnchorFlickeringResources.class));
	}

	public DarkAnchorFlickeringAppearance(
			final DarkAnchorFlickeringResources resources) {
		super(resources);
	}
}

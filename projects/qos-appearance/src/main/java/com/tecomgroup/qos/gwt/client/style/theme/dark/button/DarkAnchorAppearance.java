/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.button;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorBaseAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources.DarkStyle;

/**
 * @author kshnyakin.m
 * 
 */
public class DarkAnchorAppearance extends AnchorBaseAppearance {

	public interface DarkAnchorResources extends AnchorBaseResources {
		@Source({
				"com/tecomgroup/qos/gwt/client/style/theme/base/button/AnchorBaseAppearance.css",
				"DarkAnchorAppearance.css"})
		@Import(DarkStyle.class)
		@Override
		DarkAnchorStyle style();
	}

	@ImportedWithPrefix("darkAnchor")
	public interface DarkAnchorStyle extends AnchorBaseStyle {
	}

	public DarkAnchorAppearance() {
		this((DarkAnchorResources) GWT.create(DarkAnchorResources.class));
	}

	public DarkAnchorAppearance(final DarkAnchorResources resources) {
		super(resources);
	}
}

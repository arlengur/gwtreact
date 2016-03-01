/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.container;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.container.BorderLayoutBaseAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkBorderLayoutAppearance extends BorderLayoutBaseAppearance {

	public interface DarkBorderLayoutResources extends BorderLayoutResources {
		@Override
		@Source({"com/sencha/gxt/theme/base/client/container/BorderLayout.css",
				"DarkBorderLayout.css"})
		public DarkBorderLayoutStyle css();
	}

	public interface DarkBorderLayoutStyle extends BorderLayoutStyle {

	}

	public DarkBorderLayoutAppearance() {
		super(
				GWT.<DarkBorderLayoutResources> create(DarkBorderLayoutResources.class));
	}

}

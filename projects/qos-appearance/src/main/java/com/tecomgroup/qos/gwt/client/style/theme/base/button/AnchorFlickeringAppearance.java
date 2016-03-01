/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.button;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertSeverityStyle;

/**
 * @author ivlev.e
 * 
 */
public class AnchorFlickeringAppearance extends AnchorBaseAppearance {

	public interface AnchorFlickeringResources extends AnchorBaseResources {

		@Override
		@Source({
				"com/tecomgroup/qos/gwt/client/style/theme/dark/grid/DarkAlertSeverityStyle.css",
				"AnchorBaseAppearance.css", "AnchorFlickeringAppearance.css"})
		AnchorFlickeringStyle style();
	}

	public interface AnchorFlickeringStyle
			extends
				AnchorBaseStyle,
				AlertSeverityStyle {
	}

	public AnchorFlickeringAppearance() {
		this((AnchorFlickeringResources) GWT
				.create(AnchorFlickeringResources.class));
	}

	public AnchorFlickeringAppearance(
			final AnchorFlickeringResources anchorBaseResources) {
		super(anchorBaseResources);
	}

	@Override
	public AnchorFlickeringResources getResources() {
		return (AnchorFlickeringResources) super.getResources();
	}

}

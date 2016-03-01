/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.panel.light;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.frame.CollapsibleFrame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.tecomgroup.qos.gwt.client.style.theme.dark.panel.DarkFramedPanelAppearance;

/**
 * @author sviyazov.a
 * 
 */
public class DarkLightestFramedPanelAppearance
		extends
			DarkFramedPanelAppearance {

	public interface DarkLightestFramedPanelDivFrameResources
			extends
				DarkFramedPanelDivFrameResources {
		@Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css",
				"DarkLightestFramedPanelDivFrame.css"})
		@Override
        DarkLightestFramePanelNestedDivFrameStyle style();
	}

    public interface DarkLightestFramePanelNestedDivFrameStyle
            extends
            DarkFramePanelNestedDivFrameStyle {

    }

	public DarkLightestFramedPanelAppearance() {
		this(
				GWT.<DarkFramePanelResources> create(DarkFramePanelResources.class));
	}

	public DarkLightestFramedPanelAppearance(
			final ContentPanelResources resources,
			final FramedPanelTemplate template, final CollapsibleFrame frame) {
		super(resources, template, frame);
	}

	public DarkLightestFramedPanelAppearance(
			final DarkFramePanelResources resources) {
		super(
				resources,
				GWT.<FramedPanelTemplate> create(FramedPanelTemplate.class),
				new NestedDivFrame(
						GWT.<FramedPanelDivFrameResources> create(DarkLightestFramedPanelDivFrameResources.class)));
	}

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.frame.CollapsibleFrame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame.NestedDivFrameStyle;
import com.sencha.gxt.theme.base.client.panel.FramedPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.gray.client.panel.GrayHeaderFramedAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkFramedPanelAppearance extends FramedPanelBaseAppearance {

	public interface DarkFramedPanelDivFrameResources
			extends
				FramedPanelDivFrameResources,
				ClientBundle {

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		ImageResource background();

		@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
		@Override
		ImageResource bottomBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Override
		ImageResource bottomLeftBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Override
		ImageResource bottomRightBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Vertical)
		@Override
		ImageResource leftBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Override
		ImageResource rightBorder();

		@Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css",
				"DarkFramedPanelDivFrame.css"})
		@Override
		DarkFramePanelNestedDivFrameStyle style();

		@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
		@Override
		ImageResource topBorder();

		@Override
		ImageResource topLeftBorder();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.Both)
		ImageResource topRightBorder();

	}

	public interface DarkFramePanelNestedDivFrameStyle
			extends
				NestedDivFrameStyle {

	}

	public interface DarkFramePanelResources extends ContentPanelResources {
		@Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css",
				"DarkFramedPanel.css"})
		@Override
		FramedPanelStyle style();
	}

	public interface FramedPanelStyle extends ContentPanelStyle {

	}

	public DarkFramedPanelAppearance() {
		this(
				GWT.<DarkFramePanelResources> create(DarkFramePanelResources.class));
	}

	public DarkFramedPanelAppearance(final ContentPanelResources resources,
			final FramedPanelTemplate template, final CollapsibleFrame frame) {
		super(resources, template, frame);
	}

	public DarkFramedPanelAppearance(final DarkFramePanelResources resources) {
		super(
				resources,
				GWT.<FramedPanelTemplate> create(FramedPanelTemplate.class),
				new NestedDivFrame(
						GWT.<FramedPanelDivFrameResources> create(DarkFramedPanelDivFrameResources.class)));
	}

	@Override
	public HeaderDefaultAppearance getHeaderAppearance() {
		return new GrayHeaderFramedAppearance();
	}
}

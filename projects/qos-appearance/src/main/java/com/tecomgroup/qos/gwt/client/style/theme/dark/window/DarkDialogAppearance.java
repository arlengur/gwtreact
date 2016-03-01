/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame.NestedDivFrameStyle;
import com.sencha.gxt.theme.base.client.panel.FramedPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderResources;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderStyle;
import com.sencha.gxt.widget.core.client.Window.WindowAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.panel.DarkFramedPanelAppearance.FramedPanelStyle;

/**
 * @author abondin
 * 
 */
public class DarkDialogAppearance extends FramedPanelBaseAppearance
		implements
			WindowAppearance {

	public interface DarkDialogDivFrameResources
			extends
				FramedPanelDivFrameResources,
				ClientBundle {

		@Source("com/sencha/gxt/theme/base/client/shared/clear.gif")
		ImageResource background();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Override
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/bottomBorder.gif")
		ImageResource bottomBorder();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/bottomLeftBorder.gif")
		ImageResource bottomLeftBorder();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/bottomRightBorder.gif")
		ImageResource bottomRightBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Vertical)
		@Override
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/leftBorder.gif")
		ImageResource leftBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Override
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/rightBorder.gif")
		ImageResource rightBorder();

		@Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css",
				"DarkDialogDivFrame.css"})
		@Override
		DarkDialogDivFrameStyle style();

		@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
		@Override
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/topBorder.gif")
		ImageResource topBorder();

		@Override
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/topLeftBorder.gif")
		ImageResource topLeftBorder();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.Both)
		@Source("com/tecomgroup/qos/gwt/client/style/theme/dark/panel/topRightBorder.gif")
		ImageResource topRightBorder();

	}

	public interface DarkDialogDivFrameResourcesWithHeader
			extends
				DarkDialogDivFrameResources {
		@Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css",
				"DarkDialogDivFrame.css", "DarkDialogDivFrameWithHeader.css"})
		@Override
		DarkDialogDivFrameStyle style();
	}

	public interface DarkDialogDivFrameStyle extends NestedDivFrameStyle {

	}

	public interface DarkDialogHeadereStyle extends HeaderStyle {

	}

	public interface DarkDialogHeaderResources extends HeaderResources {
		@Override
		@Source({"com/sencha/gxt/theme/base/client/widget/Header.css",
				"DarkDialogHeader.css"})
		DarkDialogHeadereStyle style();
	}
	public interface DarkDialogNoHeaderResources extends HeaderResources {
		@Override
		@Source({"com/sencha/gxt/theme/base/client/widget/Header.css",
				"DarkDialogNoHeader.css"})
		DarkDialogHeadereStyle style();
	}

	public interface DarkDialogResources
			extends
				ContentPanelResources,
				ClientBundle {

		@Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css",
				"com/sencha/gxt/theme/base/client/window/Window.css",
				"DarkDialog.css"})
		@Override
		DarkDialogStyle style();

	}

	public interface DarkDialogStyle extends FramedPanelStyle {
		String ghost();
	}

	private final DarkDialogStyle style;

	private final boolean showHeader;

	public DarkDialogAppearance(final boolean showHeader) {
		this(showHeader, (DarkDialogResources) GWT
				.create(DarkDialogResources.class));
	}

	public DarkDialogAppearance(final boolean showHeader,
			final DarkDialogResources resources) {
		super(
				resources,
				GWT.<FramedPanelTemplate> create(FramedPanelTemplate.class),
				showHeader
						? new NestedDivFrame(
								GWT.<DarkDialogDivFrameResourcesWithHeader> create(DarkDialogDivFrameResourcesWithHeader.class))
						: new NestedDivFrame(
								GWT.<DarkDialogDivFrameResources> create(DarkDialogDivFrameResources.class)));
		this.showHeader = showHeader;
		this.style = resources.style();
	}

	@Override
	public HeaderDefaultAppearance getHeaderAppearance() {
		if (showHeader) {
			return new HeaderDefaultAppearance(
					GWT.<DarkDialogHeaderResources> create(DarkDialogHeaderResources.class));
		} else {
			return new HeaderDefaultAppearance(
					GWT.<DarkDialogNoHeaderResources> create(DarkDialogNoHeaderResources.class));
		}
	}

	@Override
	public String ghostClass() {
		return style.ghost();
	}

}

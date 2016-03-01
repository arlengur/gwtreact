/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.button.rectangle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.theme.base.client.frame.TableFrame;

/**
 * @author ivlev.e
 * 
 */
public class DarkButtonCellRectangleAppearance<C>
		extends
			ButtonCellDefaultAppearance<C> {

	public interface DarkButtonCellRectangleResources
			extends
				ButtonCellResources {

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource arrow();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource arrowBottom();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource split();

		@Override
		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource splitBottom();

		@Override
		@Source({"com/sencha/gxt/theme/base/client/button/ButtonCell.css",
				"DarkButtonCell.css"})
		ButtonCellStyle style();

	}

	public DarkButtonCellRectangleAppearance() {
		this(
				GWT.<DarkButtonCellRectangleResources> create(DarkButtonCellRectangleResources.class));
	}

	public DarkButtonCellRectangleAppearance(final ButtonCellResources resources) {
		this(
				resources,
				GWT.<ButtonCellTemplates> create(ButtonCellTemplates.class),
				new TableFrame(
						GWT.<DarkButtonTableRectangleResources> create(DarkButtonTableRectangleResources.class)));
	}

	public DarkButtonCellRectangleAppearance(
			final ButtonCellResources resources,
			final ButtonCellTemplates templates, final Frame frame) {
		super(resources, templates, frame);
	}

}

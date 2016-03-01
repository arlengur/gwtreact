/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.button.rectangle;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.button.ButtonTableFrameResources;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameStyle;

/**
 * @author ivlev.e
 * 
 */
public interface DarkButtonTableRectangleResources
		extends
			ButtonTableFrameResources {

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource background();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource backgroundOverBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("backgroundOverBorder.png")
	ImageResource backgroundPressedBorder();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Override
	ImageResource bottomBorder();

	@Override
	ImageResource bottomLeftBorder();

	@Override
	ImageResource bottomLeftOverBorder();

	@Override
	@Source("bottomLeftOverBorder.png")
	ImageResource bottomLeftPressedBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource bottomOverBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("bottomOverBorder.png")
	ImageResource bottomPressedBorder();

	@Override
	ImageResource bottomRightBorder();

	@Override
	ImageResource bottomRightOverBorder();

	@Override
	@Source("bottomRightOverBorder.png")
	ImageResource bottomRightPressedBorder();

	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	@Override
	ImageResource leftBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	ImageResource leftOverBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	@Source("leftOverBorder.png")
	ImageResource leftPressedBorder();

	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	@Override
	ImageResource rightBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	ImageResource rightOverBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Vertical)
	@Source("rightOverBorder.png")
	ImageResource rightPressedBorder();

	@Source({"com/sencha/gxt/theme/base/client/frame/TableFrame.css",
			"com/sencha/gxt/theme/base/client/button/ButtonTableFrame.css",
			"DarkButtonTableRectangle.css"})
	@Override
	TableFrameStyle style();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Override
	ImageResource topBorder();

	@Override
	ImageResource topLeftBorder();

	@Override
	ImageResource topLeftOverBorder();

	@Override
	@Source("topLeftOverBorder.png")
	ImageResource topLeftPressedBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	ImageResource topOverBorder();

	@Override
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("topOverBorder.png")
	ImageResource topPressedBorder();

	@Override
	ImageResource topRightBorder();

	@Override
	ImageResource topRightOverBorder();

	@Override
	@Source("topRightOverBorder.png")
	ImageResource topRightPressedBorder();

}

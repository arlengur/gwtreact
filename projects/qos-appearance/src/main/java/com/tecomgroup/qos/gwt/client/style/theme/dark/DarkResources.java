/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.resources.client.ImageResource;
import com.tecomgroup.qos.gwt.client.style.CommonResources;

/**
 * @author ivlev.e
 *
 */
public interface DarkResources extends CommonResources {

	@ImportedWithPrefix("dark")
	public interface DarkStyle extends CommonStyle {
	}

	public static final DarkResources INSTANCE = GWT
			.create(DarkResources.class);

	@Override
	@Source("button/autoscalingToggleDown.png")
	ImageResource autoscalingToggleDown();

	@Override
	@Source("button/autoscalingToggleUp.png")
	ImageResource autoscalingToggleUp();

	@Override
	@Source("button/backToChartsButtonDown.png")
	ImageResource backToChartsButtonDown();

	@Override
	@Source("button/backToChartsButtonUp.png")
	ImageResource backToChartsButtonUp();

	@Override
	@Source("button/bigTv.png")
	ImageResource bigTvButton();

	@Override
	@Source("button/buttonsDelimiter.png")
	ImageResource buttonsDelimiter();

	@Override
	@Source("button/captionsToggleDown.png")
	ImageResource captionsToggleDown();

	@Override
	@Source("button/captionsToggleUp.png")
	ImageResource captionsToggleUp();

	@Override
	@Source("cell/cellBottomLine.png")
	ImageResource cellBottomLine();

	@Override
	@Source({"com/tecomgroup/qos/gwt/client/style/common/common.css",
			"style.css"})
	DarkStyle css();

	@Override
	@Source("button/editButton.png")
	ImageResource editButton();

	@Override
	@Source("button/exportButtonDown.png")
	ImageResource exportButtonDown();

	@Override
	@Source("button/exportButtonUp.png")
	ImageResource exportButtonUp();

	@Override
	@Source("button/exportToExcelButtonDown.png")
	ImageResource exportToExcelButtonDown();

	@Override
	@Source("button/exportToExcelButtonUp.png")
	ImageResource exportToExcelButtonUp();

	@Override
	@Source("button/mouseTrackingButtonDown.png")
	ImageResource mouseTrackingButtonDown();

	@Override
	@Source("button/mouseTrackingButtonUp.png")
	ImageResource mouseTrackingButtonUp();

	@Override
	@Source("button/newGroup.png")
	ImageResource newGroup();

	@Override
	@Source("grid/playVideo.png")
	ImageResource playVideo();

	@Override
	@Source("button/renameButtonDown.png")
	ImageResource renameButtonDown();

	@Override
	@Source("button/renameButtonUp.png")
	ImageResource renameButtonUp();

	@Override
	@Source("grid/showChartForAlert.png")
	ImageResource showChartForAlert();

	@Override
	@Source("grid/showChartForReport.png")
	ImageResource showChartForReport();

	@Override
	@Source("grid/showTableForAlert.png")
	ImageResource showTableForAlert();

	@Override
	@Source("grid/showTableForReport.png")
	ImageResource showTableForReport();

	@Override
	@Source("button/synchronizeButtonToggleDown.png")
	ImageResource synchronizeButtonToggleDown();

	@Override
	@Source("button/synchronizeButtonToggleUp.png")
	ImageResource synchronizeButtonToggleUp();

	@Override
	@Source("button/tableButtonDown.png")
	ImageResource tableButtonDown();

	@Override
	@Source("button/tableButtonUp.png")
	ImageResource tableButtonUp();

	@Override
	@Source("button/thresholdsToggleDown.png")
	ImageResource thresholdsToggleDown();

	@Override
	@Source("button/thresholdsToggleUp.png")
	ImageResource thresholdsToggleUp();

	@Override
	@Source("grid/treeClose.png")
	ImageResource treeClose();

	@Override
	@Source("grid/treeOpen.png")
	ImageResource treeOpen();

	@Override
	@Source("button/undoZoomButtonDown.png")
	ImageResource undoZoomButtonDown();

	@Override
	@Source("button/undoZoomButtonUp.png")
	ImageResource undoZoomButtonUp();

}

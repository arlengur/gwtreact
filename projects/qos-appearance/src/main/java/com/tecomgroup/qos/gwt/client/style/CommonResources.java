/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author ivlev.e
 *
 */
public interface CommonResources extends ClientBundle {

	public interface CommonStyle extends CssResource {
		String addBroadcastButton();

		String addBroadcastPanel();

		String addChartSeriesSelectAgentPanel();

		String addRecordedVideoLeftPanel();

		String baseLogo();

		String blackBackgroundColor();

		String chartItem();

		String chartNameLabel();

		String clickableText();

		String commentPanelHeader();

		String containerDarkBlue();

		String containerLigth();

		String copyrightLabel();

		String cursorPointer();

		String dashBoardTable();

		String dashboardWidgetSizeImage();

		String disabledIcon();

		String disabledLinkButton();

		String gisContainer();

		String gisPanel();

		/**
		 * @deprecated - use {@link Anchor} instead of this in all cases
		 *             including grid cell
		 * @return
		 */
		@Deprecated
		String gridNavigation();

		String gridPanelHeader();

		String lineHeigth30px();

		String linkSelected();

		String loginLogo();

		String resultsTableTimeWidgetHeader();

		String selected();

		String simpleLabel();

		String size100pct();

		String synchronizeChartsButton();

		String templateBar();

		String templateLabel();

		String text11px();

		String text18px();

		String textAlignCenter();

		String textAlignRight();

		String textAlignLeft();

		String textBold();

		String textDefaultGridColor();

		String textDisabledColor();

		String textMainColor();

		String textMainFontAndSize();

		String textOverflowEllipsis();

		String themeLightBackgroundColor();

		String defaultFont();
		/**
		 * Font color
		 *
		 * #3C3C3D - in Dark theme
		 *
		 * @return
		 */
		String themeLightColor();

		/**
		 * #707073 - in Dark theme
		 *
		 * @return
		 */
		String themeLighterBackgroundColor();

		String timeDash();

		String userInformationTabPanel();

		String userLogoutButton();

		String userSettingsButton();

        String dateTimeWidget();

		String labelErrorWidget();
    }

	@Source("common/addButton.png")
	ImageResource addButton();

	@Source("common/addButtonMini.png")
	ImageResource addButtonMini();

	@Source("common/addConditionTemplate.png")
	ImageResource addConditionTemplate();

	@Source("common/addNotificationTemplate.png")
	ImageResource addNotificationTemplate();

	@Source("common/addRectangleButton.png")
	ImageResource addRectangleButton();

	// Alerts
	@Source("common/alerts/ack.png")
	ImageResource alertAck();

	@Source("common/alerts/clear.png")
	ImageResource alertClear();

	@Source("common/alerts/comment.png")
	ImageResource alertComment();

	@Source("common/alerts/details.png")
	ImageResource alertDetails();

	@Source("common/alerts/originator.png")
	ImageResource alertOriginator();

	@Source("common/alerts/source.png")
	ImageResource alertSource();

	@Source("common/alerts/unack.png")
	ImageResource alertUnAck();

	@Source("common/attention.png")
	ImageResource attention();

	ImageResource autoscalingToggleDown();

	ImageResource autoscalingToggleUp();

	ImageResource backToChartsButtonDown();

	ImageResource backToChartsButtonUp();

	@Source("common/logoPoint.png")
	ImageResource baseLogoPoint();

	@Source("common/logoVision.png")
	ImageResource baseLogoVision();

	ImageResource bigTvButton();

	@Source("common/buildChartButton.png")
	@ImageOptions(height = 16, width = 16)
	ImageResource buildChartButton();

	ImageResource buttonsDelimiter();

	ImageResource captionsToggleDown();

	ImageResource captionsToggleUp();

	ImageResource cellBottomLine();

	@Source("common/buildChartButton.png")
	@ImageOptions(height = 16, width = 16)
	ImageResource chartTypeButton();

	@Source("common/clearConditionTemplate.png")
	ImageResource clearConditionTemplate();

	@Source("common/clearFilters.png")
	ImageResource clearFilters();

	@Source("common/clearNotificationTemplate.png")
	ImageResource clearNotificationTemplate();

	@Source("common/clearSeriesButton.png")
	ImageResource clearSeriesButton();

	@Source("common/createChartWidgetIconStar.png")
	ImageResource createChartWidgetIconStar();

	@Source("common/createdChartWidgetIconStar.png")
	ImageResource createdChartWidgetIconStar();

	@Source("common/createdWidgetIconStar.png")
	ImageResource createdWidgetIcon();

	@Source("common/createWidgetIconStar.png")
	ImageResource createWidgetIcon();

	@Source("common/common.css")
	CommonStyle css();

	@ImageOptions(repeatStyle = RepeatStyle.None)
	@Source("common/dash.png")
	ImageResource dash();

	@Source("common/delete.png")
	ImageResource deleteButton();

	@Source("common/deletePolicyActionsTemplate.png")
	ImageResource deletePolicyActionsTemplate();

	@Source("common/deleteUser.png")
	ImageResource deleteUserButton();

	@Source("common/devider.png")
	ImageResource devider();

	@Source("common/disableUser.png")
	ImageResource disableUserButton();

	@Source("common/editButton.png")
	ImageResource editButton();

	@Source("common/editPolicyActionsTemplate.png")
	ImageResource editPolicyActionsTemplate();

	@Source("common/enableUser.png")
	ImageResource enableUserButton();

	ImageResource exportButtonDown();

	ImageResource exportButtonUp();

	@Source("common/exportToExcelButton.png")
	ImageResource exportToExcelButton();

	ImageResource exportToExcelButtonDown();

	ImageResource exportToExcelButtonUp();

	@Source("common/gridCellCollapseButton.png")
	ImageResource gridCellCollapseButton();

	@Source("common/gridCellExpandButton.png")
	ImageResource gridCellExpandButton();

	@Source("common/gridCellRemoveButton.png")
	ImageResource gridCellRemoveButton();

	@Source("common/gridCellRemoveMiniButton.png")
	ImageResource gridCellRemoveMiniButton();

	@Source("common/loadTemplateButton.png")
	ImageResource loadTemplateButton();

	@Source("common/loginButton.png")
	ImageResource loginButton();

	@Source("common/loginLogoPoint.png")
	ImageResource loginLogoPoint();

	@Source("common/loginLogoVision.png")
	ImageResource loginLogoVision();

	@Source("common/logout.png")
	ImageResource logout();

	ImageResource mouseTrackingButtonDown();

	ImageResource mouseTrackingButtonUp();

	@Source("common/new.png")
	ImageResource newButton();

	@Source("common/newGroup.png")
	ImageResource newGroup();

	@Source("common/newPolicyActionsTemplate.png")
	ImageResource newPolicyActionsTemplate();

	@Source("common/newUser.png")
	ImageResource newUserButton();

	ImageResource playVideo();

	ImageResource renameButtonDown();

	ImageResource renameButtonUp();

	@Source("common/saveTemplateButton.png")
	ImageResource saveTemplateButton();

	ImageResource showChartForAlert();

	ImageResource showChartForReport();

	ImageResource showTableForAlert();

	ImageResource showTableForReport();

	@Source("common/sic.png")
	ImageResource sic();

	ImageResource synchronizeButtonToggleDown();

	ImageResource synchronizeButtonToggleUp();

	ImageResource tableButtonDown();

	ImageResource tableButtonUp();

	ImageResource thresholdsToggleDown();

	ImageResource thresholdsToggleUp();

	@Source("common/transparent.png")
	ImageResource transparent1x1();

	ImageResource treeClose();

	ImageResource treeOpen();

	ImageResource undoZoomButtonDown();

	ImageResource undoZoomButtonUp();

	@Source("common/updateButton.png")
	ImageResource updateButton();

	@Source("common/updateButtonInvert.png")
	ImageResource updateButtonInvert();

	@Source("common/userSettings.png")
	ImageResource userSettings();

	@Source("common/1x1_widget_size.png")
	ImageResource widget1x1();

	@Source("common/1x2_widget_size.png")
	ImageResource widget1x2();

	@Source("common/2x1_widget_size.png")
	ImageResource widget2x1();

	@Source("common/2x2_widget_size.png")
	ImageResource widget2x2();

	@Source("common/3x1_widget_size.png")
	ImageResource widget3x1();

	@Source("common/3x2_widget_size.png")
	ImageResource widget3x2();

}

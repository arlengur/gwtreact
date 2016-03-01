/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark;

import java.util.Map;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonCellAppearance;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.theme.base.client.menu.ItemBaseAppearance;
import com.sencha.gxt.theme.base.client.menu.MenuBaseAppearance;
import com.sencha.gxt.theme.base.client.menu.MenuItemBaseAppearance;
import com.sencha.gxt.theme.base.client.panel.FramedPanelBaseAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.Window.WindowAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutAppearance;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel.CheckBoxColumnAppearance;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GroupingView.GroupingViewAppearance;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar.PagingToolBarAppearance;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar.ToolBarAppearance;
import com.tecomgroup.qos.gwt.client.style.CommonResources;
import com.tecomgroup.qos.gwt.client.style.common.AlertSeverityMarkerAppearance;
import com.tecomgroup.qos.gwt.client.style.common.PagerAppearance;
import com.tecomgroup.qos.gwt.client.style.common.TileAppearance;
import com.tecomgroup.qos.gwt.client.style.common.button.AnchorAppearance;
import com.tecomgroup.qos.gwt.client.style.common.button.ImageAnchorAppearance;
import com.tecomgroup.qos.gwt.client.style.common.cell.AlertCommentCellAppearance;
import com.tecomgroup.qos.gwt.client.style.common.cell.ButtonedGroupingCellAppearance;
import com.tecomgroup.qos.gwt.client.style.common.cell.IconedActionCellAppearance;
import com.tecomgroup.qos.gwt.client.style.common.cell.LiveStreamBasicCellAppearance;
import com.tecomgroup.qos.gwt.client.style.common.cell.LiveStreamCellAppearance;
import com.tecomgroup.qos.gwt.client.style.common.cell.RecordedStreamCellAppearance;
import com.tecomgroup.qos.gwt.client.style.common.field.CheckBoxBorderedAppearance;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance;
import com.tecomgroup.qos.gwt.client.style.common.grid.FilteredColumnHeaderAppearance;
import com.tecomgroup.qos.gwt.client.style.common.grid.PropertyGridAppearance;
import com.tecomgroup.qos.gwt.client.style.common.grid.TemplatesGridAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.ImageAnchorBaseAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.cell.CustomButtonCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.cell.IconedActionCellBaseAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkAnchorAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkAnchorFlickeringAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkButtonCellHugeAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkButtonCellHyperlinkAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkButtonCellLightAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.DarkButtonSmallIconAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.rectangle.DarkButtonCellRectangleAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkAlertCommentCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkAlertSeverityMarkerAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkButtonedGroupingCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkLiveStreamBasicCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkLiveStreamCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkRecordedStreamCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.container.DarkBorderLayoutAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.field.DarkCheckBoxBorderedAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.field.DarkTriggerFieldAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkAlertsGridAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkCheckBoxColumnAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkColumnHeaderAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkGridAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkGridStandardAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkGroupingViewAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkPropertyGridAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkRowEditorAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkTemplatesGridAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.menu.DarkItemAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.menu.DarkMenuAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.menu.DarkMenuItemAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.panel.DarkFramedPanelAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.panel.DarkTileAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.panel.light.DarkLightFramedPanelAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.panel.light.DarkLightestFramedPanelAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.tabs.DarkTabPanelAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.toolbar.DarkPagingToolBarAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.toolbar.DarkToolBarAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.widget.DarkPagerAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.window.DarkDialogAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkAppearanceFactory implements AppearanceFactory {

	@Override
	public AlertCommentCellAppearance alertCommentCellAppearance() {
		return new DarkAlertCommentCellAppearance();
	}

	@Override
	public AlertSeverityMarkerAppearance alertSeverityMarkerAppearance() {
		return new DarkAlertSeverityMarkerAppearance();
	}

	@Override
	public AnchorAppearance anchorAppearance() {
		return new DarkAnchorAppearance();
	}

	@Override
	public BorderLayoutAppearance borderLayoutAppearance() {
		return new DarkBorderLayoutAppearance();
	}

	@Override
	public <T> ButtonCellAppearance<T> buttonCellHugeAppearance() {
		return new DarkButtonCellHugeAppearance<T>();
	}

	@Override
	public <T> ButtonCellAppearance<T> buttonCellHyperlinkAppearance() {
		return new DarkButtonCellHyperlinkAppearance<T>();
	}

	@Override
	public <T> ButtonCellAppearance<T> buttonCellLightAppearance() {
		return new DarkButtonCellLightAppearance<T>();
	}
	@Override
	public <C> DarkButtonCellRectangleAppearance<C> buttonCellRectangleAppearance() {
		return new DarkButtonCellRectangleAppearance<C>();
	}

	@Override
	public <T> ButtonCellAppearance<T> buttonCellSmallIconAppearance() {
		return new DarkButtonSmallIconAppearance<T>();
	}

	@Override
	public <C> ButtonedGroupingCellAppearance<C> buttonedGroupingCellAppearance() {
		return new DarkButtonedGroupingCellAppearance<C>();
	}

	@Override
	public GroupingViewAppearance buttonedGroupingViewAppearance() {
		return new DarkGroupingViewAppearance();
	}
	@Override
	public CheckBoxBorderedAppearance checkBoxBorderedAppearance() {
		return new DarkCheckBoxBorderedAppearance();
	}

	@Override
	public <M> CheckBoxColumnAppearance<M> checkBoxColumnAppearance() {
		return new DarkCheckBoxColumnAppearance<M>();
	}

	@Override
	public FilteredColumnHeaderAppearance columnHeaderAppearance() {
		return new DarkColumnHeaderAppearance();
	}

	@Override
	public <T> ButtonCellAppearance<T> defaultCellAppearance() {
		return new CustomButtonCellAppearance<T>();
	}

	@Override
	public WindowAppearance dialogAppearance() {
		return new DarkDialogAppearance(false);
	}

	@Override
	public WindowAppearance dialogAppearanceWithHeader() {
		return new DarkDialogAppearance(true);
	}

	@Override
	public AnchorAppearance flickeringAnchorAppearance() {
		return new DarkAnchorFlickeringAppearance();
	}

	@Override
	public FramedPanelBaseAppearance framedPanelAppearance() {
		return new DarkFramedPanelAppearance();
	}

	@Override
	public AlertsGridAppearance gridAlertsAppearance() {
		return new DarkAlertsGridAppearance();
	}

	@Override
	public GridAppearance gridAppearance() {
		return new DarkGridAppearance();
	}

	@Override
	public PropertyGridAppearance gridPropertyAppearance() {
		return new DarkPropertyGridAppearance();
	}

	@Override
	public GridAppearance gridStandardAppearance() {
		return new DarkGridStandardAppearance();
	}

	@Override
	public TemplatesGridAppearance gridTemplatesAppearance() {
		return new DarkTemplatesGridAppearance();
	}

	@Override
	public <C> IconedActionCellAppearance<C> iconedActionCellAppearance(
			final ImageResource icon, final String title) {
		return new IconedActionCellBaseAppearance<C>(icon, title);
	}

	@Override
	public ImageAnchorAppearance imageAnchorAppearance(final String title) {
		return new ImageAnchorBaseAppearance(title);
	}

	@Override
	public ItemBaseAppearance itemAppearance() {
		return new DarkItemAppearance();
	}

	@Override
	public FramedPanelBaseAppearance lightestFramedPanelAppearance() {
		return new DarkLightestFramedPanelAppearance();
	}

	@Override
	public FramedPanelBaseAppearance lightFramedPanelAppearance() {
		return new DarkLightFramedPanelAppearance();
	}

	@Override
	public <C extends Map<String, String>> LiveStreamBasicCellAppearance<Map<String, String>> liveStreamBasicCellAppearance() {
		return new DarkLiveStreamBasicCellAppearance<Map<String, String>>();
	}

	@Override
	public <C extends Map<String, String>> LiveStreamCellAppearance<Map<String, String>> liveStreamCellAppearance() {
		return new DarkLiveStreamCellAppearance<Map<String, String>>();
	}

	@Override
	public MenuBaseAppearance menuAppearance() {
		return new DarkMenuAppearance();
	}

	@Override
	public MenuItemBaseAppearance menuItemAppearance() {
		return new DarkMenuItemAppearance();
	}

	@Override
	public PagerAppearance pagerAppearance() {
		return new DarkPagerAppearance();
	}

	@Override
	public PagingToolBarAppearance pagingToolBarAppearance() {
		return new DarkPagingToolBarAppearance();
	}

	@Override
	public <C extends Map<String, String>> RecordedStreamCellAppearance<Map<String, String>> recordedStreamCellAppearance() {
		return new DarkRecordedStreamCellAppearance<Map<String, String>>();
	}

	@Override
	public CommonResources resources() {
		return DarkResources.INSTANCE;
	}

	@Override
	public GridRowEditing.RowEditorAppearance rowEditorAppearance() {
		return new DarkRowEditorAppearance();
	}

	@Override
	public TabPanelAppearance tabPanelAppearance() {
		return new DarkTabPanelAppearance();
	}

	@Override
	public TileAppearance tileAppearance(final boolean hasSaveButton,
			final boolean hasChartButton) {
		return new DarkTileAppearance(hasSaveButton, hasChartButton);
	}

	@Override
	public TileAppearance tileAppearance(final boolean hasSaveButton,
			final boolean hasChartButton, final int rowSpan, final int colSpan) {
		return new DarkTileAppearance(hasSaveButton, hasChartButton, rowSpan,
				colSpan);
	}

	@Override
	public ToolBarAppearance toolBarAppearance() {
		return new DarkToolBarAppearance();
	}

	@Override
	public TriggerFieldAppearance triggerFieldAppearance() {
		return new DarkTriggerFieldAppearance();
	}
}

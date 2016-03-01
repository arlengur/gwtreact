/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme;

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
import com.tecomgroup.qos.gwt.client.style.theme.dark.button.rectangle.DarkButtonCellRectangleAppearance;

/**
 * @author ivlev.e
 * 
 */
public interface AppearanceFactory {

	public enum Theme {
		DARK
	}

	public final static String DIALOG_WITH_HEADER = "dialogWithHeader";

	public abstract AlertCommentCellAppearance alertCommentCellAppearance();

	public abstract AlertSeverityMarkerAppearance alertSeverityMarkerAppearance();

	public abstract AnchorAppearance anchorAppearance();

	public abstract BorderLayoutAppearance borderLayoutAppearance();

	public abstract <T> ButtonCellAppearance<T> buttonCellHugeAppearance();

	public abstract <T> ButtonCellAppearance<T> buttonCellHyperlinkAppearance();

	public abstract <T> ButtonCellAppearance<T> buttonCellLightAppearance();

	public abstract <C> DarkButtonCellRectangleAppearance<C> buttonCellRectangleAppearance();

	public abstract <T> ButtonCellAppearance<T> buttonCellSmallIconAppearance();

	public abstract <C> ButtonedGroupingCellAppearance<C> buttonedGroupingCellAppearance();

	public abstract GroupingViewAppearance buttonedGroupingViewAppearance();

	public abstract CheckBoxBorderedAppearance checkBoxBorderedAppearance();

	public abstract <M> CheckBoxColumnAppearance<M> checkBoxColumnAppearance();

	public abstract FilteredColumnHeaderAppearance columnHeaderAppearance();

	public abstract <T> ButtonCellAppearance<T> defaultCellAppearance();

	public abstract WindowAppearance dialogAppearance();

	public abstract WindowAppearance dialogAppearanceWithHeader();

	public abstract AnchorAppearance flickeringAnchorAppearance();

	public abstract FramedPanelBaseAppearance framedPanelAppearance();

	public abstract AlertsGridAppearance gridAlertsAppearance();

	public abstract GridAppearance gridAppearance();

	public abstract PropertyGridAppearance gridPropertyAppearance();

	public abstract GridAppearance gridStandardAppearance();

	public abstract TemplatesGridAppearance gridTemplatesAppearance();

	public abstract <C> IconedActionCellAppearance<C> iconedActionCellAppearance(
			ImageResource icon, String title);

	public abstract ImageAnchorAppearance imageAnchorAppearance(String title);

	public abstract ItemBaseAppearance itemAppearance();

	public abstract FramedPanelBaseAppearance lightestFramedPanelAppearance();

	public abstract FramedPanelBaseAppearance lightFramedPanelAppearance();

	public abstract <C extends Map<String, String>> LiveStreamBasicCellAppearance<Map<String, String>> liveStreamBasicCellAppearance();

	public abstract <C extends Map<String, String>> LiveStreamCellAppearance<Map<String, String>> liveStreamCellAppearance();

	public abstract MenuBaseAppearance menuAppearance();

	public abstract MenuItemBaseAppearance menuItemAppearance();

	public abstract PagerAppearance pagerAppearance();

	public abstract PagingToolBarAppearance pagingToolBarAppearance();

	public abstract <C extends Map<String, String>> RecordedStreamCellAppearance<Map<String, String>> recordedStreamCellAppearance();

	public abstract CommonResources resources();

	public abstract GridRowEditing.RowEditorAppearance rowEditorAppearance();

	public abstract TabPanelAppearance tabPanelAppearance();

	public abstract TileAppearance tileAppearance(boolean hasSaveButton,
			boolean hasChartButton);

	public abstract TileAppearance tileAppearance(boolean hasSaveButton,
			boolean hasChartButton, int rowSpan, int colSpan);

	public abstract ToolBarAppearance toolBarAppearance();

	public abstract TriggerFieldAppearance triggerFieldAppearance();
}

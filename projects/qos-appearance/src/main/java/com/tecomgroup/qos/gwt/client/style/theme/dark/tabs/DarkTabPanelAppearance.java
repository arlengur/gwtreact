/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.tabs;

import static com.google.gwt.resources.client.ImageResource.RepeatStyle.Both;
import static com.google.gwt.resources.client.ImageResource.RepeatStyle.Horizontal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.tabs.TabPanelBaseAppearance;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.tecomgroup.qos.gwt.client.style.common.AppearanceUtils;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertSeverityStyle;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.TabItemConfigWithConditionLevel;

/**
 * @author abondin
 *
 */
public class DarkTabPanelAppearance extends TabPanelBaseAppearance {
	public interface DarkTabPanelResources
			extends
				TabPanelResources,
				ClientBundle {

		@ImageOptions(repeatStyle = Both)
		@Source("com/sencha/gxt/theme/gray/client/tabs/bottomInactiveLeftBackground.gif")
		ImageResource bottomInactiveLeftBackground();

		@ImageOptions(repeatStyle = Both)
		@Source("com/sencha/gxt/theme/gray/client/tabs/bottomInactiveRightBackground.gif")
		ImageResource bottomInactiveRightBackground();

		@ImageOptions(repeatStyle = Both)
		@Source("com/sencha/gxt/theme/gray/client/tabs/bottomLeftBackground.gif")
		ImageResource bottomLeftBackground();

		@ImageOptions(repeatStyle = Both)
		@Source("com/sencha/gxt/theme/gray/client/tabs/bottomRightBackground.gif")
		ImageResource bottomRightBackground();

		@Source("com/sencha/gxt/theme/gray/client/tabs/scrollerLeft.gif")
		ImageResource scrollerLeft();

		@Source("com/sencha/gxt/theme/gray/client/tabs/scrollerLeftOver.gif")
		ImageResource scrollerLeftOver();

		@Source("com/sencha/gxt/theme/gray/client/tabs/scrollerRight.gif")
		ImageResource scrollerRight();

		@Source("com/sencha/gxt/theme/gray/client/tabs/scrollerRightOver.gif")
		ImageResource scrollerRightOver();

		@Override
		@Source({
				"com/tecomgroup/qos/gwt/client/style/theme/dark/grid/DarkAlertSeverityStyle.css",
				"com/sencha/gxt/theme/base/client/tabs/TabPanel.css",
				"DarkTabPanel.css"})
		DarkTabPanelStyle style();

		@ImageOptions(repeatStyle = Horizontal)
		ImageResource tabCenter();

		@ImageOptions(repeatStyle = Horizontal)
		ImageResource tabCenterActive();

		@ImageOptions(repeatStyle = Horizontal)
		ImageResource tabCenterOver();

		ImageResource tabClose();

		ImageResource tabLeft();

		ImageResource tabLeftActive();

		ImageResource tabLeftOver();

		@ImageOptions(repeatStyle = Both)
		ImageResource tabRight();

		@ImageOptions(repeatStyle = Both)
		ImageResource tabRightActive();

		@ImageOptions(repeatStyle = Both)
		ImageResource tabRightOver();

		@ImageOptions(repeatStyle = Horizontal)
		@Source("com/sencha/gxt/theme/gray/client/tabs/tabStripBackground.gif")
		ImageResource tabStripBackground();

		@ImageOptions(repeatStyle = Horizontal)
		@Source("com/sencha/gxt/theme/gray/client/tabs/tabStripBottomBackground.gif")
		ImageResource tabStripBottomBackground();

	}

	public interface DarkTabPanelStyle
			extends
				TabPanelStyle,
				AlertSeverityStyle {
	}

	public DarkTabPanelAppearance() {
		this(GWT.<DarkTabPanelResources> create(DarkTabPanelResources.class),
				GWT.<Template> create(Template.class), GWT
						.<ItemTemplate> create(ItemTemplate.class));
	}

	public DarkTabPanelAppearance(final DarkTabPanelResources resources,
			final Template template, final ItemTemplate itemTemplate) {
		super(resources, template, itemTemplate);
	}

	public DarkTabPanelStyle getStyle() {
		return (DarkTabPanelStyle) style;
	}

	@Override
	public void insert(final XElement parent, final TabItemConfig config,
			final int index) {
		super.insert(parent, config, index);
		if (config instanceof TabItemConfigWithConditionLevel) {
			setSeverityStyle((XElement) getStrip(parent).getChild(index),
					config);
		}
	}

	private void setSeverityStyle(final XElement item,
			final TabItemConfig config) {
		final TabItemConfigWithConditionLevel conditionConfig = (TabItemConfigWithConditionLevel) config;
		final String severityStyle = AppearanceUtils.getSeverityStyle(
				getStyle(), conditionConfig.getSeverity());
		item.setClassName(severityStyle, conditionConfig.isConditionEnabled());
	}

	@Override
	public void updateItem(final XElement item, final TabItemConfig config) {
		super.updateItem(item, config);
		if (config instanceof TabItemConfigWithConditionLevel) {
			setSeverityStyle(item, config);
		}
	}

}

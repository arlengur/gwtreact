/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;

/**
 * 
 * Table Header with filter icon
 * 
 * @author abondin
 * 
 */
public abstract class FilteredColumnHeaderAppearance
		implements
			ColumnHeaderAppearance {
	public interface ColumnHeaderTemplate extends XTemplates {
		@XTemplate(source = "com/sencha/gxt/theme/base/client/grid/ColumnHeader.html")
		SafeHtml render(FilteredColumnHeaderStyle style);
	}

	public interface FilteredColumnHeaderResources extends ClientBundle {

		// preventInlining only need for ie6 ie7 because of bottom alignment
		@ImageOptions(repeatStyle = RepeatStyle.Horizontal, preventInlining = true)
		ImageResource columnHeader();

		@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
		ImageResource columnHeaderOver();

		ImageResource columnMoveBottom();

		ImageResource columnMoveTop();

		ImageResource columnsIcon();

		ImageResource filterIcon();

		ImageResource sortAsc();

		ImageResource sortAscendingIcon();

		ImageResource sortDesc();

		ImageResource sortDescendingIcon();

		@Source("FilteredColumnHeader.css")
		FilteredColumnHeaderStyle style();

	}

	public interface FilteredColumnHeaderStyle
			extends
				CssResource,
				ColumnHeaderStyles {
		String filterIcon();
	}

	protected final FilteredColumnHeaderResources resources;

	protected final FilteredColumnHeaderStyle style;

	protected final ColumnHeaderTemplate templates = GWT
			.create(ColumnHeaderTemplate.class);
	public FilteredColumnHeaderAppearance(
			final FilteredColumnHeaderResources resources) {
		this.resources = resources;
		this.style = this.resources.style();

		StyleInjectorHelper.ensureInjected(style, true);
	}
	@Override
	public ImageResource columnsIcon() {
		return resources.columnsIcon();
	}

	@Override
	public String columnsWrapSelector() {
		return "." + style.headerInner();
	}

	/**
	 * @return the resources
	 */
	public FilteredColumnHeaderResources getResources() {
		return resources;
	}

	/**
	 * @return the style
	 */
	public FilteredColumnHeaderStyle getStyle() {
		return style;
	}

	@Override
	public void render(final SafeHtmlBuilder sb) {
		sb.append(templates.render(style));
	}

	@Override
	public ImageResource sortAscendingIcon() {
		return resources.sortAscendingIcon();
	}

	@Override
	public ImageResource sortDescendingIcon() {
		return resources.sortDescendingIcon();
	}

	@Override
	public ColumnHeaderStyles styles() {
		return style;
	}
}
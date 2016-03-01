/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.style.common.cell.ButtonedGroupingCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingCell;

/**
 * @author ivlev.e
 * 
 */
public class DarkButtonedGroupingCellAppearance<C>
		implements
			ButtonedGroupingCellAppearance<C> {

	public interface DarkChartSeriesResources extends ClientBundle {

		ImageResource cellBottomLine();

		@Source("DarkButtonedGroupingCellAppearance.css")
		DarkChartSeriesStyle style();
	}

	public interface DarkChartSeriesStyle extends CssResource {
		String cell();

		String removeIcon();

		String text();
	}

	public interface DarkChartSeriesTemplate extends XTemplates {

		@XTemplate("<div class=\"{style.cell}\">")
		SafeHtml cell(DarkChartSeriesStyle style);

		@XTemplate("<span class=\"{style.removeIcon}\">{removeIconHtml}</span>")
		SafeHtml removeIcon(DarkChartSeriesStyle style, SafeHtml removeIconHtml);

		@XTemplate("<pre class=\"{style.text}\">{text}</pre>")
		SafeHtml textWithStyles(DarkChartSeriesStyle style, SafeHtml text);
	}

	private DarkChartSeriesStyle style;

	private DarkChartSeriesTemplate template;

	public DarkButtonedGroupingCellAppearance() {
		this(
				GWT.<DarkChartSeriesResources> create(DarkChartSeriesResources.class));
	}

	public DarkButtonedGroupingCellAppearance(
			final DarkChartSeriesResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();

		this.template = GWT.create(DarkChartSeriesTemplate.class);
	}

	@Override
	public boolean isRemoveButtonPressed(final XElement element) {
		return element.findParent("." + style.removeIcon(), 2) != null;
	}

	@Override
	public void render(final ButtonedGroupingCell<C> cell,
			final Context context, final C value, final SafeHtmlBuilder sb) {
		sb.append(template.cell(style));
		sb.append(template.textWithStyles(style,
				SafeHtmlUtils.fromString(value.toString())));
		sb.append(template.removeIcon(
				style,
				AbstractImagePrototype.create(
						DarkResources.INSTANCE.gridCellRemoveMiniButton())
						.getSafeHtml()));
		sb.appendHtmlConstant("</div>");
	}
}

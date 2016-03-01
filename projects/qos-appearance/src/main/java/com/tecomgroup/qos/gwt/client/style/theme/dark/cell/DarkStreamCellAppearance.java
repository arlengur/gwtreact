/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import java.util.Map;
import java.util.Set;

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
import com.tecomgroup.qos.gwt.client.style.common.cell.StreamCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.StreamCell;

/**
 * @author ivlev.e
 * 
 */
public class DarkStreamCellAppearance<C extends Map<String, String>>
		implements
			StreamCellAppearance<C> {

	public interface DarkLiveStreamTemplate extends XTemplates {

		@XTemplate("<div class=\"{style.cell}\">")
		SafeHtml cell(DarkStreamStyle style);

		@XTemplate("<span class=\"{style.removeIcon}\">{removeIconHtml}</span>")
		SafeHtml removeIcon(DarkStreamStyle style, SafeHtml removeIconHtml);

		@XTemplate("<span class=\"{style.rowIcon}\">{rowIconHtml}</span>")
		SafeHtml rowIcon(DarkStreamStyle style, SafeHtml rowIconHtml);

		@XTemplate("<span class=\"{style.key}\">")
		SafeHtml rowItem(DarkStreamStyle style);

		@XTemplate("<pre class=\"{style.text}\">{text}")
		SafeHtml textWithStyles(DarkStreamStyle style, SafeHtml text);
	}

	public interface DarkStreamResources extends ClientBundle {

		ImageResource cellBottomLine();

		@Source("DarkLiveStreamCellAppearance.css")
		DarkStreamStyle style();
	}

	public interface DarkStreamStyle extends CssResource {
		String cell();

		String key();

		String removeIcon();

		String rowIcon();

		String text();
	}

	protected DarkStreamStyle style;

	protected DarkLiveStreamTemplate template;

	public DarkStreamCellAppearance() {
		this(GWT.<DarkStreamResources> create(DarkStreamResources.class));
	}

	public DarkStreamCellAppearance(final DarkStreamResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();

		this.template = GWT.create(DarkLiveStreamTemplate.class);
	}

	@Override
	public boolean isRemoveButtonPressed(final XElement element) {
		return element.findParent("." + style.removeIcon(), 2) != null;
	}

	@Override
	public void render(final StreamCell<C> cell,
			final Set<String> excludedProperties, final Context context,
			final C value, final SafeHtmlBuilder sb) {
		sb.append(template.cell(style));

		final SafeHtmlBuilder sbContent = new SafeHtmlBuilder();

		for (final String key : value.keySet()) {
			if (excludedProperties.contains(key)) {
				continue;
			}
			sbContent.append(template.rowItem(style));
			sbContent.append(SafeHtmlUtils.fromTrustedString(key + ": "));
			sbContent.appendHtmlConstant("</span>");
			sbContent.appendHtmlConstant("<span>");
			sbContent.append(SafeHtmlUtils.fromTrustedString(value.get(key)));
			sbContent.appendHtmlConstant("</span><br/>");
		}
		sb.append(template.textWithStyles(style, sbContent.toSafeHtml()));
		sb.appendHtmlConstant("</pre>");
		sb.append(template.removeIcon(
				style,
				AbstractImagePrototype.create(
						DarkResources.INSTANCE.gridCellRemoveMiniButton())
						.getSafeHtml()));
		sb.appendHtmlConstant("</div>");
	}

}

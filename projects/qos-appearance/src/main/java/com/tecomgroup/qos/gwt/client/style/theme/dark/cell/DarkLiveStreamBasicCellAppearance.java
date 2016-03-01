/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import java.util.Map;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.XTemplates;
import com.tecomgroup.qos.gwt.client.style.common.cell.LiveStreamBasicCellAppearance;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamBasicCell;

/**
 * @author ivlev.e
 * 
 */
public class DarkLiveStreamBasicCellAppearance<C extends Map<String, String>>
		implements
			LiveStreamBasicCellAppearance<Map<String, String>> {

	public interface DarkLiveStreamBasicResources extends ClientBundle {

		ImageResource cellBottomLine();

		@Source("DarkLiveStreamBasicCellAppearance.css")
		DarkLiveStreamBasicStyle style();
	}

	public interface DarkLiveStreamBasicStyle extends CssResource {

		String cell();

		String header();

		String key();

		String rowIcon();

		String text();
	}

	public interface DarkLiveStreamBasicTemplate extends XTemplates {

		@XTemplate("<div class=\"{style.cell}\">")
		SafeHtml cell(DarkLiveStreamBasicStyle style);

		@XTemplate("<div class=\"{style.header}\">{header}</div>")
		SafeHtml header(DarkLiveStreamBasicStyle style, String header);

		@XTemplate("<span class=\"{style.rowIcon}\">{rowIconHtml}</span>")
		SafeHtml rowIcon(DarkLiveStreamBasicStyle style, SafeHtml rowIconHtml);

		@XTemplate("<span class=\"{style.key}\">")
		SafeHtml rowItem(DarkLiveStreamBasicStyle style);

		@XTemplate("<pre class=\"{style.text}\">{text}")
		SafeHtml textWithStyles(DarkLiveStreamBasicStyle style, SafeHtml text);
	}

	protected DarkLiveStreamBasicStyle style;

	protected DarkLiveStreamBasicTemplate template;

	public DarkLiveStreamBasicCellAppearance() {
		this(
				GWT.<DarkLiveStreamBasicResources> create(DarkLiveStreamBasicResources.class));
	}

	public DarkLiveStreamBasicCellAppearance(
			final DarkLiveStreamBasicResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();

		this.template = GWT.create(DarkLiveStreamBasicTemplate.class);
	}

	@Override
	public void render(final LiveStreamBasicCell<Map<String, String>> cell,
			final String headerPropertyName, final Context context,
			final Map<String, String> value, final SafeHtmlBuilder sb) {
		sb.append(template.cell(style));

		final SafeHtmlBuilder sbContent = new SafeHtmlBuilder();

		if (headerPropertyName != null) {
			if (value.containsKey(headerPropertyName)) {
				final String header = value.remove(headerPropertyName);
				sb.append(template.header(style, header));
			}
		}

		for (final String key : value.keySet()) {
			sbContent.append(template.rowItem(style));
			sbContent.append(SafeHtmlUtils.fromTrustedString(key + ": "));
			sbContent.appendHtmlConstant("</span>");
			sbContent.appendHtmlConstant("<span>");
			sbContent.append(SafeHtmlUtils.fromTrustedString(value.get(key)));
			sbContent.appendHtmlConstant("</span><br/>");
		}
		sb.append(template.textWithStyles(style, sbContent.toSafeHtml()));
		sb.appendHtmlConstant("</pre>");
		sb.appendHtmlConstant("</div>");
	}

}

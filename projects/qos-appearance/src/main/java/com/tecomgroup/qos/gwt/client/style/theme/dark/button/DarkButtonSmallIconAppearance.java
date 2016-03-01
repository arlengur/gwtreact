/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.button;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonCellAppearance;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * @author ivlev.e
 * 
 */
public class DarkButtonSmallIconAppearance<C>
		implements
			ButtonCellAppearance<C> {

	public interface DarkButtonCellTemplate extends XTemplates {

		@XTemplate("<div class=\"{style.button}\">")
		SafeHtml button(DarkButtonSmallIconStyle style);

		@XTemplate("<span class=\"{style.icon}\">{imageHtml}</span>")
		SafeHtml icon(DarkButtonSmallIconStyle style, SafeHtml imageHtml);

		@XTemplate("<span class=\"{style.text}\">{text}</span>")
		SafeHtml textWithStyles(DarkButtonSmallIconStyle style, SafeHtml text);
	}

	public interface DarkButtonSmallIconCellResources extends ClientBundle {
		@Source("DarkButtonSmallIconAppearance.css")
		DarkButtonSmallIconStyle style();
	}

	public interface DarkButtonSmallIconStyle extends CssResource {
		String button();

		String icon();

		String text();
	}

	protected final DarkButtonSmallIconStyle style;

	protected final DarkButtonCellTemplate template;

	public DarkButtonSmallIconAppearance() {
		this(
				GWT.<DarkButtonSmallIconCellResources> create(DarkButtonSmallIconCellResources.class));
	}

	public DarkButtonSmallIconAppearance(
			final DarkButtonSmallIconCellResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();

		this.template = GWT.create(DarkButtonCellTemplate.class);
	}

	@Override
	public XElement getButtonElement(final XElement parent) {
		return null;
	}

	@Override
	public XElement getFocusElement(final XElement parent) {
		return null;
	}

	@Override
	public void onFocus(final XElement parent, final boolean focused,
			final NativeEvent event) {

	}

	@Override
	public void onOver(final XElement parent, final boolean over,
			final NativeEvent event) {

	}

	@Override
	public void onPress(final XElement parent, final boolean pressed,
			final NativeEvent event) {

	}

	@Override
	public void onToggle(final XElement parent, final boolean pressed) {

	}

	@Override
	public void render(final ButtonCell<C> cell, final Context context,
			final C value, final SafeHtmlBuilder sb) {
		final ImageResource icon = cell.getIcon();
		sb.append(template.button(style));
		if (icon != null) {
			sb.append(template.icon(style, AbstractImagePrototype.create(icon)
					.getSafeHtml()));
		}
		sb.append(template.textWithStyles(style,
				SafeHtmlUtils.fromString(cell.getText())));
		sb.appendHtmlConstant("</div>");
	}

}

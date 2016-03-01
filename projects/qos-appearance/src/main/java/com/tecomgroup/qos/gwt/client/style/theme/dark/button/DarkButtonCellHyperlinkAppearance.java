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
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonCellAppearance;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles.CommonStylesDefaultAppearance.CommonDefaultStyles;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources.DarkStyle;

/**
 * @author ivlev.e
 * 
 */
public class DarkButtonCellHyperlinkAppearance<C>
		implements
			ButtonCellAppearance<C> {

	@ImportedWithPrefix("hyperlink")
	public interface DarkButtonCellHyperlinkStyle extends CssResource {
		String button();
	}

	public interface DarkButtonCellResources extends ClientBundle {

		@Source("DarkButtonCellHyperlinkAppearance.css")
		@Import({DarkStyle.class, CommonDefaultStyles.class})
		DarkButtonCellHyperlinkStyle style();
	}

	public interface DarkButtonCellTemplate extends XTemplates {

		@XTemplate(value = "<div class=\"{style.button}\">{text}</div>")
		SafeHtml textWithStyles(DarkButtonCellHyperlinkStyle style,
				SafeHtml text);
	}

	protected final DarkButtonCellHyperlinkStyle style;

	protected final DarkButtonCellTemplate template;

	public DarkButtonCellHyperlinkAppearance() {
		this(
				GWT.<DarkButtonCellResources> create(DarkButtonCellResources.class));
	}

	public DarkButtonCellHyperlinkAppearance(
			final DarkButtonCellResources resources) {
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
		sb.append(template.textWithStyles(style,
				SafeHtmlUtils.fromString(cell.getText())));

	}
}

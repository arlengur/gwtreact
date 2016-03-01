/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Window;
import com.tecomgroup.qos.gwt.client.style.common.button.AnchorAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorBaseAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorBaseAppearance.AnchorBaseStyle;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.menu.XToolbarItem;

/**
 * Widget for representation simple html hyperlink.
 * 
 * @author ivlev.e
 */
public class Anchor extends Component implements XToolbarItem {

	private final AnchorBaseStyle style;

	private final String text;

	private final String href;

	public Anchor(final String href, final String text) {
		this(href, text, null, (AnchorAppearance) GWT
				.create(AnchorBaseAppearance.class));
	}

	public Anchor(final String href, final String text,
			final AnchorAppearance appearance) {
		this(href, text, null, appearance);
	}

	public Anchor(final String href, final String text, final String target) {
		this(href, text, target, (AnchorAppearance) GWT
				.create(AnchorBaseAppearance.class));
	}

	public Anchor(final String href, final String text, String target,
			final AnchorAppearance appearance) {
		this.text = text;
		this.href = href;
		if (target == null) {
			target = "_self";
		}
		final SafeHtmlBuilder sb = new SafeHtmlBuilder();
		appearance.render(sb, UriUtils.fromTrustedString(href), text, target);
		setElement(XDOM.create(sb.toSafeHtml()));
		style = appearance.getResources().style();
	}

	@Override
	public String getMenuTitle() {
		return text;
	}

	@Override
	public String getPlaceToken() {
		return href;
	}

	@Override
	public Window getPopupWindow() {
		return null;
	}

	/**
	 * @return the style
	 */
	public AnchorBaseStyle getStyle() {
		return style;
	}
}

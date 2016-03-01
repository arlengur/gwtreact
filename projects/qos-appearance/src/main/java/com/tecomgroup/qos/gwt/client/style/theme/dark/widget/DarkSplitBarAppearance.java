/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.SplitBar.SplitBarAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkSplitBarAppearance implements SplitBarAppearance {

	public interface DarkSplitBarResources extends ClientBundle {
		@Source("DarkSplitBar.css")
		DarkSplitBarStyle css();

		ImageResource miniBottom();

		ImageResource miniLeft();

		ImageResource miniRight();

		ImageResource miniTop();
	}

	public interface DarkSplitBarStyle extends CssResource {
		String bar();

		String horizontalBar();

		String mini();

		String miniBottom();

		String miniLeft();

		String miniOver();

		String miniRight();

		String miniTop();

		String proxy();

		String verticalBar();
	}

	private final DarkSplitBarResources resources;
	private final DarkSplitBarStyle style;

	public DarkSplitBarAppearance() {
		this(GWT.<DarkSplitBarResources> create(DarkSplitBarResources.class));
	}

	public DarkSplitBarAppearance(final DarkSplitBarResources resources) {
		this.resources = resources;
		this.style = this.resources.css();
		this.style.ensureInjected();
	}

	@Override
	public String miniClass(final Direction direction) {
		String cls = style.mini();

		switch (direction) {
			case UP :
				cls += " " + style.miniTop();
				break;
			case DOWN :
				cls += " " + style.miniBottom();
				break;
			case LEFT :
				cls += " " + style.miniLeft();
				break;
			case RIGHT :
				cls += " " + style.miniRight();
				break;
		}

		return cls;
	}

	@Override
	public String miniSelector() {
		return "." + style.mini();
	}

	@Override
	public void onMiniOver(final XElement mini, final boolean over) {
		mini.setClassName(style.miniOver(), over);
	}

	@Override
	public String proxyClass() {
		return style.proxy();
	}

	@Override
	public void render(final SafeHtmlBuilder sb, final LayoutRegion region) {
		String cls = "";
		if (region == LayoutRegion.SOUTH || region == LayoutRegion.NORTH) {
			cls = style.horizontalBar();
		} else {
			cls = style.verticalBar();
		}
		sb.appendHtmlConstant("<div class='" + cls + "'></div>");
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.style.theme.base.panel.TileBaseAppearance.TileBaseStyle;

/**
 * @author ivlev.e
 * 
 */
public interface TileAppearance {

	XElement getContentElement(XElement parent);

	XElement getHeaderElement(XElement parent);

	boolean isRemoveButtonPressed(XElement element);

	boolean isSaveButtonPressed(XElement element);

    boolean isChartButtonPressed(XElement element);

    void render(SafeHtmlBuilder sb, String title);

	TileBaseStyle style();
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * @author ivlev.e
 * 
 */
public interface VideoPanelAppearance {

	int getBottomBorderHeight();

	XElement getContentElement(XElement parent);

	int getLeftBorderWidth();

	int getRightBorderWidth();

	int getTopBorderHeight();

	boolean isAddToDashboardButtonPressed(final XElement element);

	public boolean isAddedToDashboard(final XElement element);

	boolean isCloseButtonPressed(final XElement element);

	boolean isDownloadButtonPressed(final XElement element);

	void render(SafeHtmlBuilder sb, String title, boolean closable,
			boolean hasDownloadButton, String downloadButtonTitle, boolean hasAddToDashboardButton,
			boolean isAddedToDashboard);

	void setAddedToDashboard(final XElement element,
			boolean isAddedToDashboard);

}

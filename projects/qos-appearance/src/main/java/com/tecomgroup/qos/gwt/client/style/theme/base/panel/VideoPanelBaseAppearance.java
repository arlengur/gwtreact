/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.tecomgroup.qos.gwt.client.style.common.VideoPanelAppearance;

/**
 * @author ivlev.e
 *
 */
public class VideoPanelBaseAppearance implements VideoPanelAppearance {

	public interface VideoPanelResources extends ClientBundle {

		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource addedToDasboardButton();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource addToDasboardButton();

		ImageResource bottomLeftBorder();

		ImageResource bottomRightBorder();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource cross();

		@ImageOptions(repeatStyle = RepeatStyle.None)
		ImageResource downloadButton();

		@ImageOptions(repeatStyle = RepeatStyle.Vertical)
		ImageResource leftBorder();

		@ImageOptions(repeatStyle = RepeatStyle.Vertical)
		ImageResource rightBorder();

		@Source("VideoPanel.css")
		VideoPanelStyle style();

		ImageResource topLeftBorder();

		ImageResource topRightBorder();
	}

	public interface VideoPanelStyle extends CssResource {

		String addedToDasboardButton();

		String addToDasboardButton();

		String bodyWrap();

		String bottomLeft();

		String bottomRight();

		String bottomVideoBorder();

		String content();

		String contentArea();

		String downloadButton();

		String left();

		String leftPosition();

		String removeButton();

		String right();

		String rightPosition();

		String title();

		String topLeft();

		String topRight();

		String topVideoBorder();
	}

	public interface VideoPanelTemplate extends XTemplates {
		@XTemplate(source = "VideoPanelTemplate.html")
		SafeHtml template(VideoPanelStyle style, String title,
				final boolean closable, final boolean hasDownloadButton, String downloadButtonTitle,
				final boolean hasAddToDashboardButton,
				final boolean isAddedToDashboard);
	}

	protected VideoPanelResources resources;

	protected VideoPanelStyle style;

	protected VideoPanelTemplate template;

	public VideoPanelBaseAppearance() {
		this((VideoPanelResources) GWT.create(VideoPanelResources.class));
	}

	public VideoPanelBaseAppearance(final VideoPanelResources resources) {
		this.resources = resources;
		this.style = resources.style();
		StyleInjectorHelper.ensureInjected(this.style, true);
		this.template = GWT.create(VideoPanelTemplate.class);
	}

	@Override
	public int getBottomBorderHeight() {
		return resources.bottomLeftBorder().getHeight();
	}

	@Override
	public XElement getContentElement(final XElement parent) {
		return parent.selectNode("." + style.content());
	}

	@Override
	public int getLeftBorderWidth() {
		return resources.leftBorder().getWidth();
	}

	@Override
	public int getRightBorderWidth() {
		return resources.rightBorder().getWidth();
	}

	@Override
	public int getTopBorderHeight() {
		return resources.topRightBorder().getHeight();
	}

	@Override
	public boolean isAddToDashboardButtonPressed(final XElement element) {
		return element.hasClassName(style.addToDasboardButton())
				|| element.hasClassName(style.addedToDasboardButton());
	}

	@Override
	public boolean isAddedToDashboard(final XElement element) {
		return element.hasClassName(style.addedToDasboardButton());
	}

	@Override
	public boolean isCloseButtonPressed(final XElement element) {
		return element.hasClassName(style.removeButton());
	}

	@Override
	public boolean isDownloadButtonPressed(final XElement element) {
		return element.hasClassName(style.downloadButton());
	}

	@Override
	public void render(final SafeHtmlBuilder sb, final String title,
			final boolean closable, final boolean hasDownloadButton, final String downloadButtonTitle,
			final boolean hasAddToDashboardButton,
			final boolean isAddedToDashboard) {
		sb.append(template.template(style, title, closable, hasDownloadButton, downloadButtonTitle,
				hasAddToDashboardButton, isAddedToDashboard));
	}

	@Override
	public void setAddedToDashboard(final XElement element,
			final boolean isAddedToDashboard) {
		String selector, newClass, oldClass;

		if (isAddedToDashboard) {
			selector = "." + style.addToDasboardButton();
			newClass = style.addedToDasboardButton();
			oldClass = style.addToDasboardButton();
		} else {
			selector = "." + style.addedToDasboardButton();
			newClass = style.addToDasboardButton();
			oldClass = style.addedToDasboardButton();
		}

		element.child(selector).replaceClassName(oldClass, newClass);
	}

}

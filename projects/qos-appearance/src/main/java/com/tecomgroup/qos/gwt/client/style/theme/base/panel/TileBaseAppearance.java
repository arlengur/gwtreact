/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.panel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.tecomgroup.qos.gwt.client.style.common.TileAppearance;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;

/**
 * @author ivlev.e
 *
 */
public class TileBaseAppearance implements TileAppearance {

    public interface TileBaseResources extends ClientBundle {

		@ImageOptions(repeatStyle = RepeatStyle.None, width = 10)
		@Source("saveWidgetButton.png")
		ImageResource saveWidgetButton10();

		@ImageOptions(repeatStyle = RepeatStyle.None, width = 13)
		@Source("saveWidgetButton.png")
		ImageResource saveWidgetButton13();

        @ImageOptions(repeatStyle = RepeatStyle.None, width = 10)
        @Source("chartWidgetButton.png")
        ImageResource chartWidgetButton10();

        @ImageOptions(repeatStyle = RepeatStyle.None, width = 13)
        @Source("chartWidgetButton.png")
        ImageResource chartWidgetButton13();

		@Source("TileBase.css")
		TileBaseStyle style();

		@Source({"TileBase1024.css", "TileBase.css"})
		TileBaseStyle style1024();

		@Source({"TileBase1280x1024.css", "TileBase.css"})
		TileBaseStyle style1280x1024();

		@Source({"TileBase1280x720.css", "TileBase.css"})
		TileBaseStyle style1280x720();

		@Source({"TileBase1280x800.css", "TileBase.css"})
		TileBaseStyle style1280x800();

		@Source({"TileBase1600.css", "TileBase.css"})
		TileBaseStyle style1600();

		@Source({"TileBase1920.css", "TileBase.css"})
		TileBaseStyle style1920();

		@Source({"TileBase800x480.css", "TileBase.css"})
		TileBaseStyle style800x480();

		@Source({"TileBase800x600.css", "TileBase.css"})
		TileBaseStyle style800x600();

		@ImageOptions(repeatStyle = RepeatStyle.None, width = 10)
		@Source("tileCross.png")
		ImageResource tileCross10();

		@ImageOptions(repeatStyle = RepeatStyle.None, width = 13)
		@Source("tileCross.png")
		ImageResource tileCross13();
	}

	public interface TileBaseStyle extends CssResource {
		String container_1x1();

		String container_1x2();

		String container_2x1();

		String container_2x2();

		String container_3x1();

		String container_3x2();

		String content();

		String header();

		String innerContent();

		String removeButton();

		String saveButton();

        String chartButton();

		String title();
	}

	private final static int DEFAULT_HEADER_HEIGHT = 14;

	/**
	 * Samsung Galaxy I, II
	 */
	private static final String MEDIA_800x480 = "@media screen and (max-device-width: 640px) {";
	/**
	 * Desktop
	 */
	private static final String MEDIA_800x600 = "@media only screen and (min-width: 750px) {";
	private static final String MEDIA_1024 = "@media only screen and (min-width: 1000px) {";
	private static final String MEDIA_1280x720 = "@media only screen and (min-width: 1224px) and (min-height: 670px) {";
	private static final String MEDIA_1280x800 = "@media only screen and (min-width: 1224px) and (min-height: 750px) {";
	private static final String MEDIA_1280x1024 = "@media only screen and (min-width: 1224px) and (min-height: 900px) {";
	private static final String MEDIA_1600 = "@media only screen and (min-width: 1550px) {";

	private static final String MEDIA_1920 = "@media only screen and (min-width: 1900px) {";

	protected DivElement container;

	protected DivElement header;

	protected DivElement content;

	protected DivElement title;

	protected DivElement removeButton;

	protected DivElement saveButton;

    private DivElement chartButton;

	private final TileBaseResources resources;

	public TileBaseAppearance(final boolean hasSaveButton, final boolean hasChartButton) {
		this(hasSaveButton, hasChartButton, 1, 1);
	}

	public TileBaseAppearance(final boolean hasSaveButton, final boolean hasChartButton,
                              final int rowSpan, final int colSpan) {
		this((TileBaseResources) GWT.create(TileBaseResources.class),
				hasSaveButton, hasChartButton, rowSpan, colSpan);
	}

	public TileBaseAppearance(final TileBaseResources resources,
			final boolean hasSaveButton, final boolean hasChartButton,
            final int rowSpan, final int colSpan) {
		this.resources = resources;

		container = Document.get().createDivElement();
		header = Document.get().createDivElement();
		content = Document.get().createDivElement();
		title = Document.get().createDivElement();
		removeButton = Document.get().createDivElement();

		if (hasSaveButton) {
			saveButton = Document.get().createDivElement();
			header.appendChild(saveButton);
		}

        if (hasChartButton) {
            chartButton = Document.get().createDivElement();
            header.appendChild(chartButton);
        }

        header.appendChild(title);
		header.appendChild(removeButton);

		final int headerHeight = DEFAULT_HEADER_HEIGHT / rowSpan;
		final int contentHeight = 100 - headerHeight;

		header.getStyle().setProperty("height", headerHeight + "%");
		content.getStyle().setProperty("height", contentHeight + "%");

		container.appendChild(header);
		container.appendChild(content);

		appendStyleNames(rowSpan, colSpan);
		injectMediaStylesheets();
	}

	private void appendStyleNames(final int rowSpan, final int colSpan) {
		container.addClassName(getContainerClassName(resources.style800x480(),
				rowSpan, colSpan));
		container.addClassName(getContainerClassName(resources.style800x600(),
				rowSpan, colSpan));
		container.addClassName(getContainerClassName(resources.style1024(),
				rowSpan, colSpan));
		container.addClassName(getContainerClassName(resources.style1280x720(),
				rowSpan, colSpan));
		container.addClassName(getContainerClassName(resources.style1280x800(),
				rowSpan, colSpan));
		container.addClassName(getContainerClassName(
				resources.style1280x1024(), rowSpan, colSpan));
		container.addClassName(getContainerClassName(resources.style1600(),
				rowSpan, colSpan));
		container.addClassName(getContainerClassName(resources.style1920(),
				rowSpan, colSpan));

		header.addClassName(resources.style().header());
		content.addClassName(resources.style().content());

		title.addClassName(resources.style().title());
		removeButton.addClassName(resources.style().removeButton());
		if (saveButton != null) {
			saveButton.addClassName(resources.style().saveButton());
		}
        if (chartButton != null) {
            chartButton.addClassName(resources.style().chartButton());
        }
    }

    private String getContainerClassName(final TileBaseStyle style,
            final int rowSpan, final int colSpan) {
		String className = "";
		if (rowSpan == 1 && colSpan == 1) {
			className = style.container_1x1();
		} else if (rowSpan == 1 && colSpan == 2) {
			className = style.container_2x1();
		} else if (rowSpan == 2 && colSpan == 1) {
			className = style.container_1x2();
		} else if (rowSpan == 2 && colSpan == 2) {
			className = style.container_2x2();
		} else if (rowSpan == 1 && colSpan == 3) {
			className = style.container_3x1();
		} else if (rowSpan == 2 && colSpan == 3) {
			className = style.container_3x2();
		}
		return className;
    }

	@Override
	public XElement getContentElement(final XElement parent) {
		return parent.selectNode("." + resources.style().content());
	}

	@Override
	public XElement getHeaderElement(final XElement parent) {
		return parent.selectNode("." + resources.style().header());
	}

	private void injectMediaStylesheets() {
		// Inject default styles
		StyleInjectorHelper.ensureInjected(resources.style800x600(), true);
		// Inject styles for different devices
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(MEDIA_800x480,
				resources.style800x480().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(MEDIA_800x600,
				resources.style800x600().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(MEDIA_1024,
				resources.style1024().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(
				MEDIA_1280x720, resources.style1280x720().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(
				MEDIA_1280x800, resources.style1280x800().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(
				MEDIA_1280x1024, resources.style1280x1024().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(MEDIA_1600,
				resources.style1600().getText()));
		StyleInjector.injectAtEnd(StyleUtils.wrapMediaStylesheet(MEDIA_1920,
				resources.style1920().getText()));
	}

	@Override
	public boolean isRemoveButtonPressed(final XElement element) {
		return element.hasClassName(resources.style().removeButton());
	}

	@Override
	public boolean isSaveButtonPressed(final XElement element) {
		return element.hasClassName(resources.style().saveButton());
	}

    @Override
    public boolean isChartButtonPressed(final XElement element) {
        return element.hasClassName(resources.style().chartButton());
    }

	@Override
	public void render(final SafeHtmlBuilder sb, final String title) {
		this.title.setInnerText(title);
		sb.append(SafeHtmlUtils.fromTrustedString(container.getString()));
	}

	@Override
	public TileBaseStyle style() {
		return resources.style();
	}
}

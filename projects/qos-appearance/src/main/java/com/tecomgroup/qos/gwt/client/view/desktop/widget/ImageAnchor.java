/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.widget.core.client.Component;
import com.tecomgroup.qos.gwt.client.style.common.button.ImageAnchorAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorBaseAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.ImageAnchorBaseAppearance;

/**
 * Widget for representation image as hyperlink.
 * 
 * @author kunilov.p
 * 
 */
public class ImageAnchor extends Component {

	public ImageAnchor(final String href, final ImageResource image) {
		this(href, image, null, (ImageAnchorAppearance) GWT
				.create(ImageAnchorBaseAppearance.class));
	}

	public ImageAnchor(final String href, final ImageResource image,
			final ImageAnchorAppearance appearance) {
		this(href, image, null, appearance);
	}

	public ImageAnchor(final String href, final ImageResource image,
			final String target) {
		this(href, image, target, (ImageAnchorAppearance) GWT
				.create(AnchorBaseAppearance.class));
	}

	public ImageAnchor(final String href, final ImageResource image,
			String target, final ImageAnchorAppearance appearance) {
		if (target == null) {
			target = "_self";
		}
		final SafeHtmlBuilder sb = new SafeHtmlBuilder();
		appearance.render(sb, UriUtils.fromTrustedString(href),
				AbstractImagePrototype.create(image).getSafeHtml(), target);
		setElement(XDOM.create(sb.toSafeHtml()));
	}
}

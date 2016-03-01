/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.sencha.gxt.core.client.XTemplates;
import com.tecomgroup.qos.gwt.client.style.common.button.ImageAnchorAppearance;

/**
 * @author kunilov.p
 * 
 */
public class ImageAnchorBaseAppearance implements ImageAnchorAppearance {

	public interface ImageAnchorBaseTemplate extends XTemplates {
		@XTemplate("<div title=\"{title}\" style=\"text-align:center; cursor:pointer;\">"
				+ "<a href=\"{href}\" target=\"{target}\">{content}</a></div>")
		SafeHtml template(String title, SafeUri href, SafeHtml content,
				String target);
	}

	private final ImageAnchorBaseTemplate template;

	private final String title;

	public ImageAnchorBaseAppearance() {
		this("");
	}

	public ImageAnchorBaseAppearance(final String title) {
		this.title = title;
		template = GWT.create(ImageAnchorBaseTemplate.class);
	}

	@Override
	public void render(final SafeHtmlBuilder sb, final SafeUri href,
			final SafeHtml content, final String target) {
		sb.append(template.template(title, href, content, target));
	}
}

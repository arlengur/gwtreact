/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.sencha.gxt.core.client.XTemplates;
import com.tecomgroup.qos.gwt.client.style.common.button.AnchorAppearance;

/**
 * @author ivlev.e
 * 
 */
public class AnchorBaseAppearance implements AnchorAppearance {

	public interface AnchorBaseResources extends ClientBundle {

		@Source("AnchorBaseAppearance.css")
		AnchorBaseStyle style();
	}

	@ImportedWithPrefix("anchor")
	public interface AnchorBaseStyle extends CssResource {

		String anchor();

		String anchorContainer();
	}

	public interface AnchorBaseTemplate extends XTemplates {
		@XTemplate(source = "AnchorBaseAppearance.html")
		SafeHtml template(AnchorBaseStyle style, SafeUri href, String text,
				String target);
	}

	private final AnchorBaseTemplate template;

	private final AnchorBaseStyle style;

	private final AnchorBaseResources resources;

	public AnchorBaseAppearance() {
		this((AnchorBaseResources) GWT.create(AnchorBaseResources.class));
	}

	public AnchorBaseAppearance(final AnchorBaseResources anchorBaseResources) {
		resources = anchorBaseResources;
		style = anchorBaseResources.style();
		style.ensureInjected();
		template = GWT.create(AnchorBaseTemplate.class);
	}

	@Override
	public AnchorBaseResources getResources() {
		return resources;
	}

	@Override
	public void render(final SafeHtmlBuilder sb, final SafeUri href,
			final String text, final String target) {
		sb.append(template.template(style, href, text, target));
	}

}

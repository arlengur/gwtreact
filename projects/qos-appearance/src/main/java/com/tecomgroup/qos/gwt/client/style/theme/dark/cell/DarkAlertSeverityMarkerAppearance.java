/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.AlertSeverityMarkerAppearance;
import com.tecomgroup.qos.gwt.client.style.common.AppearanceUtils;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertSeverityStyle;

/**
 * @author novohatskiy.r
 * 
 */
public class DarkAlertSeverityMarkerAppearance
		implements
			AlertSeverityMarkerAppearance {

	public interface DarkAlertSeverityMarkerResources extends ClientBundle {
		@Source({
				"com/tecomgroup/qos/gwt/client/style/theme/dark/grid/DarkAlertSeverityStyle.css",
				"DarkAlertSeverityMarkerAppearance.css"})
		DarkAlertSeverityMarkerStyle style();
	}

	public interface DarkAlertSeverityMarkerStyle
			extends
				CssResource,
				AlertSeverityStyle {
		String coloredSquare();
	}

	public interface DarkAlertSeverityMarkerTemplate extends XTemplates {
		@XTemplate("<div class=\"{style.coloredSquare} {severityClass}\"></div>")
		SafeHtml coloredSquare(DarkAlertSeverityMarkerStyle style,
				String severityClass);
	}

	private final DarkAlertSeverityMarkerStyle style;

	private final DarkAlertSeverityMarkerTemplate template;

	public DarkAlertSeverityMarkerAppearance() {
		this(
				GWT.<DarkAlertSeverityMarkerResources> create(DarkAlertSeverityMarkerResources.class));
	}

	public DarkAlertSeverityMarkerAppearance(
			final DarkAlertSeverityMarkerResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();
		this.template = GWT.create(DarkAlertSeverityMarkerTemplate.class);
	}

	@Override
	public void render(final Context context, final PerceivedSeverity value,
			final SafeHtmlBuilder sb) {
		sb.append(template.coloredSquare(style,
				AppearanceUtils.getSeverityStyle(style, value)));
	}
}

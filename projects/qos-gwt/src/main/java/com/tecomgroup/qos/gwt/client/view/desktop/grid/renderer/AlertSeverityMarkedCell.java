/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.common.AlertSeverityMarkerAppearance;

/**
 * Grid cell which contains localized {@link PerceivedSeverity} label and
 * colored marker for it
 * 
 * @author novohatskiy.r
 * 
 */
public class AlertSeverityMarkedCell extends AlertSeverityCell {

	private final AlertSeverityMarkerAppearance appearance;

	/**
	 * @param messages
	 */
	public AlertSeverityMarkedCell(final QoSMessages messages,
			final AlertSeverityMarkerAppearance appearance) {
		super(messages);
		this.appearance = appearance;
	}

	@Override
	public void render(final Context context, final PerceivedSeverity value,
			final SafeHtmlBuilder sb) {
		appearance.render(context, value, sb);
		super.render(context, value, sb);
	}

}

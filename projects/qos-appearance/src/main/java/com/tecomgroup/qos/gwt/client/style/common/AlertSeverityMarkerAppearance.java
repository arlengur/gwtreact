/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;

/**
 * Appearance for colored marker which represents {@link PerceivedSeverity}
 * 
 * @author novohatskiy.r
 * 
 */
public interface AlertSeverityMarkerAppearance {

	void render(final Context context, final PerceivedSeverity value,
			final SafeHtmlBuilder sb);

}

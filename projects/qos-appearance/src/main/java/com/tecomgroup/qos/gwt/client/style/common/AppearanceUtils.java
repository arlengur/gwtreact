/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertSeverityStyle;

/**
 * @author novohatskiy.r
 * 
 */
public class AppearanceUtils {

	public static String getSeverityStyle(final AlertSeverityStyle style,
			final PerceivedSeverity severity) {
		String styleName;
		if (severity == null) {
			styleName = style.severityNone();
		} else {
			switch (severity) {
				case CRITICAL :
					styleName = style.severityCritical();
					break;
				case INDETERMINATE :
					styleName = style.severityIndeterminate();
					break;
				case MAJOR :
					styleName = style.severityMajor();
					break;
				case MINOR :
					styleName = style.severityMinor();
					break;
				case NOTICE :
					styleName = style.severityNotice();
					break;
				case WARNING :
					styleName = style.severityWarning();
					break;
				default :
					styleName = null;
					break;
			}
		}
		return styleName;
	}

}

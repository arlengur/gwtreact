/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;

/**
 * Tab config that store severity (for tab header coloring)
 * 
 * @author abondin
 * 
 */
public class TabItemConfigWithConditionLevel extends TabItemConfig {

	private final PerceivedSeverity severity;

	private boolean conditionEnabled = false;

	/**
	 * 
	 */
	public TabItemConfigWithConditionLevel() {
		this(null, PerceivedSeverity.CRITICAL);
	}

	/**
	 * @param text
	 */
	public TabItemConfigWithConditionLevel(final String text,
			final PerceivedSeverity severity) {
		super(text);
		this.severity = severity;
	}

	/**
	 * @return the severity
	 */
	public PerceivedSeverity getSeverity() {
		return severity;
	}

	/**
	 * @return the conditionEnabled
	 */
	public boolean isConditionEnabled() {
		return conditionEnabled;
	}

	/**
	 * @param conditionEnabled
	 *            the conditionEnabled to set
	 */
	public void setConditionEnabled(final boolean conditionEnabled) {
		this.conditionEnabled = conditionEnabled;
	}
}

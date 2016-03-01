/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author abondin
 * 
 */
public class AlertSeverityPropertyEditor
		extends
			EnumerationPropertyEditor<PerceivedSeverity> {

	/**
	 * @param messages
	 */
	public AlertSeverityPropertyEditor(final QoSMessages messages) {
		super(messages);
	}

	@Override
	protected Collection<PerceivedSeverity> getAllEnumerationValues() {
		return Arrays.asList(PerceivedSeverity.values());
	}

	@Override
	public void populateLabels(final Map<PerceivedSeverity, String> labels) {
		labels.putAll(LabelUtils.getAllSeverityLabels(messages));
	}

}

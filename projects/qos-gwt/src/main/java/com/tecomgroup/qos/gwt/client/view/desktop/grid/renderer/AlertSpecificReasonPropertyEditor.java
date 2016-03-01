/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author abondin
 * 
 */
public class AlertSpecificReasonPropertyEditor
		extends
			EnumerationPropertyEditor<SpecificReason> {

	public static class Cell extends PropertyDisplayCell<SpecificReason> {
		public Cell(final QoSMessages messages) {
			super(new AlertSpecificReasonPropertyEditor(messages));
		}
	}

	/**
	 * @param messages
	 */
	public AlertSpecificReasonPropertyEditor(final QoSMessages messages) {
		super(messages);
	}

	@Override
	protected Collection<SpecificReason> getAllEnumerationValues() {
		return Arrays.asList(SpecificReason.values());
	}

	@Override
	public void populateLabels(final Map<SpecificReason, String> labels) {
		labels.put(SpecificReason.NONE, messages.specificReasonNone());
		labels.put(SpecificReason.UNKNOWN, messages.specificReasonUnknown());
	}

}

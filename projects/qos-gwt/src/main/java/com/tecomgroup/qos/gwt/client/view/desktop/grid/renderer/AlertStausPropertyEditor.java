/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author abondin
 * 
 */
public class AlertStausPropertyEditor extends EnumerationPropertyEditor<Status> {

	public static class Cell extends PropertyDisplayCell<Status> {
		public Cell(final QoSMessages messages) {
			super(new AlertStausPropertyEditor(messages));
		}
	}

	/**
	 * @param messages
	 */
	public AlertStausPropertyEditor(final QoSMessages messages) {
		super(messages);
	}

	@Override
	protected Collection<Status> getAllEnumerationValues() {
		return Arrays.asList(Status.values());
	}

	@Override
	public void populateLabels(final Map<Status, String> labels) {
		labels.put(Status.ACTIVE, messages.active());
		labels.put(Status.CLEARED, messages.cleared());
	}

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author abondin
 * 
 */
public class AlertUpdateTypePropertyEditor
		extends
			EnumerationPropertyEditor<MAlertType.UpdateType> {

	public static class Cell extends PropertyDisplayCell<MAlertType.UpdateType> {
		public Cell(final QoSMessages messages) {
			super(new AlertUpdateTypePropertyEditor(messages));
		}
	}

	/**
	 * @param messages
	 */
	public AlertUpdateTypePropertyEditor(final QoSMessages messages) {
		super(messages);
	}

	@Override
	protected Collection<MAlertType.UpdateType> getAllEnumerationValues() {
		return Arrays.asList(MAlertType.UpdateType.values());
	}

	@Override
	public void populateLabels(final Map<MAlertType.UpdateType, String> labels) {
		labels.putAll(LabelUtils.getUpdateTypeLabels(messages));
	}

}

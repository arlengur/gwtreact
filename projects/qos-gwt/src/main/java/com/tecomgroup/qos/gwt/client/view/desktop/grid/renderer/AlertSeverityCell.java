/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author novohatskiy.r
 * 
 */
public class AlertSeverityCell extends PropertyDisplayCell<PerceivedSeverity> {

	public AlertSeverityCell(final QoSMessages messages) {
		super(new AlertSeverityPropertyEditor(messages));
	}

}
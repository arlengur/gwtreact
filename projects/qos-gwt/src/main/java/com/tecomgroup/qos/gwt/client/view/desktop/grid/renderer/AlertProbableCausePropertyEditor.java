/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author abondin
 * 
 */
public class AlertProbableCausePropertyEditor
		extends
			EnumerationPropertyEditor<ProbableCause> {

	public static class Cell
			extends
				PropertyDisplayCell<ProbableCause> {
		public Cell(final QoSMessages messages) {
			super(new AlertProbableCausePropertyEditor(messages));
		}
	}

	/**
	 * @param messages
	 */
	public AlertProbableCausePropertyEditor(final QoSMessages messages) {
		super(messages);
	}

	@Override
	protected Collection<ProbableCause> getAllEnumerationValues() {
		return Arrays.asList(ProbableCause.values());
	}

	@Override
	public void populateLabels(final Map<ProbableCause, String> labels) {
		labels.put(ProbableCause.THRESHOLD_CROSSED,
				messages.probableCauseThresholdCrossed());
		// TODO Add all probable causes
	}

}

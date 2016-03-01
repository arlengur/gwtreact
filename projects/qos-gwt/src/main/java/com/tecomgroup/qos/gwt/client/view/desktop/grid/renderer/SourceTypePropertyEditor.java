/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.sencha.gxt.cell.core.client.PropertyDisplayCell;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.Source.Type;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author abondin
 * 
 */
public class SourceTypePropertyEditor
		extends
			EnumerationPropertyEditor<Source.Type> {

	public static class Cell extends PropertyDisplayCell<Source.Type> {
		public Cell(final QoSMessages messages) {
			super(new SourceTypePropertyEditor(messages));
		}
	}

	/**
	 * @param messages
	 */
	public SourceTypePropertyEditor(final QoSMessages messages) {
		super(messages);
	}

	@Override
	protected Collection<Type> getAllEnumerationValues() {
		return Arrays.asList(Source.Type.values());
	}

	@Override
	public void populateLabels(final Map<Source.Type, String> labels) {
		labels.put(Type.AGENT, messages.sourceTypeAgent());
		labels.put(Type.MODULE, messages.sourceTypeModule());
		labels.put(Type.POLICY, messages.sourceTypePolicy());
		labels.put(Type.POLICY_MANAGER, messages.sourceTypePolicyManager());
		labels.put(Type.SERVER, messages.sourceTypeServer());
		labels.put(Type.STREAM, messages.sourceTypeStream());
		labels.put(Type.TASK, messages.sourceTypeTask());
	}

}

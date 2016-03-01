/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Map;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.domain.MUser.Role;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author meleshin.o
 * 
 */
public class RoleLabelProvider implements LabelProvider<Role> {
	private final Map<Role, String> labels;

	public RoleLabelProvider(final QoSMessages messages) {
		labels = LabelUtils.getRoleLabels(messages);
	}

	@Override
	public String getLabel(final Role role) {
		return labels.get(role);
	}
}

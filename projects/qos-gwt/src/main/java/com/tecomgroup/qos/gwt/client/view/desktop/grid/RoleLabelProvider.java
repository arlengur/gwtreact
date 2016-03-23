/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Map;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author meleshin.o
 * 
 */
public class RoleLabelProvider implements LabelProvider<MRole> {
	public RoleLabelProvider(final QoSMessages messages) {
		return;
	}

	@Override
	public String getLabel(final MRole role) {
		return role.getName();
	}
}

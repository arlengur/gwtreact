/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Iterator;
import java.util.Map;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author meleshin.o
 * 
 */
public class RoleListValueProvider extends ValueProviderWithPath<MUser, String> {
	private static final String ROLE_LIST_SEPARATOR = ", ";

	public RoleListValueProvider(final QoSMessages messages) {
		super("roles");
	}

	@Override
	public String getValue(final MUser user) {
		String result = "";
		for (final Iterator<MRole> iter = user.getRoles().iterator(); iter
				.hasNext();) {
			result += iter.next().getName();
			if (iter.hasNext()) {
				result += ROLE_LIST_SEPARATOR;
			}
		}

		return result;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.Iterator;
import java.util.Map;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUser.Role;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;

/**
 * @author meleshin.o
 * 
 */
public class RoleListValueProvider extends ValueProviderWithPath<MUser, String> {

	private final Map<Role, String> labels;

	private static final String ROLE_LIST_SEPARATOR = ", ";

	public RoleListValueProvider(final QoSMessages messages) {
		super("roles");
		this.labels = LabelUtils.getRoleLabels(messages);
	}

	@Override
	public String getValue(final MUser user) {
		String result = "";
		for (final Iterator<Role> iter = user.getRoles().iterator(); iter
				.hasNext();) {
			result += labels.get(iter.next());
			if (iter.hasNext()) {
				result += ROLE_LIST_SEPARATOR;
			}
		}

		return result;
	}
}

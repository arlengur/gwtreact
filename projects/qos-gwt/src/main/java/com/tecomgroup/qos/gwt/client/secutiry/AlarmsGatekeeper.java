/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.rbac.UISubject;

import java.util.Arrays;
import java.util.List;

public class AlarmsGatekeeper extends BaseGatekeeper{

	public static List<UISubject> pages = Arrays.asList(PermissionScope.ALERTS);

	@Inject
	public AlarmsGatekeeper(final CurrentUser currentUser) {
		super(currentUser);
	}

	@Override
	public List<UISubject> getPermission() {
		return pages;
	}
}

/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.rbac.MRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartsGatekeeper extends BaseGatekeeper{

	public static List<UISubject> pages = Arrays.asList(PermissionScope.CHARTS);

	@Inject
	public ChartsGatekeeper(final CurrentUser currentUser) {
		super(currentUser);
	}

	@Override
	public List<UISubject> getPermission() {
		return pages;
	}
}

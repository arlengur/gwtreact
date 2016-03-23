/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.rbac.UISubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordedVideoGatekeeper extends BaseGatekeeper{

	public static List<UISubject> pages = Arrays.asList(PermissionScope.RECORDED_VIDEO);

	@Inject
	public RecordedVideoGatekeeper(final CurrentUser currentUser) {
		super(currentUser);
	}

	@Override
	public List<UISubject> getPermission() {
		return pages;
	}
}

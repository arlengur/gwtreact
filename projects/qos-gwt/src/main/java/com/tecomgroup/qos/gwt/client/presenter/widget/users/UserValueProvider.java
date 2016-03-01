/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.users;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;

/**
 * @author ivlev.e
 * 
 */
public class UserValueProvider extends ValueProviderWithPath<MUser, String> {

	@Override
	public String getValue(final MUser user) {
		return UserLabelProvider.toLabel(user);
	}

}

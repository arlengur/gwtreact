/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.users;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserLabelProvider;

/**
 * @author ivlev.e
 * 
 */
public class UserRow implements TreeGridRow {
	private static final long serialVersionUID = -762716763720774936L;

	private final MUser user;

	private final String group;

	public UserRow(final MUser user, final String group) {
		this.user = user;
		this.group = group;
	}

	@Override
	public String getKey() {
		return getName() + group;
	}

	@Override
	public String getName() {
		return UserLabelProvider.toLabel(user);
	}

	public MUser getUser() {
		return user;
	}
}

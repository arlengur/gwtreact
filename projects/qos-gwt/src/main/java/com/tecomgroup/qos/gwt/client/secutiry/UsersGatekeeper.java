/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUser.Role;

public class UsersGatekeeper extends BaseGatekeeper  implements Gatekeeper {

	private final CurrentUser currentUser;

	@Inject
	public UsersGatekeeper(final CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public boolean canReveal() {
		final MUser user = currentUser.getUser();
		if(user != null && (isPermittedPage(MUser.Page.USER_MGMT, user))) {
			return true;
		}
		return false;
	}

}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUser.Role;

/**
 * 
 * Отображать presenter только для пользователей с правами
 * {@link MUser.Role#ROLE_ADMIN}
 * 
 * @author abondin
 * 
 */
// FIXME cannot extend from UserGatekeeper because of weird gininjector
// exception about 2 instances of UserGatekeeper
public class AdminGatekeeper extends BaseGatekeeper implements Gatekeeper {

	/**
	 *
	 * @param user
	 * @return true if logged user is Administrator
	 */
	public static boolean isAdmin(final MUser user) {
		return user.hasRole(Role.ROLE_CONFIGURATOR) || user.hasRole(Role.ROLE_SUPER_ADMIN) || user.hasRole(Role.ROLE_ADMIN);
	}

	private final CurrentUser currentUser;

	@Inject
	public AdminGatekeeper(final CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public boolean canReveal() {
		final MUser user = currentUser.getUser();
		return isAdmin(user);
	}

}

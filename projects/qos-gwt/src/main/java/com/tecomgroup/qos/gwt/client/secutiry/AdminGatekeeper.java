/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.PredefinedRoles;
import com.tecomgroup.qos.domain.rbac.UISubject;

import java.util.List;

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
public class AdminGatekeeper extends BaseGatekeeper{

	/**
	 *
	 * @param user
	 * @return true if logged user is Administrator
	 */

	@Inject
	public AdminGatekeeper(final CurrentUser currentUser) {
		super(currentUser);
	}

	@Override
	public List<UISubject> getPermission() {
		return null;
	}

	public static boolean isAdmin(final MUser user) {
		return  user.hasRole(PredefinedRoles.ROLE_SUPER_ADMIN);
	}

	@Override
	public boolean canReveal() {
		final MUser user = currentUser.getUser();
		return isAdmin(user);
	}

}

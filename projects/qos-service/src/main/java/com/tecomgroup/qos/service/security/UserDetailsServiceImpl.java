/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.security;

import java.util.ArrayList;
import java.util.List;
import com.tecomgroup.qos.util.AuditLogger;
import com.tecomgroup.qos.util.AuditLogger.SyslogActionStatus;
import com.tecomgroup.qos.util.AuditLogger.SyslogCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.service.InternalUserService;

/**
 * @author ivlev.e
 * 
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private InternalUserService internalUserService;

	@Override
	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException {

		UserDetails userDetails = null;

		final MUser user = internalUserService.findUser(username);

		if (user != null &&
				!user.isLdapAuthenticated() &&
				!user.isDisabled()) {
			final String password = user.getPassword();
			final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			for (final MUser.Role role : user.getRoles()) {
				final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
						role.toString());
				authorities.add(grantedAuthority);
			}
			userDetails = new User(username, password, !user.isDisabled(),
					true, true, true, authorities);
		} else {
			AuditLogger.loginAction(SyslogActionStatus.NOK, username, "LOCAL User {}  not found or disabled", username);
			throw new UsernameNotFoundException("User " + username
					+ " not found");
		}
		AuditLogger.loginAction(SyslogActionStatus.OK, username, " LOCAL User {} attempt to log in.", username);
		return userDetails;
	}

}

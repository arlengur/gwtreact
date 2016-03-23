/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.service.InternalUserService;

/**
 * @author sviyazov.a
 * 
 */
public class CustomLdapAuthoritiesPopulator
		extends
			DefaultLdapAuthoritiesPopulator {

	@Autowired
	private InternalUserService internalUserService;

	public CustomLdapAuthoritiesPopulator(final ContextSource contextSource,
			final String groupSearchBase) {
		super(contextSource, groupSearchBase);
	}

	@Override
	public Set<GrantedAuthority> getGroupMembershipRoles(final String userDn,
			final String username) {
		final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

		final MUser user = internalUserService.findUser(username);

		if (user != null) {
			for (final MRole role : user.getRoles()) {
				final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
						role.toString());
				authorities.add(grantedAuthority);
			}
		} else {
			throw new UsernameNotFoundException("User " + username
					+ " not found");
		}
		return authorities;
	}
}

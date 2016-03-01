/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.security;

import com.tecomgroup.qos.util.AuditLogger;
import com.tecomgroup.qos.util.AuditLogger.SyslogActionStatus;
import com.tecomgroup.qos.util.AuditLogger.SyslogCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.service.InternalUserService;

/**
 * @author sviyazov.a
 * 
 */
public class CustomLdapAuthenticationProvider
		extends
			LdapAuthenticationProvider {

	@Autowired
	private InternalUserService internalUserService;

	@Value("${security.ldap.enabled}")
	private boolean ldapEnabled;

	public CustomLdapAuthenticationProvider(
			final LdapAuthenticator authenticator) {
		super(authenticator);
	}

	public CustomLdapAuthenticationProvider(
			final LdapAuthenticator authenticator,
			final LdapAuthoritiesPopulator authoritiesPopulator,
			final boolean isLdapEnabled) {
		super(authenticator, authoritiesPopulator);
		this.ldapEnabled = isLdapEnabled;
	}

	@Override
	protected DirContextOperations doAuthentication(
			final UsernamePasswordAuthenticationToken authentication) {

		if (ldapEnabled) {
			String userLogin=authentication.getPrincipal().toString();
			final MUser user = internalUserService.findUser(authentication
					.getPrincipal().toString());

			if (user != null) {
				if(user.isDisabled()) {
					throw new BadCredentialsException("User disabled");
				}

				if (user.isLdapAuthenticated()) {
					try {
						DirContextOperations result= super.doAuthentication(authentication);
						AuditLogger.loginAction(SyslogActionStatus.OK, userLogin, "LDAP User {} successfully logged in", userLogin);
						return result;
					}catch (AuthenticationException e)
					{
						AuditLogger.loginAction(SyslogActionStatus.NOK, userLogin, "LDAP User {} unable to login user", userLogin);
						throw e;
					}
				} else {
					throw new BadCredentialsException(
							"LDAP : The user "
									+ user.getLogin()
									+ " isn't LDAP user. Authentication in LDAP is forbidden");
				}
			} else {
				AuditLogger.loginAction(SyslogActionStatus.NOK, userLogin, "LDAP User {}  Bad credentials", userLogin);
				throw new BadCredentialsException("Bad credentials");
			}
		}
		return null;
	}
}

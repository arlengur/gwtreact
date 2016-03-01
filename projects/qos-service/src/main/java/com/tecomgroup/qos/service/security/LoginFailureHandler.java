/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * @author ivlev.e
 * 
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request,
			final HttpServletResponse response,
			final AuthenticationException exception) throws IOException,
			ServletException {
		int statusCode = HttpServletResponse.SC_OK;
		if (exception instanceof AccountStatusException) {
			statusCode = HttpServletResponse.SC_FORBIDDEN;
		} else if (exception instanceof BadCredentialsException) {
			statusCode = HttpServletResponse.SC_UNAUTHORIZED;
		}
		response.sendError(statusCode,
				"Authentication Failed: " + exception.getMessage());
	}

}

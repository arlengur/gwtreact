/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.security;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;

import com.tecomgroup.qos.exception.SecurityException;
import com.tecomgroup.qos.exception.SecurityException.Reason;

/**
 * @author abondin
 * 
 */
@Aspect
public class SecurityExceptionInterceptor {

	@AfterThrowing(pointcut = "execution(* org.springframework.security.access.AccessDecisionManager.decide(..))", throwing = "ex")
	public void handleException(final AccessDeniedException ex) {
		//throw new SecurityException(Reason.INCORRECT_LOGIN);
		throw ex;
	}

}

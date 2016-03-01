/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Фильтр для принудительного отключения кэша. Решают проблемму GWT-RPC для IOS
 * 6.0
 * 
 * @see https://groups.google.com/forum/?fromgroups=#!topic/google-web-toolkit/
 *      CWkgCXLi8tA
 * 
 * @author abondin
 * 
 */
public class HeaderFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request,
			final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletResponse response = (HttpServletResponse) res;
		response.addHeader("Cache-Control", "no-cache");
		chain.doFilter(request, res);
	}

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

}

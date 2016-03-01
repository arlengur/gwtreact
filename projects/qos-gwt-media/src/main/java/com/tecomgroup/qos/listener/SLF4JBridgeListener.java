/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.listener;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.bridge.SLF4JBridgeHandler;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URI;

/**
 * @author sviyazov.a
 *
 */
public class SLF4JBridgeListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();
		String log4jConfigFile = context.getInitParameter("internal_log4j");
		String externalLog4jConfigFile = context
				.getInitParameter("external_log4j");
		log4jConfigFile = context.getRealPath("") + log4jConfigFile;
		URI externalConfig = URI.create(externalLog4jConfigFile);
		File file = new File(externalConfig);
		if (file.exists()) {
			DOMConfigurator.configure(externalConfig.getPath());
		} else {
			DOMConfigurator.configure(log4jConfigFile);
		}
		SLF4JBridgeHandler.install();
	}
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// do nothing
	}
}

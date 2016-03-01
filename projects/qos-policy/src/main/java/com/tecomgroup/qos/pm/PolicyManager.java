/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tecomgroup.qos.util.Utils;

/**
 * Bootstrap class
 * 
 * @author abondin
 * 
 */
public class PolicyManager {
	public static final String BUILD_INFO_PROJECT = "qos-policy";

	public static final String BUILD_INFO_FILE_SUFFIX = "-git.properties";

	private final static Logger LOGGER = Logger.getLogger(PolicyManager.class);

	public static String getVersion() {
		String applicationVersion = null;
		final Properties properties = new Properties();
		try {
			properties.load(PolicyManager.class
					.getResourceAsStream("/META-INF/" + BUILD_INFO_PROJECT
							+ BUILD_INFO_FILE_SUFFIX));
			applicationVersion = Utils.getApplicationVersion(properties);

		} catch (final Exception ex) {
			LOGGER.warn("Could not read package version.", ex);
		}

		return "******************************************\n"
				+ "* Policy Manager Version: " + applicationVersion + "\n"
				+ "******************************************\n";
	}

	public static void main(final String[] args) {
		if (args.length > 0 && "-version".equals(args[0])) {
			System.out.println(getVersion());
		} else {
			LOGGER.warn("\n" + getVersion());
			new ClassPathXmlApplicationContext(
					new String[]{"classpath:com/tecomgroup/qos/pm/pmContext.xml"});
		}
	}
}

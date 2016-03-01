/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools;

import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * 
 * @author abondin
 * @see QoSTool
 * 
 */
public class Tools {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:/com/tecomgroup/qos/tools/toolsContext.xml");

		final QoSTool listTools = (QoSTool) context.getBean("listTools");

		if (args.length == 0) {
			printHelp(listTools);
		} else {
			boolean executed = false;
			final String command = args[0].trim();
			try {
				for (final Map.Entry<String, QoSTool> entry : context
						.getBeansOfType(QoSTool.class).entrySet()) {
					if (command.equalsIgnoreCase(entry.getKey())) {
						System.out.println(entry.getValue().getDescription());
						entry.getValue().execute();
						executed = true;
						break;
					}
				}
				if (!executed) {
					System.out.println("ERROR: No command " + command
							+ " found.");
					printHelp(listTools);
				}
			} finally {
				context.destroy();
			}
		}
	}

	private static void printHelp(final QoSTool listTools) {
		System.out.println("QoS Development Tools commands:");
		listTools.execute();
	}

}

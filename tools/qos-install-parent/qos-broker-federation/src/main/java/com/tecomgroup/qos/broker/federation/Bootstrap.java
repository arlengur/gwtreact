/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.broker.federation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bootstrap class for HTTP requester
 * @author novohatskiy.r
 *
 */
public class Bootstrap {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] {
						"classpath:com/tecomgroup/qos/broker/federation/applicationContext.xml"
						});
		Requester requester = (Requester) applicationContext.getBean("requester");
		requester.start();
	}

}

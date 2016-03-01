/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 * @author kunilov.p
 * 
 */
public class HibernateSessionFactoryBean extends LocalSessionFactoryBean {

	private static Logger LOGGER = Logger
			.getLogger(HibernateSessionFactoryBean.class);

	@Override
	public void setHibernateProperties(final Properties hibernateProperties) {
		if (hibernateProperties.getProperty(Environment.DEFAULT_SCHEMA) != null) {
			final String defaultSchema = hibernateProperties
					.getProperty(Environment.DEFAULT_SCHEMA);
			if (defaultSchema.trim().isEmpty()) {
				hibernateProperties.remove(Environment.DEFAULT_SCHEMA);
				LOGGER.info("Remove hibernate property ("
						+ Environment.DEFAULT_SCHEMA + ") from configuration");
			}
		}
		super.setHibernateProperties(hibernateProperties);
	}
}

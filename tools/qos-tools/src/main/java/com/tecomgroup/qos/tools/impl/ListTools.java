/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.tools.QoSTool;

/**
 * 
 * Отображает список всех утилит
 * 
 * @author abondin
 * 
 */
@Component
public class ListTools implements QoSTool, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void execute() {
		for (final Map.Entry<String, QoSTool> entry : applicationContext
				.getBeansOfType(QoSTool.class).entrySet()) {
			System.out.println("-----" + entry.getKey() + " : "
					+ entry.getValue().getDescription());
		}
	}

	@Override
	public String getDescription() {
		return "Get all QoS tool names";
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author novohatskiy.r
 * 
 */
@Component
public class QoSSnmpAgentBootstrap implements BeanFactoryAware {

	@Value("${snmp.nbi.enabled}")
	private boolean agentEnabled;

	@Override
	public void setBeanFactory(final BeanFactory beanFactory)
			throws BeansException {
		if (agentEnabled) {
			beanFactory.getBean(QoSSnmpAgent.class);
		}
	}

}

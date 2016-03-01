/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.table.service;

import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.snmp.mib.EntityMib;
import com.tecomgroup.qos.snmp.mib.QligentVisionMib;

/**
 * Provides ability to get {@link SnmpTableService} corresponding to a part of
 * MIB.
 * 
 * @author novohatskiy.r
 * 
 */
@Lazy
@Component
public class SnmpTableServiceFactory implements BeanFactoryAware {

	private BeanFactory beanFactory;

	/**
	 * Returns {@link SnmpTableService} of given class.
	 * 
	 * @param clazz
	 * @return
	 */
	public SnmpTableService<MOTableRow<Variable>> getSnmpTableService(
			final Class<? extends SnmpTableService<MOTableRow<Variable>>> clazz) {
		return beanFactory.getBean(clazz);
	}

	/**
	 * Returns {@link SnmpTableService} for given OID.
	 * 
	 * @param tableOID
	 * @return
	 */
	public SnmpTableService<MOTableRow<Variable>> getSnmpTableService(
			final OID tableOID) {
		Class<? extends SnmpTableService<MOTableRow<Variable>>> clazz = null;
		if (EntityMib.oidEntPhysicalEntry.equals(tableOID)
				|| QligentVisionMib.oidEntPhysicalEntry.equals(tableOID)) {
			clazz = EntPhysicalTableService.class;
		} else if (EntityMib.oidEntPhysicalContainsEntry.equals(tableOID)
				|| QligentVisionMib.oidEntPhysicalContainsEntry
						.equals(tableOID)) {
			clazz = EntPhysicalContainsTableService.class;
		} else if (QligentVisionMib.oidVisionAlarmEntry.equals(tableOID)) {
			clazz = VisionAlarmTableService.class;
		} else if (QligentVisionMib.oidVisionCurrentResultEntry
				.equals(tableOID)) {
			clazz = VisionCurrentResultTableService.class;
		} else if (QligentVisionMib.oidVisionPhysicalTopLevelEntry.equals(tableOID)){
            clazz = VisionTopLevelEntityTableService.class;
        }

		if (clazz != null) {
			return getSnmpTableService(clazz);
		}

		return null;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory)
			throws BeansException {
		this.beanFactory = beanFactory;
	}
}

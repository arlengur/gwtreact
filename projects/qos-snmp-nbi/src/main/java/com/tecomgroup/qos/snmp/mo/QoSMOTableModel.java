/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mo;

import java.util.Date;

import org.snmp4j.agent.mo.MOTableModel;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.Variable;

/**
 * {@link MOTableModel} that provides ability to determine last modification
 * date and time.
 * 
 * @author novohatskiy.r
 * 
 */
public interface QoSMOTableModel<R extends MOTableRow<Variable>>
		extends
			MOTableModel<R> {

	Date getLastEntityModificationTimestamp();

}

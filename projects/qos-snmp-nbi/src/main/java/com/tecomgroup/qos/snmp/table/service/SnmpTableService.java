/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.table.service;

import java.util.Date;
import java.util.Iterator;

import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

/**
 * Represents a single table in MIB.
 * 
 * @author novohatskiy.r
 * 
 */
public interface SnmpTableService<M extends MOTableRow<Variable>> {

	/**
	 * Returns the modification timestamp of last modified entity from this
	 * table.
	 */
	Date getLastEntityModificationTimestamp();

	/**
	 * Returns row data for given OID.
	 * 
	 * @param index
	 * @return
	 */
	M getRow(OID index);

	/**
	 * Returns the number of rows available in this table.
	 * 
	 * @return
	 */
	int getRowCount();

	/**
	 * Creates an iterator for this table.
	 * 
	 * @return
	 */
	Iterator<M> iterator();

	/**
	 * Creates an iterator starting by given OID.
	 * 
	 * @param lowerBound
	 * @return
	 */
	Iterator<M> iterator(OID lowerBound);

}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mo;

import java.util.Date;
import java.util.Iterator;

import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.tecomgroup.qos.snmp.table.service.SnmpTableService;

/**
 * Implements {@link QoSMOTableModel} by delegating invocations to corresponding
 * {@link SnmpTableService}.
 * 
 * @author novohatskiy.r
 * 
 */
public class DefaultQoSMOTableModel<M extends MOTableRow<Variable>>
		implements
			QoSMOTableModel<M> {

	private final SnmpTableService<M> snmpTableService;

	public DefaultQoSMOTableModel(final SnmpTableService<M> snmpTableService) {
		this.snmpTableService = snmpTableService;
	}

	@Override
	public boolean containsRow(final OID index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OID firstIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public M firstRow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getLastEntityModificationTimestamp() {
		return snmpTableService.getLastEntityModificationTimestamp();
	}

	@Override
	public M getRow(final OID index) {
		return snmpTableService.getRow(index);
	}

	@Override
	public int getRowCount() {
		return snmpTableService.getRowCount();
	}

	@Override
	public synchronized Iterator<M> iterator() {
		return snmpTableService.iterator();
	}

	@Override
	public OID lastIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public M lastRow() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Iterator<M> tailIterator(final OID lowerBound) {
		return snmpTableService.iterator(lowerBound);
	}

}

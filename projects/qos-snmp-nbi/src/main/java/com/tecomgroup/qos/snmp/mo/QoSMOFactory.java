/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mo;

import org.snmp4j.agent.mo.DefaultMOFactory;
import org.snmp4j.agent.mo.DefaultMOTable;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableModel;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.snmp.table.service.SnmpTableService;
import com.tecomgroup.qos.snmp.table.service.SnmpTableServiceFactory;

/**
 * Uses {@link SnmpTableServiceFactory} to create table models.
 * 
 * @author novohatskiy.r
 * 
 */
@Lazy
@Component(value = "QoSMOFactory")
public class QoSMOFactory extends DefaultMOFactory {

	@Autowired
	private SnmpTableServiceFactory snmpTableServiceFactory;

	public QoSMOFactory() {
		super();
		addSNMPv2TCs(this);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public MOTable createTable(final OID oid, final MOTableIndex indexDef,
			final MOColumn[] columns, final MOTableModel model) {
		MOTable table = null;
		if (model instanceof QoSMOTableModel) {
			table = new QoSMOTable<MOTableRow<Variable>, MOColumn<Variable>, QoSMOTableModel<MOTableRow<Variable>>>(
					oid, indexDef, columns,
					(QoSMOTableModel<MOTableRow<Variable>>) model);
		} else {
			table = new DefaultMOTable<MOTableRow<Variable>, MOColumn<Variable>, MOTableModel<MOTableRow<Variable>>>(
					oid, indexDef, columns, model);
		}
		return table;
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public MOTableModel<MOTableRow<Variable>> createTableModel(
			final OID tableOID, final MOTableIndex indexDef,
			final MOColumn[] columns) {
		final SnmpTableService<MOTableRow<Variable>> snmpTableService = snmpTableServiceFactory
				.getSnmpTableService(tableOID);
		if (snmpTableService != null) {
			return new DefaultQoSMOTableModel<MOTableRow<Variable>>(
					snmpTableService);
		}
		return super.createTableModel(tableOID, indexDef, columns);
	}
}

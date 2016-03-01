/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.table.service;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.snmp4j.agent.mo.DefaultMOTableRow;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.service.SnmpService;
import com.tecomgroup.qos.snmp.util.EntityConverter;

/**
 * QLIGENT-VISION-MIB's visionAlarmTable service.
 * 
 * @author novohatskiy.r
 */
@Lazy
@Service
public class VisionAlarmTableService
		implements
			SnmpTableService<MOTableRow<Variable>> {

	private class VisionAlarmEntryIterator
			implements
				Iterator<MOTableRow<Variable>> {

		private int index;

		VisionAlarmEntryIterator(final int index) {
			this.index = index;
		}

		@Override
		public boolean hasNext() {
			return snmpService.getNextAlertIndex(index) > -1;
		}

		@Override
		public MOTableRow<Variable> next() {
			final Integer nextIndex = snmpService.getNextAlertIndex(index);
			if (nextIndex == null) {
				throw new NoSuchElementException("No entities of "
						+ MAlert.class + " with id > " + index + " found.");
			} else {
				index = nextIndex;
			}

			return getRow(new OID(new int[]{nextIndex}));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Autowired
	private SnmpService snmpService;

	@Override
	public Date getLastEntityModificationTimestamp() {
		return snmpService.getLastAlertModificationTimestamp();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOTableRow<Variable> getRow(final OID index) {
		if (index.size() > 1) {
			throw new IllegalArgumentException("Index " + index
					+ " is incorrect. Index of visionAlertTable should contain"
					+ " one sub-identifier");
		}

		final MAlert alert = snmpService.getAlertById(index.get(0));
		if (alert == null) {
			throw new NoSuchElementException("No entities of " + MAlert.class
					+ " with id = " + index.get(0) + " found.");
		}
		return new DefaultMOTableRow(index,
				EntityConverter.convertToVariables(alert));
	}

	@Override
	public int getRowCount() {
		return snmpService.getAlertRowCount();
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator() {
		return new VisionAlarmEntryIterator(0);
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator(final OID lowerBound) {
		int index = 0;
		if (lowerBound != null) {
			if (lowerBound.size() > 1) {
				throw new IllegalArgumentException(
						"Index "
								+ lowerBound
								+ " is incorrect. Index of visionAlarmTable should contain"
								+ " one sub-identifier");
			}
			index = lowerBound.get(0);
		}
		return new VisionAlarmEntryIterator(index);
	}
}

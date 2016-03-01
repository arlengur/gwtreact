/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.table.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.snmp4j.agent.mo.DefaultMOTableRow;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.DateAndTime;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.service.SnmpService;
import com.tecomgroup.qos.snmp.mib.EntPhysicalMibSection;

/**
 * ENTITY-MIB's entPhysicalTable service.
 * 
 * @author novohatskiy.r
 * 
 */
@Lazy
@Service
public class EntPhysicalTableService
		implements
			SnmpTableService<MOTableRow<Variable>> {

	private class EntPhysicalEntryIterator
			implements
				Iterator<MOTableRow<Variable>> {

		private int index;

		EntPhysicalEntryIterator(final int index) {
			this.index = index;
		}

		@Override
		public boolean hasNext() {
			return snmpService.getNextEntPhysicalIndex(index) > -1;
		}

		@Override
		public MOTableRow<Variable> next() {
			final Integer nextIndex = snmpService
					.getNextEntPhysicalIndex(index);
			if (nextIndex == null) {
				throw new NoSuchElementException("No entities of "
						+ MSource.class + " with snmpId > " + index + " found.");
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

	private static Variable[] convertToVariables(final MSource source) {
		final Variable[] values = createStubValues();

		Integer containerId = 0;
		final MSource container = source.getParent();
		if (container != null) {
			containerId = container.getSnmpId();
		}
		values[EntPhysicalMibSection.IDX_ENTPHYSICALCONTAINEDIN] = new Integer32(
				containerId);

		values[EntPhysicalMibSection.IDX_ENTPHYSICALNAME] = new OctetString(
				source.getDisplayName());

		values[EntPhysicalMibSection.IDX_ENTPHYSICALSERIALNUM] = new OctetString(
				source.getKey());

		values[EntPhysicalMibSection.IDX_ENTPHYSICALISFRU] = new Integer32(2);

		if (source.getCreationDateTime() != null) {
			final GregorianCalendar creationDateCalendar = new GregorianCalendar();
			creationDateCalendar.setTime(source.getCreationDateTime());
			values[EntPhysicalMibSection.IDX_ENTPHYSICALMFGDATE] = DateAndTime
					.makeDateAndTime(creationDateCalendar);
		}

		if (source instanceof MAgent) {
			values[EntPhysicalMibSection.IDX_ENTPHYSICALCLASS] = new Integer32(
					11);
			final MAgent agent = (MAgent) source;
			values[EntPhysicalMibSection.IDX_ENTPHYSICALDESCR] = new OctetString(
					agent.getDescription());
			values[EntPhysicalMibSection.IDX_ENTPHYSICALSOFTWAREREV] = new OctetString(
					agent.getPlatform());
			values[EntPhysicalMibSection.IDX_ENTPHYSICALMODELNAME] = new OctetString(
					agent.getNetAddress());
		} else if (source instanceof MAgentModule) {
			values[EntPhysicalMibSection.IDX_ENTPHYSICALCLASS] = new Integer32(
					9);
		} else if (source instanceof MAgentTask) {
			values[EntPhysicalMibSection.IDX_ENTPHYSICALCLASS] = new Integer32(
					8);
		}

		return values;
	}

	private static Variable[] createStubValues() {
		final Variable[] values = new Variable[EntPhysicalMibSection.ENTPHYSICAL_COLUMN_COUNT];
		values[EntPhysicalMibSection.IDX_ENTPHYSICALDESCR] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALVENDORTYPE] = new OID();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALCLASS] = new Integer32();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALPARENTPOS] = new Integer32();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALHARDWAREREV] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALFIRMWAREREV] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALSOFTWAREREV] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALMFGNAME] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALMODELNAME] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALALIAS] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALASSETID] = new OctetString();
		values[EntPhysicalMibSection.IDX_ENTPHYSICALMFGDATE] = new OctetString(
				"00000000");
		values[EntPhysicalMibSection.IDX_ENTPHYSICALURIS] = new OctetString();
		return values;
	}

	@Autowired
	private SnmpService snmpService;

	@Override
	public Date getLastEntityModificationTimestamp() {
		return snmpService.getLastEntityModificationTimestamp();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOTableRow<Variable> getRow(final OID rowIndex) {
		if (rowIndex.size() > 1) {
			throw new IllegalArgumentException("Index " + rowIndex
					+ " is incorrect. Index of entPhysicalTable should contain"
					+ " one sub-identifier");
		}

		final MSource source = snmpService.getSourceBySnmpId(rowIndex.get(0));
		if (source == null) {
			throw new NoSuchElementException("No entities of " + MSource.class
					+ " with snmpId = " + rowIndex.get(0) + " found.");
		}
		return new DefaultMOTableRow(rowIndex, convertToVariables(source));
	}

	@Override
	public int getRowCount() {
		return snmpService.getEntPhysicalRowCount();
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator() {
		return new EntPhysicalEntryIterator(0);
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator(final OID lowerBound) {
		int index = 0;
		if (lowerBound != null) {
			if (lowerBound.size() > 1) {
				throw new IllegalArgumentException(
						"Index "
								+ lowerBound
								+ " is incorrect. Index of entPhysicalTable should contain"
								+ " one sub-identifier");
			}
			index = lowerBound.get(0);
		}

		return new EntPhysicalEntryIterator(index);
	}

}

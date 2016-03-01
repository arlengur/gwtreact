/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.table.service;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.snmp4j.agent.mo.DefaultMOTableRow;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.service.SnmpService;
import com.tecomgroup.qos.snmp.util.EntityConverter;

/**
 * ENTITY-MIB's entPhysicalContainsTable service.
 * 
 * @author novohatskiy.r
 * 
 */
@Lazy
@Service
public class EntPhysicalContainsTableService
		implements
			SnmpTableService<MOTableRow<Variable>> {
	private class EntPhysicalContainsEntryIterator
			implements
				Iterator<MOTableRow<Variable>> {

		private Pair<Integer, Integer> index;

		public EntPhysicalContainsEntryIterator() {
			this(new ImmutablePair<Integer, Integer>(0, 0));
		}

		public EntPhysicalContainsEntryIterator(
				final Pair<Integer, Integer> index) {
			this.index = index;
		}

		@Override
		public boolean hasNext() {
			return snmpService.getNextEntPhysicalContainsIndex(index) != null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public MOTableRow<Variable> next() {
			final Pair<Integer, Integer> nextIndex = snmpService
					.getNextEntPhysicalContainsIndex(index);
			if (nextIndex == null) {
				throw new NoSuchElementException(
						"There are no elements found for indexes greater than entPhysicalIndex="
								+ index.getLeft()
								+ " and entPhysicalChildIndex="
								+ index.getRight());
			}

			index = nextIndex;

			final Variable[] values = {new Integer32(index.getRight())};
			return new DefaultMOTableRow(EntityConverter.convertToOID(index),
					values);
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
		return snmpService.getLastEntityModificationTimestamp();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOTableRow<Variable> getRow(final OID index) {
		MOTableRow<Variable> row = null;

		final Pair<Integer, Integer> currentIndex = new ImmutablePair<Integer, Integer>(
				index.get(0), index.get(1));

		if (snmpService.doesEntPhysicalContainsRowExist(currentIndex)) {
			row = new DefaultMOTableRow(index, new Variable[]{new Integer32(
					currentIndex.getRight())});
		}

		return row;
	}

	@Override
	public int getRowCount() {
		return snmpService.getEntPhysicalContainsRowCount();
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator() {
		return new EntPhysicalContainsEntryIterator();
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator(final OID lowerBound) {
		Iterator<MOTableRow<Variable>> iterator;
		if (lowerBound == null) {
			iterator = new EntPhysicalContainsEntryIterator();
		} else {
			iterator = new EntPhysicalContainsEntryIterator(
					new ImmutablePair<Integer, Integer>(lowerBound.get(0),
							lowerBound.get(1)));
		}

		return iterator;
	}

}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.table.service;

import java.util.*;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.snmp4j.agent.mo.DefaultMOTableRow;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.service.SnmpService;
import com.tecomgroup.qos.snmp.util.EntityConverter;
import com.tecomgroup.qos.snmp.util.ResultWrapper;

/**
 * QLIGENT-VISION-MIB's visionCurrentResultTable service.
 * 
 * @author novohatskiy.r
 */
@Lazy
@Service
public class VisionCurrentResultTableService
		implements
			SnmpTableService<MOTableRow<Variable>> {

	private class VisionCurrentResultEntryIterator
			implements
				Iterator<MOTableRow<Variable>> {

        private Pair<Integer,Integer> current;
        private Pair<Integer,Integer> next;

		public VisionCurrentResultEntryIterator() {
            current = new ImmutablePair<>(0,0);
		}

		public VisionCurrentResultEntryIterator(
				final Pair<Integer, Integer> index) {
            current = index;
		}

		@Override
		public boolean hasNext() {
            final Pair<Integer, Integer> maybeNext = snmpService.getNextCurrentResultIndex(current);
            if(maybeNext != null) {
                next = maybeNext;
            }
            return maybeNext != null;
		}

		@Override
		public MOTableRow<Variable> next() {
            if(next == null) {
                next = snmpService.getNextCurrentResultIndex(current);
            }
            current = next;
            final MOTableRow<Variable> result = getRow(EntityConverter.convertToOID(next));
            next = null;
			return result;
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
		return DateUtils.round(new Date(), Calendar.MINUTE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOTableRow<Variable> getRow(final OID index) {
        final int taskSnmpId = index.get(0);
        final int paramSnmpId = index.get(1);

		final ResultWrapper result = snmpService.getCurrentResult(taskSnmpId, paramSnmpId);
		if (result == null) {
			throw new NoSuchElementException("No results with id = " + index
					+ " found.");
		}
		return new DefaultMOTableRow(index,
				EntityConverter.convertToVariables(result));
	}

	@Override
	public int getRowCount() {
		return snmpService.getCurrentResultRowCount();
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator() {
		return new VisionCurrentResultEntryIterator();
	}

	@Override
	public Iterator<MOTableRow<Variable>> iterator(final OID lowerBound) {
		final Iterator<MOTableRow<Variable>> iterator;

		if (lowerBound == null) {
			iterator = new VisionCurrentResultEntryIterator();
		} else {
			iterator = new VisionCurrentResultEntryIterator(
					new ImmutablePair<>(lowerBound.get(0),
							lowerBound.get(1)));
		}

		return iterator;
	}

}

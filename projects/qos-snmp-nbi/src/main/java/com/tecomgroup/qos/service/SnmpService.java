/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.snmp.util.ResultWrapper;

/**
 * Used to retrieve data requested by an SNMP agent.
 * 
 * @author novohatskiy.r
 * 
 */
public interface SnmpService extends Service {

	/**
	 * Discovers whether this physical parent-child relation exists or not.
	 * 
	 * @param index
	 *            - parent and child indexes to be checked
	 * @return relation existence
	 */
	boolean doesEntPhysicalContainsRowExist(Pair<Integer, Integer> index);

	/**
	 * @param id
	 * @return {@link MAlert} for given identifier.
	 */
	MAlert getAlertById(long id);

	/**
	 * Returns a number of alerts.
	 * 
	 * @return
	 */
	int getAlertRowCount();

	/**
	 * Returns a number of current result entries.
	 * 
	 * @return
	 */
	int getCurrentResultRowCount();

	/**
	 * Returns a number of physical parent-child relations.
	 * 
	 * @return
	 */
	int getEntPhysicalContainsRowCount();

	/**
	 * Returns a number of physical entities.
	 * 
	 * @return
	 */
	int getEntPhysicalRowCount();

	/**
	 * Returns modification timestamp of last modified alert
	 */
	Date getLastAlertModificationTimestamp();

	/**
	 * Returns last modification timestamp of supported entities
	 */
	Date getLastEntityModificationTimestamp();

	/**
	 * Returns next alert index.
	 * 
	 * @param index
	 * @return
	 */
	int getNextAlertIndex(long index);

	/**
	 * Returns next index of current result entry.
	 * 
	 * @param index
	 * @return
	 */
	Pair<Integer, Integer> getNextCurrentResultIndex(
			Pair<Integer, Integer> index);

	/**
	 * Returns next physical parent-child relation indexes for given pair.
	 * 
	 * @param index
	 *            - current index
	 * @return
	 */
	Pair<Integer, Integer> getNextEntPhysicalContainsIndex(
			Pair<Integer, Integer> index);

	/**
	 * Returns next physical entity index.
	 * 
	 * @param index
	 * @return
	 */
	int getNextEntPhysicalIndex(int index);

	/**
	 * Returns {@link MSource} for given SNMP identifier.
	 * 
	 * @param snmpId
	 * @return
	 */
	MSource getSourceBySnmpId(int snmpId);

    /**
     * Given SNMP IDs of task and parameter returns {@link ResultWrapper}
     * from which visionCurrentResultTable row can be reconstructed.
     *
     * @param taskSnmpId ID of task, as declared in visionPhysicalTable
     * @param paramSnmpId ID of parameter
     * @return {@link ResultWrapper} for corresponding visionCurrentResultTable row.
     */
    ResultWrapper getCurrentResult(int taskSnmpId, int paramSnmpId);

    Integer getNextTopLevelEntityIndex(Integer current);

    int getTopLevelEntityCount();
}

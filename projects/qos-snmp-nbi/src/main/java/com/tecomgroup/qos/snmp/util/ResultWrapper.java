/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.util;

import java.util.Date;

import com.tecomgroup.qos.domain.MResultParameterConfiguration;

/**
 * @author novohatskiy.r
 * 
 */
public class ResultWrapper {

    private final int taskSnmpId;

	private MResultParameterConfiguration resultParameterConfiguration;

	private Object value;

	private Date dateTime;

	public ResultWrapper(
            final int taskSnmpId,
			final MResultParameterConfiguration resultParameterConfiguration,
			final Object value, final Date dateTime) {
        this.taskSnmpId = taskSnmpId;
		this.setResultParameterConfiguration(resultParameterConfiguration);
		this.value = value;
		this.dateTime = dateTime;
	}

    public int getTaskSnmpId() {
        return taskSnmpId;
    }

	/**
	 * @return the dateTime
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * @return the resultParameterConfiguration
	 */
	public MResultParameterConfiguration getResultParameterConfiguration() {
		return resultParameterConfiguration;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param dateTime
	 *            the dateTime to set
	 */
	public void setDateTime(final Date dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @param resultParameterConfiguration
	 *            the resultParameterConfiguration to set
	 */
	public void setResultParameterConfiguration(
			final MResultParameterConfiguration resultParameterConfiguration) {
		this.resultParameterConfiguration = resultParameterConfiguration;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final Object value) {
		this.value = value;
	}

}

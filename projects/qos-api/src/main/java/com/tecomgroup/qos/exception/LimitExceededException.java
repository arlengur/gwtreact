/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

/**
 * @author ivlev.e
 * 
 */
public class LimitExceededException extends QOSException {

	private static final long serialVersionUID = -5558122166535273328L;

	private int limitValue;

	private int actualValue;

	public LimitExceededException() {
		super();
	}

	public LimitExceededException(final int limitValue, final int actualValue) {
		this();
		this.limitValue = limitValue;
		this.actualValue = actualValue;
	}

	public int getActualValue() {
		return actualValue;
	}

	public int getLimitValue() {
		return limitValue;
	}

}

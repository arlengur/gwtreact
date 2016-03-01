/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;


/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class UnaryCriterion extends CriterionWithParameter {

	private UnaryOperation operation;

	public UnaryCriterion() {
		super();
	}

	public UnaryCriterion(final String parameter, final Operation operation) {
		this();
		this.parameter = parameter;
		if (!(operation instanceof UnaryOperation)) {
			throw new IllegalArgumentException("Incorrect type of operation "
					+ operation
					+ ". Operation should be the instance of UnaryOperation");
		}
		this.operation = (UnaryOperation) operation;
	}

	/**
	 * @return the operation
	 */
	public UnaryOperation getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final UnaryOperation operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return operation + " " + parameter;
	}
}

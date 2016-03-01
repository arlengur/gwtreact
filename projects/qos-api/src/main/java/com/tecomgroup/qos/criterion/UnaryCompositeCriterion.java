/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.io.Serializable;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class UnaryCompositeCriterion
		implements
			CompositeCriterion,
			Serializable {

	private Criterion criterion;
	private UnaryCompositeOperation operation;

	public UnaryCompositeCriterion() {
		super();
	}

	public UnaryCompositeCriterion(final Criterion criterion,
			final UnaryCompositeOperation operation) {
		this();
		this.criterion = criterion;
		this.operation = operation;
	}

	/**
	 * @return the criterion
	 */
	public Criterion getCriterion() {
		return criterion;
	}

	/**
	 * @return the operation
	 */
	public UnaryCompositeOperation getOperation() {
		return operation;
	}
	/**
	 * @param criterion
	 *            the criterion to set
	 */
	public void setCriterion(final Criterion criterion) {
		this.criterion = criterion;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final UnaryCompositeOperation operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return operation + " (" + criterion + ")";
	}
}

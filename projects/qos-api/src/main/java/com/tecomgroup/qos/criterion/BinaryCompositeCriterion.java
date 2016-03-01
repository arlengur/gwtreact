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
public class BinaryCompositeCriterion
		implements
			CompositeCriterion,
			Serializable {

	private Criterion left;

	private Criterion right;

	private BinaryCompositeOperation operation;
	public BinaryCompositeCriterion() {
		super();
	}
	public BinaryCompositeCriterion(final Criterion left,
			final Criterion right, final BinaryCompositeOperation operation) {
		this();
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	/**
	 * @return the left
	 */
	public Criterion getLeft() {
		return left;
	}

	/**
	 * @return the operation
	 */
	public BinaryCompositeOperation getOperation() {
		return operation;
	}

	/**
	 * @return the right
	 */
	public Criterion getRight() {
		return right;
	}

	/**
	 * @param left
	 *            the left to set
	 */
	public void setLeft(final Criterion left) {
		this.left = left;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final BinaryCompositeOperation operation) {
		this.operation = operation;
	}

	/**
	 * @param right
	 *            the right to set
	 */
	public void setRight(final Criterion right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "(" + left + ") " + operation + " (" + right + ")";
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.util.Date;

import com.tecomgroup.qos.TimeConstants;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public abstract class TernaryCriterion<T> extends CriterionWithParameter {
	public static class DateTernaryCriterion extends TernaryCriterion<Date> {

		public DateTernaryCriterion() {
			super();
		}

		public DateTernaryCriterion(final String parameter, final Date low,
				final Date high, final Operation operation) {
			super(parameter, low, high, operation);
		}
	}

	public static class NumberTernaryCriterion extends TernaryCriterion<Number> {

		public NumberTernaryCriterion() {
			super();
		}

		public NumberTernaryCriterion(final String parameter,
				final Number high, final Number low, final Operation operation) {
			super(parameter, low, high, operation);
		}
	}

	public static class OnDayCriterion extends DateTernaryCriterion {

		private Date day;

		public OnDayCriterion() {
			super();
		}

		public OnDayCriterion(final String parameter, final Date day) {
			super(parameter, new Date(day.getTime() - HOURS12), new Date(
					day.getTime() + HOURS12), TernaryOperation.BETWEEN);
			this.day = day;
		}
		/**
		 * @return the day
		 */
		public Date getDay() {
			return day;
		}

		/**
		 * @param day
		 *            the day to set
		 */
		public void setDay(final Date day) {
			this.day = day;
		}
	}

	private final static Long HOURS12 = TimeConstants.MILLISECONDS_PER_DAY / 2;

	private T left;
	private T right;
	private TernaryOperation operation;

	public TernaryCriterion() {
		super();
	}

	public TernaryCriterion(final String parameter, final T low, final T high,
			final Operation operation) {
		this();
		if (!(operation instanceof TernaryOperation)) {
			throw new IllegalArgumentException("Incorrect type of operation "
					+ operation
					+ ". Operation should be the instance of TernaryOperation");
		}
		this.parameter = parameter;
		this.left = low;
		this.right = high;
		this.operation = (TernaryOperation) operation;
	}

	/**
	 * @return the left
	 */
	public T getLeft() {
		return left;
	}

	/**
	 * @return the operation
	 */
	public TernaryOperation getOperation() {
		return operation;
	}

	/**
	 * @return the right
	 */
	public T getRight() {
		return right;
	}

	/**
	 * @param left
	 *            the left to set
	 */
	public void setLeft(final T left) {
		this.left = left;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final TernaryOperation operation) {
		this.operation = operation;
	}

	/**
	 * @param right
	 *            the right to set
	 */
	public void setRight(final T right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return parameter + " " + operation + " (" + left + ", " + right + ")";
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.util.Collection;
import java.util.Date;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public abstract class BinaryCriterion<T> extends CriterionWithParameter {

	public static class BooleanBinaryCriterion extends BinaryCriterion<Boolean> {
		public BooleanBinaryCriterion() {
			super();
		}

		public BooleanBinaryCriterion(final String parameter,
				final Boolean value, final Operation operation) {
			super(parameter, value, operation);
		}
	}

	public static class CollectionBinaryCriterion
			extends
				BinaryCriterion<Collection<?>> {
		public CollectionBinaryCriterion() {
			super();
		}

		public CollectionBinaryCriterion(final String parameter,
				final Collection<?> value, final Operation operation) {
			super(parameter, value, operation);
		}
	}

	public static class DateBinaryCriterion extends BinaryCriterion<Date> {
		public DateBinaryCriterion() {
			super();
		}

		public DateBinaryCriterion(final String parameter, final Date value,
				final Operation operation) {
			super(parameter, value, operation);
		}
	}

	public static class EntityBinaryCriterion extends BinaryCriterion<Object> {
		public EntityBinaryCriterion() {
			super();
		}

		public EntityBinaryCriterion(final String parameter,
				final Object value, final Operation operation) {
			super(parameter, value, operation);
		}
	}

	public static class EnumBinaryCriterion extends BinaryCriterion<Enum<?>> {
		public EnumBinaryCriterion() {
			super();
		}

		public EnumBinaryCriterion(final String parameter, final Enum<?> value,
				final Operation operation) {
			super(parameter, value, operation);
		}
	}

	public static class NumberBinaryCriterion extends BinaryCriterion<Number> {
		public NumberBinaryCriterion() {
			super();
		}

		public NumberBinaryCriterion(final String parameter,
				final Number value, final Operation operation) {
			super(parameter, value, operation);
		}
	}

	public static class StringBinaryCriterion extends BinaryCriterion<String> {
		public StringBinaryCriterion() {
			super();
		}

		public StringBinaryCriterion(final String parameter,
				final String value, final Operation operation) {
			super(parameter, value, operation);
		}
	}

	private T value;
	private BinaryOperation operation;

	public BinaryCriterion() {
		super();
	}

	public BinaryCriterion(final String parameter, final T value,
			final Operation operation) {
		this();
		if (!(operation instanceof BinaryOperation)) {
			throw new IllegalArgumentException("Incorrect type of operation "
					+ operation
					+ ". Operation should be the instance of BinaryOperation");
		}
		this.parameter = parameter;
		this.value = value;
		this.operation = (BinaryOperation) operation;
	}

	/**
	 * @return the operation
	 */
	public BinaryOperation getOperation() {
		return operation;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final BinaryOperation operation) {
		this.operation = operation;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return parameter + " " + operation + " " + value;
	}
}

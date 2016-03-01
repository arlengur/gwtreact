/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.util.Collection;
import java.util.Date;

import com.tecomgroup.qos.criterion.Criterion.BinaryCompositeOperation;
import com.tecomgroup.qos.criterion.Criterion.BinaryOperation;
import com.tecomgroup.qos.criterion.Criterion.Operation;
import com.tecomgroup.qos.criterion.Criterion.TernaryOperation;
import com.tecomgroup.qos.criterion.Criterion.UnaryCompositeOperation;
import com.tecomgroup.qos.criterion.Criterion.UnaryOperation;
import com.tecomgroup.qos.domain.MAbstractEntity;

/**
 * @author kunilov.p
 * 
 */
public class CriterionQueryFactory {

	private static class CriterionFactory implements CriterionQuery {

		@Override
		public Criterion and(final Criterion left, final Criterion right) {
			return new BinaryCompositeCriterion(left, right,
					BinaryCompositeOperation.AND);
		}

		@Override
		public Criterion between(final String propertyName, final Object low,
				final Object high) {
			return createTernaryCriterion(propertyName, low, high,
					TernaryOperation.BETWEEN);
		}

		@Override
		public Criterion collectionContains(final String propertyName,
				final Object value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.CONTAINS);
		}

		private Criterion createBinaryCriterion(final String propertyName,
				final Object value, final Operation operation) {
			BinaryCriterion<?> critetion = null;
			if (value != null) {
				if (value instanceof Boolean) {
					critetion = new BinaryCriterion.BooleanBinaryCriterion(
							propertyName, (Boolean) value, operation);
				} else if (value instanceof Number) {
					critetion = new BinaryCriterion.NumberBinaryCriterion(
							propertyName, (Number) value, operation);
				} else if (value instanceof String) {
					critetion = new BinaryCriterion.StringBinaryCriterion(
							propertyName, (String) value, operation);
				} else if (value instanceof Date) {
					critetion = new BinaryCriterion.DateBinaryCriterion(
							propertyName, (Date) value, operation);
				} else if (value instanceof Collection<?>) {
					critetion = new BinaryCriterion.CollectionBinaryCriterion(
							propertyName, (Collection<?>) value, operation);
				} else if (value instanceof Enum<?>) {
					critetion = new BinaryCriterion.EnumBinaryCriterion(
							propertyName, (Enum<?>) value, operation);
				} else if (value instanceof MAbstractEntity) {
					critetion = new BinaryCriterion.EntityBinaryCriterion(
							propertyName, value, operation);
				} else {
					throw new UnsupportedOperationException("Type "
							+ value.getClass() + " not supported");
				}
			} else {
				throw new IllegalArgumentException("Value can not be NULL");
			}
			return critetion;
		}
		private Criterion createTernaryCriterion(final String propertyName,
				final Object low, final Object high, final Operation operation) {
			TernaryCriterion<?> critetion = null;
			if (low != null && high != null) {
				if (!low.getClass().equals(high.getClass())) {
					throw new IllegalArgumentException(
							"Parameters of TernaryCriterion should be the same type. Low type is "
									+ low.getClass() + ". High type is "
									+ high.getClass());
				}
				if (low instanceof Number && high instanceof Number) {
					critetion = new TernaryCriterion.NumberTernaryCriterion(
							propertyName, (Number) low, (Number) high,
							operation);
				} else if (low instanceof Date && high instanceof Date) {
					critetion = new TernaryCriterion.DateTernaryCriterion(
							propertyName, (Date) low, (Date) high, operation);
				} else {
					throw new UnsupportedOperationException("Types "
							+ low.getClass() + ", " + high.getClass()
							+ " not supported");
				}
			} else {
				throw new IllegalArgumentException("Value can not be NULL");
			}
			return critetion;
		}
		@Override
		public Criterion eq(final String propertyName, final Object value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.EQ);
		}

		@Override
		public Criterion ge(final String propertyName, final Object value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.GE);
		}

		@Override
		public Criterion ilike(final String propertyName, final Object value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.ILIKE);
		}

		@Override
		public Criterion in(final String propertyName, final Collection<?> value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.IN);
		}

		@Override
		public Criterion isEmpty(final String propertyName) {
			return new UnaryCriterion(propertyName, UnaryOperation.isEmpty);
		}

		@Override
		public Criterion isNotEmpty(final String propertyName) {
			return new UnaryCriterion(propertyName, UnaryOperation.isNotEmpty);
		}

		@Override
		public Criterion isNotNull(final String propertyName) {
			return new UnaryCriterion(propertyName, UnaryOperation.isNotNull);
		}

		@Override
		public Criterion isNull(final String propertyName) {
			return new UnaryCriterion(propertyName, UnaryOperation.isNull);
		}

		@Override
		public Criterion istringContains(final String propertyName,
				final Object value) {
			return createBinaryCriterion(propertyName, "%" + value + "%",
					BinaryOperation.ILIKE);
		}

		@Override
		public Criterion le(final String propertyName, final Object value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.LE);
		}

		@Override
		public Criterion like(final String propertyName, final Object value) {
			return createBinaryCriterion(propertyName, value,
					BinaryOperation.LIKE);
		}

		@Override
		public Criterion not(final Criterion criterion) {
			return new UnaryCompositeCriterion(criterion,
					UnaryCompositeOperation.NOT);
		}

		@Override
		public Criterion or(final Criterion left, final Criterion right) {
			return new BinaryCompositeCriterion(left, right,
					BinaryCompositeOperation.OR);
		}

		@Override
		public Criterion stringContains(final String propertyName,
				final Object value) {
			return createBinaryCriterion(propertyName, "%" + value + "%",
					BinaryOperation.LIKE);
		}

	}

	private static volatile CriterionQuery query;

	public static CriterionQuery getQuery() {
		if (query == null) {
			synchronized (CriterionQueryFactory.class) {
				if (query == null) {
					query = new CriterionFactory();
				}
			}
		}
		return query;
	}

	private CriterionQueryFactory() {
		super();
	}
}

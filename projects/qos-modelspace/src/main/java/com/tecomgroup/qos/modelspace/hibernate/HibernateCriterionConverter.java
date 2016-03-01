/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import java.util.HashMap;
import java.util.Map;

import com.tecomgroup.qos.criterion.BinaryCompositeCriterion;
import com.tecomgroup.qos.criterion.BinaryCriterion;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionConverter;
import com.tecomgroup.qos.criterion.TernaryCriterion;
import com.tecomgroup.qos.criterion.UnaryCompositeCriterion;
import com.tecomgroup.qos.criterion.UnaryCriterion;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class HibernateCriterionConverter implements CriterionConverter {

	public static class QueryWrapper {

		private final String query;
		private final Map<String, ?> arguments;
		public QueryWrapper(final String query) {
			this(query, new HashMap<String, Object>());
		}

		public QueryWrapper(final String query, final Map<String, ?> arguments) {
			this.query = query;
			this.arguments = arguments;
		}
		@SuppressWarnings({"unchecked", "rawtypes"})
		public void addAll(final Map<String, ?> arguments) {
			this.arguments.putAll((Map) arguments);
		}
		/**
		 * @return the arguments
		 */
		public Map<String, ?> getArguments() {
			return arguments;
		}

		/**
		 * @return the query
		 */
		public String getQuery() {
			return query;
		}

	}

	/**
	 * 
	 */
	private static final int PARAM_MAX_NUMBER = 10000;

	private int parameterSuffix = 0;

	private static volatile CriterionConverter converter;

	public static CriterionConverter getConverter() {
		if (converter == null) {
			synchronized (HibernateCriterionConverter.class) {
				if (converter == null) {
					converter = new HibernateCriterionConverter();
				}
			}
		}
		return converter;
	}

	private HibernateCriterionConverter() {
		super();
	}

	/**
	 * @param criterion
	 * @return
	 */
	private QueryWrapper fromBinaryCompositeCriterion(
			final String rootEntityAlias,
			final BinaryCompositeCriterion criterion) {
		if (criterion.getLeft() == null && criterion.getRight() == null) {
			throw new ModelSpaceException(
					"Cannot composite two null criterions");
		}
		if (criterion.getLeft() == null) {
			return fromCriterion(rootEntityAlias, criterion.getRight());
		} else if (criterion.getRight() == null) {
			return fromCriterion(rootEntityAlias, criterion.getLeft());
		}
		final QueryWrapper left = fromCriterion(rootEntityAlias,
				criterion.getLeft());
		final QueryWrapper right = fromCriterion(rootEntityAlias,
				criterion.getRight());
		String condition = "(" + left.getQuery() + ")";
		switch (criterion.getOperation()) {
			case AND :
				condition += " and ";
				break;
			case OR :
				condition += " or ";
				break;
			default :
				throw new ModelSpaceException(
						"Binary Composite operation is not supported "
								+ criterion.getOperation());
		}
		condition += "(" + right.getQuery() + ")";
		final QueryWrapper wrapper = new QueryWrapper(condition);
		wrapper.addAll(left.getArguments());
		wrapper.addAll(right.getArguments());
		return wrapper;
	}

	private QueryWrapper fromBinaryCriterion(final String rootEntityAlias,
			final BinaryCriterion<?> criterion) {
		final String fullConditionParameterName = rootEntityAlias + "."
				+ criterion.getParameter();
		String condition = fullConditionParameterName;
		final String paramName = getNewParam();
		final String paramSuffix = ":" + paramName;
		Object paramValue = criterion.getValue();
		switch (criterion.getOperation()) {
			case EQ :
				condition += " = " + paramSuffix;
				break;
			case GE :
				condition += " > " + paramSuffix;
				break;
			case LE :
				condition += " < " + paramSuffix;
				break;
			case LIKE :
				condition += " like " + paramSuffix;
				break;
			case ILIKE :
				condition = "lower(" + condition + ")";
				condition += " like " + paramSuffix;
				paramValue = paramValue.toString().toLowerCase();
				break;
			case IN :
				condition += " in (" + paramSuffix + ")";
				break;
			case CONTAINS :
				condition = paramSuffix + " in elements("
						+ fullConditionParameterName + ")";
				break;
			default :
				throw new ModelSpaceException(
						"Binary operation is not supported "
								+ criterion.getOperation());
		}
		return new QueryWrapper(condition, SimpleUtils.asMap(paramName,
				paramValue));
	}

	private QueryWrapper fromCriterion(final String rootEntityAlias,
			final Criterion criterion) {
		final QueryWrapper result;
		if (criterion instanceof BinaryCriterion<?>) {
			result = fromBinaryCriterion(rootEntityAlias,
					(BinaryCriterion<?>) criterion);
		} else if (criterion instanceof UnaryCriterion) {
			result = fromUnaryCriterion(rootEntityAlias,
					(UnaryCriterion) criterion);
		} else if (criterion instanceof BinaryCompositeCriterion) {
			result = fromBinaryCompositeCriterion(rootEntityAlias,
					(BinaryCompositeCriterion) criterion);
		} else if (criterion instanceof UnaryCompositeCriterion) {
			result = fromUnaryCompositeCriterion(rootEntityAlias,
					(UnaryCompositeCriterion) criterion);
		} else if (criterion instanceof TernaryCriterion<?>) {
			result = fromTernaryCriterion(rootEntityAlias,
					(TernaryCriterion<?>) criterion);
		} else {
			throw new ModelSpaceException("Criterion "
					+ criterion.getClass().getSimpleName()
					+ " is not supported.");
		}
		return result;
	}

	/**
	 * @param criterion
	 * @return
	 */
	private QueryWrapper fromTernaryCriterion(final String rootEntityAlias,
			final TernaryCriterion<?> criterion) {
		String condition = rootEntityAlias + "." + criterion.getParameter()
				+ " ";
		final String param1 = getNewParam();
		final String param2 = getNewParam();
		switch (criterion.getOperation()) {
			case BETWEEN :
				condition += "between :" + param1 + " and  :" + param2;
				break;
			default :
				throw new ModelSpaceException(
						"Ternary operation is not supported "
								+ criterion.getOperation());
		}
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(param1, criterion.getLeft());
		params.put(param2, criterion.getRight());
		return new QueryWrapper(condition, params);
	}

	/**
	 * @param criterion
	 * @return
	 */
	private QueryWrapper fromUnaryCompositeCriterion(
			final String rootEntityAlias,
			final UnaryCompositeCriterion criterion) {
		String condition;
		switch (criterion.getOperation()) {
			case NOT :
				condition = "not (";
				break;
			default :
				throw new ModelSpaceException(
						"Unary Composite operation is not supported "
								+ criterion.getOperation());
		}
		final QueryWrapper wrapper = fromCriterion(rootEntityAlias,
				criterion.getCriterion());
		condition += wrapper.getQuery() + ")";
		return new QueryWrapper(condition, wrapper.getArguments());
	}

	/**
	 * @param criterion
	 * @return
	 */
	private QueryWrapper fromUnaryCriterion(final String rootEntityAlias,
			final UnaryCriterion criterion) {
		String condition = rootEntityAlias + "." + criterion.getParameter();
		switch (criterion.getOperation()) {
			case isNull :
				condition += " is null";
				break;
			case isNotNull :
				condition += " is not null";
				break;
			case isEmpty :
				condition = "size(" + criterion.getParameter() + ") = 0";
				break;
			case isNotEmpty :
				condition = "size(" + criterion.getParameter() + ") <> 0";
				break;
			default :
				throw new ModelSpaceException(
						"Binary operation is not supported "
								+ criterion.getOperation());
		}
		return new QueryWrapper(condition);
	}

	/**
	 * @return the parameterSuffix
	 */
	public String getNewParam() {
		if (++parameterSuffix > PARAM_MAX_NUMBER) {
			parameterSuffix = 0;
		}
		return "param" + parameterSuffix;
	}

	@Override
	public Object toNativeCriterion(final String rootEntityAlias,
			final Criterion criterion) {
		return fromCriterion(rootEntityAlias, criterion);
	}
}

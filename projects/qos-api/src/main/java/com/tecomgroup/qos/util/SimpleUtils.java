/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import com.tecomgroup.qos.HasUniqueKey;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.exception.SourceNotFoundException;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author kunilov.p
 *
 */
public class SimpleUtils {

	public static interface Function<P, R> {
		R apply(P param);
	}

	/**
	 * Simple interface for passing handler functions with no parameters.
	 */
	public static interface SimpleHandler {
		void handle();
	}

	public static final String COMMA_SPACE_SEPARATOR = ", ";

	public static final String DEFAULT_JMX_DOMAIN = "com.tecomgroup.qos";

	public static final String DATE_PARAMETER_NAME = "resultTime";

	public static final String NUMBER_FORMAT = "0.00000";

	public static final String SLASH = "/";

	public final static byte MAX_PERCENTAGE_VALUE = 100;

	public static List<MAlertIndication> alertsToIndications(
			final Collection<MAlert> alerts, final UpdateType updateType) {
		final List<MAlertIndication> indications = new ArrayList<MAlertIndication>();
		for (final MAlert alert : alerts) {
			indications
					.add(new MAlertIndication(alert, new Date(), updateType));
		}
		return indications;
	}

	public static <T, V> Map<T, V> asMap(final T key, final V value) {
		final Map<T, V> map = new HashMap<T, V>();
		map.put(key, value);
		return map;
	}

	public static int asPercentage(final long value, final long total) {
		return (int) ((value / (double) total) * SimpleUtils.MAX_PERCENTAGE_VALUE);
	}

	/**
	 * 
	 * @param value
	 * @return null if value is null or NaN, true if value = 1.0
	 */
	public static Boolean doubleAsBoolean(final Double value) {
		return (value == null || Double.isNaN(value)) ? null : !value
				.equals(0.0);
	}

	/**
	 * Finds root {@link MSystemComponent} only for {@link MAgentModule},
	 * {@link MAgentTask} and NOT finds for {@link MPolicy}.
	 * 
	 * @param source
	 * @return
	 */
	public static MSystemComponent findSystemComponent(final MSource source) {
		MSource parent = source;
		while (parent != null && !(parent instanceof MSystemComponent)) {
			parent = parent.getParent();
		}
		if (parent == null || !(parent instanceof MSystemComponent)) {
			throw new SourceNotFoundException(
					"System component is absent in the hierarchy of " + source);
		}
		return (MSystemComponent) parent;
	}

	public static String getJMXObjectName(final String type,
			final String beanName) {
		if (type == null) {
			return DEFAULT_JMX_DOMAIN + ":name=" + beanName;
		} else {
			return DEFAULT_JMX_DOMAIN + ":type=" + type + ",name=" + beanName;
		}
	}

	/**
	 * Gets set of keys as {@link HasUniqueKey#getKey()} of collection of
	 * {@link HasUniqueKey};
	 * 
	 */
	public static <T extends HasUniqueKey> Set<String> getKeys(
			final Collection<T> collection) {
		final Set<String> resultSet = new HashSet<String>();
		for (final T element : collection) {
			resultSet.add(element.getKey());
		}
		return resultSet;
	}

	public static <T, E> Set<T> getKeysByValue(final Map<T, E> map,
			final E value) {
		final Set<T> keys = new HashSet<T>();
		for (final Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	/**
	 * Converts collection of {@link HasUniqueKey} to map with pairs
	 * {@link HasUniqueKey#getKey}={@link HasUniqueKey}
	 * 
	 */
	public static <T extends HasUniqueKey> Map<String, T> getMap(
			final Collection<T> collection) {
		final Map<String, T> resultMap = new HashMap<String, T>();
		for (final T element : collection) {
			resultMap.put(element.getKey(), element);
		}
		return resultMap;
	}

	public static <T, R> Map<R, List<T>> groupBy(
			final Function<T, R> function, final Collection<T> input) {
		final Map<R, List<T>> result = new HashMap<R, List<T>>();
		for (final T t : input) {
			final R r = function.apply(t);
			final List<T> existing = result.get(r);
			if (existing == null) {
				final List<T> list = new ArrayList<T>();
				list.add(t);
				result.put(r, list);
			} else {
				existing.add(t);
			}
		}
		return result;
	}

	public static boolean isDouble(final String value) {
		boolean result = true;
		try {
			Double.parseDouble(value);
		} catch (final Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean isInt(final String intValue) {
		boolean result = true;
		try {
			Integer.parseInt(intValue);
		} catch (final Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean isLong(final String longValue) {
		boolean result = true;
		try {
			Long.parseLong(longValue);
		} catch (final Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean isNotNullAndNotEmpty(final Collection<?> collection) {
		return !(collection == null || collection.isEmpty());
	}

	public static boolean isNotNullAndNotEmpty(final Object[] array) {
		return !(array == null || array.length == 0);
	}

	/**
	 * Check if string is not null and not empty
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotNullAndNotEmpty(final String value) {
		return !(value == null || value.trim().isEmpty());
	}

	/**
	 * Phone number should contain leading plus symbol. <br/>
	 * Allowed characters: parentheses (), hyphen -, digits, spaces. <br/>
	 * Nested brackets are not allowed. At least one digit should be present
	 * between opening and closing brackets.<br/>
	 * <br/>
	 * Examples of allowed numbers: +7 905 86 84 932, +7(905)86-(84)-932
	 */
	public static boolean isPhoneNumberValid(final String phoneNumber) {
		boolean result;

		if (phoneNumber == null || phoneNumber.trim().length() == 0
				|| !phoneNumber.startsWith("+")) {
			result = false;
		} else {
			// cuts off leading +
			String phone = phoneNumber.substring(1);

			// checks if the next symbols are either brackets, hyphen, spaces or
			// digits
			result = phone.matches("[ \\d)(-]+");
			if (result) {
				// removes all spaces and hyphens as we no longer care about
				// them
				phone = phone.replaceAll("(\\s|-|\\+)+", "");

				// checks that for every opening bracket there are closing one
				// and there is at least one character between brackets
				result = phone.matches("([^)(]*(\\([\\d+]+\\))*[^)(]*)+");
			}
		}

		return result;
	}

	public static Map<String, Set<MAgentTask>> mapTasksByAgentKey(
			final List<MAgentTask> tasks) {
		final Map<String, Set<MAgentTask>> result = new LinkedHashMap<String, Set<MAgentTask>>();
		for (final MAgentTask task : tasks) {
			final String agentKey = task.getModule().getAgent().getKey();
			Set<MAgentTask> agentTasks = result.get(agentKey);
			if (agentTasks == null) {
				agentTasks = new LinkedHashSet<MAgentTask>();
				result.put(agentKey, agentTasks);
			}
			agentTasks.add(task);
		}
		return result;
	}

	/**
	 * Merge criterions with null checks using
	 * {@link CriterionQuery#and(Criterion, Criterion)}.
	 * 
	 * @param criterions
	 * @return mergedCriterion
	 */
	public static Criterion mergeCriterions(final Criterion... criterions) {
		Criterion resultCriterion = null;
		if (criterions != null) {
			for (final Criterion criterion : criterions) {
				resultCriterion = mergeCriterions(resultCriterion, criterion);
			}
		}
		return resultCriterion;
	}

	/**
	 * Merge two criterions with null checks using
	 * {@link CriterionQuery#and(Criterion, Criterion)}.
	 * 
	 * @param leftCriterion
	 * @param rightCriterion
	 * @return mergedCriterion
	 */
	public static Criterion mergeCriterions(final Criterion leftCriterion,
			final Criterion rightCriterion) {
		Criterion resultCriterion = leftCriterion;
		if (leftCriterion == null) {
			resultCriterion = rightCriterion;
		} else if (rightCriterion != null) {
			resultCriterion = CriterionQueryFactory.getQuery().and(
					leftCriterion, rightCriterion);
		}

		return resultCriterion;
	}

	public static Double safeFromLong(final Long value) {
		return value == null ? null : value.doubleValue();
	}

	public static Double safeParseDouble(final String value) {
		return value == null ? null : Double.parseDouble(value);
	}

	/**
	 * @param text
	 * @param maxLength
	 * @return null if given string is null - first maxLength characters else
	 */
	public static String safeTrimToMaxLength(final String text,
			final int maxLength) {
		return text == null ? null : text.substring(0,
				text.length() <= maxLength ? text.length() : maxLength);
	}

	public static Boolean stringAsBoolean(final String booleanValue) {
		Boolean result = null;
		if (isNotNullAndNotEmpty(booleanValue)) {
			if (isInt(booleanValue)) {
				final Integer intValue = Integer.parseInt(booleanValue);
				if (intValue.equals(0)) {
					result = false;
				} else if (intValue.equals(1)) {
					result = true;
				}
			} else if (isDouble(booleanValue)) {
				final Double doubleValue = Double.parseDouble(booleanValue);
				if (doubleValue.equals(0.0)) {
					result = false;
				} else if (doubleValue.equals(1.0)) {
					result = true;
				}
			} else {
				final String valueToLowerCase = booleanValue.toLowerCase();
				if ("false".equals(valueToLowerCase)) {
					result = false;
				} else if ("true".equals(valueToLowerCase)) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Returns string containing item names, each separated from other with
	 * comma and space. Names are obtained via toString() method.
	 * 
	 * @param items
	 * @return
	 */
	public static String toCommaSeparatedString(
			final Collection<? extends Object> items) {
		String result = "";
		for (final Iterator<? extends Object> iter = items.iterator(); iter
				.hasNext();) {
			result += iter.next();
			if (iter.hasNext()) {
				result += COMMA_SPACE_SEPARATOR;
			}
		}

		return result;
	}

	public static boolean validateBoolean(final String booleanValue) {
		boolean result = false;
		if (isNotNullAndNotEmpty(booleanValue)) {
			if (isInt(booleanValue)) {
				final Integer intValue = Integer.parseInt(booleanValue);
				if (intValue.equals(0) || intValue.equals(1)) {
					result = true;
				}
			} else {
				final String valueToLowerCase = booleanValue.toLowerCase();
				if ("false".equals(valueToLowerCase)
						|| "true".equals(valueToLowerCase)) {
					result = true;
				}
			}
		}
		return result;
	}

	public static <T extends Enum<T>> boolean validateEnum(
			final Class<T> enumType, final String value) {
		boolean result = isNotNullAndNotEmpty(value);
		if (result) {
			try {
				Enum.valueOf(enumType, value);
			} catch (final Exception ex) {
				result = false;
			}
		}
		return result;
	}

	public static boolean validateMap(final Map<?, ?> map) {
		return !(map == null || map.isEmpty());
	}

	public static boolean validateTimestamp(final String timestamp) {
		return isNotNullAndNotEmpty(timestamp) && isLong(timestamp);
	}

	public static boolean validateTimeZone(final String timeZone) {
		return isNotNullAndNotEmpty(timeZone);
	}

	public static <K1, K2, V> void updateNestedMap(final Map<K1, Map<K2, V>> nestedMap, final K1 key1, final K2 key2, final V value) {
		if(nestedMap.containsKey(key1)) {
			nestedMap.get(key1).put(key2, value);
		} else {
			HashMap<K2, V> innerMap = new HashMap<K2, V>();
			innerMap.put(key2, value);
			nestedMap.put(key1, innerMap);
		}
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.results;

import java.util.Date;

import com.google.gwt.i18n.client.NumberFormat;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
public class ParameterRow implements ResultRow {

	/**
	 * 
	 * @param value
	 * @param parameterType
	 * @param messages
	 * @param inverseBoolean
	 *            - true to get inverse boolean value (get cease condition by
	 *            raise)
	 * @return yes/no/unknown for {@link ParameterType#BOOL}, formatted number
	 *         for other
	 */
	public static String formatValue(final Double value,
			final ParameterType parameterType,
			final FormattedResultMessages messages, final boolean inverseBoolean) {
		if (value == null) {
			return null;
		}
		if (parameterType == ParameterType.BOOL) {
			final Boolean booleanValue = SimpleUtils.doubleAsBoolean(value);
			if (booleanValue == null) {
				return messages.unknown();
			} else {
				if (inverseBoolean) {
					return booleanValue ? messages.no() : messages.yes();
				} else {
					return booleanValue ? messages.yes() : messages.no();
				}
			}
		} else {
			return numberFormatter.format(value);
		}
	}

	private final String displayName;

	private Double value;

	private Date date;

	private final MParameterThreshold threshold;

	private final ParameterType parameterType;

	/**
	 * Value is created by
	 * {@link ParameterIdentifier#createTaskStorageKey(String)}
	 */
	private final String taskStorageKey;

	private static final NumberFormat numberFormatter = NumberFormat
			.getFormat(SimpleUtils.NUMBER_FORMAT);

	/**
	 * 
	 * @param key
	 *            taskStorageKey
	 *            {@link ParameterIdentifier#createTaskStorageKey(String)}
	 * @param displayName
	 * @param value
	 * @param date
	 * @param threshold
	 */
	public ParameterRow(final String key, final String displayName,
			final Double value, final Date date,
			final MParameterThreshold threshold,
			final ParameterType parameterType) {
		this(key, displayName, threshold, parameterType);
		this.value = value;
		this.date = date;
	}

	/**
	 * 
	 * @param key
	 *            taskStorageKey
	 *            {@link ParameterIdentifier#createTaskStorageKey(String)}
	 * @param displayName
	 * @param threshold
	 */
	public ParameterRow(final String key, final String displayName,
			final MParameterThreshold threshold,
			final ParameterType parameterType) {
		super();
		this.taskStorageKey = key;
		this.displayName = displayName;
		this.threshold = threshold;
		this.parameterType = parameterType;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getFormatedValue(final FormattedResultMessages messages) {
		return formatValue(value, parameterType, messages, false);
	}

	@Override
	public String getKey() {
		return taskStorageKey;
	}

	@Override
	public String getName() {
		return displayName;
	}
	public MParameterThreshold getThreshold() {
		return threshold;
	}

	@Override
	public Double getValue() {
		return value;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final Double value) {
		this.value = value;
	}
}

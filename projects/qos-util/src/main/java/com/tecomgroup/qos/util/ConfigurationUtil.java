/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MProperty.PropertyValueType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultConfigurationTemplate;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.MResultParameterLocation;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * @author kunilov.p
 *
 */
public class ConfigurationUtil {

	public static final String PROPERTY_PARAMETER_SEPARATOR_FOR_URL = "#";
	public static final String SEPARATOR_SUBSTITUTION_FOR_URL = "~@~";
	public static final String PROPERTY_PARAMETER_SEPARATOR_FOR_ALERT_SETTINGS = ", ";
	public static final String NAME_VALUE_SEPARATOR = "=";
	public static final String PARAMETER_NAME = "parameterName";
	private static final String NULL_VALUE = "null";

	/**
	 * Checks whether a collection of {@link MResultParameterConfiguration} is
	 * compatible.
	 *
	 * @param parameterConfigurations
	 * @return true if parameterConfigurations are compatible otherwise false
	 */
	public static boolean areParameterConfigurationsCompatible(
			final Collection<MResultParameterConfiguration> parameterConfigurations) {
		boolean result = true;

		if (parameterConfigurations.size() > 1) {

			final Iterator<MResultParameterConfiguration> iterator = parameterConfigurations
					.iterator();

			final MResultParameterConfiguration parameterToCompare = iterator
					.next();

			final String unitsToCompare = parameterToCompare.getUnits();
			final ParameterType typeToCompare = parameterToCompare.getType();

			while (iterator.hasNext()) {
				final MResultParameterConfiguration currentParameter = iterator
						.next();
				if (!MAbstractEntity.equals(unitsToCompare,
						currentParameter.getUnits())
						|| !MAbstractEntity.equals(typeToCompare,
								currentParameter.getType())) {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Checks whether configurations compatible.
	 *
	 * @param idealConfiguration
	 * @param configurationToCheck
	 * @return true if configurations are compatible otherwise false
	 */
	public static Boolean areResultConfigurationsCompatible(
			final MResultConfiguration idealConfiguration,
			final MResultConfiguration configurationToCheck) {
		boolean result = true;

		if (!idealConfiguration.getSamplingRate().equals(
				configurationToCheck.getSamplingRate())) {
			result = false;
		}
		if (result) {
			boolean foundParameterConfiguration = false;
			for (final MResultParameterConfiguration sourceParameterConfiguration : idealConfiguration
					.getParameterConfigurations()) {
				for (final MResultParameterConfiguration targetParameterConfiguration : configurationToCheck
						.getParameterConfigurations()) {
					final MResultParameterLocation sourceParameterLocation = sourceParameterConfiguration
							.getLocation();
					final MResultParameterLocation targetParameterLocation = targetParameterConfiguration
							.getLocation();
					if ((sourceParameterLocation.getFullFilePath(""))
							.equals(targetParameterLocation.getFullFilePath(""))) {
						foundParameterConfiguration = true;
						if (!sourceParameterConfiguration.getName().equals(
								targetParameterConfiguration.getName())) {
							result = false;
						}
						if (!sourceParameterConfiguration.getAggregationType()
								.equals(targetParameterConfiguration
										.getAggregationType())) {
							result = false;
						}
					}
				}
				if (foundParameterConfiguration) {
					break;
				}
			}
		}

		return result;
	}

	public static MResultConfiguration createFromTemplate(
			final MResultConfigurationTemplate templateResultConfiguration,
			final MAgentTask agentTask) {
		if (templateResultConfiguration == null) {
			throw new ServiceException(
					"Cannot create MResultConfiguration without template");
		}
		if (agentTask == null) {
			throw new ServiceException(
					"Cannot create MResultConfiguration without agent task");
		}
		final MResultConfiguration instance = new MResultConfiguration();
		instance.setTemplateResultConfiguration(templateResultConfiguration);
		instance.setSamplingRate(templateResultConfiguration.getSamplingRate());
		return instance;
	}

	/**
	 *
	 * @param alert
	 * @return {@link ParameterIdentifier} or null if alert is not associated
	 *         with results
	 */
	public static ParameterIdentifier getAssociatedParameterIdentifier(
			final MAlert alert) {
		ParameterIdentifier parameterIdentifier = null;
		if (SimpleUtils.isNotNullAndNotEmpty(alert.getSettings())) {
			parameterIdentifier = ConfigurationUtil
					.stringToParameterIdentifier(
							alert.getSettings(),
							true,
							ConfigurationUtil.PROPERTY_PARAMETER_SEPARATOR_FOR_ALERT_SETTINGS);

		}
		return parameterIdentifier;
	}

	/**
	 * Forms string representing {@link ParameterIdentifier}.
	 *
	 * @param parameterIdentifier
	 *            The {@link ParameterIdentifier}
	 * @param onlyRequiredProperties
	 *            Only required properties will be used to form string in case
	 *            of true, otherwise all properties will be used.
	 * @param propertySeparator
	 *            The separator of properties
	 * @return
	 */
	public static String parameterIdentifierToString(
			final ParameterIdentifier parameterIdentifier,
			final boolean onlyRequiredProperties, final String propertySeparator) {
		final StringBuilder sb = new StringBuilder();

		if (parameterIdentifier != null) {
			sb.append(PARAMETER_NAME).append(NAME_VALUE_SEPARATOR)
			.append(parameterIdentifier.getName());
			if (SimpleUtils.isNotNullAndNotEmpty(parameterIdentifier
					.getProperties())) {
				sb.append(propertySeparator).append(
						propertiesToString(parameterIdentifier.getProperties(),
								onlyRequiredProperties, propertySeparator));
			}
		}

		return sb.toString();
	}

	/**
	 * Uses {@link ConfigurationUtil#PROPERTY_PARAMETER_SEPARATOR_FOR_URL} as
	 * property separator to form string representing properties.
	 *
	 * @param properties
	 *            The list of properties
	 * @param onlyRequired
	 *            Only required properties will be used to form string in case
	 *            of true, otherwise all properties will be used.
	 * @return
	 */
	public static String propertiesToString(final List<MProperty> properties,
			final boolean onlyRequired) {
		return propertiesToString(properties, onlyRequired,
				PROPERTY_PARAMETER_SEPARATOR_FOR_URL);
	}

	/**
	 * Forms string representing list of properties.
	 *
	 * @param properties
	 *            The list of properties
	 * @param onlyRequired
	 *            Only required properties will be used to form string in case
	 *            of true, otherwise all properties will be used.
	 * @param propertySeparator
	 *            The separator of properties
	 * @return
	 */
	public static String propertiesToString(final List<MProperty> properties,
			final boolean onlyRequired, final String propertySeparator) {
		final StringBuilder sb = new StringBuilder();
		if (SimpleUtils.isNotNullAndNotEmpty(properties)) {
			boolean isFirst = true;
			for (final MProperty property : properties) {
				if (onlyRequired && property.isRequired() || !onlyRequired) {
					if (!isFirst) {
						sb.append(propertySeparator);
					}
					String propertyValue = property.getValue();
					propertyValue = propertyValue == null
							? NULL_VALUE
									: propertyValue;
					propertyValue = propertyValue.replaceAll(propertySeparator,
							SEPARATOR_SUBSTITUTION_FOR_URL);
					sb.append(property.getName()).append(NAME_VALUE_SEPARATOR)
					.append(propertyValue);
					isFirst = false;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Parses string to {@link ParameterIdentifier}.
	 *
	 * <b> IMPORTANT: Use only to parse string formed by
	 * {@link ConfigurationUtil#parameterIdentifierToString(ParameterIdentifier, boolean, String)}
	 * </b>
	 *
	 *
	 * @param parameterIdentifierString
	 *            The string representing {@link ParameterIdentifier}
	 * @param requiredProperites
	 *            All parsed properties will be marked as required in case of
	 *            true otherwise not.
	 * @param propertySeparator
	 *            The separator of properties
	 * @return {@link ParameterIdentifier} or null if the string is not a string
	 *         prepresenting {@link ParameterIdentifier}.
	 */
	public static ParameterIdentifier stringToParameterIdentifier(
			final String parameterIdentifierString,
			final boolean requiredProperites, final String propertySeparator) {
		ParameterIdentifier parameterIdentifier = null;

		if (SimpleUtils.isNotNullAndNotEmpty(parameterIdentifierString)) {
			final String trimmedParameterIdentifierString = parameterIdentifierString
					.trim();
			if (trimmedParameterIdentifierString.startsWith(PARAMETER_NAME)) {
				final int parameterNameTokenStringIndex = trimmedParameterIdentifierString
						.indexOf(propertySeparator);
				String parameterNameTokenString = null;
				if (parameterNameTokenStringIndex >= 0) {
					parameterNameTokenString = trimmedParameterIdentifierString
							.substring(0, parameterNameTokenStringIndex);
				} else {
					parameterNameTokenString = trimmedParameterIdentifierString;
				}
				final String[] parameterNameTokens = parameterNameTokenString
						.split(NAME_VALUE_SEPARATOR);
				final String parameterName = parameterNameTokens[1].trim();

				List<MProperty> properties = null;
				if (parameterNameTokenStringIndex >= 0) {
					final String propertyString = parameterIdentifierString
							.substring(parameterNameTokenStringIndex
									+ NAME_VALUE_SEPARATOR.length(),
									parameterIdentifierString.length());
					properties = stringToProperties(propertyString,
							requiredProperites, propertySeparator);
				}
				parameterIdentifier = new ParameterIdentifier(parameterName,
						properties);
			}
		}

		return parameterIdentifier;
	}

	/**
	 * Uses {@link ConfigurationUtil#PROPERTY_PARAMETER_SEPARATOR_FOR_URL} as
	 * property separator to parse string to list of properties.
	 *
	 * <b> IMPORTANT: Use only to parse string formed by
	 * {@link ConfigurationUtil#propertiesToString(List, boolean, String)} or
	 * {@link ConfigurationUtil#propertiesToString(List, boolean)} </b>
	 *
	 * @param propertyString
	 *            The string representing properties
	 * @param required
	 *            All parsed properties will be marked as required in case of
	 *            true otherwise not.
	 * @return list of properties
	 */
	public static List<MProperty> stringToProperties(
			final String propertyString, final boolean required) {
		return stringToProperties(propertyString, required,
				PROPERTY_PARAMETER_SEPARATOR_FOR_URL);
	}

	/**
	 * Parses string to list of properties.
	 *
	 * <b> IMPORTANT: Use only to parse string formed by
	 * {@link ConfigurationUtil#propertiesToString(List, boolean, String)} or
	 * {@link ConfigurationUtil#propertiesToString(List, boolean)} </b>
	 *
	 * FIXME: properties будут иметь тип по умолчанию
	 * {@link PropertyValueType#SAFE_STRING}. Сделать чтобы тип передавался в
	 * строке propertyString
	 *
	 * @param propertyString
	 *            The string representing properties
	 * @param required
	 *            All parsed properties will be marked as required in case of
	 *            true otherwise not.
	 * @param propertySeparator
	 *            The separator of properties
	 * @return list of properties
	 */
	public static List<MProperty> stringToProperties(
			final String propertyString, final boolean required,
			final String propertySeparator) {
		List<MProperty> resultProperties = null;

		if (SimpleUtils.isNotNullAndNotEmpty(propertyString)) {
			resultProperties = new ArrayList<MProperty>();

			final String[] properties = propertyString.split(propertySeparator);
			for (final String propertyPair : properties) {
				final String[] property = propertyPair
						.split(NAME_VALUE_SEPARATOR);
				final String propertyName = property[0].trim();
				// Don't trim propertyValue. Some properties may contain
				// spaces
				String propertyValue = property[1];
				propertyValue = propertyValue.replaceAll(
						SEPARATOR_SUBSTITUTION_FOR_URL, propertySeparator);
				if (NULL_VALUE.equals(propertyValue)) {
					propertyValue = null;
				}
				resultProperties.add(new MProperty(propertyName, propertyValue,
						required));
			}
		}

		return resultProperties;
	}
}

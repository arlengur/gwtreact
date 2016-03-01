/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.PropertiesContainer;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;

/**
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MResultConfigurationTemplate
		extends
			MResultConfigurationSharedData implements PropertiesContainer {

	private String parameterDisplayNameFormat;

	@OneToMany(cascade = CascadeType.ALL)
	private List<MProperty> propertyConfigurations;

	public MResultConfigurationTemplate() {
		super();
	}

	@Override
	public void addProperty(final MProperty property) {
		propertyConfigurations.add(property);
	}

	@Transient
	@JsonIgnore
	public MResultParameterConfiguration findTemplateParameterConfiguration(
			final ParameterIdentifier parameterIdentifier) {
		MResultParameterConfiguration result = null;

		final List<MResultParameterConfiguration> parameterConfigurations = findParameterConfigurations(parameterIdentifier
				.getName());

		final boolean providedPropertiesAreEmpty = parameterIdentifier
				.getProperties() == null
				|| parameterIdentifier.getProperties().isEmpty();
		for (final MResultParameterConfiguration parameterConfiguration : parameterConfigurations) {
			final boolean parameterPropertiesAreEmpty = parameterConfiguration
					.getProperties() == null
					|| parameterConfiguration.getProperties().isEmpty();
			if (providedPropertiesAreEmpty && parameterPropertiesAreEmpty) {
				result = parameterConfiguration;
				break;
			} else if (!providedPropertiesAreEmpty) {
				Map<String, MProperty> parameterProperties = null;
				if (parameterPropertiesAreEmpty) {
					parameterProperties = ParameterIdentifier
							.getRequiredProperties(propertyConfigurations);
				} else {
					parameterProperties = ParameterIdentifier
							.getRequiredProperties(parameterConfiguration
									.getProperties());
				}
				final Map<String, MProperty> providedProperties = parameterIdentifier
						.getPropertyMap();
				final Set<String> requiredProvidedPropertyNames = new HashSet<String>(
						providedProperties.keySet());
				requiredProvidedPropertyNames.retainAll(parameterProperties
						.keySet());

				if (!requiredProvidedPropertyNames.isEmpty()) {
					result = parameterConfiguration;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @return the parameterDisplayNameFormat
	 */
	public String getParameterDisplayNameFormat() {
		return parameterDisplayNameFormat;
	}

	/**
	 * Alias to {@link #getPropertyConfigurations()} to support
	 * {@link #PropertiesContainer}
	 */
	@Override
	@Transient
	@JsonIgnore
	public List<MProperty> getProperties() {
		return propertyConfigurations;
	}

	@Override
	public MProperty getProperty(final String name) {
		MProperty result = null;
		for (final MProperty property : propertyConfigurations) {
			if (property.getName().equals(name)) {
				result = property;
				break;
			}
		}
		return result;
	}

	/**
	 * @return the propertyConfigurations
	 */
	public List<MProperty> getPropertyConfigurations() {
		return propertyConfigurations;
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean hasParameter(final ParameterIdentifier parameterIdentifier) {
		return findTemplateParameterConfiguration(parameterIdentifier) != null;
	}

	@Override
	public void removeProperty(final MProperty property) {
		propertyConfigurations.remove(property);
	}

	/**
	 * @param parameterDisplayNameFormat
	 *            the parameterDisplayNameFormat to set
	 */
	public void setParameterDisplayNameFormat(
			final String parameterDisplayNameFormat) {
		this.parameterDisplayNameFormat = parameterDisplayNameFormat;
	}

	/**
	 * @param propertyConfigurations
	 *            the propertyConfigurations to set
	 */
	public void setPropertyConfigurations(
			final List<MProperty> propertyConfigurations) {
		this.propertyConfigurations = propertyConfigurations;
	}

	public boolean updateSimpleFields(
			final MResultConfigurationTemplate resultConfigurationTemplate) {
		boolean isUpdated = false;

		if (resultConfigurationTemplate != null) {
			final String updatedParameterDisplayNameFormat = resultConfigurationTemplate
					.getParameterDisplayNameFormat();

			if (!equals(getParameterDisplayNameFormat(),
					updatedParameterDisplayNameFormat)) {
				setParameterDisplayNameFormat(updatedParameterDisplayNameFormat);
				isUpdated = true;
			}
		}

		return isUpdated;
	}
}

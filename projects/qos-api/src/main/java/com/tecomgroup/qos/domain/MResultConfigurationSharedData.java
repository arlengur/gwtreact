/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MResultConfigurationSharedData extends MAbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	@Transient
	@JsonIgnore
	public static boolean arePropertiesCompatible(
			final List<MProperty> sourceProperties,
			final List<MProperty> targetProperties) {
		boolean result = false;

		if (sourceProperties == null || sourceProperties.isEmpty()) {
			if (targetProperties == null || targetProperties.isEmpty()) {
				result = true;
			}
		} else if (targetProperties == null || targetProperties.isEmpty()) {
			boolean foundOnlyNullAndEmptyProperty = true;
			for (final MProperty sourceProperty : sourceProperties) {
				if (sourceProperty.getValue() != null
						&& !sourceProperty.getValue().isEmpty()) {
					foundOnlyNullAndEmptyProperty = false;
					break;
				}
			}
			if (foundOnlyNullAndEmptyProperty) {
				result = true;
			}
		} else {
			final Map<String, MProperty> sourceRequiredPropertyMap = ParameterIdentifier
					.getRequiredProperties(sourceProperties);
			final Map<String, MProperty> targetRequiredPropertyMap = ParameterIdentifier
					.getRequiredProperties(targetProperties);

			if (sourceRequiredPropertyMap.size() == targetRequiredPropertyMap
					.size()) {
				boolean equalProperties = true;
				for (final String targetPropertyName : targetRequiredPropertyMap
						.keySet()) {
					final MProperty sourceProperty = sourceRequiredPropertyMap
							.get(targetPropertyName);
					final MProperty targetProperty = targetRequiredPropertyMap
							.get(targetPropertyName);
					if ((sourceProperty == null ^ targetProperty == null)
							|| (sourceProperty.getValue() == null ^ targetProperty
									.getValue() == null)
							|| ((sourceProperty.getValue() != null) && !sourceProperty
									.getValue().equals(
											targetProperty.getValue()))) {
						equalProperties = false;
						break;
					}
				}
				if (equalProperties) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * @uml.property name="parameterConfigurations"
	 */
	@OneToMany(cascade = {CascadeType.ALL})
	protected List<MResultParameterConfiguration> parameterConfigurations;

	/**
	 * @uml.property name="samplingRate"
	 */
	protected Long samplingRate;

	public MResultConfigurationSharedData() {
		super();
		parameterConfigurations = new ArrayList<MResultParameterConfiguration>();
	}

	/**
	 * 
	 * @param parameterConfiguration
	 * @uml.property name="parameterConfigurations"
	 */
	@Transient
	@JsonIgnore
	public void addParameterConfiguration(
			final MResultParameterConfiguration parameterConfiguration) {
		if (parameterConfigurations == null) {
			parameterConfigurations = new ArrayList<MResultParameterConfiguration>();
		}
		parameterConfigurations.add(parameterConfiguration);
	}

	/**
	 * Finds parameter for specified parameterIdentifier.
	 * 
	 * @param parameterIdentifier
	 * @return parameter
	 */
	@Transient
	@JsonIgnore
	public MResultParameterConfiguration findParameterConfiguration(
			final ParameterIdentifier parameterIdentifier) {
		MResultParameterConfiguration result = null;

		final List<MResultParameterConfiguration> parameterConfigurations = findParameterConfigurations(parameterIdentifier
				.getName());
		for (final MResultParameterConfiguration parameterConfiguration : parameterConfigurations) {
			if (arePropertiesCompatible(parameterConfiguration.getProperties(),
					parameterIdentifier.getProperties())) {
				result = parameterConfiguration;
				break;
			}
		}

		return result;
	}

	/**
	 * Finds parameters for specified collection of its parameterIdentifiers
	 * (collection of {@link ParameterIdentifier} or Strings
	 * {@link ParameterIdentifier#createParameterStorageKey()}).
	 * 
	 * @param parameterIdentifiers
	 * @return list of parameters.
	 */
	@Transient
	@JsonIgnore
	public List<MResultParameterConfiguration> findParameterConfigurations(
			final Collection<?> parameterIdentifiers) {
		final List<MResultParameterConfiguration> result = new ArrayList<MResultParameterConfiguration>();
		if (parameterConfigurations != null && parameterIdentifiers != null) {
			final Map<String, MResultParameterConfiguration> parameterConfigurationMap = getParameterConfigurationsAsMap();
			for (final Object parameterIdentifier : parameterIdentifiers) {
				String parameterStorageKey = null;
				if (parameterIdentifier instanceof ParameterIdentifier) {
					parameterStorageKey = ((ParameterIdentifier) parameterIdentifier)
							.createParameterStorageKey();
				} else if (parameterIdentifier instanceof String) {
					parameterStorageKey = (String) parameterIdentifier;
				} else {
					throw new IllegalArgumentException("Only collection of "
							+ ParameterIdentifier.class.getName()
							+ " or String (parameterStorageKey) is supported");
				}
				if (parameterStorageKey != null
						&& parameterConfigurationMap
								.containsKey(parameterStorageKey)) {
					result.add(parameterConfigurationMap
							.get(parameterStorageKey));
				}
			}
		}
		return result;
	}

	/**
	 * Finds all parameters with specified name.
	 * 
	 * @param parameterName
	 * @return list of parameters
	 */
	@Transient
	@JsonIgnore
	public List<MResultParameterConfiguration> findParameterConfigurations(
			final String parameterName) {
		final List<MResultParameterConfiguration> resultParameterConfiguration = new ArrayList<MResultParameterConfiguration>();
		if (parameterConfigurations != null) {
			for (final MResultParameterConfiguration parameterConfiguration : parameterConfigurations) {
				if (parameterConfiguration.getName().equals(parameterName)) {
					resultParameterConfiguration.add(parameterConfiguration);
				}
			}
		}
		return resultParameterConfiguration;
	}

	/**
	 * Getter of the property <tt>parameterConfigurations</tt>
	 * 
	 * @return Returns the parameterConfigurations.
	 * @uml.property name="parameterConfigurations"
	 */
	public List<MResultParameterConfiguration> getParameterConfigurations() {
		return parameterConfigurations;
	}

	/**
	 * Gets only not disabled parameters if onlyActive=true otherwise return all
	 * parameters.
	 * 
	 * @param onlyActive
	 *            true or false
	 * @return list of parameters.
	 */
	@Transient
	@JsonIgnore
	public List<MResultParameterConfiguration> getParameterConfigurations(
			final boolean onlyActive) {
		if (onlyActive) {
			final List<MResultParameterConfiguration> result = new ArrayList<MResultParameterConfiguration>();
			if (parameterConfigurations != null) {
				for (final MResultParameterConfiguration parameterConfiguration : parameterConfigurations) {
					if (!parameterConfiguration.isDisabled()) {
						result.add(parameterConfiguration);
					}
				}
			}
			return result;
		} else {
			return parameterConfigurations;
		}
	}
	/**
	 * Gets all parameters as map where key is created by
	 * {@link ParameterIdentifier#createParameterStorageKey()}
	 * 
	 * @return map of parameters.
	 */
	@Transient
	@JsonIgnore
	public Map<String, MResultParameterConfiguration> getParameterConfigurationsAsMap() {
		final Map<String, MResultParameterConfiguration> map = new HashMap<String, MResultParameterConfiguration>();
		if (parameterConfigurations != null) {
			for (final MResultParameterConfiguration param : parameterConfigurations) {
				map.put(param.getParameterIdentifier()
						.createParameterStorageKey(), param);
			}
		}
		return map;
	}

	/**
	 * Gets parameterIdentifiers for all parameters.
	 * 
	 * @return list of parameterIdentifiers.
	 */
	@Transient
	@JsonIgnore
	public List<ParameterIdentifier> getParameterIdentifiers() {
		final List<ParameterIdentifier> parameterIdentifiers = new ArrayList<ParameterIdentifier>();
		if (parameterConfigurations != null) {
			for (final MResultParameterConfiguration parameterConfiguration : parameterConfigurations) {
				parameterIdentifiers.add(parameterConfiguration
						.getParameterIdentifier());
			}
		}
		return parameterIdentifiers;
	}

	/**
	 * Gets all parameterIdentifiers as map where key is created by
	 * {@link ParameterIdentifier#createParameterStorageKey()}
	 * 
	 * @return map of parameterIdentifiers
	 */
	@Transient
	@JsonIgnore
	public Map<String, ParameterIdentifier> getParameterIdentifiersAsMap() {
		final Map<String, ParameterIdentifier> map = new HashMap<String, ParameterIdentifier>();
		if (parameterConfigurations != null) {
			for (final MResultParameterConfiguration param : parameterConfigurations) {
				final ParameterIdentifier parameterIdentifier = param
						.getParameterIdentifier();
				map.put(parameterIdentifier.createParameterStorageKey(),
						parameterIdentifier);
			}
		}
		return map;
	}

	/**
	 * Gets names of all parameters.
	 * 
	 * @return set of names of all parameters.
	 */
	@Transient
	@JsonIgnore
	public Set<String> getParameterNames() {
		final Set<String> parameterNames = new HashSet<String>();
		if (parameterConfigurations != null) {
			for (final MResultParameterConfiguration param : parameterConfigurations) {
				parameterNames.add(param.getName());
			}
		}
		return parameterNames;
	}

	/**
	 * Getter of the property <tt>samplingRate</tt>
	 * 
	 * @return Returns the samplingRate.
	 * @uml.property name="samplingRate"
	 */
	public Long getSamplingRate() {
		return samplingRate;
	}

	@Transient
	@JsonIgnore
	public boolean hasParameter(final ParameterIdentifier parameterIdentifier) {
		return findParameterConfiguration(parameterIdentifier) != null;
	}

	@Transient
	@JsonIgnore
	public boolean hasParameters() {
		return parameterConfigurations != null
				&& !parameterConfigurations.isEmpty();
	}

	/**
	 * @param parameterIdentifier
	 *            the identifier of the parameter.
	 * @return true if parameter is not null and disabled otherwise false.
	 */
	@Transient
	@JsonIgnore
	public boolean isParameterDisabled(
			final ParameterIdentifier parameterIdentifier) {
		final MResultParameterConfiguration parameterConfiguration = findParameterConfiguration(parameterIdentifier);
		return parameterConfiguration != null
				&& parameterConfiguration.isDisabled();
	}

	/**
	 * 
	 * @param parameterConfiguration
	 */
	@Transient
	@JsonIgnore
	public void removeParameterConfiguration(
			final MResultParameterConfiguration parameterConfiguration) {
		if (parameterConfigurations != null
				&& !parameterConfigurations.isEmpty()) {
			parameterConfigurations.remove(parameterConfiguration);
		}
	}

	/**
	 * Setter of the property <tt>parameterConfigurations</tt>
	 * 
	 * @param parameterConfigurations
	 *            The parameterConfigurations to set.
	 * @uml.property name="parameterConfigurations"
	 */
	public void setParameterConfigurations(
			final List<MResultParameterConfiguration> parameterConfigurations) {
		this.parameterConfigurations = parameterConfigurations;
	}

	/**
	 * Setter of the property <tt>samplingRate</tt>
	 * 
	 * @param samplingRate
	 *            The samplingRate to set.
	 * @uml.property name="samplingRate"
	 */
	public void setSamplingRate(final Long samplingRate) {
		this.samplingRate = samplingRate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

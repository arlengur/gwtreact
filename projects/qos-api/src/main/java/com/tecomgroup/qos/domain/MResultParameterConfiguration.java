/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.PropertiesContainer;
import com.tecomgroup.qos.UpdatableEntity;
import com.tecomgroup.qos.domain.MProperty.PropertyType;

/**
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MResultParameterConfiguration extends MAbstractEntity
		implements
			Disabled,
			PropertiesContainer,
			UpdatableEntity<MResultParameterConfiguration> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public enum AggregationType {
		NONE, AVERAGE, MAX, MIN;
	}

	@Embeddable
	public static class ParameterIdentifier
			implements
				Serializable,
				PropertiesContainer {

		public static String createPropertyStorageKey(
				final List<MProperty> properties) {
			return createPropertyStorageKey(properties, false);
		}

		private static String createPropertyStorageKey(
				final List<MProperty> properties, final boolean asSafeString) {
			final StringBuilder storageKey = new StringBuilder();
			if (properties != null) {
				boolean isFirst = true;
				for (final MProperty property : properties) {
					final String propertyValue = property.getValue();
					if (property.isRequired()
							&& propertyValue != null
							&& !propertyValue.isEmpty()
							&& (!asSafeString || asSafeString
									&& property.hasSafeValue())) {
						if (!isFirst) {
							storageKey.append(STORAGE_KEY_SEPARATOR);
						}
						storageKey.append(property.getValue());
						isFirst = false;
					}
				}
			}
			return storageKey.toString();
		}

		public static Map<String, MProperty> getProperties(
				final List<MProperty> properties) {
			return getProperties(properties, PropertyType.ALL);
		}

		private static Map<String, MProperty> getProperties(
				final List<MProperty> properties,
				final PropertyType propertyType) {
			final Map<String, MProperty> propertyMap = new HashMap<String, MProperty>();
			if (properties != null) {
				for (final MProperty property : properties) {
					if (property.hasPropertyType(propertyType)) {
						propertyMap.put(property.getName(), property);
					}
				}
			}
			return propertyMap;
		}

		public static Map<String, MProperty> getRequiredProperties(
				final List<MProperty> properties) {
			return getProperties(properties, PropertyType.REQUIRED);
		}

		public static Map<String, MProperty> getUnrequiredProperties(
				final List<MProperty> properties) {
			return getProperties(properties, PropertyType.UNREQUIRED);
		}

		@Column(nullable = false)
		private String name;

		@ManyToMany(cascade = {CascadeType.ALL})
		@NotFound(action = NotFoundAction.IGNORE)
		private List<MProperty> properties;

		public ParameterIdentifier() {
			super();
		}

		public ParameterIdentifier(final ParameterIdentifier parameterIdentifier) {
			this();
			name = parameterIdentifier.getName();
			if (parameterIdentifier.getProperties() != null) {
				properties = new ArrayList<MProperty>();
				for (final MProperty property : parameterIdentifier
						.getProperties()) {
					properties.add(new MProperty(property));
				}
			}
		}

		public ParameterIdentifier(final String name,
				final List<MProperty> properties) {
			this();
			this.name = name;
			this.properties = properties;
		}

		@Override
		public void addProperty(final MProperty property) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @return unique, but not safe parameter storage key as string.
		 */
		public String createParameterStorageKey() {
			return createParameterStorageKey(false);
		}

		private String createParameterStorageKey(final boolean asSafeString) {
			String parameterStorageKey = name;
			final String propertyStorageKey = createPropertyStorageKey(
					properties, asSafeString);
			if (propertyStorageKey != null && !propertyStorageKey.isEmpty()) {
				parameterStorageKey += STORAGE_KEY_SEPARATOR
						+ propertyStorageKey;
			}
			return parameterStorageKey;
		}

		/**
		 * @return safe parameter storage key as string.
		 */
		public String createParameterStorageKeyAsSafeString(
				final String uniqueExtraKey) {
			return createParameterStorageKey(true)
					+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
					+ uniqueExtraKey;
		}

		public String createTaskStorageKey(final String taskKey) {
			return taskKey + STORAGE_KEY_SEPARATOR
					+ createParameterStorageKey();
		}

		public String getName() {
			return name;
		}

		@Override
		public List<MProperty> getProperties() {
			return properties;
		}

		@Override
		public MProperty getProperty(final String name) {
			return getPropertyByName(properties, name);
		}

		@JsonIgnore
		public Map<String, MProperty> getPropertyMap() {
			return ParameterIdentifier.getProperties(properties);
		}

		public boolean hasUnsafeRequiredProperties() {
			boolean result = false;
			final List<MProperty> properties = getProperties();
			if (properties != null && !properties.isEmpty()) {
				for (final MProperty property : properties) {
					if (property.isRequired() && !property.hasSafeValue()) {
						result = true;
						break;
					}
				}
			}
			return result;
		}

		@Override
		public void removeProperty(final MProperty property) {
			throw new UnsupportedOperationException();
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setProperties(final List<MProperty> properties) {
			this.properties = properties;
		}

		@Override
		public String toString() {
			return "{name = " + name + ", properties = "
					+ (properties == null ? "null" : properties.toString())
					+ "}";
		}
	}

	public enum ParameterType {
		LEVEL, COUNTER, PERCENTAGE, BOOL, PROPERTY
	}

	public static String STORAGE_KEY_SEPARATOR = ".";

	private static MProperty getPropertyByName(
			final List<MProperty> properties, final String name) {
		MProperty result = null;
		for (final MProperty property : properties) {
			if (property.getName().equals(name)) {
				result = property;
				break;
			}
		}
		return result;
	}

	/**
	 * This property is null for
	 * {@link MResultConfigurationTemplate#parameterConfigurations}.
	 * 
	 * It must be only updated using
	 * {@link MResultConfigurationTemplate#parameterDisplayNameFormat} of
	 * {@link MAgentModule#templateResultConfiguration}
	 */
	private String displayFormat;

	@Transient
	private String parsedDisplayFormat;

	@Column(nullable = false, name = "PARAMETER_TYPE")
	@Enumerated(EnumType.STRING)
	private ParameterType type;

	private String units;

	/**
	 * @uml.property name="name"
	 */
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean disabled = false;

    @Column(nullable = false, insertable = false, updatable = false, unique = true, name = "snmp_id")
    @Generated(GenerationTime.INSERT)
    private int snmpId;

	/**
	 * @uml.property name="aggregationType"
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AggregationType aggregationType;

	/**
	 * @uml.property name="threshold"
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	@NotFound(action = NotFoundAction.IGNORE)
	private MParameterThreshold threshold;

	/**
	 * @uml.property name="displayName"
	 */
	private String displayName;

	/**
	 * @uml.property name="description"
	 */
	@Column(length = 1024)
	private String description;

	/**
	 * @uml.property name="location"
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	private MResultParameterLocation location;

	/**
	 * It must be only updated using
	 * {@link MResultConfigurationTemplate#propertyConfigurations} of
	 * {@link MAgentModule#templateResultConfiguration}
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	private List<MProperty> properties;

	public MResultParameterConfiguration() {
		super();
	}

	public MResultParameterConfiguration(
			final MResultParameterConfiguration parameterConfiguration) {
		this(parameterConfiguration.getName(), parameterConfiguration
				.getDisplayName(), parameterConfiguration.getAggregationType(),
				parameterConfiguration.getType(), new MParameterThreshold(
						parameterConfiguration.getThreshold()),
				parameterConfiguration.getUnits(), parameterConfiguration
						.getDescription());
		if (parameterConfiguration.getProperties() != null) {
			this.properties = new ArrayList<MProperty>(
					parameterConfiguration.getProperties());
		}
	}

	public MResultParameterConfiguration(
			final MResultParameterConfiguration parameterConfiguration,
			final MResultConfigurationTemplate templateResultConfiguration) {
		this(parameterConfiguration.getName(), parameterConfiguration
				.getDisplayName(), parameterConfiguration.getAggregationType(),
				parameterConfiguration.getType(), new MParameterThreshold(
						parameterConfiguration.getThreshold()),
				parameterConfiguration.getUnits(), parameterConfiguration
						.getDescription());
		addProperties(parameterConfiguration.getProperties(),
				templateResultConfiguration.getPropertyConfigurations());
		this.displayFormat = templateResultConfiguration
				.getParameterDisplayNameFormat();
		this.disabled = parameterConfiguration.isDisabled();
	}

	public MResultParameterConfiguration(final String name,
			final String displayName, final AggregationType aggregationType,
			final ParameterType type, final MParameterThreshold threshold) {
		this();
		this.name = name;
		this.displayName = displayName;
		this.aggregationType = aggregationType;
		this.type = type;
		this.threshold = threshold;
	}

	public MResultParameterConfiguration(final String name,
			final String displayName, final AggregationType aggregationType,
			final ParameterType type, final MParameterThreshold threshold,
			final String units, final String description) {
		this(name, displayName, aggregationType, type, threshold);
		this.units = units;
		this.description = description;
	}

	private void addProperties(final List<MProperty> parameterProperties,
			final List<MProperty> templateProperties) {
		if (properties == null) {
			properties = new ArrayList<MProperty>();
		}
		List<MProperty> propertiesToSet = null;
		if (parameterProperties != null && !parameterProperties.isEmpty()) {
			propertiesToSet = parameterProperties;
		} else {
			propertiesToSet = templateProperties;
		}
		if (propertiesToSet != null && !propertiesToSet.isEmpty()) {
			for (final MProperty property : propertiesToSet) {
				properties.add(new MProperty(property));
			}
		}
	}

	@Override
	public void addProperty(final MProperty property) {
		properties.add(property);
	}

	/**
	 * Getter of the property <tt>aggregationType</tt>
	 * 
	 * @return Returns the aggregationType.
	 * @uml.property name="aggregationType"
	 */
	public AggregationType getAggregationType() {
		return aggregationType;
	}

	/**
	 * Getter of the property <tt>description</tt>
	 * 
	 * @return Returns the Description.
	 * @uml.property name="Description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the displayFormat
	 */
	public String getDisplayFormat() {
		return displayFormat;
	}

	/**
	 * Getter of the property <tt>displayName</tt>
	 * 
	 * @return Returns the displayName.
	 * @uml.property name="displayName"
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Getter of the property <tt>location</tt>
	 * 
	 * @return Returns the location.
	 * @uml.property name="location"
	 */
	public MResultParameterLocation getLocation() {
		return location;
	}

	/**
	 * Getter of the property <tt>parameterName</tt>
	 * 
	 * @return Returns the parameterName.
	 * @uml.property name="parameterName"
	 */
	public String getName() {
		return name;
	}

	@Transient
	@JsonIgnore
	public ParameterIdentifier getParameterIdentifier() {
		return new ParameterIdentifier(name, properties);
	}

	/**
	 * @return the parsedDisplayFormat
	 */
	public String getParsedDisplayFormat() {
		if (parsedDisplayFormat == null || parsedDisplayFormat.isEmpty()) {
			parsedDisplayFormat = parseDisplayFormat();
		}
		return parsedDisplayFormat;
	}

	/**
	 * @return the properties
	 */
	@Override
	public List<MProperty> getProperties() {
		return properties;
	}

	@Override
	public MProperty getProperty(final String name) {
		return getPropertyByName(properties, name);
	}

	/**
	 * Getter of the property <tt>parameterThreshold</tt>
	 * 
	 * @return Returns the threshold.
	 * @uml.property name="threshold"
	 */
	public MParameterThreshold getThreshold() {
		return threshold;
	}

	/**
	 * @return the type
	 */
	public ParameterType getType() {
		return type;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

    public int getSnmpId() {
        return snmpId;
    }

    public void setSnmpId(final int snmpId)  {
        this.snmpId = snmpId;
    }

	private String parseDisplayFormat() {
		final String nameToDisplay = (displayName == null || displayName
				.isEmpty()) ? name : displayName;
		String result = nameToDisplay;
		if (displayFormat != null && !displayFormat.isEmpty()) {
			result = displayFormat.replace("${displayName}", nameToDisplay);
			boolean propertiesAreAvailable = false;
			for (final MProperty property : properties) {
				String propertyValue = property.getValue();
				if (propertyValue == null) {
					propertyValue = "";
				} else if (!propertyValue.isEmpty()) {
					propertiesAreAvailable = true;
				}
				result = result.replace("${properties." + property.getName()
						+ "}", propertyValue);
			}
			if (!propertiesAreAvailable) {
				result = nameToDisplay;
			} else {
				// replace with empty string all missed properties
				while (result.contains("${")) {
					result = result.replaceFirst("\\$\\{.*\\}", "");
				}
			}
		}
		return result;
	}

	@Override
	public void removeProperty(final MProperty property) {
		properties.remove(property);
	}

	/**
	 * Setter of the property <tt>aggregationType</tt>
	 * 
	 * @param aggregationType
	 *            The aggregationType to set.
	 * @uml.property name="aggregationType"
	 */
	public void setAggregationType(final AggregationType aggregationType) {
		this.aggregationType = aggregationType;
	}

	/**
	 * Setter of the property <tt>parameterDescription</tt>
	 * 
	 * @param description
	 *            The parameterDescription to set.
	 * @uml.property name="parameterDescription"
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @param displayFormat
	 *            the displayFormat to set
	 */
	public void setDisplayFormat(final String displayFormat) {
		this.displayFormat = displayFormat;
		parsedDisplayFormat = null;
	}

	/**
	 * Setter of the property <tt>displayName</tt>
	 * 
	 * @param displayName
	 *            The displayName to set.
	 * @uml.property name="displayParameterName"
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Setter of the property <tt>location</tt>
	 * 
	 * @param location
	 *            The location to set.
	 * @uml.property name="location"
	 */
	public void setLocation(final MResultParameterLocation location) {
		this.location = location;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * 
	 * @param name
	 *            The name to set.
	 * @uml.property name="name"
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(final List<MProperty> properties) {
		this.properties = properties;
	}

	/**
	 * Setter of the property <tt>threshold</tt>
	 * 
	 * @param threshold
	 *            The threshold to set.
	 * @uml.property name="threshold"
	 */
	public void setThreshold(final MParameterThreshold threshold) {
		this.threshold = threshold;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final ParameterType type) {
		this.type = type;
	}

	/**
	 * @param units
	 *            the units to set
	 */
	public void setUnits(final String units) {
		this.units = units;
	}

	@Override
	public String toString() {
		return "{" + getParsedDisplayFormat() + "}";
	}

	/**
	 * Don't update {@link MResultParameterConfiguration#displayFormat}. It must
	 * be only updated using
	 * {@link MResultConfigurationTemplate#parameterDisplayNameFormat} of
	 * {@link MAgentModule#templateResultConfiguration}
	 * 
	 * Dont'update {@link MResultParameterConfiguration#properties}. It must be
	 * only updated using
	 * {@link MResultConfigurationTemplate#propertyConfigurations} of
	 * {@link MAgentModule#templateResultConfiguration}
	 * 
	 * @param sourceParameter
	 * 
	 */
	@Override
	public boolean updateSimpleFields(
			final MResultParameterConfiguration sourceParameter) {
		boolean isUpdated = false;

		if (sourceParameter != null) {
			if (!equals(getDescription(), sourceParameter.getDescription())) {
				setDescription(sourceParameter.getDescription());
				isUpdated = true;
			}

			if (!equals(getDisplayName(), sourceParameter.getDisplayName())) {
				setDisplayName(sourceParameter.getDisplayName());
				isUpdated = true;
			}

			if (!equals(getUnits(), sourceParameter.getUnits())) {
				setUnits(sourceParameter.getUnits());
				isUpdated = true;
			}

			final MParameterThreshold parameterThreshold = sourceParameter
					.getThreshold();
			if (parameterThreshold != null) {
				if (getThreshold() != null) {
					isUpdated |= getThreshold().updateSimpleFields(
							parameterThreshold);
				} else {
					setThreshold(new MParameterThreshold(parameterThreshold));
					isUpdated = true;
				}
			}
		}
		return isUpdated;
	}
}

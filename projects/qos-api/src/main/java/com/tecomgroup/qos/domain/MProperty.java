/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Динамически создаваемое свойство.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MProperty extends MUpdatableEntity {

	public enum PropertyType {
		REQUIRED, UNREQUIRED, ALL
	}

	public enum PropertyValueType {
		SAFE_STRING, COMMON_STRING
	}

	public static boolean hasPropertyType(final MProperty property,
			final PropertyType type) {
		return (PropertyType.REQUIRED.equals(type) && property.isRequired())
				|| (PropertyType.UNREQUIRED.equals(type) && !property
						.isRequired()) || PropertyType.ALL.equals(type);
	}

	/**
	 * @uml.property name="name"
	 */
	@Column(nullable = false)
	private String name;

	private String displayName;

	/**
	 * @uml.property name="type"
	 */
	@Column(nullable = false, name = "PROPERTY_TYPE")
	@Enumerated(EnumType.STRING)
	private PropertyValueType type = PropertyValueType.SAFE_STRING;

	/**
	 * @uml.property name="value"
	 */
	private String value;

	/**
	 * @uml.property name="isRequired"
	 */
	@Column(nullable = false)
	private boolean required = false;

	public MProperty() {
		super();
	}

	public MProperty(final MProperty property) {
		this();
		if (property != null) {
			setDisplayName(property.getDisplayName());
			setName(property.getName());
			setRequired(property.isRequired());
			setType(property.getType());
			setValue(property.getValue());
		}
	}

	public MProperty(final String name, final boolean required,
			final String displayName) {
		this();
		this.name = name;
		this.required = required;
		this.displayName = displayName;
	}

	public MProperty(final String name, final boolean required,
			final String displayName, final String value) {
		this(name, required, displayName);
		this.value = value;
	}

	public MProperty(final String name, final String value) {
		this();
		this.name = name;
		this.value = value;
	}

	public MProperty(final String name, final String value,
			final boolean required) {
		this(name, value);
		this.required = required;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public PropertyValueType getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	public boolean hasPropertyType(final PropertyType type) {
		return MProperty.hasPropertyType(this, type);
	}

	public boolean hasSafeValue() {
		return !PropertyValueType.COMMON_STRING.equals(type);
	}

	/**
	 * Getter of the property <tt>isRequired</tt>
	 * 
	 * @return Returns the required.
	 * @uml.property name="required"
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
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
	 * Setter of the property <tt>isRequired</tt>
	 * 
	 * @param required
	 *            The isRequired to set.
	 * @uml.property name="required"
	 */
	public void setRequired(final boolean required) {
		this.required = required;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final PropertyValueType type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{name = " + name + ", value = " + value + ", required = "
				+ required + ", type = " + type + "}";
	}

	@Override
	public boolean updateSimpleFields(final MUpdatableEntity entity) {
		boolean isUpdated = false;

		if (entity instanceof MProperty) {
			final MProperty property = (MProperty) entity;

			if (!equals(getDisplayName(), property.getDisplayName())) {
				setDisplayName(property.getDisplayName());
				isUpdated = true;
			}
			if (!equals(isRequired(), property.isRequired())) {
				setRequired(property.isRequired());
				isUpdated = true;
			}
			if (!equals(getType(), property.getType())) {
				setType(property.getType());
				isUpdated = true;
			}
			if (!equals(getValue(), property.getValue())) {
				setValue(property.getValue());
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}

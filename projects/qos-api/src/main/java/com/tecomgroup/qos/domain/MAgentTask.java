/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.tecomgroup.qos.Deleted;
import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.PropertiesContainer;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;

/**
 * Объект задачи для БК.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MAgentTask extends MSource
		implements
			Disabled,
			Deleted,
			PropertiesContainer {

	/**
	 * Property name to find related recording task
	 */
	public static final String RELEATED_RECORDING_TASK_PROPERTY_NAME = "relatedRecordingTask";

	/**
	 * Параметры задачи.
	 * 
	 * @uml.property name="properties"
	 */
	@OneToMany(cascade = {CascadeType.ALL})
	@NotFound(action = NotFoundAction.IGNORE)
	private List<MProperty> properties;

	@Column(nullable = false)
	private boolean disabled = false;

	@Column(nullable = false)
	private boolean deleted = false;

	/**
	 * {@link MSource#parent}
	 */
	@JoinColumn(name = "parent_id", insertable = false, updatable = false)
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE})
	@Deprecated
	private MAgentModule module;

	/**
	 * @uml.property name="resultConfiguration"
	 */
	@OneToOne(cascade = CascadeType.ALL)
	private MResultConfiguration resultConfiguration;

	public MAgentTask() {
		super();
	}

	public MAgentTask(final String key) {
		this();
		setKey(key);
	}

	@Override
	public void addProperty(final MProperty property) {
		properties.add(property);
	}

	/**
	 * {@link MSource#getParent()}
	 */
	@Transient
	public MAgentModule getModule() {
		return (MAgentModule) getParent();
	}

	/**
	 * Getter of the property <tt>properties</tt>
	 * 
	 * @return Returns the properties.
	 * @uml.property name="properties"
	 */
	@Override
	public List<MProperty> getProperties() {
		return properties;
	}

	@Override
	public MProperty getProperty(final String name) {
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
	 * Getter of the property <tt>resultConfiguration</tt>
	 * 
	 * @return Returns the resultConfiguration.
	 * @uml.property name="resultConfiguration"
	 */
	public MResultConfiguration getResultConfiguration() {
		return resultConfiguration;
	}

	@Transient
	@JsonIgnore
	public boolean hasParameter(final ParameterIdentifier parameterIdentifier) {
		return resultConfiguration.hasParameter(parameterIdentifier);
	}

	@Transient
	public boolean hasParameters() {
		boolean result = false;
		if (resultConfiguration != null && resultConfiguration.hasParameters()) {
			result = true;
		}
		return result;
	}

	@Transient
	@JsonIgnore
	public boolean hasTemplateParameter(
			final ParameterIdentifier parameterIdentifier) {
		return getModule().getTemplateResultConfiguration().hasParameter(
				parameterIdentifier);
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public boolean isDisabled() {
		return disabled;
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
		return resultConfiguration.isParameterDisabled(parameterIdentifier);
	}

	@Override
	public void removeProperty(final MProperty property) {
		properties.remove(property);
	}

	@Override
	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * {@link MSource#setParent(MSource)}
	 */
	@Deprecated
	@Transient
	public void setModule(final MAgentModule module) {
		setParent(module);
	}

	/**
	 * Setter of the property <tt>properties</tt>
	 * 
	 * @param properties
	 *            The properties to set.
	 * @uml.property name="properties"
	 */
	public void setProperties(final List<MProperty> properties) {
		this.properties = properties;
	}

	/**
	 * Setter of the property <tt>resultConfiguration</tt>
	 * 
	 * @param resultConfiguration
	 *            The resultConfiguration to set.
	 * @uml.property name="resultConfiguration"
	 */
	public void setResultConfiguration(
			final MResultConfiguration resultConfiguration) {
		this.resultConfiguration = resultConfiguration;
	}
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.annotations.Formula;

/**
 * Класс для представления действий (тип опроса).
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MAgentModule extends MSource {
	/**
	 * {@link MSource#key}
	 */
	@Formula("entity_key")
	@Deprecated
	private String name;

	/**
	 * {@link MSource#parent}
	 */
	@JoinColumn(name = "parent_id", insertable = false, updatable = false)
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE})
	@Deprecated
	private MAgent agent;

	/**
	 * @uml.property name="templateResultConfiguration"
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	private MResultConfigurationTemplate templateResultConfiguration;

	/**
	 * {@link MSource#getParent()}
	 */
	@Transient
	public MAgent getAgent() {
		return (MAgent) getParent();
	}

	/**
	 * {@link MSource#getKey()}
	 */
	@Deprecated
	@Transient
	public String getName() {
		return getKey();
	}

	/**
	 * Getter of the property <tt>templateResultConfiguration</tt>
	 * 
	 * @return Returns the templateResultConfiguration.
	 * @uml.property name="templateResultConfiguration"
	 */
	public MResultConfigurationTemplate getTemplateResultConfiguration() {
		return templateResultConfiguration;
	}

	@Transient
	public boolean hasTemplateParameters() {
		boolean result = false;
		if (templateResultConfiguration != null
				&& templateResultConfiguration.hasParameters()) {
			result = true;
		}
		return result;
	}

	/**
	 * {@link MSource#setParent(MSource)}
	 */
	@Deprecated
	@Transient
	public void setAgent(final MAgent agent) {
		setParent(agent);
	}

	/**
	 * {@link MSource#setKey(String)}
	 */
	@Deprecated
	@Transient
	public void setName(final String name) {
		setKey(name);
	}

	/**
	 * Setter of the property <tt>templateResultConfiguration</tt>
	 * 
	 * @param templateResultConfiguration
	 *            The templateResultConfiguration to set.
	 * @uml.property name="templateResultConfiguration"
	 */
	public void setTemplateResultConfiguration(
			final MResultConfigurationTemplate templateResultConfiguration) {
		this.templateResultConfiguration = templateResultConfiguration;
	}
}

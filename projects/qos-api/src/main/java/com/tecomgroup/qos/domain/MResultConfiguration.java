/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MResultConfiguration extends MResultConfigurationSharedData {

	/**
	 * @see {@link #getTemplateResultConfiguration()}
	 */
	@Deprecated
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(cascade = {CascadeType.ALL})
	private MResultConfigurationTemplate templateResultConfiguration;

	public MResultConfiguration() {
		super();
	}

	/**
	 * It is not synchronized with
	 * {@link MAgentModule#getTemplateResultConfiguration()}. Use
	 * {@link MAgentModule#getTemplateResultConfiguration()} to get
	 * {@link MResultConfigurationTemplate}.
	 * 
	 * @return Returns the {@link MResultConfigurationTemplate}
	 */
	@Deprecated
	public MResultConfigurationTemplate getTemplateResultConfiguration() {
		return templateResultConfiguration;
	}

	public void setTemplateResultConfiguration(
			final MResultConfigurationTemplate templateResultConfiguration) {
		this.templateResultConfiguration = templateResultConfiguration;
	}
}

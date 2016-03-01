/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.SerializedCriterionContainer;

/**
 * 
 * Шаблон, с сохранением {@link Criterion}
 * 
 * @author abondin
 * 
 */
@MappedSuperclass
public abstract class MUserTemplateWithCriterion extends MUserAbstractTemplate
		implements
			SerializedCriterionContainer {

	private static final long serialVersionUID = -4283003979357749943L;

	@Transient
	private Criterion criterion;

	@Column(length = 10240)
	private String serializedCriterion;

	public MUserTemplateWithCriterion() {
		super();
	}

	public MUserTemplateWithCriterion(
			final MUserTemplateWithCriterion templateWithCriterion) {
		super(templateWithCriterion);
		setCriterion(templateWithCriterion.getCriterion());
		setSerializedCriterion(templateWithCriterion.getSerializedCriterion());
	}

	public MUserTemplateWithCriterion(final String name) {
		super(name);
	}

	@Transient
	@Override
	public Criterion getCriterion() {
		return criterion;
	}

	/**
	 * @return the serializedCriterion
	 */
	@Override
	public String getSerializedCriterion() {
		return serializedCriterion;
	}

	@Override
	public void setCriterion(final Criterion criterion) {
		this.criterion = criterion;
	}
	/**
	 * @param serializedCriterion
	 *            the serializedCriterion to set
	 */
	@Override
	public void setSerializedCriterion(final String serializedCriterion) {
		this.serializedCriterion = serializedCriterion;
	}

}

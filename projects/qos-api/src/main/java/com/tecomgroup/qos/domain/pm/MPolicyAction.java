/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.tecomgroup.qos.UpdatableEntity;
import com.tecomgroup.qos.domain.MAbstractEntity;

/**
 * 
 * Base class for all policy actions
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MPolicyAction extends MAbstractEntity
		implements
			UpdatableEntity<MPolicyAction> {

	protected String name;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public MPolicyAction() {
		super();
	}

	public MPolicyAction(final MPolicyAction action) {
		this();
		this.name = action.getName();
	}

	public abstract MPolicyAction copy();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean updateSimpleFields(final MPolicyAction policyAction) {
		boolean isUpdated = false;

		if (policyAction != null) {
			if (!equals(getName(), policyAction.getName())) {
				setName(policyAction.getName());
				isUpdated = true;
			}
		}
		return isUpdated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

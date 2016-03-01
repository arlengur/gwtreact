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
 * Base class for all policy conditions
 * 
 * @author abondin
 * 
 */
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@SuppressWarnings("serial")
public abstract class MPolicyCondition extends MAbstractEntity
		implements
			UpdatableEntity<MPolicyCondition> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public abstract MPolicyCondition copy();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

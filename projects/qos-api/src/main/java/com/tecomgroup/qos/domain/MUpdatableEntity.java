/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.tecomgroup.qos.UpdatableEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public abstract class MUpdatableEntity extends MAbstractEntity
		implements
			UpdatableEntity<MUpdatableEntity> {
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
}

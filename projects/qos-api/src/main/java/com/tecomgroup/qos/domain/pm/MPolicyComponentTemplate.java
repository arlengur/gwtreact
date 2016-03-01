/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.*;

import com.tecomgroup.qos.UpdatableEntity;
import com.tecomgroup.qos.domain.MAbstractEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A template of some part data of policy.
 * 
 * @author kunilov.p
 * 
 */
@Entity
public abstract class MPolicyComponentTemplate extends MAbstractEntity
		implements
			UpdatableEntity<MPolicyComponentTemplate> {

	private static final long serialVersionUID = 7688640437570808351L;

	@Column(nullable = false, unique = true)
	protected String name;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public MPolicyComponentTemplate() {
		super();
	}

	public MPolicyComponentTemplate(
			final MPolicyComponentTemplate componentTemplate) {
		this.name = componentTemplate.getName();
	}

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
	public boolean updateSimpleFields(final MPolicyComponentTemplate template) {
		boolean isUpdated = false;

		if (template != null) {
			if (!equals(getName(), template.getName())) {
				setName(template.getName());
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

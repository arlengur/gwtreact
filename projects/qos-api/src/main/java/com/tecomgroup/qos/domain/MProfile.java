/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Набор задач для БК, объедененных в профайл.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MProfile extends MLoggedEntity {

	/**
	 * @uml.property name="displayName"
	 */
	@Column()
	private String displayName;

	/**
	 * @uml.property name="description"
	 */
	@Column(length = 1024)
	private String description;

	/**
	 * Getter of the property <tt>description</tt>
	 * 
	 * @return Returns the description.
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Setter of the property <tt>description</tt>
	 * 
	 * @param description
	 *            The description to set.
	 * @uml.property name="description"
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Объект, для которого необходимо иметь историю создания и изменений.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public abstract class MLoggedEntity extends MAbstractEntity {

	/**
	 * @uml.property name="modificationDateTime"
	 */
	private Date modificationDateTime;

	/**
	 * @uml.property name="creationDateTime"
	 */
	private Date creationDateTime;

	/**
	 * @uml.property name="createdBy"
	 */
	private String createdBy;

	/**
	 * @uml.property name="modifiedBy"
	 */
	private String modifiedBy;

	/**
	 * @uml.property name="version"
	 */
	private long version;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * Getter of the property <tt>createdBy</tt>
	 * 
	 * @return Returns the createdBy.
	 * @uml.property name="createdBy"
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Getter of the property <tt>creationDateTime</tt>
	 * 
	 * @return Returns the creationDateTime.
	 * @uml.property name="creationDateTime"
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * Getter of the property <tt>modificationDateTime</tt>
	 * 
	 * @return Returns the modificationDateTime.
	 * @uml.property name="modificationDateTime"
	 */
	public Date getModificationDateTime() {
		return modificationDateTime;
	}

	/**
	 * Getter of the property <tt>modifiedBy</tt>
	 * 
	 * @return Returns the modifiedBy.
	 * @uml.property name="modifiedBy"
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Getter of the property <tt>version</tt>
	 * 
	 * @return Returns the version.
	 * @uml.property name="version"
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Setter of the property <tt>createdBy</tt>
	 * 
	 * @param createdBy
	 *            The createdBy to set.
	 * @uml.property name="createdBy"
	 */
	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Setter of the property <tt>creationDateTime</tt>
	 * 
	 * @param creationDateTime
	 *            The creationDateTime to set.
	 * @uml.property name="creationDateTime"
	 */
	public void setCreationDateTime(final Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * Setter of the property <tt>modificationDateTime</tt>
	 * 
	 * @param modificationDateTime
	 *            The modificationDateTime to set.
	 * @uml.property name="modificationDateTime"
	 */
	public void setModificationDateTime(final Date modificationDateTime) {
		this.modificationDateTime = modificationDateTime;
	}

	/**
	 * Setter of the property <tt>modifiedBy</tt>
	 * 
	 * @param modifiedBy
	 *            The modifiedBy to set.
	 * @uml.property name="modifiedBy"
	 */
	public void setModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * Setter of the property <tt>version</tt>
	 * 
	 * @param version
	 *            The version to set.
	 * @uml.property name="version"
	 */
	public void setVersion(final long version) {
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

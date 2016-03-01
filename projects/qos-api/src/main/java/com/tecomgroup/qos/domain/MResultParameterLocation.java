/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.tecomgroup.qos.ResultConfigurationSettings;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
@Deprecated
// Delete me
public class MResultParameterLocation extends MAbstractEntity {

	/**
	 * @uml.property name="fileName"
	 */
	private String fileName;

	/**
	 * @uml.property name="fileLocation"
	 */
	private String fileLocation;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public MResultParameterLocation() {
		super();
	}

	public MResultParameterLocation(
			final MResultParameterLocation parameterLocation) {
		this(parameterLocation.getFileName(), parameterLocation
				.getFileLocation());
	}

	public MResultParameterLocation(final String fileName,
			final String fileLocation) {
		this();
		this.fileName = fileName;
		this.fileLocation = fileLocation;
	}

	/**
	 * Getter of the property <tt>fileLocation</tt>
	 * 
	 * @return Returns the fileLocation.
	 * @uml.property name="fileLocation"
	 */
	public String getFileLocation() {
		return fileLocation;
	}

	/**
	 * Getter of the property <tt>fileName</tt>
	 * 
	 * @return Returns the fileName.
	 * @uml.property name="fileName"
	 */
	public String getFileName() {
		return fileName;
	}

	public String getFullFilePath(final String storageHome) {
		return storageHome + "/" + fileLocation + fileName
				+ ResultConfigurationSettings.RRD_EXTENSION;
	}

	public String getFullFilePath(final String storageHome,
			final String fileNamePrefix) {
		return storageHome + "/" + fileLocation + fileNamePrefix + fileName
				+ ResultConfigurationSettings.RRD_EXTENSION;
	}

	/**
	 * Setter of the property <tt>fileLocation</tt>
	 * 
	 * @param fileLocation
	 *            The fileLocation to set.
	 * @uml.property name="fileLocation"
	 */
	public void setFileLocation(final String fileLocation) {
		this.fileLocation = fileLocation;
	}

	/**
	 * Setter of the property <tt>fileName</tt>
	 * 
	 * @param fileName
	 *            The fileName to set.
	 * @uml.property name="fileName"
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

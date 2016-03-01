/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

import javax.persistence.*;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MVideoResult extends MAbstractEntity {

	@Column(nullable = false)
	@Embedded
	private Source source;

	@Column(nullable = false)
	private Date startDateTime;

	@Column(nullable = false)
	private Date endDateTime;

	@Column(nullable = false)
	private String fileName;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @param endDateTime
	 *            the endDateTime to set
	 */
	public void setEndDateTime(final Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final Source source) {
		this.source = source;
	}

	/**
	 * @param startDateTime
	 *            the startDateTime to set
	 */
	public void setStartDateTime(final Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

import javax.persistence.*;

/**
 * Расписание выполнения задач.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MSchedule extends MAbstractEntity {
	/**
	 * @uml.property name="startDateTime"
	 */
	@Column(nullable = false)
	private Date startDateTime;

	/**
	 * @uml.property name="endDateTime"
	 */
	private Date endDateTime;

	/**
	 * @uml.property name="duration"
	 */
	private long duration;

	/**
	 * @uml.property name="interval"
	 */
	private long interval;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * Getter of the property <tt>duration</tt>
	 * 
	 * @return Returns the duration.
	 * @uml.property name="duration"
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Getter of the property <tt>endDateTime</tt>
	 * 
	 * @return Returns the endDateTime.
	 * @uml.property name="endDateTime"
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * Getter of the property <tt>interval</tt>
	 * 
	 * @return Returns the interval.
	 * @uml.property name="interval"
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * Getter of the property <tt>startDateTime</tt>
	 * 
	 * @return Returns the startDateTime.
	 * @uml.property name="startDateTime"
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * Setter of the property <tt>duration</tt>
	 * 
	 * @param duration
	 *            The duration to set.
	 * @uml.property name="duration"
	 */
	public void setDuration(final long duration) {
		this.duration = duration;
	}

	/**
	 * Setter of the property <tt>endDateTime</tt>
	 * 
	 * @param endDateTime
	 *            The endDateTime to set.
	 * @uml.property name="endDateTime"
	 */
	public void setEndDateTime(final Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * Setter of the property <tt>interval</tt>
	 * 
	 * @param interval
	 *            The interval to set.
	 * @uml.property name="interval"
	 */
	public void setInterval(final long interval) {
		this.interval = interval;
	}

	/**
	 * Setter of the property <tt>startDateTime</tt>
	 * 
	 * @param startDateTime
	 *            The startDateTime to set.
	 * @uml.property name="startDateTime"
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

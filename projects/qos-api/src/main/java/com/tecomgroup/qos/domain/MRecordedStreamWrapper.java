/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.Type;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MRecordedStreamWrapper extends MStreamWrapper {
	private String videoTimeZone;

	private Date startDateTime;

	private Date endDateTime;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Type intervalType;

	public MRecordedStreamWrapper() {
		super();
	}

	public MRecordedStreamWrapper(final MRecordedStreamWrapper streamWrapper) {
		super(streamWrapper);
		setStartDateTime(streamWrapper.getStartDateTime());
		setEndDateTime(streamWrapper.getEndDateTime());
		setVideoTimeZone(streamWrapper.getVideoTimeZone());
		setIntervalType(streamWrapper.getIntervalType());
	}

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return TimeInterval.getEndDate(endDateTime, intervalType);
	}

	public Type getIntervalType() {
		return intervalType;
	}

	/**
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return TimeInterval.getStartDate(startDateTime, getEndDateTime(),
				intervalType);
	}

	/**
	 * @return the stream
	 */
	@Override
	public MRecordedStream getStream() {
		return (MRecordedStream) super.getStream();
	}

	/**
	 * @return the videoTimeZone
	 */
	public String getVideoTimeZone() {
		return videoTimeZone;
	}

	/**
	 * @param endDateTime
	 *            the endDateTime to set
	 */
	public void setEndDateTime(final Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public void setIntervalType(final Type intervalType) {
		this.intervalType = intervalType;
	}

	/**
	 * @param startDateTime
	 *            the startDateTime to set
	 */
	public void setStartDateTime(final Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @param videoTimeZone
	 *            the videoTimeZone to set
	 */
	public void setVideoTimeZone(final String videoTimeZone) {
		this.videoTimeZone = videoTimeZone;
	}
}
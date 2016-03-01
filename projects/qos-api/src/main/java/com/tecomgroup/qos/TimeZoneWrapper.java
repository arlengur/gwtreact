/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.io.Serializable;

/**
 * @author zamkin.a
 * 
 */
@SuppressWarnings("serial")
public class TimeZoneWrapper
		implements
			Serializable,
			Comparable<TimeZoneWrapper> {

	private int offset;
	private String timeZoneId;
	private String timeZoneLabel;

	public TimeZoneWrapper() {
		super();
	}

	/**
	 * Creates new instance of {@link TimeZoneWrapper}.
	 * 
	 * @param timeZoneId
	 *            The id of the time zone.
	 * @param timeZoneLabel
	 *            The display name of the time zone.
	 * @param offset
	 *            The offset in milliseconds.
	 */
	public TimeZoneWrapper(final String timeZoneId, final String timeZoneLabel,
			final int offset) {
		super();
		this.timeZoneId = timeZoneId;
		this.timeZoneLabel = timeZoneLabel;
		this.offset = offset;
	}

	/**
	 * Compare TimeZoneWrapper
	 * 
	 */
	@Override
	public int compareTo(final TimeZoneWrapper other) {
		final int offsetTimeZone1 = other.getOffset();
		final int offsetTimeZone2 = this.getOffset();
		int result;
		if (offsetTimeZone1 != offsetTimeZone2) {
			result = offsetTimeZone2 - offsetTimeZone1;
		} else {
			result = this.getTimeZoneId().compareTo(other.getTimeZoneId());
		}
		return result;
	}

	/**
	 * Gets offset in milliseconds.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the timeZoneID
	 */
	public String getTimeZoneId() {
		return timeZoneId;
	}

	/**
	 * @return the timeZoneLabel
	 */
	public String getTimeZoneLabel() {
		return timeZoneLabel;
	}

	/**
	 * Sets offset in milliseconds.
	 * 
	 * @param offset
	 *            the offset to set
	 */
	public void setOffset(final int offset) {
		this.offset = offset;
	}

	/**
	 * @param timeZoneID
	 *            the timeZoneID to set
	 */
	public void setTimeZoneId(final String timeZoneID) {
		this.timeZoneId = timeZoneID;
	}

	/**
	 * @param timeZoneLabel
	 *            the timeZoneLabel to set
	 */
	public void setTimeZoneLabel(final String timeZoneLabel) {
		this.timeZoneLabel = timeZoneLabel;
	}
}

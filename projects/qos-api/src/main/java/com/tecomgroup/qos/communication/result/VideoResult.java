/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.result;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
public class VideoResult {

	private String streamKey;

	private String startDateTime;

	private String endDateTime;

	private String fileName;

	@JsonIgnore
	private Date convertedStartDateTime;

	@JsonIgnore
	private Date convertedEndDateTime;

	/**
	 * @return the convertedEndDateTime
	 */
	public Date getConvertedEndDateTime() {
		if (convertedEndDateTime == null) {
			convertedEndDateTime = Result.parseDateTime(endDateTime);
		}
		return convertedEndDateTime;
	}

	/**
	 * @return the convertedStartDateTime
	 */
	public Date getConvertedStartDateTime() {
		if (convertedStartDateTime == null) {
			convertedStartDateTime = Result.parseDateTime(startDateTime);
		}
		return convertedStartDateTime;
	}
	/**
	 * @return the endDateTime
	 */
	public String getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the startDateTime
	 */
	public String getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @return the streamKey
	 */
	public String getStreamKey() {
		return streamKey;
	}

	/**
	 * @param endDateTime
	 *            the endDateTime to set
	 */
	public void setEndDateTime(final String endDateTime) {
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
	 * @param startDateTime
	 *            the startDateTime to set
	 */
	public void setStartDateTime(final String startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @param streamKey
	 *            the streamKey to set
	 */
	public void setStreamKey(final String streamKey) {
		this.streamKey = streamKey;
	}

}

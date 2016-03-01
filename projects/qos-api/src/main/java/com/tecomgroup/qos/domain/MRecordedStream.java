/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MRecordedStream extends MStream {

	private static void copyRecordedStream(final MRecordedStream source,
			final MRecordedStream target) {
		if (source != null && target != null) {
			target.setTemplateDownloadUrl(source.getTemplateDownloadUrl());
			target.setTemplateStreamUrl(source.getTemplateStreamUrl());
			target.setStreamUrl(source.getStreamUrl());
			target.setDownloadUrl(source.getDownloadUrl());
		}
	}

	@Column(nullable = false)
	private String templateStreamUrl;

	@Column(nullable = false)
	private String templateDownloadUrl;

	@JsonIgnore
	@Transient
	private String streamUrl;

	@JsonIgnore
	@Transient
	private String downloadUrl;

	public MRecordedStream() {
		super();
	}

	public MRecordedStream(final MRecordedStream stream) {
		super(stream);
		copyRecordedStream(stream, this);
	}

	@Override
	public void copyTo(final MStream stream) {
		super.copyTo(stream);
		if (stream instanceof MRecordedStream) {
			copyRecordedStream(this, (MRecordedStream) stream);
		} else {
			throw new ClassCastException("Incorrect type of copying stream: "
					+ stream);
		}
	}

	@Transient
	@Override
	public void createTaskRelatedFields(final MAgentTask task) {
		super.createTaskRelatedFields(task);
		streamUrl = parseTemplateUrl(templateStreamUrl, task.getProperties());
		downloadUrl = parseTemplateUrl(templateDownloadUrl,
				task.getProperties());
	}

	/**
	 * @return the downloadUrl
	 */
	public String getDownloadUrl() {
		return downloadUrl;
	}

	/**
	 * @return the streamUrl
	 */
	public String getStreamUrl() {
		return streamUrl;
	}

	/**
	 * @return the templateDownloadUrl
	 */
	public String getTemplateDownloadUrl() {
		return templateDownloadUrl;
	}

	/**
	 * @return the templateStreamUrl
	 */
	public String getTemplateStreamUrl() {
		return templateStreamUrl;
	}

	/**
	 * @param downloadUrl
	 *            the downloadUrl to set
	 */
	public void setDownloadUrl(final String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	/**
	 * @param streamUrl
	 *            the streamUrl to set
	 */
	public void setStreamUrl(final String streamUrl) {
		this.streamUrl = streamUrl;
	}

	/**
	 * @param templateDownloadUrl
	 *            the templateDownloadUrl to set
	 */
	public void setTemplateDownloadUrl(final String templateDownloadUrl) {
		this.templateDownloadUrl = templateDownloadUrl;
	}

	/**
	 * @param templateStreamUrl
	 *            the templateStreamUrl to set
	 */
	public void setTemplateStreamUrl(final String templateStreamUrl) {
		this.templateStreamUrl = templateStreamUrl;
	}

	@Override
	public String toString() {
		return "{ key = " + key + ", source = " + source + ", displayName = "
				+ displayName + ", templateStreamUrl = " + templateStreamUrl
				+ ", templateDownloadUrl = " + templateDownloadUrl
				+ ", properties = "
				+ (properties == null ? "null" : properties.toString()) + " }";
	}

	@Override
	public boolean updateSimpleFields(final MStream stream) {
		boolean isUpdated = super.updateSimpleFields(stream);

		if (stream instanceof MRecordedStream) {
			final MRecordedStream recordedStream = (MRecordedStream) stream;

			if (!equals(getTemplateStreamUrl(),
					recordedStream.getTemplateStreamUrl())) {
				setTemplateStreamUrl(recordedStream.getTemplateStreamUrl());
				isUpdated = true;
			}
			if (!equals(getTemplateDownloadUrl(),
					recordedStream.getTemplateDownloadUrl())) {
				setTemplateDownloadUrl(recordedStream.getTemplateDownloadUrl());
				isUpdated = true;
			}
		}

		return isUpdated;
	}
}

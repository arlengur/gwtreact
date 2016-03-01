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
public class MLiveStream extends MStream {

	private static void copyLiveStream(final MLiveStream source,
			final MLiveStream target) {
		if (source != null && target != null) {
			target.setTemplateUrl(source.getTemplateUrl());
			target.setUrl(source.getUrl());
		}
	}

	@Column(nullable = false)
	private String templateUrl;

	@JsonIgnore
	@Transient
	private String url;

	public MLiveStream() {
		super();
	}

	public MLiveStream(final MLiveStream stream) {
		super(stream);
		copyLiveStream(stream, this);
	}

	@Override
	public void copyTo(final MStream stream) {
		super.copyTo(stream);
		if (stream instanceof MLiveStream) {
			copyLiveStream(this, (MLiveStream) stream);
		} else {
			throw new ClassCastException("Incorrect type of copying stream: "
					+ stream);
		}
	}

	@Transient
	@Override
	public void createTaskRelatedFields(final MAgentTask task) {
		super.createTaskRelatedFields(task);
		url = parseTemplateUrl(templateUrl, task.getProperties());
	}

	/**
	 * @return the templateUrl
	 */
	public String getTemplateUrl() {
		return templateUrl;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * @param templateUrl
	 *            the templateUrl to set
	 */
	public void setTemplateUrl(final String templateUrl) {
		this.templateUrl = templateUrl;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "{ key = " + key + ", source = " + source + ", displayName = "
				+ displayName + ", templateUrl = " + templateUrl
				+ ", properties = "
				+ (properties == null ? "null" : properties.toString()) + " }";
	}

	@Override
	public boolean updateSimpleFields(final MStream stream) {
		boolean isUpdated = super.updateSimpleFields(stream);

		if (stream instanceof MLiveStream) {
			final MLiveStream liveStream = (MLiveStream) stream;

			if (!equals(getTemplateUrl(), liveStream.getTemplateUrl())) {
				setTemplateUrl(liveStream.getTemplateUrl());
				isUpdated = true;
			}
		}

		return isUpdated;
	}
}

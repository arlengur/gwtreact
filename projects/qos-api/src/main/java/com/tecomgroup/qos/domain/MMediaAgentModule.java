/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MMediaAgentModule extends MAgentModule {

	@OneToMany(cascade = CascadeType.ALL)
	private List<MStream> templateStreams;

	@Transient
	@JsonIgnore
	public void addTemplateStream(final MStream stream) {
		templateStreams.add(stream);
	}

	/**
	 * @return the templateStreams
	 */
	public List<MStream> getTemplateStreams() {
		return templateStreams;
	}

	@Transient
	@JsonIgnore
	public void removeTemplateStream(final MStream stream) {
		templateStreams.remove(stream);
	}

	/**
	 * @param templateStreams
	 *            the templateStreams to set
	 */
	public void setTemplateStreams(final List<MStream> templateStreams) {
		this.templateStreams = templateStreams;
	}
}

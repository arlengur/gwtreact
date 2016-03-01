/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.pm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class PMConfiguration implements Serializable {
	private List<MPolicy> policies;

	private Source source;

	public PMConfiguration() {
		super();
	}

	public PMConfiguration(final PMConfiguration pmConfiguration) {
		this();
		this.source = pmConfiguration.getSource();
		if (pmConfiguration.getPolicies() == null) {
			this.policies = new ArrayList<MPolicy>();
		} else {
			this.policies = new ArrayList<MPolicy>(
					pmConfiguration.getPolicies());
		}
	}

	public PMConfiguration(final Source source, final List<MPolicy> policies) {
		this();
		this.source = source;
		this.policies = policies;
	}

	/**
	 * @return the policies
	 */
	public List<MPolicy> getPolicies() {
		return policies;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @param policies
	 *            the policies to set
	 */
	public void setPolicies(final List<MPolicy> policies) {
		this.policies = policies;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final Source source) {
		this.source = source;
	}
}

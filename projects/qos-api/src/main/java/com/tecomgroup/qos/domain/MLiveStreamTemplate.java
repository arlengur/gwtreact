/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MLiveStreamTemplate extends MStreamTemplate {

	@OneToMany(cascade = {CascadeType.ALL})
	private List<MLiveStreamWrapper> wrappers;

	public MLiveStreamTemplate() {
		super();
	}

	public MLiveStreamTemplate(final MLiveStreamTemplate streamTemplate) {
		super(streamTemplate);
		wrappers = new ArrayList<MLiveStreamWrapper>();
		for (final MLiveStreamWrapper streamWrapper : streamTemplate
				.getWrappers()) {
			wrappers.add(new MLiveStreamWrapper(streamWrapper));
		}
	}

	public MLiveStreamTemplate(final String name) {
		super(name);
	}

	@Override
	public MUserAbstractTemplate copy() {
		return new MLiveStreamTemplate(this);
	}

	/**
	 * @return the wrappers
	 */
	@Override
	public List<MLiveStreamWrapper> getWrappers() {
		return wrappers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setWrappers(final List<? extends MStreamWrapper> wrappers) {
		this.wrappers = (List<MLiveStreamWrapper>) wrappers;
	}
}

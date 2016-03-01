/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.TimeInterval;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MRecordedStreamTemplate extends MStreamTemplate {

	@OneToMany(cascade = {CascadeType.ALL})
	private List<MRecordedStreamWrapper> wrappers;

	@Embedded
	private TimeInterval syncTimeInterval;

	public MRecordedStreamTemplate() {
		super();
	}

	public MRecordedStreamTemplate(final MRecordedStreamTemplate streamTemplate) {
		super(streamTemplate);
		wrappers = new ArrayList<MRecordedStreamWrapper>();
		for (final MRecordedStreamWrapper streamWrapper : streamTemplate
				.getWrappers()) {
			wrappers.add(new MRecordedStreamWrapper(streamWrapper));
		}
		syncTimeInterval = streamTemplate.getSyncTimeInterval();
	}

	public MRecordedStreamTemplate(final String name) {
		super(name);
	}

	@Override
	public MUserAbstractTemplate copy() {
		return new MRecordedStreamTemplate(this);
	}

	public TimeInterval getSyncTimeInterval() {
		return syncTimeInterval;
	}

	/**
	 * @return the wrappers
	 */
	@Override
	public List<MRecordedStreamWrapper> getWrappers() {
		return wrappers;
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isValid() {
		boolean valid = true;
		if (syncTimeInterval != null) {
			valid = syncTimeInterval.isValid();
		}
		return valid && super.isValid();
	}

	public void setSyncTimeInterval(final TimeInterval syncTimeInterval) {
		this.syncTimeInterval = syncTimeInterval;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setWrappers(final List<? extends MStreamWrapper> wrappers) {
		this.wrappers = (List<MRecordedStreamWrapper>) wrappers;
	}

}

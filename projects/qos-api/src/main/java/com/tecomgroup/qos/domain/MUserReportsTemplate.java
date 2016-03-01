/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.TimeInterval;

/**
 * 
 * Пользовательский шаблон для страницы отчётов
 * 
 * @author abondin
 * 
 */
@Entity
public class MUserReportsTemplate extends MUserTemplateWithGrid {
	private static final long serialVersionUID = -4512057700198458369L;

	@ElementCollection
	private Set<String> sourceKeys;

	@Embedded
	private TimeInterval timeInterval;

	public MUserReportsTemplate() {
		super();
	}

	public MUserReportsTemplate(final MUserReportsTemplate template) {
		super(template);
		setSourceKeys(new HashSet<String>(template.getSourceKeys()));

		final TimeInterval sourceTimeInterval = template.getTimeInterval();
		setTimeInterval(TimeInterval.get(sourceTimeInterval.getType(),
				sourceTimeInterval.getStartDateTime(),
				sourceTimeInterval.getEndDateTime(),
				sourceTimeInterval.getTimeZoneType(),
				sourceTimeInterval.getTimeZone(),
				sourceTimeInterval.getClientTimeZone()));
	}

	public MUserReportsTemplate(final String name) {
		super(name);
	}

	@Override
	public MUserAbstractTemplate copy() {
		return new MUserReportsTemplate(this);
	}

	/**
	 * @return the sources
	 */
	public Set<String> getSourceKeys() {
		return sourceKeys;
	}

	/**
	 * @return the timeInterval
	 */
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isValid() {
		return !sourceKeys.isEmpty() && timeInterval.isValid();
	}

	/**
	 * @param sourceKeys
	 *            the sources to set
	 */
	public void setSourceKeys(final Set<String> sourceKeys) {
		this.sourceKeys = sourceKeys;
	}

	/**
	 * @param timeInterval
	 *            the timeInterval to set
	 */
	public void setTimeInterval(final TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

}

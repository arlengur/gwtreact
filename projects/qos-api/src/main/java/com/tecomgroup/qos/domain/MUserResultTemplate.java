/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.TimeInterval;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MUserResultTemplate extends MUserAbstractTemplate {

	@OneToMany(cascade = {CascadeType.ALL})
	private Set<MChartSeries> series;

	@Embedded
	private TimeInterval timeInterval;

	@Column(nullable = false)
	private boolean chartsSynchronizationEnabled;

	public MUserResultTemplate() {
		chartsSynchronizationEnabled = false;
	}

	public MUserResultTemplate(final MUserResultTemplate resultTemplate) {
		super(resultTemplate);

		setTimeInterval(TimeInterval.get(resultTemplate.getTimeInterval()));

		chartsSynchronizationEnabled = resultTemplate.chartsSynchronizationEnabled;

		series = new HashSet<MChartSeries>();
		if (resultTemplate.getSeries() != null) {
			for (final MChartSeries series : resultTemplate.getSeries()) {
				this.series.add(new MChartSeries(series));
			}
		}
	}

	public MUserResultTemplate(final String name) {
		super(name);
		chartsSynchronizationEnabled = false;
	}

	@Override
	public MUserAbstractTemplate copy() {
		return new MUserResultTemplate(this);
	}

	/**
	 * @return the series
	 */
	public Set<MChartSeries> getSeries() {
		return series;
	}

	/**
	 * @return the timeInterval
	 */
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	/**
	 * @return the chartsSynchronizationEnabled
	 */
	public boolean isChartsSynchronizationEnabled() {
		return chartsSynchronizationEnabled;
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isValid() {
		return !series.isEmpty() && timeInterval.isValid();
	}

	/**
	 * @param chartsSynchronizationEnabled
	 *            the chartsSynchronizationEnabled to set
	 */
	public void setChartsSynchronizationEnabled(
			final boolean chartsSynchronizationEnabled) {
		this.chartsSynchronizationEnabled = chartsSynchronizationEnabled;
	}

	/**
	 * @param series
	 *            the series to set
	 */
	public void setSeries(final Set<MChartSeries> series) {
		this.series = series;
	}

	/**
	 * @param timeInterval
	 *            the timeInterval to set
	 */
	public void setTimeInterval(final TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

}

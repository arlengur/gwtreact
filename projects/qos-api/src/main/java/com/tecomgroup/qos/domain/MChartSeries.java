/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;

import static com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MChartSeries extends MAbstractEntity implements WidgetData {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public static String getUniqueKey(final String chartName, final String key) {
		return chartName + "-" + key;
	}

	@OneToOne
	private MAgentTask task;

	@OneToOne
	private MResultParameterConfiguration parameter;

	private String chartName;

	public MChartSeries() {
		super();
	}

	public MChartSeries(final MAgentTask task,
			final MResultParameterConfiguration parameter,
			final String chartName) {
		this.task = task;
		this.parameter = parameter;
		this.chartName = chartName;
	}

	public MChartSeries(final MChartSeries series) {
		this();
		setChartName(series.getChartName());
		setTask(series.getTask());
		setParameter(series.getParameter());
	}

	/**
	 * @return the agent
	 */
	@Transient
	public MAgent getAgent() {
		return task.getModule().getAgent();
	}

	/**
	 * @return the chartName
	 */
	public String getChartName() {
		return chartName;
	}

	public String getKey() {
		return getAgent().getName()
				+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
				+ getParameter().getParameterIdentifier().createTaskStorageKey(
						getTask().getKey());
	}

	/**
	 * @return the parameter
	 */
	public MResultParameterConfiguration getParameter() {
		return parameter;
	}

	/**
	 * @return the task
	 */
	public MAgentTask getTask() {
		return task;
	}

	public String getUniqueKey() {
		return getChartName() + "-" + getKey();
	}

	/**
	 * @param chartName
	 *            the chartName to set
	 */
	public void setChartName(final String chartName) {
		this.chartName = chartName;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(final MResultParameterConfiguration parameter) {
		this.parameter = parameter;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(final MAgentTask task) {
		this.task = task;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(task.getModule().getAgent() + " | " + task.getKey() + " | "
				+ parameter.getParsedDisplayFormat());
		return sb.toString();
	};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

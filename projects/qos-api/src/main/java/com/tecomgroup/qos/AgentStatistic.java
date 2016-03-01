/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.io.Serializable;
import java.util.*;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author sviyazov.a
 * 
 */
@SuppressWarnings("serial")
public class AgentStatistic implements Serializable {
	private Date lastResultTime;
	private Long hanldedResults = 0l;
	private MAgent.AgentRegistrationState state;
	private String agentVersion;
	//private Map<String,Object> properties;
	private final Map<String, TaskStatistic> tasksStatistic = new HashMap<String, TaskStatistic>();
	private MAgent systemComponent;
	private Date registrationTime;
	@JsonIgnore
	private Long id;

	public AgentStatistic() {
	}

	public AgentStatistic(final MAgent component) {
		this(component, null, null);
	}

	public AgentStatistic(final MAgent component, final Date registrationTime, final Date lastResultTime) {
		this.systemComponent = component;
		this.registrationTime = registrationTime;
		this.id = component.getId();
		this.lastResultTime = lastResultTime;
	}

	public void addTaskStatistic(MAgentTask task, String group)
	{
		synchronized (tasksStatistic) {
			tasksStatistic.put(task.getKey(), new TaskStatistic(task,group));
		}
	}

	public void resetTasksStatistic()
	{
		synchronized (tasksStatistic) {
			tasksStatistic.clear();
		}
	}

	public Long getHanldedResults() {
		return hanldedResults;
	}

	public Date getLastResultTime() {
		return lastResultTime;
	}

	public void recordHandledResults(final int resultNumber,
			final Date lastResultTime) {
		hanldedResults += resultNumber;
		this.lastResultTime = lastResultTime;
	}

	public void setHanldedResults(final Long hanldedResults) {
		this.hanldedResults = hanldedResults;
	}

	public void setLastResultTime(final Date lastResultTime) {
		this.lastResultTime = lastResultTime;
	}

	public MAgent.AgentRegistrationState getState() {
		return state;
	}

	public void setState(MAgent.AgentRegistrationState state) {
		this.state = state;
	}

	public Collection<TaskStatistic> getTasksStatistic() {
		synchronized (tasksStatistic) {
			return tasksStatistic.values();
		}
	}

	public String getAgentVersion() {
		return agentVersion;
	}

	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

	/**
	 * @return the agent
	 */
	public MAgent getComponent() {
		return systemComponent;
	}
	/**
	 * @param component
	 *            the agent to set
	 */
	public void setComponent(final MAgent component) {
		this.systemComponent = component;
	}
	/**
	 * @return the registrationTime
	 */
	public Date getRegistrationTime() {
		return registrationTime;
	}
	/**
	 * @param registrationTime
	 *            the registrationTime to set
	 */
	public void setRegistrationTime(final Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

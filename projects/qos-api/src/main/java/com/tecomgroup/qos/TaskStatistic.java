/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MSystemComponent;

@SuppressWarnings("serial")
public class TaskStatistic  implements Serializable {

	private String key;
	private String displayName;
	private Date lastResultTime;
	private Date registrationTime;
	private Long hanldedResults = 0l;
	private String group;
	//private String state;

	public TaskStatistic() {
		super();
	}

	public TaskStatistic(final MAgentTask task,String group) {
		this(task, null, null,group);
	}

	public TaskStatistic(final MAgentTask task,
						 final Date registrationTime, final Date lastResultTime,String group) {
		this.key = task.getKey();
		this.displayName = task.getDisplayName();
		this.registrationTime=registrationTime;
		this.lastResultTime = lastResultTime;
		this.group=group;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}

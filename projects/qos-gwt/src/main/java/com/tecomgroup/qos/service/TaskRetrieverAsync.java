/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAgentTask;

/**
 * @author kunilov.p
 * 
 */
public interface TaskRetrieverAsync {
	void getAgentTasks(Collection<String> agentNames, Integer startPosition,
			Integer size, boolean activeOnly,
			AsyncCallback<List<MAgentTask>> callback);

	void getAgentTasks(String agentName, Set<String> moduleNames,
			Integer startPosition, Integer size,
			AsyncCallback<List<MAgentTask>> callback);

	void getAgentTasks(String agentName, Set<String> moduleNames,
			TimeInterval timeInterval, Integer startPosition, Integer size,
			AsyncCallback<List<MAgentTask>> callback);

	void getAgentTasks(String agentName, Set<String> moduleNames,
			TimeInterval timeInterval, Integer startPosition, Integer size,
			boolean onlyWithParameters, AsyncCallback<List<MAgentTask>> callback);

	void getAgentTasks(String agentName, Set<String> moduleNames,
			TimeInterval timeInterval, final Integer startPosition,
			final Integer size, boolean onlyWithParameters, boolean onlyActive,
			AsyncCallback<List<MAgentTask>> callback);

	void getTaskByKey(String taskKey, AsyncCallback<MAgentTask> callback);

	void getTasksByKeys(Collection<String> taskKeys,
			AsyncCallback<List<MAgentTask>> callback);

	void getTasksByKeys(Collection<String> taskKeys, boolean activeOnly,
			AsyncCallback<List<MAgentTask>> callback);

    void getTasksByIds(Collection<Long> taskIds, AsyncCallback<List<MAgentTask>> callback);
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAgentTask;

/**
 * 
 * Сервис для работы с задачами
 * 
 * @author abondin
 * 
 */
@RemoteServiceRelativePath("springServices/taskService")
public interface TaskRetriever extends Service, RemoteService {

	List<MAgentTask> getAgentTasks(Collection<String> agentNames,
			Integer startPosition, Integer size, boolean activeOnly);

	List<MAgentTask> getAgentTasks(String agentName, Set<String> moduleNames,
			Integer startPosition, Integer size);

	List<MAgentTask> getAgentTasks(String agentName, Set<String> moduleNames,
			TimeInterval timeInterval, Integer startPosition, Integer size);

	List<MAgentTask> getAgentTasks(String agentName, Set<String> moduleNames,
			TimeInterval timeInterval, Integer startPosition, Integer size,
			boolean onlyWithParameters);

	List<MAgentTask> getAgentTasks(String agentName, Set<String> moduleNames,
			TimeInterval timeInterval, Integer startPosition, Integer size,
			boolean onlyWithParameters, boolean onlyActive);

	MAgentTask getTaskByKey(String taskKey);

	List<MAgentTask> getTasksByKeys(Collection<String> taskKeys);

    List<MAgentTask> getTasksByIds(Collection<Long> taskIds);

	List<MAgentTask> getTasksByKeys(Collection<String> taskKeys,
			boolean activeOnly);
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.TaskEvent;

/**
 * 
 * Сервис для работы с задачами
 * 
 * @author abondin
 * 
 */
@RemoteServiceRelativePath("springServices/taskService")
public interface TaskService extends TaskRetriever {

	/**
	 * Creates or updates provided task and remove it from cache.
	 * 
	 * @param task
	 */
	void createOrUpdateTask(MAgentTask task);

	/**
	 * Deletes tasks by provided keys and sends event {@link TaskEvent} with
	 * {@link EventType#DELETE}.
	 * 
	 * @param taskKeys
	 */
	void deleteTasks(Set<String> taskKeys);
}

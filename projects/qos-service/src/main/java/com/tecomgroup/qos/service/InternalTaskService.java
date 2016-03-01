/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MResultConfigurationTemplate;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * A service to manage tasks for internal server needs.
 * 
 * @author kunilov.p
 * 
 */
public interface InternalTaskService
		extends
			TaskService,
			Disabler<MAgentTask>,
			Deleter<MAgentTask> {

	/**
	 * Get all deleted tasks of provided agent
	 * 
	 * @param agentKey
	 * @return
	 */
	List<MAgentTask> getAgentDeletedTasks(String agentKey);

	/**
	 * Get all tasks
	 *
	 * @return
	 */
	public List<MAgentTask>  getAllTasks();

	/**
	 * Gets the task from cache if there is one or loads it from db and store in
	 * cache. This method must be used only in {@link RRDResultService} to
	 * process results.
	 * 
	 * @param taskKey
	 * @return {@link MAgentTask}
	 */
	MAgentTask getTaskFromCache(String taskKey);

	/**
	 * Register new tasks. This method is alias for
	 * {@link TaskService#updateTasks(String, List)}.
	 * 
	 * @param agentName
	 * @param tasks
	 */
	void registerTasks(String agentName, List<MAgentTask> tasks);

	/**
	 * Resetes disabled task and related policies and alerts.
	 * 
	 * @param taskKey
	 * @return {@link MAgentTask}
	 */
	MAgentTask resetDisabledTask(String taskKey);

	/**
	 * Updates module template and task parameter configurations.
	 * 
	 * @param agentName
	 * @param moduleName
	 * @param oldTemplateConfiguration
	 * @param newTemplateConfiguration
	 * 
	 * @return true if configurations are updated otherwise false
	 */
	boolean updateModuleAndTaskParameterConfigurations(String agentName,
			String moduleName,
			MResultConfigurationTemplate oldTemplateConfiguration,
			MResultConfigurationTemplate newTemplateConfiguration);

	/**
	 * Updates task result configuration.
	 * 
	 * @param task
	 * @return
	 */
	void updateTaskConfiguration(MAgentTask task);

	/**
	 * Updates tasks related to provided agent.
	 * 
	 * @param agentName
	 * @param tasks
	 */
	void updateTasks(String agentName, List<MAgentTask> tasks);

	/**
	 * Validates the configuration of the provided task.
	 * 
	 * @param task
	 * @return true if validation is successful.
	 * @throws ServiceException
	 *             if after validation task configuration is still null.
	 */
	boolean validateTaskConfiguration(MAgentTask task);
}

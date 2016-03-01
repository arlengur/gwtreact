/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.request;

import java.util.List;

import com.tecomgroup.qos.communication.message.QoSRequest;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.communication.response.UpdateModulesResponse;
import com.tecomgroup.qos.communication.response.UpdateTasksResponse;
import com.tecomgroup.qos.domain.MAgentTask;

/**
 * @author kunilov.p
 * 
 */
public class UpdateTasks extends QoSRequest {

	private List<MAgentTask> tasks;

	/**
	 * @return the tasks
	 */
	public List<MAgentTask> getTasks() {
		return tasks;
	}

	public RequestResponse responseError(final String serverName,
			final Throwable throwable) {
		final UpdateModulesResponse response = new UpdateModulesResponse(
				throwable);
		response.setServerName(serverName);
		return response;
	}

	@Override
	public RequestResponse responseError(final Throwable throwable) {
		return new UpdateTasksResponse(throwable);
	}

	@Override
	public RequestResponse responseOk() {
		return new UpdateTasksResponse();
	}

	public RequestResponse responseOk(final String serverName) {
		final UpdateModulesResponse response = new UpdateModulesResponse();
		response.setServerName(serverName);
		return response;
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(final List<MAgentTask> tasks) {
		this.tasks = tasks;
	}
}

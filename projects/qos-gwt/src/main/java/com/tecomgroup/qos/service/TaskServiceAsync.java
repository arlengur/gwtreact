/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.domain.MAgentTask;

/**
 * @author abondin
 */
public interface TaskServiceAsync extends TaskRetrieverAsync {

	void createOrUpdateTask(MAgentTask task, AsyncCallback<Void> callback);

	void deleteTasks(Set<String> taskKeys, AsyncCallback<Void> callback);
}

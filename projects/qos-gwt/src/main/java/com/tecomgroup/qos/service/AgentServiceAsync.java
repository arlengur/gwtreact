/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;

/**
 * @author abondin
 * @see AgentService
 */
public interface AgentServiceAsync {
	void delete(MAgent agent, AsyncCallback<Void> callback);

	void deleteAgents(Set<String> agentKeys, AsyncCallback<Void> callback);

	void getAgentByKey(String key, AsyncCallback<MAgent> callback);

	void getAgentsByKeys(Set<String> agentKeys,
			AsyncCallback<List<MAgent>> callback);

	void getAllAgentKeys(AsyncCallback<List<String>> callback);

	void getAllAgentKeysNoFiltering(AsyncCallback<List<String>> callback);

	void getAllAgents(AsyncCallback<List<MAgent>> callback);

	void getAllModuleKeysByAgentKey(String agentKey,
			AsyncCallback<List<String>> callback);

	void getAllModulesByAgentKey(String agentKey,
			AsyncCallback<List<MAgentModule>> callback);

	void getModule(String agentKey, String moduleKey,
			AsyncCallback<MAgentModule> callback);

	void getRegisteredAgents(AsyncCallback<Set<String>> callback);

	void doesAgentExist(String agent, AsyncCallback<Boolean> callback);

	void doesAgentPermitted(String agent, AsyncCallback<Boolean> callback);

	void getProbeKeysUserCanManage(AsyncCallback<List<String>> callback);

	void registerAgent(MAgent agent, List<MAgentModule> modules,
			AsyncCallback<Long> callback);

	void updateModules(String agentKey, List<MAgentModule> modules,
			AsyncCallback<Void> callback);
}

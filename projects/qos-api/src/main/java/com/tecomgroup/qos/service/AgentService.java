/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/agentService")
public interface AgentService extends Service, RemoteService, Deleter<MAgent> {
	/**
	 * Deletes {@link MAgent} by provided keys.
	 * 
	 * @param agentKeys
	 */
	void deleteAgents(Set<String> agentKeys);

	/**
	 * Gets {@link MAgent} by its key.
	 * 
	 * @param key
	 * @return
	 */
	MAgent getAgentByKey(String key);

	/**
	 * Gets list of {@link MAgent} by its keys.
	 * 
	 * @param agentKeys
	 * @return
	 */
	List<MAgent> getAgentsByKeys(Set<String> agentKeys);

	/**
	 * Gets all agent keys in the system
	 * 
	 * @return
	 * @throws ServiceException
	 */
	List<String> getAllAgentKeys() throws ServiceException;

	/**
	 * Gets all agent keys in the system avoiding an authorize filtering
	 *
	 * @return
	 * @throws ServiceException
	 */
	List<String> getAllAgentKeysNoFiltering() throws ServiceException;

	/**
	 * Gets all {@link MAgent} in the system.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	List<MAgent> getAllAgents() throws ServiceException;

	/**
	 * Gets list of {@link MAgentModule} by its agent key.
	 * 
	 * @param agentKey
	 * @return
	 */
	List<String> getAllModuleKeysByAgentKey(final String agentKey);

	/**
	 * Gets list of {@link MAgentModule} by its agent key.
	 * 
	 * @param agentKey
	 * @return
	 */
	List<MAgentModule> getAllModulesByAgentKey(String agentKey);

	/**
	 * Gets {@link MAgentModule} by its key and agent key.
	 * 
	 * @param agentKey
	 * @param moduleKey
	 * @return
	 */
	MAgentModule getModule(String agentKey, String moduleKey);

	/**
	 * @return the set of registered {@link MAgent}.
	 */
	Set<String> getRegisteredAgents();

	/**
	 * If {@link MAgent} with given key exists in data store.
	 * 
	 * @param agentKey
	 * @return
	 */
	boolean doesAgentExist(String agentKey);

	boolean doesAgentPermitted(String agentKey);

	public List<String> getProbeKeysUserCanManage();

	/**
	 * Register {@link MAgent} in the system
	 * 
	 * @param agent
	 * @return an agent id
	 */
	MAgent registerAgent(MAgent agent, List<MAgentModule> modules);

	/**
	 * Updates existing modules by provided updated ones.
	 * 
	 * @param agentKey
	 * @param updatedModules
	 */
	void updateModules(String agentKey, List<MAgentModule> updatedModules);

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tecomgroup.qos.communication.message.QoSRequest;
import com.tecomgroup.qos.communication.response.RegisterAgentResponse;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.pm.MPolicy;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Запрос на регистрацию агента
 * 
 * @author abondin
 * 
 */
public class RegisterAgent extends QoSRequest {
	public static final String AGENT_KEY_HEADER="agent.key";
	public static final String AGENT_DISPLAY_NAME_HEADER="agent.displayName";
	public static final String AGENT_CONFIGURATION_PROPERTY_KEY="configuration";
	public static final String AGENT_CONFIGURATION_SCHEMA_PROPERTY_KEY="config_schema";
	public static final String AGENT_VERSION_PROPERTY_KEY="version";

	private MAgent agent;
	private List<MAgentTask> tasks = new ArrayList<MAgentTask>();
	private List<MPolicy> policies = new ArrayList<MPolicy>();
	private List<MAlertType> alertTypes = new ArrayList<MAlertType>();
	private List<MAgentModule> modules = new ArrayList<MAgentModule>();
	private Map<String,Object> agent_properties=new HashMap<>();
	/**
	 * @return the agent
	 */
	public MAgent getAgent() {
		return agent;
	}

	/**
	 * @return the alertTypes
	 */
	public List<MAlertType> getAlertTypes() {
		return alertTypes;
	}

	/**
	 * @return the modules
	 */
	public List<MAgentModule> getModules() {
		return modules;
	}

	/**
	 * @return the policies
	 */
	public List<MPolicy> getPolicies() {
		return policies;
	}

	/**
	 * @return the tasks
	 */
	public List<MAgentTask> getTasks() {
		return tasks;
	}

	public RequestResponse responseError(final String serverName,
			final Throwable throwable) {
		final RegisterAgentResponse response = new RegisterAgentResponse(
				throwable);
		response.setServerName(serverName);
		return response;
	}

	@Override
	public RequestResponse responseError(final Throwable throwable) {
		return new RegisterAgentResponse(throwable);
	}

	@Override
	public RegisterAgentResponse responseOk() {
		return new RegisterAgentResponse();
	}

	public RegisterAgentResponse responseOk(final String serverName,
			final String agentId) {
		final RegisterAgentResponse response = responseOk();
		response.setAgentId(agentId);
		response.setServerName(serverName);
		return response;
	}

	public RegisterAgentResponse responseOkWithMinorError(
			final String serverName, final String agentId,
			final Throwable minorError) {
		final RegisterAgentResponse response = responseOk(serverName, agentId);
		response.setReason(minorError.toString());
		return response;
	}

	public String getStringProperty(String key){
		Object configurationObject= getAgent_properties().get(key);
		if(configurationObject!=null)
		{
			String configurationXML=configurationObject.toString();
			if(!configurationXML.isEmpty())
			{
				return configurationXML;
			}
		}
		return null;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(final MAgent agent) {
		this.agent = agent;
	}
	/**
	 * @param alertTypes
	 *            the alertTypes to set
	 */
	public void setAlertTypes(final List<MAlertType> alertTypes) {
		this.alertTypes = alertTypes;
	}
	/**
	 * @param modules
	 *            the modules to set
	 */
	public void setModules(final List<MAgentModule> modules) {
		this.modules = modules;
	}

	/**
	 * @param policies
	 *            the policies to set
	 */
	public void setPolicies(final List<MPolicy> policies) {
		this.policies = policies;
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(final List<MAgentTask> tasks) {
		this.tasks = tasks;
	}

	public Map<String, Object> getAgent_properties() {
		return agent_properties;
	}

	public void setAgent_properties(Map<String, Object> agent_properties) {
		this.agent_properties = agent_properties;
	}
}

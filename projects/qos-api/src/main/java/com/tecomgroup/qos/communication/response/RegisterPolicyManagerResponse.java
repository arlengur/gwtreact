/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.response;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
public class RegisterPolicyManagerResponse extends RequestResponse {

	private Map<Source, PMConfiguration> pmConfigurations = Collections
			.emptyMap();

	private String serverName;

	private Set<String> registeredAgents = Collections.emptySet();

	public RegisterPolicyManagerResponse() {
		super();
	}

	public RegisterPolicyManagerResponse(final Throwable ex) {
		super(ex);
	}

	/**
	 * @return the pmConfigurations
	 */
	public Map<Source, PMConfiguration> getPmConfigurations() {
		return pmConfigurations;
	}

	/**
	 * @return the registeredAgents
	 */
	public Set<String> getRegisteredAgents() {
		return registeredAgents;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param pmConfigurations
	 *            the pmConfigurations to set
	 */
	public void setPmConfigurations(
			final Map<Source, PMConfiguration> pmConfigurations) {
		this.pmConfigurations = pmConfigurations;
	}

	/**
	 * @param registeredAgents
	 *            the registeredAgents to set
	 */
	public void setRegisteredAgents(final Set<String> registeredAgents) {
		this.registeredAgents = registeredAgents;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}

}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.request;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.communication.message.QoSRequest;
import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.communication.response.RegisterPolicyManagerResponse;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
public class RegisterPolicyManager extends QoSRequest {

	private String policyManagerName;
	private Collection<String> supportedAgents;

	public RegisterPolicyManager() {
		super();
	}

	public RegisterPolicyManager(final String policyManagerName,
			final Collection<String> supportedAgents) {
		this.policyManagerName = policyManagerName;
		this.supportedAgents = supportedAgents;
	}

	/**
	 * @return the policyManagerName
	 */
	public String getPolicyManagerName() {
		return policyManagerName;
	}

	/**
	 * @return the supportedAgents
	 */
	public Collection<String> getSupportedAgents() {
		return supportedAgents;
	}

	public RequestResponse responseError(final String serverName,
			final Throwable throwable) {
		final RegisterPolicyManagerResponse response = new RegisterPolicyManagerResponse(
				throwable);
		response.setServerName(serverName);
		return response;
	}

	@Override
	public RequestResponse responseError(final Throwable throwable) {
		return new RegisterPolicyManagerResponse(throwable);
	}

	@Override
	public RequestResponse responseOk() {
		return new RegisterPolicyManagerResponse();
	}

	public RequestResponse responseOk(final String serverName,
			final Set<String> registeredAgents,
			final Map<Source, PMConfiguration> pmConfigurations) {
		final RegisterPolicyManagerResponse response = new RegisterPolicyManagerResponse();
		response.setServerName(serverName);
		response.setPmConfigurations(pmConfigurations);
		response.setRegisteredAgents(registeredAgents);
		return response;
	}

	/**
	 * @param policyManagerName
	 *            the policyManagerName to set
	 */
	public void setPolicyManagerName(final String policyManagerName) {
		this.policyManagerName = policyManagerName;
	}

	/**
	 * @param supportedAgents
	 *            the supportedAgents to set
	 */
	public void setSupportedAgents(final List<String> supportedAgents) {
		this.supportedAgents = supportedAgents;
	}
}

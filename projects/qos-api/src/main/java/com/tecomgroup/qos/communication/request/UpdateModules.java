/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.request;

import java.util.List;

import com.tecomgroup.qos.communication.message.QoSRequest;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.communication.response.UpdateModulesResponse;
import com.tecomgroup.qos.domain.MAgentModule;

/**
 * @author kunilov.p
 * 
 */
public class UpdateModules extends QoSRequest {

	private List<MAgentModule> modules;

	/**
	 * @return the modules
	 */
	public List<MAgentModule> getModules() {
		return modules;
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
		return new UpdateModulesResponse(throwable);
	}

	@Override
	public RequestResponse responseOk() {
		return new UpdateModulesResponse();
	}

	public RequestResponse responseOk(final String serverName) {
		final UpdateModulesResponse response = new UpdateModulesResponse();
		response.setServerName(serverName);
		return response;
	}

	/**
	 * @param modules
	 *            the modules to set
	 */
	public void setModules(final List<MAgentModule> modules) {
		this.modules = modules;
	}
}

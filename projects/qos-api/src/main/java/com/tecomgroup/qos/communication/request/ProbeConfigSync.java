/*
 * Copyright (C) 2016 Qligent.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.request;

import com.tecomgroup.qos.communication.message.QoSRequest;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.communication.response.UpdateModulesResponse;
import com.tecomgroup.qos.communication.response.ProbeConfigSyncResponse;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProbeConfigSync extends QoSRequest {

	@JsonProperty("configuration")
	private String configuration;

	@JsonProperty("config_schema")
	private String schema;

	@JsonProperty("agent_key")
	private String agentKey;

	public String getAgentKey() {
		return agentKey;
	}

	public void setAgentKey(String agentKey) {
		this.agentKey = agentKey;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public RequestResponse responseError(final String serverName,
			final Throwable throwable) {
		final ProbeConfigSyncResponse response = new ProbeConfigSyncResponse(
				throwable);
		response.setServerName(serverName);
		return response;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public RequestResponse responseError(final Throwable throwable) {
		return new ProbeConfigSyncResponse(throwable);
	}

	@Override
	public RequestResponse responseOk() {
		return new ProbeConfigSyncResponse();
	}

	public RequestResponse responseOk(final String serverName) {
		final ProbeConfigSyncResponse response = new ProbeConfigSyncResponse();
		response.setServerName(serverName);
		return response;
	}
}

/*
 * Copyright (C) 2016 Q`ligent.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.communication.request;

import com.tecomgroup.qos.communication.message.QoSRequest;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.communication.response.TaskStatusResponse;
import com.tecomgroup.qos.domain.probestatus.MEventProperty;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AgentActionStatus extends QoSRequest implements Serializable{

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("state")
	private MProbeEvent.STATUS status;

	@JsonProperty("timestamp")
	private Date dateTime;

	private List<MEventProperty> properties;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public MProbeEvent.STATUS getStatus() {
		return status;
	}

	public void setStatus(MProbeEvent.STATUS status) {
		this.status = status;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public List<MEventProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<MEventProperty> properties) {
		this.properties = properties;
	}

	public RequestResponse responseError(final String serverName,
										 final Throwable throwable) {
		final TaskStatusResponse response = new TaskStatusResponse(
				throwable);
		response.setServerName(serverName);
		return response;
	}

	@Override
	public RequestResponse responseError(final Throwable throwable) {
		return new TaskStatusResponse(throwable);
	}

	@Override
	public RequestResponse responseOk() {
		return new TaskStatusResponse();
	}

	public RequestResponse responseOk(final String serverName) {
		final TaskStatusResponse response = new TaskStatusResponse();
		response.setServerName(serverName);
		return response;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import java.util.Map;
import java.util.Set;

/**
 * @author sviyazov.a
 * 
 */
public class PolicySendActionMessage extends QoSMessage {

	private Set<String> contacts;

	private String body;

	private String subject;

	private Map<String, Object> outputParameters;

	public PolicySendActionMessage() {
		super();
	}

	public PolicySendActionMessage(final String subject, final String body,
			final Set<String> contacts,
			final Map<String, Object> outputParameters) {
		this();
		this.body = body;
		this.subject = subject;
		this.contacts = contacts;
		this.outputParameters = outputParameters;
	}

	public String getBody() {
		return body;
	}

	public Set<String> getContacts() {
		return contacts;
	}

	public Map<String, Object> getOutputParameters() {
		return outputParameters;
	}

	public String getSubject() {
		return subject;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public void setContacts(final Set<String> contacts) {
		this.contacts = contacts;
	}

	public void setOutputParameters(final Map<String, Object> outputParameters) {
		this.outputParameters = outputParameters;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}
}

/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import java.io.Serializable;

import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;

/**
 * Important wrapper for {@link MPolicySendEmail} and {@link MPolicySendSms}
 * actions.
 * 
 * @author ivlev.e
 */
public class PolicyActionWrapper implements Serializable {

	private static final long serialVersionUID = 931444208772797619L;

	private String type;

	private MContactInformation recipient;

	private Long id;

	public PolicyActionWrapper() {
		super();
	}

	public PolicyActionWrapper(final Long id, final PolicyActionType type,
			final MContactInformation recipient) {
		this(type, recipient);
		this.id = id;
	}

	public PolicyActionWrapper(final PolicyActionType type,
			final MContactInformation recipient) {
		this();
		this.type = type.toString();
		this.recipient = recipient;
	}

	public Long getId() {
		return id;
	}

	public MContactInformation getRecipient() {
		return recipient;
	}

	public String getType() {
		return type;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setRecipient(final MContactInformation receiver) {
		this.recipient = receiver;
	}

	public void setType(final String type) {
		this.type = type;
	}
}

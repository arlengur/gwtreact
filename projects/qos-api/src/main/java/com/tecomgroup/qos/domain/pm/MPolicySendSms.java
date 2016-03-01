/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Entity;

/**
 * 
 * Responsible for sending SMS notifications when policy triggers.
 * 
 * @author ivlev.e
 */
@Entity(name = "MPolicySendSmsAction")
public class MPolicySendSms extends MPolicyActionWithContacts {

	private static final long serialVersionUID = 631997375707328330L;

	public MPolicySendSms() {
		super();
	}

	public MPolicySendSms(final MPolicySendSms policyAction) {
		super(policyAction);
	}

	@Override
	public MPolicySendSms copy() {
		return new MPolicySendSms(this);
	}
}

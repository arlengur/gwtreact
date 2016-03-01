/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Entity;

/**
 * Responsible for sending email notifications when policy triggers.
 * 
 * @author novohatskiy.r
 * 
 */
@Entity(name = "MPolicySendEmailAction")
public class MPolicySendEmail extends MPolicyActionWithContacts {

	private static final long serialVersionUID = -7389450331720982801L;

	public MPolicySendEmail() {
		super();
	}

	public MPolicySendEmail(final MPolicySendEmail policyAction) {
		super(policyAction);
	}

	@Override
	public MPolicySendEmail copy() {
		return new MPolicySendEmail(this);
	}
}

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author abondin
 * 
 */
@Entity(name = "MPolicySendAlertAction")
@SuppressWarnings("serial")
public class MPolicySendAlert extends MPolicyAction {

	@Column(nullable = false)
	private String alertType;

	public MPolicySendAlert() {
		super();
	}

	public MPolicySendAlert(final MPolicySendAlert policyAction) {
		super(policyAction);
		this.alertType = policyAction.getAlertType();
	}

	@Override
	public MPolicySendAlert copy() {
		return new MPolicySendAlert(this);
	}

	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}

	/**
	 * @param alertType
	 *            the alertType to set
	 */
	public void setAlertType(final String alertType) {
		this.alertType = alertType;
	}

	@Override
	public boolean updateSimpleFields(final MPolicyAction policyAction) {
		boolean isUpdated = super.updateSimpleFields(policyAction);

		if (policyAction instanceof MPolicySendAlert) {
			final MPolicySendAlert policySendAlert = (MPolicySendAlert) policyAction;
			if (!equals(getAlertType(), policySendAlert.getAlertType())) {
				setAlertType(policySendAlert.getAlertType());
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}

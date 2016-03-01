/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;

/**
 * @author ivlev.e
 * 
 */
public class PolicyActionContactsValueProvider
		extends
			ValueProviderWithPath<PolicyActionWrapper, MContactInformation> {

	@Override
	public MContactInformation getValue(final PolicyActionWrapper policyActionWrapper) {
		return policyActionWrapper.getRecipient();
	}

	@Override
	public void setValue(final PolicyActionWrapper policyActionWrapper,
			final MContactInformation contact) {
		policyActionWrapper.setRecipient(contact);
	}
}

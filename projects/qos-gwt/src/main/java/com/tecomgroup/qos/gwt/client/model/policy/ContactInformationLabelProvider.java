/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserLabelProvider;

/**
 * @author ivlev.e
 * 
 */
public class ContactInformationLabelProvider
		implements
			LabelProvider<MContactInformation> {

	public static String toLabel(final MContactInformation contact) {
		String value = null;
		if (contact instanceof MUser) {
			value = UserLabelProvider.toLabel((MUser) contact);
		} else if (contact instanceof MUserGroup) {
			value = ((MUserGroup) contact).getName();
		}
		return value;
	}

	@Override
	public String getLabel(final MContactInformation contact) {
		return ContactInformationLabelProvider.toLabel(contact);
	}

}

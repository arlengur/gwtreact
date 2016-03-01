/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.tecomgroup.qos.domain.MContactInformation;

/**
 * @author ivlev.e
 * 
 */
public class ContactInformationModelKeyProvider
		implements
			ModelKeyProvider<MContactInformation> {

	@Override
	public String getKey(final MContactInformation item) {
		return item.getKey();
	}

}

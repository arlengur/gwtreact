/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.policy;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MContactInformation;

/**
 * @author ivlev.e
 * 
 */
public interface PolicyActionWrapperProperties
		extends
			PropertyAccess<PolicyActionWrapper> {

	@Path("id")
	ModelKeyProvider<PolicyActionWrapper> key();

	ValueProvider<PolicyActionWrapper, MContactInformation> recipient();

	ValueProvider<PolicyActionWrapper, String> type();
}

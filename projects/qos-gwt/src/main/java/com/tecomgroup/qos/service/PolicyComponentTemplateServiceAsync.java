/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;

/**
 * 
 * @author ivlev.e
 * 
 */
public interface PolicyComponentTemplateServiceAsync {

	void doesTemplateExist(String name, String templateClass,
			AsyncCallback<Boolean> callback);

	void getAllActionsTemplates(
			AsyncCallback<Collection<MPolicyActionsTemplate>> callback);

	void getAllConditionsTemplates(
			AsyncCallback<Collection<MPolicyConditionsTemplate>> callback);

	void getConditionsTemplates(ParameterType parameterType,
			AsyncCallback<Collection<MPolicyConditionsTemplate>> callback);

	void getTemplateByName(String name, final String templateClass,
			AsyncCallback<MPolicyComponentTemplate> callback);

	void removeTemplate(String name, final String templateClass,
			AsyncCallback<Void> callback);

	void removeTemplates(Collection<String> names, final String templateClass,
			AsyncCallback<Void> callback);

	<M extends MPolicyComponentTemplate> void saveOrUpdateTemplate(M template,
			boolean reapplyToPolicies, AsyncCallback<M> callback);
}

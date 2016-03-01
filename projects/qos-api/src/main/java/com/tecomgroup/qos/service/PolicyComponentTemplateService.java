/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.exception.QOSException;

/**
 * A service to manipulate {@link MPolicyActionsTemplate} objects.
 * 
 * @author novohatskiy.r
 * 
 */
@RemoteServiceRelativePath("springServices/policyComponentTemplateService")
public interface PolicyComponentTemplateService extends Service, RemoteService {

	/**
	 * Checks whether {@link MPolicyComponentTemplate} with provided name
	 * exists.
	 * 
	 * @param name
	 * @param templateClass
	 * @return true if {@link MPolicyComponentTemplate} exists otherwise false.
	 * @throws QOSException
	 */
	boolean doesTemplateExist(String name, String templateClass)
			throws QOSException;

	/**
	 * Returns all existing {@link MPolicyActionsTemplate} objects.
	 * 
	 * @return the collection of all existing {@link MPolicyActionsTemplate} or
	 *         empty collection if there are no templates.
	 */
	Collection<MPolicyActionsTemplate> getAllActionsTemplates();

	/**
	 * Returns all existing {@link MPolicyConditionsTemplate} objects.
	 * 
	 * @return the collection of all existing {@link MPolicyConditionsTemplate}
	 *         or empty collection if there are no templates.
	 */
	Collection<MPolicyConditionsTemplate> getAllConditionsTemplates();

	/**
	 * @return {@link MPolicyConditionsTemplate} with matching
	 *         {@link ParameterType}
	 */
	Collection<MPolicyConditionsTemplate> getConditionsTemplates(
			ParameterType parameterType);

	/**
	 * Returns {@link MPolicyComponentTemplate} with given name.
	 * 
	 * @param name
	 * @param templateClass
	 * @return {@link MPolicyComponentTemplate} or null if there is no template
	 *         with given name.
	 * @throws QOSException
	 */
	MPolicyComponentTemplate getTemplateByName(String name,
			final String templateClass) throws QOSException;

	/**
	 * Removes template by its name.
	 * 
	 * @param name
	 * @throws QOSException
	 */
	void removeTemplate(String name, final String templateClass)
			throws QOSException;

	/**
	 * Removes collection of actions templates by their names
	 * 
	 * @param names
	 * @throws QOSException
	 */
	void removeTemplates(Collection<String> names, final String templateClass)
			throws QOSException;

	/**
	 * Saves or updates given template.
	 * 
	 * @param template
	 * @param reapplyToPolicies
	 *            actual only when updating.
	 * @return
	 * @throws QOSException
	 */
	<M extends MPolicyComponentTemplate> M saveOrUpdateTemplate(M template,
			boolean reapplyToPolicies) throws QOSException;
}
